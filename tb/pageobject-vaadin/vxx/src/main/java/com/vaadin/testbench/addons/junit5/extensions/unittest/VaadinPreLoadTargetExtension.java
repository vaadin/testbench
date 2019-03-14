package com.vaadin.testbench.addons.junit5.extensions.unittest;

import static com.vaadin.testbench.addons.junit5.extension.unitest.PageObjectFunctions.PAGE_OBJECT_NAVIGATION_TARGET;
import static com.vaadin.testbench.addons.junit5.extension.unitest.PageObjectFunctions.PAGE_OBJECT_PRELOAD;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.vaadin.dependencies.core.logger.HasLogger;

public class VaadinPreLoadTargetExtension implements BeforeEachCallback, HasLogger {

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    context.getTestMethod()
           .ifPresent(m -> {
             final VaadinTest annotation = m.getAnnotation(VaadinTest.class);

             if (annotation != null) {
               final boolean preLoad = annotation.preLoad();
               storeMethodPlain().apply(context).put(PAGE_OBJECT_PRELOAD , preLoad);
               final String target = annotation.navigateAsString();
               if (target != null && ! target.isEmpty()) {
                 storeMethodPlain().apply(context).put(PAGE_OBJECT_NAVIGATION_TARGET , target);
               }
             }
//             if (annotation != null) {
//
//               if(annotation.preLoad()){
//                 final Class navigate = annotation.navigate();
//                 if( ! navigate.equals(Object.class)){
//                   final Annotation route = navigate.getAnnotation(Route.class);
//                   if(route != null){
//                     throw new RuntimeException("Usage of Route not implemented until now");
//                   } else logger().info("no Annotation Route found .. " + navigate);
//                 } else logger().info("navigate class is of type object");
//
//
//                 final String target = annotation.navigateAsString();
//                 final PageObject pageObject = pageObject().apply(context);
//                 if (target != null && ! target.isEmpty()) {
//                   pageObject.loadPage(target);
//                 } else {
//                   pageObject.loadPage();
//                 }
//               } else logger().info("preLoad is deactivated for this test " + m.getName());
//             }
           });
  }
}
