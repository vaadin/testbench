/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.textfield;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonWrap;
import com.vaadin.testbench.unit.ComponentQuery;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for TextField components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Wraps({ TextField.class, PasswordField.class, EmailField.class,
        BigDecimalField.class, IntegerField.class })
public class TextFieldWrap<T extends GeneratedVaadinTextField<T, V>, V>
        extends ComponentWrap<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public TextFieldWrap(T component) {
        super(component);
    }

    /**
     * Set the value to the component if it is usable.
     * <p>
     * For a non interactable component an IllegalStateException will be thrown
     * as the end user would not be able to set a value.
     *
     * @param value
     *            value to set
     */
    public void setValue(V value) {
        ensureComponentIsUsable();

        if (value == null && getComponent().getEmptyValue() != null) {
            throw new IllegalArgumentException(
                    "Field doesn't allow null values");
        }

        if (hasValidation() && value != null
                && getValidationSupport().isInvalid(value.toString())) {
            if (getComponent().isPreventInvalidInputBoolean()) {
                throw new IllegalArgumentException(
                        "Given value doesn't pass field value validation. Check validation settings for field.");
            }
            LoggerFactory.getLogger(TextFieldWrap.class).warn(
                    "Gave invalid input, but value set as invalid input is not prevented.");
        }

        getComponent().setValue(value);
    }

    private boolean hasValidation() {
        return getValidationSupport() != null;
    }

    private TextFieldValidationSupport getValidationSupport() {
        try {
            return (TextFieldValidationSupport) getField("validationSupport")
                    .get(getComponent());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            // NO-OP Field didn't exist for given GeneratedVaadinTextField
            // implementation
        }
        return null;
    }

    @Override
    public boolean isUsable() {
        // TextFields can be read only so the usable check needs extending
        return super.isUsable() && !getComponent().isReadOnly();
    }

    /**
     * Mixin interface to simplify creation of {@link TextFieldWrap} wrappers
     * for component instances, avoiding explicit casts.
     *
     * Wrapper creation is based on {@link Mixable} functionality, so this mixin
     * requires to be applied on a class already implementing the
     * {@link Mixable#wrap(Class, Component)} method.
     *
     * Usually used with test classes extending
     * {@link com.vaadin.testbench.unit.UIUnitTest}.
     *
     *
     * <pre>
     * {@code
     * class ViewTest extends UIUnitTest implements TextFieldWrap.Mapper {
     *
     *     &#64;Test
     *     void useCaseTest() {
     *         ...
     *         // given view.firstName is a TextField
     *         TheView view = navigate(TheView.class);
     *
     *         // without mapper mixin
     *         TextFieldWrap tf_ = wrap(TextFieldWrapper.class, view.firstName);
     *         tf_.setValue("John");
     *
     *         // with mixin
     *         wrap(view.firstName).setValue("John");
     *         ...
     *     }
     * }
     * }
     * </pre>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public interface Mixin extends Mixable {

        TextFieldKind<String, TextField> TEXTFIELD = new TextFieldKind<>(
                TextField.class);

        default <T extends GeneratedVaadinTextField<T, V>, V> TextFieldWrap<T, V> wrap(
                T textField) {
            return wrap(TextFieldWrap.class, textField);
        }

        default ComponentQuery<TextField, TextFieldWrap<TextField, String>> $textField() {
            return $(TextField.class);
        }

        default <V, T extends GeneratedVaadinTextField<T, V>> ComponentQuery<T, TextFieldWrap<T, V>> $textField(
                Class<T> componentType) {
            return $(componentType);
        }

        default <V, T extends GeneratedVaadinTextField<T, V>, W extends TextFieldWrap<? extends T, V>, Q extends ComponentQuery<T, W>> Q $(
                TextFieldKind kind) {
            return (Q) $(kind.componentType);
        }

        default <V, T extends GeneratedVaadinTextField<T, V>, W extends TextFieldWrap<? extends T, V>, Q extends ComponentQuery<T, W>> Q $(
                TextFieldKind kind, Class<V> valueType) {
            return (Q) $(kind.componentType);
        }

    }

    public static class TextFieldKind<V, C extends GeneratedVaadinTextField<C, V>>
            extends Mixable.TypedKind<V, C, TextFieldWrap<C, V>> {

        private TextFieldKind(Class<C> componentType) {
            super(componentType, (Class) TextFieldWrap.class);
        }

        public <X, Y extends GeneratedVaadinTextField<Y, X>> TextFieldKind<X, Y> as(
                Class<Y> ct) {
            return new TextFieldKind<>(ct);
        }

    }

}
