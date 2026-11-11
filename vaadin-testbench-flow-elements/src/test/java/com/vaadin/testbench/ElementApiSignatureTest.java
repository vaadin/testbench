/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.button.testbench.ButtonElement;

/**
 * The element API names no Selenium type: a test written against it can run on
 * any backend, and Selenium is reached only through the backend escape hatch.
 * <p>
 * The API is what a test reaches from the element classes: their supertypes,
 * and every {@code com.vaadin} type their public and protected signatures name,
 * followed from there ({@code TestBenchElement}, {@code ElementQuery}, the
 * mixins, {@code TestBenchCommandExecutor}, the SPI). A signature leaks when a
 * type in it, or a type argument, bound or {@code throws} clause, is a Selenium
 * type or extends one.
 */
public class ElementApiSignatureTest {

    /** Selenium-typed until v2 decides on screenshot comparison. */
    private static final Set<String> NOT_FOLLOWED = Set
            .of("com.vaadin.testbench.screenshot");

    @Test
    public void elementApiNamesNoSeleniumType() throws Exception {
        Set<String> leaks = new TreeSet<>();
        for (Class<?> type : elementApi()) {
            check(type, leaks);
        }
        Assert.assertEquals("Selenium in the element API", Set.of(), leaks);
    }

    @Test
    public void theApiReachesTheCoreTypes() throws Exception {
        Set<Class<?>> api = elementApi();
        for (Class<?> core : List.of(TestBenchElement.class,
                ElementQuery.class, HasLabel.class, HasElementQuery.class,
                com.vaadin.testbench.commands.TestBenchCommandExecutor.class,
                com.vaadin.testbench.spi.Backend.class,
                com.vaadin.testbench.spi.ElementBackend.class, Keys.class,
                Point.class, NoSuchElementException.class)) {
            Assert.assertTrue(core.getName(), api.contains(core));
        }
    }

    @Test
    public void theCheckSeesALeak() {
        Set<String> leaks = new TreeSet<>();
        check(WebElementAdapter.class, leaks);
        Assert.assertFalse(leaks.isEmpty());
    }

    private static Set<Class<?>> elementApi()
            throws IOException, URISyntaxException {
        Deque<Class<?>> toVisit = new ArrayDeque<>(elementClasses());
        Set<Class<?>> api = new HashSet<>();
        while (!toVisit.isEmpty()) {
            Class<?> type = toVisit.pop();
            if (!api.add(type)) {
                continue;
            }
            for (Class<?> reached : reachedTypes(type)) {
                if (isFollowed(reached) && !api.contains(reached)) {
                    toVisit.push(reached);
                }
            }
        }
        return api;
    }

    private static List<Class<?>> elementClasses()
            throws IOException, URISyntaxException {
        Path root = Path.of(ButtonElement.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        try (Stream<Path> files = Files.walk(root)) {
            return files.filter(file -> file.toString().endsWith(".class"))
                    .map(file -> root.relativize(file).toString())
                    .map(name -> name.substring(0, name.length() - 6)
                            .replace('/', '.'))
                    .map(ElementApiSignatureTest::load)
                    .filter(ElementApiSignatureTest::isApi).toList();
        }
    }

    private static Class<?> load(String name) {
        try {
            return Class.forName(name, false,
                    ElementApiSignatureTest.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isApi(Class<?> type) {
        return !type.isAnonymousClass() && !type.isLocalClass()
                && !type.isSynthetic() && isVisible(type.getModifiers());
    }

    private static boolean isVisible(int modifiers) {
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
    }

    private static boolean isFollowed(Class<?> type) {
        return type.getName().startsWith("com.vaadin.") && isApi(type)
                && !NOT_FOLLOWED.contains(type.getPackageName());
    }

    private static void check(Class<?> type, Set<String> leaks) {
        String owner = type.getName();
        if (isSelenium(type)) {
            leaks.add(owner + ": is or extends a Selenium type");
        }
        for (Type supertype : supertypes(type)) {
            report(owner + " supertype", supertype, leaks);
        }
        for (Field field : type.getDeclaredFields()) {
            if (isVisible(field.getModifiers()) && !field.isSynthetic()) {
                report(describe(field), field.getGenericType(), leaks);
            }
        }
        for (Executable executable : executables(type)) {
            for (Type parameter : executable.getGenericParameterTypes()) {
                report(describe(executable), parameter, leaks);
            }
            for (Type thrown : executable.getGenericExceptionTypes()) {
                report(describe(executable), thrown, leaks);
            }
            for (TypeVariable<?> variable : executable.getTypeParameters()) {
                report(describe(executable), variable, leaks);
            }
            if (executable instanceof Method method) {
                report(describe(executable), method.getGenericReturnType(),
                        leaks);
            }
        }
    }

    private static void report(String where, Type type, Set<String> leaks) {
        for (Class<?> named : classesIn(type)) {
            if (isSelenium(named)) {
                leaks.add(where + ": " + named.getName());
            }
        }
    }

    private static Set<Class<?>> reachedTypes(Class<?> type) {
        Set<Class<?>> reached = new HashSet<>();
        for (Type supertype : supertypes(type)) {
            reached.addAll(classesIn(supertype));
        }
        for (Field field : type.getDeclaredFields()) {
            if (isVisible(field.getModifiers())) {
                reached.addAll(classesIn(field.getGenericType()));
            }
        }
        for (Executable executable : executables(type)) {
            for (Type parameter : executable.getGenericParameterTypes()) {
                reached.addAll(classesIn(parameter));
            }
            for (Type thrown : executable.getGenericExceptionTypes()) {
                reached.addAll(classesIn(thrown));
            }
            if (executable instanceof Method method) {
                reached.addAll(classesIn(method.getGenericReturnType()));
            }
        }
        reached.addAll(Arrays.asList(type.getDeclaredClasses()));
        return reached;
    }

    private static List<Type> supertypes(Class<?> type) {
        List<Type> supertypes = new java.util.ArrayList<>(
                Arrays.asList(type.getGenericInterfaces()));
        if (type.getGenericSuperclass() != null) {
            supertypes.add(type.getGenericSuperclass());
        }
        return supertypes;
    }

    private static List<Executable> executables(Class<?> type) {
        return Stream
                .concat(Arrays.stream(type.getDeclaredMethods()),
                        Arrays.stream(type.getDeclaredConstructors()))
                .filter(executable -> isVisible(executable.getModifiers())
                        && !executable.isSynthetic()
                        && !(executable instanceof Method method
                                && method.isBridge()))
                .toList();
    }

    /** Every class a type names, through arrays, arguments and bounds. */
    private static Set<Class<?>> classesIn(Type type) {
        Set<Class<?>> classes = new HashSet<>();
        collect(type, classes, new HashSet<>());
        return classes;
    }

    private static void collect(Type type, Set<Class<?>> classes,
            Set<TypeVariable<?>> seen) {
        if (type instanceof Class<?> clazz) {
            if (clazz.isArray()) {
                collect(clazz.getComponentType(), classes, seen);
            } else {
                classes.add(clazz);
            }
        } else if (type instanceof ParameterizedType parameterized) {
            collect(parameterized.getRawType(), classes, seen);
            for (Type argument : parameterized.getActualTypeArguments()) {
                collect(argument, classes, seen);
            }
        } else if (type instanceof WildcardType wildcard) {
            for (Type bound : wildcard.getUpperBounds()) {
                collect(bound, classes, seen);
            }
            for (Type bound : wildcard.getLowerBounds()) {
                collect(bound, classes, seen);
            }
        } else if (type instanceof GenericArrayType array) {
            collect(array.getGenericComponentType(), classes, seen);
        } else if (type instanceof TypeVariable<?> variable
                && seen.add(variable)) {
            for (Type bound : variable.getBounds()) {
                collect(bound, classes, seen);
            }
        }
    }

    private static boolean isSelenium(Class<?> type) {
        if (type.getName().startsWith("org.openqa.")) {
            return true;
        }
        if (type.getSuperclass() != null && isSelenium(type.getSuperclass())) {
            return true;
        }
        return Arrays.stream(type.getInterfaces())
                .anyMatch(ElementApiSignatureTest::isSelenium);
    }

    private static String describe(Member member) {
        String name = member.getDeclaringClass().getName() + "."
                + (member instanceof Constructor<?> ? "<init>"
                        : member.getName());
        if (member instanceof Executable executable) {
            name += Arrays.stream(executable.getParameterTypes())
                    .map(Class::getSimpleName).toList();
        }
        return name;
    }
}
