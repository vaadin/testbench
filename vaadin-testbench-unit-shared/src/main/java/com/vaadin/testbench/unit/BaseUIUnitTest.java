/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.googlecode.gentyref.GenericTypeReflector;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.pro.licensechecker.Capabilities;
import com.vaadin.pro.licensechecker.Capability;
import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.testbench.unit.internal.MockInternalSeverError;
import com.vaadin.testbench.unit.internal.MockVaadin;
import com.vaadin.testbench.unit.internal.Routes;
import com.vaadin.testbench.unit.internal.ShortcutsKt;
import com.vaadin.testbench.unit.internal.UtilsKt;
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
 * 
 * @deprecated Replace the vaadin-testbench-unit dependency with
 *             browserless-test-junit6 and use the corresponding class from the
 *             com.vaadin.browserless package instead. This class will be
 *             removed in a future version.
 */
@Deprecated(forRemoval = true, since = "10.1")
public abstract class BaseUIUnitTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BaseUIUnitTest.class.getPackageName());
    private static final ConcurrentHashMap<String, Routes> routesCache = new ConcurrentHashMap<>();

    protected static final Map<Class<?>, Class<? extends ComponentTester>> testers = new HashMap<>();
    protected static final Set<String> scanned = new HashSet<>();

    private TestSignalEnvironment signalsTestEnvironment;

    static {
        testers.putAll(scanForTesters("com.vaadin.flow.component"));
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
                properties.getProperty("testbench.version"), null,
                Capabilities.of(Capability.PRE_TRIAL));
    }

    // Visible for test
    static Map<Class<?>, Class<? extends ComponentTester>> scanForTesters(
            String... packages) {
        try (ScanResult scan = new ClassGraph().enableClassInfo()
                .enableAnnotationInfo().acceptPackages(packages).scan(2)) {
            ClassInfoList testerList = scan
                    .getClassesWithAnnotation(Tests.class.getName());
            Map<Class<?>, Class<? extends ComponentTester>> testerMap = new HashMap<>();
            testerList
                    .filter(classInfo -> classInfo
                            .extendsSuperclass(ComponentTester.class))
                    .forEach(classInfo -> {
                        try {
                            final Class<?> tester = UtilsKt
                                    .findClassOrThrow(classInfo.getName());
                            final Class<? extends Component>[] annotation = tester
                                    .getAnnotation(Tests.class).value();
                            for (Class<? extends Component> component : annotation) {
                                testerMap.put(component,
                                        (Class<? extends ComponentTester>) tester);
                            }
                            // -- Enable annotation with fqn for components with
                            // generics
                            final String[] classes = tester
                                    .getAnnotation(Tests.class).fqn();

                            Arrays.stream(classes).map(clazz -> {
                                try {
                                    return UtilsKt.findClassOrThrow(clazz);
                                } catch (ClassNotFoundException e) {
                                    logTypeLoadingIssue(e,
                                            "Tester '{}' cannot be loaded because of missing component class '{}' on classpath",
                                            classInfo.getName(), clazz);
                                }
                                return null;
                            }).filter(Objects::nonNull)
                                    .forEach(clazz -> testerMap.put(clazz,
                                            (Class<? extends ComponentTester>) tester));

                        } catch (TypeNotPresentException e) {
                            logTypeLoadingIssue(e,
                                    "Tester '{}' cannot be loaded because of missing class '{}' on classpath",
                                    classInfo.getName(), e.typeName());
                        } catch (ClassNotFoundException
                                | NoClassDefFoundError e) {
                            logTypeLoadingIssue(e,
                                    "Tester '{}' cannot be loaded because of missing class on classpath: {}",
                                    classInfo.getName(), e.getMessage());
                        }
                    });
            return Collections.unmodifiableMap(testerMap);
        }
    }

    private static void logTypeLoadingIssue(Throwable ex, String message,
            Object... args) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(message, args, ex);
        } else {
            LOGGER.warn(message, args);
        }
    }

    protected synchronized Routes discoverRoutes() {
        return discoverRoutes(scanPackages());
    }

    /**
     * Discover and return Routes for mocked Vaadin core system.
     *
     * @see #initVaadinEnvironment()
     * @return Routes
     */
    protected static synchronized Routes discoverRoutes(
            Set<String> packageNames) {
        packageNames = packageNames == null || packageNames.isEmpty()
                ? Set.of("")
                : packageNames;

        return packageNames.stream()
                .map(pkg -> routesCache.computeIfAbsent(pkg,
                        p -> new Routes().autoDiscoverViews(p)))
                .reduce(new Routes(), Routes::merge);
    }

    /**
     * Create mocked Vaadin core obects, such as session, servlet populated with
     * Routes, UI etc. for testing and find testers for the components.
     */
    protected void initVaadinEnvironment() {
        scanTesters();
        MockVaadin.setup(discoverRoutes(), MockedUI::new, lookupServices());
        initSignalsSupport();
    }

    protected void initSignalsSupport() {
        signalsTestEnvironment = TestSignalEnvironment.register();
    }

    /**
     * Scan testers and populate testers map with them. The test method can find
     * appropriate test based on testers map.
     *
     * @see #test(Component)
     */
    protected void scanTesters() {
        if (getClass().isAnnotationPresent(ComponentTesterPackages.class)) {
            final List<String> packages = Arrays.asList(getClass()
                    .getAnnotation(ComponentTesterPackages.class).value());
            if (!scanned.containsAll((packages))) {
                scanned.addAll(packages);
                testers.putAll(scanForTesters(getClass()
                        .getAnnotation(ComponentTesterPackages.class).value()));
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

    /**
     * Tears down mocked Vaadin.
     */
    protected void cleanVaadinEnvironment() {
        if (signalsTestEnvironment != null) {
            signalsTestEnvironment.unregister();
            signalsTestEnvironment = null;
        }
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
        verifyAndGetUI().navigate(navigationTarget);
        return validateNavigationTarget(navigationTarget);
    }

    private <T extends Component> T validateNavigationTarget(
            Class<T> navigationTarget) {
        final HasElement currentView = getCurrentView();
        if (!navigationTarget.isAssignableFrom(currentView.getClass())) {
            if (currentView instanceof MockInternalSeverError) {
                System.err.println(
                        currentView.getElement().getProperty("stackTrace"));
            }
            throw new IllegalArgumentException(
                    "Navigation resulted in unexpected class "
                            + currentView.getClass().getName() + " instead of "
                            + navigationTarget.getName());
        }
        return navigationTarget.cast(currentView);
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
        verifyAndGetUI().navigate(navigationTarget, parameter);
        return validateNavigationTarget(navigationTarget);
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
        verifyAndGetUI().navigate(navigationTarget,
                new RouteParameters(parameters));
        return validateNavigationTarget(navigationTarget);
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
        verifyAndGetUI().navigate(location);
        return validateNavigationTarget(expectedTarget);
    }

    /**
     * Simulates a keyboard shortcut performed on the browser.
     *
     * @param key
     *            Primary key of the shortcut. This must not be a
     *            {@link KeyModifier}.
     * @param modifiers
     *            Key modifiers. Can be empty.
     */
    public void fireShortcut(Key key, KeyModifier... modifiers) {
        UI ui = verifyAndGetUI();
        // TODO: should this logic be moved to ShortcutsKt.fireShortcut?
        if (ui.hasModalComponent()) {
            ShortcutsKt._fireShortcut(
                    ui.getInternals().getActiveModalComponent(), key,
                    modifiers);
        } else {
            ShortcutsKt.fireShortcut(key, modifiers);
        }
    }

    /**
     * Get the current view instance that is shown on the ui.
     *
     * @return current view
     */
    public HasElement getCurrentView() {
        return verifyAndGetUI().getInternals().getActiveRouterTargetsChain()
                .get(0);
    }

    // Visible to ComponentWrap
    @SuppressWarnings("unchecked")
    static <T extends ComponentTester<Y>, Y extends Component> T internalWrap(
            Y component) {
        return (T) initialize(getTester(component.getClass()), component);
    }

    static <T extends ComponentTester<Y>, Y extends Component> T internalWrap(
            Class<T> wrap, Y component) {
        return initialize(wrap, component);
    }

    // Visible to ComponentWrap
    static <T extends Component> ComponentQuery<T> internalQuery(
            Class<T> componentType) {
        return new ComponentQuery<>(componentType);
    }

    /**
     * Wrap component with ComponentTester best matching component type.
     *
     * @param component
     *            component to get test wrapper for
     * @param <T>
     *            tester type
     * @param <Y>
     *            component type
     * @return component in wrapper with test helpers
     */
    public <T extends ComponentTester<Y>, Y extends Component> T test(
            Y component) {
        verifyAndGetUI();
        return internalWrap(component);
    }

    /**
     * Wrap component in given ComponentTester.
     *
     * @param tester
     *            test wrapper to use
     * @param component
     *            component to wrap
     * @param <T>
     *            tester type
     * @param <Y>
     *            component type
     * @return initialized test wrapper for component
     */
    public <T extends ComponentTester<Y>, Y extends Component> T test(
            Class<T> tester, Y component) {
        verifyAndGetUI();
        return (T) initialize(tester, component);
    }

    private static <Y extends Component> Class<? extends ComponentTester> getTester(
            Class<Y> component) {
        Class<?> latest = component;
        do {
            if (testers.containsKey(latest)) {
                return testers.get(latest);
            }
            latest = latest.getSuperclass();
        } while (!Component.class.equals(latest));
        return ComponentTester.class;
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
        verifyAndGetUI();
        return internalQuery(componentType);
    }

    /**
     * Gets a query object for finding a component nested inside the given
     * component.
     *
     * @param componentType
     *            the type of the component(s) to search for
     * @param fromThis
     *            component used as starting element for search.
     * @param <T>
     *            the type of the component(s) to search for
     * @return a query object for finding components
     */
    public <T extends Component> ComponentQuery<T> $(Class<T> componentType,
            Component fromThis) {
        verifyAndGetUI();
        return new ComponentQuery<>(componentType).from(fromThis);
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
        return new ComponentQuery<>(componentType).from(viewComponent);
    }

    /**
     * Private initializer for tester classes.
     *
     * @param clazz
     *            tester class to initialize
     * @param component
     *            component used with tester class
     * @param <T>
     *            component tester type
     * @param <Y>
     *            component type
     * @return tester with component set
     */
    private static <T extends ComponentTester<Y>, Y extends Component> T initialize(
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
     * Processes all pending Signals tasks with a default max wait time of 100
     * milliseconds. This is a convenience method for tests that need to wait
     * for asynchronous Signal effects to complete.
     *
     * <p>
     * When Signals are triggered from background threads or non-UI contexts,
     * their effects are enqueued to simulate asynchronous processing. This
     * method allows tests to flush and execute all such pending tasks
     * synchronously, ensuring deterministic behavior in unit tests.
     *
     * <p>
     * If any {@link VaadinSession} lock is held by the current thread, it is
     * temporarily released during the wait to allow background threads to
     * acquire the lock and enqueue tasks.
     *
     * @return {@code true} if any pending Signals tasks were processed.
     * @see #runPendingSignalsTasks(long, TimeUnit)
     * @see TestSignalEnvironment#runPendingTasks(long, TimeUnit)
     */
    protected final boolean runPendingSignalsTasks() {
        return runPendingSignalsTasks(100, TimeUnit.MILLISECONDS);
    }

    /**
     * Processes all pending Signals tasks, waiting up to the specified timeout
     * for tasks to arrive. This method is essential for testing asynchronous
     * Signal effects triggered from background threads or non-UI contexts.
     *
     * <p>
     * When Signals are triggered from background threads or non-UI contexts,
     * their effects are enqueued to simulate asynchronous processing. This
     * method allows tests to flush and execute all such pending tasks
     * synchronously, ensuring deterministic behavior in unit tests.
     *
     * <p>
     * The timeout applies only to waiting for the first task to arrive. Once
     * the first task is found, all remaining tasks in the queue are processed
     * immediately without additional waiting. If any {@link VaadinSession} lock
     * is held by the current thread, it is temporarily released during the wait
     * to allow background threads to acquire the lock and enqueue tasks.
     *
     * @param maxWaitTime
     *            the maximum time to wait for the first task to arrive in the
     *            given time unit. If &lt;= 0, returns immediately if no tasks
     *            are available.
     * @param unit
     *            the time unit of the timeout value
     * @return {@code true} if any pending Signals tasks were processed.
     * @see TestSignalEnvironment#runPendingTasks(long, TimeUnit)
     */
    protected final boolean runPendingSignalsTasks(long maxWaitTime,
            TimeUnit unit) {
        if (this.signalsTestEnvironment != null) {
            return this.signalsTestEnvironment.runPendingTasks(maxWaitTime,
                    unit);
        }
        return false;
    }

    /**
     * Detects the component type for the given tester from generic declaration,
     * by inspecting class hierarchy to resolve the concrete type for
     * {@link ComponentTester} defined type variable.
     *
     * @param testerType
     *            the tester type
     * @return the component type the tester defines
     */
    @SuppressWarnings("rawtypes")
    static Class<?> detectComponentType(
            Class<? extends ComponentTester> testerType) {
        if (testerType == ComponentTester.class) {
            return Component.class;
        }
        Map<Type, Type> typeMap = new HashMap<>();
        Class<?> clazz = testerType;
        while (!clazz.equals(ComponentTester.class)) {
            extractTypeArguments(typeMap, clazz);
            clazz = clazz.getSuperclass();
        }
        return GenericTypeReflector.erase(
                typeMap.get(ComponentTester.class.getTypeParameters()[0]));
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

    /*
     * Checks that the mock UI is available, otherwise fails fast with an
     * exception giving advices on possible causes of the problem.
     *
     * Principal cause for having a null UI is that the test extends the wrong
     * base class for the current configuration, e.g. using UIUnit4Test with
     * JUnit 5 or the opposite.
     *
     */
    private UI verifyAndGetUI() {
        UI ui = UI.getCurrent();
        if (ui == null) {
            String message = "Test Vaadin environment is not initialized correctly. "
                    + "This may happen when the test is extending the wrong base class for the testing engine in use. "
                    + "Current test class is expected to run with "
                    + testingEngine() + ".";
            throw new UIUnitTestSetupException(message);
        }
        return ui;
    }

    /**
     * Gets the name of the Test Engine that is able to run the base class
     * implementation.
     *
     * The Test Engine name is reported in the exception thrown when the Vaadin
     * environment is not set up correctly.
     *
     * @return name of the Test Engine.
     */
    protected abstract String testingEngine();

}
