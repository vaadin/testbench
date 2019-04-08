package com.vaadin.testbench.addons.junit5.extensions;

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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.util.Optional;

public interface ExtensionFunctions {

    Namespace NAMESPACE_GLOBAL = Namespace.create("global");

    static Namespace namespaceForMethod(ExtensionContext ctx) {
        String name = ctx.getTestClass().get().getName();
        String methodName = ctx.getTestMethod().get().getName();
        Namespace namespace = Namespace.create(ExtensionFunctions.class,
                name, methodName);
        return namespace;
    }

    static Namespace namespaceForClass(ExtensionContext ctx) {
        String name = ctx.getTestClass().get().getName();
        Namespace namespace = Namespace.create(ExtensionFunctions.class,
                name);
        return namespace;
    }

    static boolean isMethodCtx(ExtensionContext context) {
        return context.getTestMethod().isPresent();
    }

    static boolean isClassCtx(ExtensionContext context) {
        return context.getTestMethod().isPresent();
    }

    static Store storeGlobalPlain(ExtensionContext context) {
        return context.getStore(NAMESPACE_GLOBAL);
    }

    static Store storeClassPlain(ExtensionContext context) {
        return context.getStore(namespaceForClass(context));
    }

    static Store storeMethodPlain(ExtensionContext context) {
        return context.getStore(namespaceForMethod(context));
    }

    static boolean storeContains(Store store, String key) {
        return store.get(key) != null;
    }

    /**
     * Will deliver a value (typed) from
     * 1. method Store
     * 2. class Store
     * 3. Global Store
     * or fail
     *
     * @param <T>
     * @return
     */
    static <T> Optional<T> value(Class<T> type, String key, ExtensionContext ctx) {
        if (isMethodCtx(ctx) &&
                storeMethodPlain(ctx).get(key) != null) {
            return Optional.ofNullable(storeMethodPlain(ctx).get(key, type));
        } else if (isClassCtx(ctx) && storeClassPlain(ctx).get(key) != null) {
            return Optional.ofNullable(storeClassPlain(ctx).get(key, type));
        } else if (storeGlobalPlain(ctx).get(key) != null) {
            return Optional.ofNullable(storeGlobalPlain(ctx).get(key, type));
        }

//        logger().info("No key value pair found key -> " + key);
        return Optional.empty();
    }

    static Optional<Integer> valueAsInt(String key, ExtensionContext ctx) {
        return value(Integer.class, key, ctx);
    }

    static Integer valueAsIntPlain(String key, ExtensionContext ctx) {
        return valueAsInt(key, ctx).get();
    }

    static Optional<String> valueAsString(String key, ExtensionContext ctx) {
        return value(String.class, key, ctx);
    }

    static String valueAsStringPlain(String key, ExtensionContext ctx) {
        return valueAsString(key, ctx).get();
    }
}
