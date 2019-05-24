package com.vaadin.testbench.annotations;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * A helper class for performing query operations on annotations.
 */
public class AnnotationHelper {

    private AnnotationHelper() {
    }

    /**
     * Finds the first (non-default) declaration of an annotation parameter.
     * If the {@link AnnotatedElement} is a method, it is first checked for the annotation,
     * if a non-default value is found it is found; if otherwise the enclosing class is checked.
     * If the annotated element is a class, then the class is first checked and if a non-default value is not found,
     * its superclass is checked and so forth until the root of the class hierarchy ({@link Object}) is reached.
     *
     * @param element the element from which the search starts. Could be a {@link Method} or {@link Class}
     * @param annotationClass the annotation class
     * @param parameterSupplier a function that produces the desired value from the annotation.
     *                          In most cases a method reference would be ideal e.g {@code VaadinTest::loadMode})
     * @param defaultValue the value for default comparison, also to be returned if no non-default value is found.
     * @param <AnnotationType> the annotation
     * @param <ResultType> the type of the desired annotation parameter
     * @return the first non-default
     */
    public static <AnnotationType extends Annotation, ResultType>
            ResultType findFirstSpecifiedParameter(AnnotatedElement element,
                                                   Class<AnnotationType> annotationClass,
                                                   Function<AnnotationType, ResultType> parameterSupplier,
                                                   ResultType defaultValue) {

        if (element == Object.class) {
            return defaultValue;
        }

        final AnnotationType annotation = element.getDeclaredAnnotation(annotationClass);

        if (annotation == null || defaultValue.equals(parameterSupplier.apply(annotation))) {
            final AnnotatedElement parent = element instanceof Method
                    ? ((Method) element).getDeclaringClass()
                    : ((Class) element).getSuperclass();

            return findFirstSpecifiedParameter(parent, annotationClass, parameterSupplier, defaultValue);
        }

        return parameterSupplier.apply(annotation);
    }

    /**
     * Collects and merges the parameter of all decorations of the specified annotation up the class hierarchy.
     *
     * Note: the result can possibly contain duplicate values. If duplicates are undesired,
     *       a {@link Set} could be specified as the {@code defaultValue} to make the result distinct.
     * Note: default values encoded in the annotation class can possible be in the result.
     *
     * @param element the element from which the search starts. Could be a {@link Method} or {@link Class}
     * @param annotationClass the annotation class
     * @param parameterSupplier a function that produces the desired value from the annotation.
     *                          In most cases a method reference would be ideal e.g {@code VaadinTest::loadMode})
     * @param defaultValue the value for default comparison, also to be returned if no non-default value is found.
     * @param <AnnotationType> the annotation
     * @param <ResultType> the type of the desired annotation parameter
     * @return a collection of all collected parameters
     */
    public static <AnnotationType extends Annotation, ResultType> Collection<ResultType> mergeSpecifiedParameters(
            AnnotatedElement element,
            Class<AnnotationType> annotationClass,
            Function<AnnotationType, ResultType> parameterSupplier,
            Collection<ResultType> defaultValue) {

        if (element == null || element == Object.class) {
            return defaultValue;
        }

        final AnnotationType annotation = element.getDeclaredAnnotation(annotationClass);

        if (annotation != null) {
            defaultValue.add(parameterSupplier.apply(annotation));
        }

        final AnnotatedElement parent = element instanceof Method
                ? ((Method) element).getDeclaringClass()
                : ((Class) element).getSuperclass();

        return mergeSpecifiedParameters(parent, annotationClass, parameterSupplier, defaultValue);
    }

    /**
     * Flattens a collection of arrays into a linear collection.
     * This is especially useful in combination with
     * {@link #mergeSpecifiedParameters(AnnotatedElement, Class, Function, Collection)}
     * when the parameter type is an array.
     *
     * @param collection the collection of arrays
     * @param <ResultType> the inner type
     * @return the flattened collection
     */
    public static <ResultType> Collection<ResultType> flatten(Collection<ResultType[]> collection) {
        return collection
                .stream()
                .map(Arrays::asList)
                .flatMap(List::stream)
                .collect(toList());
    }
}
