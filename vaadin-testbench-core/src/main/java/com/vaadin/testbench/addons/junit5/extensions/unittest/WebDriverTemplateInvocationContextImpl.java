package com.vaadin.testbench.addons.junit5.extensions.unittest;

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

import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.PageObject;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.util.List;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.ExtensionContextFunctions.containerInfo;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.PAGE_OBJECT_NAVIGATION_TARGET;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.PAGE_OBJECT_PRELOAD;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.storePageObject;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;
import static java.util.Collections.singletonList;
import static org.openqa.selenium.support.PageFactory.initElements;

public final class WebDriverTemplateInvocationContextImpl implements WebDriverTemplateInvocationContext {

    private final WebDriver webDriver;
    private PageObjectInvocationContextProvider pageObjectInvocationContextProvider;

    protected WebDriverTemplateInvocationContextImpl(
            PageObjectInvocationContextProvider pageObjectInvocationContextProvider,
            WebDriver webDriver) {
        this.pageObjectInvocationContextProvider = pageObjectInvocationContextProvider;
        this.webDriver = webDriver;
    }

    @Override
    public WebDriver webdriver() {
//    pageObjectInvocationContextProvider.logger().info("WebDriverTemplateInvocationContextImpl - webdriver() called");
        return webDriver;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return webdriverName(webdriver());
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new ParameterResolver() {
            @Override
            public boolean supportsParameter(ParameterContext parameterContext,
                                             ExtensionContext extensionContext) {
                final Class<?> type = parameterContext.getParameter().getType();
                return PageObject.class.isAssignableFrom(type);
            }

            @Override
            public PageObject resolveParameter(ParameterContext parameterContext,
                                               ExtensionContext extensionContext) {

//        logger().info("resolveParameter called..");
                Class<?> pageObjectClass = parameterContext
                        .getParameter()
                        .getType();

                PageObject pageObject = null;
                try {
                    final Constructor<?> constructor
                            = pageObjectClass.getConstructor(WebDriver.class, ContainerInfo.class);
                    WebDriver webDriver = webdriver();
                    pageObject = (PageObject) constructor.newInstance(webDriver, containerInfo(extensionContext));
                } catch (Exception e) {
                    throw new ParameterResolutionException("Unable to create PageObjectInstance of type "
                            + pageObjectClass);
                }

                // TODO(sven): Check if needed.
                initElements(new WebDriverExtensionFieldDecorator(webDriver), pageObject);

                // TODO(sven): Work on preload feature.

                final Boolean preLoad = storeMethodPlain(extensionContext).get(PAGE_OBJECT_PRELOAD, Boolean.class);
                if (preLoad) {
                    final String nav = storeMethodPlain(extensionContext).get(
                            PAGE_OBJECT_NAVIGATION_TARGET, String.class);
                    if (nav != null) pageObject.loadPage(nav);
                    else pageObject.loadPage();

                }
//           else logger()
//              .info("No preLoading activated for testClass/testMethod "
//                    + extensionContext.getTestClass() + " / "
//                    + extensionContext.getTestMethod());

//              pageObjectInvocationContextProvider.logger().fine("pageobject of type " + pageObjectClass.getSimpleName()
//                                                                + " was created with " + webdriverName().apply(webdriver()));
                storePageObject(pageObject, extensionContext);

                return pageObject;
            }
        });
    }
}
