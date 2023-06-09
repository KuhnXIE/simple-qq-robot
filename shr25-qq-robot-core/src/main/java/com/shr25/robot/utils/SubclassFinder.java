package com.shr25.robot.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于查找所有接口实现类
 */
public class SubclassFinder {
    public static Set<Class<?>> findSubclassesOf(Class<?> superClass, String basePackage) {
        Set<Class<?>> subclasses = new HashSet<>();
        
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(superClass));
        
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                subclasses.add(clazz);
            } catch (ClassNotFoundException e) {
                // Handle the exception as needed
            }
        }
        
        return subclasses;
    }
}
