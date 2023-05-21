/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;

/**
 * An adapter to make a generic Spring ApplicationContext act like a
 * WebApplicationContext.
 *
 * For internal use only.
 */
public class MockWebApplicationContext implements WebApplicationContext {

    private final ApplicationContext appCtx;
    private final ServletContext servletContext;

    public MockWebApplicationContext(ApplicationContext appCtx,
            ServletContext servletContext) {
        this.appCtx = appCtx;
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getId() {
        return appCtx.getId();
    }

    @Override
    public String getApplicationName() {
        return appCtx.getApplicationName();
    }

    @Override
    public String getDisplayName() {
        return appCtx.getDisplayName();
    }

    @Override
    public long getStartupDate() {
        return appCtx.getStartupDate();
    }

    @Override
    public ApplicationContext getParent() {
        return appCtx.getParent();
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory()
            throws IllegalStateException {
        return appCtx.getAutowireCapableBeanFactory();
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return appCtx.getParentBeanFactory();
    }

    @Override
    public boolean containsLocalBean(String name) {
        return appCtx.containsLocalBean(name);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return appCtx.containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return appCtx.getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return appCtx.getBeanDefinitionNames();
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType,
            boolean allowEagerInit) {
        return appCtx.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType,
            boolean allowEagerInit) {
        return appCtx.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return appCtx.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type,
            boolean includeNonSingletons, boolean allowEagerInit) {
        return appCtx.getBeanNamesForType(type, includeNonSingletons,
                allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return appCtx.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type,
            boolean includeNonSingletons, boolean allowEagerInit) {
        return appCtx.getBeanNamesForType(type, includeNonSingletons,
                allowEagerInit);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type)
            throws BeansException {
        return appCtx.getBeansOfType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type,
            boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {
        return appCtx.getBeansOfType(type, includeNonSingletons,
                allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForAnnotation(
            Class<? extends Annotation> annotationType) {
        return appCtx.getBeanNamesForAnnotation(annotationType);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(
            Class<? extends Annotation> annotationType) throws BeansException {
        return appCtx.getBeansWithAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName,
            Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return appCtx.findAnnotationOnBean(beanName, annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName,
            Class<A> annotationType, boolean allowFactoryBeanInit)
            throws NoSuchBeanDefinitionException {
        return appCtx.findAnnotationOnBean(beanName, annotationType,
                allowFactoryBeanInit);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return appCtx.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType)
            throws BeansException {
        return appCtx.getBean(name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return appCtx.getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return appCtx.getBean(requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args)
            throws BeansException {
        return appCtx.getBean(requiredType, args);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return appCtx.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return appCtx.getBeanProvider(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return appCtx.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name)
            throws NoSuchBeanDefinitionException {
        return appCtx.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name)
            throws NoSuchBeanDefinitionException {
        return appCtx.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch)
            throws NoSuchBeanDefinitionException {
        return appCtx.isTypeMatch(name, typeToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch)
            throws NoSuchBeanDefinitionException {
        return appCtx.isTypeMatch(name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return appCtx.getType(name);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit)
            throws NoSuchBeanDefinitionException {
        return appCtx.getType(name, allowFactoryBeanInit);
    }

    @Override
    public String[] getAliases(String name) {
        return appCtx.getAliases(name);
    }

    @Override
    public void publishEvent(Object event) {
        appCtx.publishEvent(event);
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage,
            Locale locale) {
        return appCtx.getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale)
            throws NoSuchMessageException {
        return appCtx.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale)
            throws NoSuchMessageException {
        return appCtx.getMessage(resolvable, locale);
    }

    @Override
    public Environment getEnvironment() {
        return appCtx.getEnvironment();
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return appCtx.getResources(locationPattern);
    }

    @Override
    public Resource getResource(String location) {
        return appCtx.getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return appCtx.getClassLoader();
    }

    @Override
    public <A extends Annotation> Set<A> findAllAnnotationsOnBean(
            String beanName, Class<A> annotationType,
            boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return appCtx.findAllAnnotationsOnBean(beanName, annotationType,
                allowFactoryBeanInit);
    }
}
