package com.vaadin.testbench.annotations;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Additional qualifier to be used on element classes which do not use an unique
 * tag name.
 * <p>
 * While custom elements can be uniquely identified using their tag name, e.g.
 * <code>&lt;testbench-button&gt;</code>. Flow views and server side composites
 * cannot be identified this way but typically needs an additional filter which
 * can be defined using this annotations.
 * <p>
 * For instance, given <code>public class MyView extends Div</code> and the
 * corresponding element class
 * <code>public class MyViewElement extends DivElement</code> would match any
 * <code>&lt;div&gt;</code> on the page. To uniquely match
 * <code>MyViewElement</code>, you could add e.g.<code>setId("myview");</code>
 * in the view and a corresponding
 * <code>@Attribute(name="id", value="myview")</code> for
 * <code>MyViewElement</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Repeatable(Attribute.Container.class)
public @interface Attribute {

    String DEFAULT_VALUE = "THE_DEFAULT_VALUE_WHICH_YOU_SURELY_NEVER_EVER_WILL_USE_FOR_REAL, RIGHT?!";
    /**
     * Replaced by the simple class name of the element class, with any
     * <code>Element</code> or <code>PageObject</code> suffix removed, and
     * converted to dash-separated-format when used with either
     * {@link #contains()} or {@link #value()},
     */
    String SIMPLE_CLASS_NAME = "THE_CONVENTION_VALUE_WHICH_YOU_SURELY_NEVER_EVER_WILL_USE_FOR_REAL, RIGHT?!";

    /**
     * The name of the attribute to check.
     * <p>
     * Matches the value of the attribute with the value in {@link #value()} or
     * matches a token inside this value with {@link #contains()}. If neither
     * {@link #value()} nor {@link #contains()} is defined, only ensures that
     * the attribute is present.
     *
     * @return the name of the attribute to match
     */
    String name();

    /**
     * The value to match with the attribute value.
     * <p>
     * This will match the given value to the full value of the attribute. To
     * match only a token, use {@link #contains()}.
     *
     * @return the value to match with the attribute value
     */
    String value() default DEFAULT_VALUE;

    /**
     * The value to find within the attribute value.
     * <p>
     * This will match if the given string is one matches one of the space
     * separated tokens the attribute value consists of. To match the whole
     * attribute value, use {@link #value()}.
     *
     * @return the value find inside the attribute value
     */
    String contains() default DEFAULT_VALUE;

    /**
     * Internal annotations to enable use of multiple {@link Attribute}
     * annotations.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @Inherited
    @interface Container {

        /**
         * Internally used to enable use of multiple {@link Attribute}
         * annotations.
         *
         * @return an array of the Attribute annotations
         */
        Attribute[] value();
    }
}
