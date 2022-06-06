/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.googlecode.gentyref.GenericTypeReflector;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.testbench.unit.internal.MockVaadin;
import com.vaadin.testbench.unit.internal.Routes;
import com.vaadin.testbench.unit.mocks.MockedUI;

/**
 * Base class for UI unit tests.
 *
 * Provides methods to set up and clean a mocked Vaadin environment.
 *
 * The class allows scan classpath for routes and error views. Subclasses should
 * typically restrict classpath scanning to a specific packages for faster
 * bootstrap, by using {@link ViewPackages} annotation. If the annotation is not
 * present a full classpath scan is performed
 *
 * For internal use only. May be renamed or removed in a future release.
 *
 * @see ViewPackages
 */
class BaseUIUnitTest {

    private static final ConcurrentHashMap<String, Routes> routesCache = new ConcurrentHashMap<>();

    protected static final Map<Class<?>, Class<? extends ComponentWrap>> wrappers = new HashMap<>();
    protected static final Set<String> scanned = new HashSet<>();

    static {
        wrappers.putAll(scanWrappers("com.vaadin.flow.component"));
        Properties properties = new Properties();
        try {
            properties.load(BaseUIUnitTest.class
                    .getResourceAsStream("testbench.properties"));
        } catch (Exception e) {
            LoggerFactory.getLogger(BaseUIUnitTest.class)
                    .warn("Unable to read TestBench properties file", e);
            throw new ExceptionInInitializerError(e);
        }

        LicenseChecker.checkLicenseFromStaticBlock("vaadin-testbench",
                properties.getProperty("testbench.version"));
    }

    private static Map<Class<?>, Class<? extends ComponentWrap>> scanWrappers(
            String... packages) {
        try (ScanResult scan = new ClassGraph().enableClassInfo()
                .enableAnnotationInfo().acceptPackages(packages).scan(2)) {
            ClassInfoList wrapperList = scan
                    .getClassesWithAnnotation(Wraps.class.getName());
            Map<Class<?>, Class<? extends ComponentWrap>> wrapperMap = new HashMap<>();
            wrapperList
                    .filter(classInfo -> classInfo
                            .extendsSuperclass(ComponentWrap.class))
                    .forEach(classInfo -> {
                        try {
                            final Class<?> wrapper = Class
                                    .forName(classInfo.getName());
                            final Class<? extends Component>[] annotation = wrapper
                                    .getAnnotation(Wraps.class).value();
                            for (Class<? extends Component> component : annotation) {
                                wrapperMap.put(component,
                                        (Class<? extends ComponentWrap>) wrapper);
                            }
                            // -- Enable annotation with fqn for components with
                            // generics
                            final String[] classes = wrapper
                                    .getAnnotation(Wraps.class).fqn();

                            Arrays.stream(classes).map(clazz -> {
                                try {
                                    return Class.forName(clazz);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }).forEach(clazz -> wrapperMap.put(clazz,
                                    (Class<? extends ComponentWrap>) wrapper));

                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return Collections.unmodifiableMap(wrapperMap);
        }
    }

    synchronized Routes discoverRoutes() {
        return discoverRoutes(scanPackages());
    }

    static synchronized Routes discoverRoutes(Set<String> packageNames) {
        packageNames = packageNames == null || packageNames.isEmpty()
                ? Set.of("")
                : packageNames;

        return packageNames.stream()
                .map(pkg -> routesCache.computeIfAbsent(pkg,
                        p -> new Routes().autoDiscoverViews(p)))
                .reduce(new Routes(), Routes::merge);
    }

    protected void initVaadinEnvironment() {
        scanForWrappers();
        MockVaadin.setup(discoverRoutes(), MockedUI::new, lookupServices());
    }

    void scanForWrappers() {
        if (getClass().isAnnotationPresent(ComponentWrapPackages.class)) {
            final List<String> packages = Arrays.asList(getClass()
                    .getAnnotation(ComponentWrapPackages.class).value());
            if (!scanned.containsAll((packages))) {
                scanned.addAll(packages);
                wrappers.putAll(scanWrappers(getClass()
                        .getAnnotation(ComponentWrapPackages.class).value()));
            }
        }
    }

    Set<String> scanPackages() {
        Set<String> packagesToScan = new HashSet<>();

        if (getClass().isAnnotationPresent(ViewPackages.class)) {
            ViewPackages packages = getClass()
                    .getAnnotation(ViewPackages.class);
            Stream.of(packages.classes()).map(Class::getPackageName)
                    .collect(Collectors.toCollection(() -> packagesToScan));
            packagesToScan.addAll(Set.of(packages.packages()));
            // Assume current class package scan if annotation exist but does
            // not provide any restriction
            if (packagesToScan.isEmpty()) {
                packagesToScan.add(getClass().getPackageName());
            }
        }
        packagesToScan.removeIf(Objects::isNull);
        return packagesToScan;
    }

    protected void cleanVaadinEnvironment() {
        MockVaadin.tearDown();
    }

    /**
     * Gets the services implementations to be used to initialized Vaadin
     * {@link com.vaadin.flow.di.Lookup}.
     *
     * Default implementation returns an empty Set. Override this method to
     * provide custom Vaadin services, such as
     * {@link com.vaadin.flow.di.InstantiatorFactory},
     * {@link com.vaadin.flow.di.ResourceProvider}, etc.
     *
     * @return set of services implementation classes, never {@literal null}.
     */
    protected Set<Class<?>> lookupServices() {
        return Collections.emptySet();
    }

    /**
     * Navigate to the given view class if it is registered.
     *
     * @param navigationTarget
     *            view class to navigate to
     * @param <T>
     *            view type
     * @return instantiated view
     */
    public <T extends Component> T navigate(Class<T> navigationTarget) {
        UI.getCurrent().navigate(navigationTarget);
        return navigationTarget.cast(getCurrentView());
    }

    /**
     * Navigate to view with url parameter.
     *
     * @param navigationTarget
     *            view class to navigate to
     * @param parameter
     *            parameter to send to view
     * @param <T>
     *            view type
     * @param <C>
     *            parameter type
     * @return instantiated view
     */
    public <C, T extends Component & HasUrlParameter<C>> T navigate(
            Class<T> navigationTarget, C parameter) {
        UI.getCurrent().navigate(navigationTarget, parameter);
        return navigationTarget.cast(getCurrentView());
    }

    /**
     * Navigate to view corresponding to the given navigation target with the
     * specified parameters.
     *
     * @param navigationTarget
     *            view class to navigate to
     * @param parameters
     *            parameters to pass to view.
     * @param <T>
     *            view type
     * @return instantiated view
     */
    public <T extends Component> T navigate(Class<T> navigationTarget,
            Map<String, String> parameters) {
        UI.getCurrent().navigate(navigationTarget,
                new RouteParameters(parameters));
        return navigationTarget.cast(getCurrentView());
    }

    /**
     * Navigate to given location string. Check that location navigated to is
     * the expected view or throw exception.
     *
     * @param location
     *            location string for navigating
     * @param expectedTarget
     *            class that is expected for navigation
     * @param <T>
     *            view type
     * @return instantiated view
     */
    public <T extends Component> T navigate(String location,
            Class<T> expectedTarget) {
        UI.getCurrent().navigate(location);
        final HasElement currentView = getCurrentView();
        if (!expectedTarget.equals(currentView.getClass())) {
            throw new IllegalArgumentException(
                    "Navigation resulted in unexpected class "
                            + currentView.getClass().getName() + " instead of "
                            + expectedTarget.getName());
        }
        return expectedTarget.cast(currentView);
    }

    /**
     * Get the current view instance that is shown on the ui.
     *
     * @return current view
     */
    public HasElement getCurrentView() {
        return UI.getCurrent().getInternals().getActiveRouterTargetsChain()
                .get(0);
    }

    /**
     * Wrap component with wrapper best matching component type.
     *
     * @param component
     *            component to get test wrapper for
     * @param <T>
     *            wrapper type
     * @param <Y>
     *            component type
     * @return component in wrapper with test helpers
     */
    public <T extends ComponentWrap<Y>, Y extends Component> T wrap(
            Y component) {
        return (T) initialize(getWrapper(component.getClass()), component);
    }

    /**
     * Wrap component in given test wrapper.
     *
     * @param wrap
     *            test wrapper to use
     * @param component
     *            component to wrap
     * @param <T>
     *            wrapper type
     * @param <Y>
     *            component type
     * @return initialized test wrapper for component
     */
    public <T extends ComponentWrap<Y>, Y extends Component> T wrap(
            Class<T> wrap, Y component) {
        return (T) initialize(wrap, component);
    }

    private <Y extends Component> Class<? extends ComponentWrap> getWrapper(
            Class<Y> component) {
        Class<?> latest = component;
        do {
            if (wrappers.containsKey(latest)) {
                return wrappers.get(latest);
            }
            latest = latest.getSuperclass();
        } while (!Component.class.equals(latest));
        return ComponentWrap.class;
    }

    /**
     * Gets a query object for finding a component inside the UI
     *
     * @param componentType
     *            the type of the component(s) to search for
     * @param <T>
     *            the type of the component(s) to search for
     * @return a query object for finding components
     */
    public <T extends Component> ComponentQuery<T> $(Class<T> componentType) {
        return new ComponentQuery<>(componentType, this::wrap);
    }

    /**
     * Gets a query object for finding a component inside the current view
     *
     * @param componentType
     *            the type of the component(s) to search for
     * @param <T>
     *            the type of the component(s) to search for
     * @return a query object for finding components
     */
    public <T extends Component> ComponentQuery<T> $view(
            Class<T> componentType) {
        Component viewComponent = getCurrentView().getElement().getComponent()
                .orElseThrow(() -> new AssertionError(
                        "Cannot get Component instance for current view"));
        return new ComponentQuery<>(componentType, this::wrap)
                .from(viewComponent);
    }

    /**
     * Private initializer for wrapper classes.
     *
     * @param clazz
     *            wrapper class to initialize
     * @param component
     *            component used with wrapper class
     * @param <T>
     *            component wrapper type
     * @param <Y>
     *            component type
     * @return wrapper with component set
     */
    private <T extends ComponentWrap<Y>, Y extends Component> T initialize(
            Class<T> clazz, Y component) {
        try {
            // Get the generic class for given wrapper. Component should be an
            // instance of this.
            final Class<?> aClass = detectComponentType(clazz);
            return clazz.getConstructor(aClass).newInstance(component);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Could not instantiate "
                    + clazz.getSimpleName() + " for component "
                    + component.getClass().getSimpleName());
        }
    }

    /**
     * Simulates a server round-trip, flushing pending component changes.
     */
    protected static void roundTrip() {
        UI.getCurrent().getInternals().getStateTree()
                .collectChanges(nodeChange -> {
                });
        UI.getCurrent().getInternals().getStateTree()
                .runExecutionsBeforeClientResponse();
    }

    /**
     * Detects the component type for the given wrapper from generic
     * declaration, by inspecting class hierarchy to resolve the concrete type
     * for {@link ComponentWrap} defined type variable.
     *
     * @param wrapperType
     *            the wrapper type
     * @return the component type the wrapper defines
     */
    @SuppressWarnings("rawtypes")
    static Class<?> detectComponentType(
            Class<? extends ComponentWrap> wrapperType) {
        if (wrapperType == ComponentWrap.class) {
            return Component.class;
        }
        Map<Type, Type> typeMap = new HashMap<>();
        Class<?> clazz = wrapperType;
        while (!clazz.equals(ComponentWrap.class)) {
            extractTypeArguments(typeMap, clazz);
            clazz = clazz.getSuperclass();
        }
        return GenericTypeReflector
                .erase(typeMap.get(ComponentWrap.class.getTypeParameters()[0]));
    }

    /**
     * Collects actual type for type variables declared by the generic
     * declaration of given clazz.
     *
     * @param typeMap
     *            map associating type variables to actual type
     * @param clazz
     *            the class to inspect for generic types
     */
    private static void extractTypeArguments(Map<Type, Type> typeMap,
            Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return;
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeParameter = ((Class<?>) parameterizedType.getRawType())
                .getTypeParameters();
        Type[] actualTypeArgument = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < typeParameter.length; i++) {
            if (typeMap.containsKey(actualTypeArgument[i])) {
                actualTypeArgument[i] = typeMap.get(actualTypeArgument[i]);
            }
            typeMap.put(typeParameter[i], actualTypeArgument[i]);
        }
    }

}
