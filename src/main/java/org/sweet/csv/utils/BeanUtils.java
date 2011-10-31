package org.sweet.csv.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.sweet.csv.mapper.MapperException;


public class BeanUtils {

    private static final Logger LOGGER = Logger.getLogger(BeanUtils.class);

    public static PropertyDescriptor getSimplePropertyDescriptor(Class<?> currentType, String property) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(currentType);

        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equals(property)) {
                return descriptor;
            }
        }

        throw new MapperException("Unknown simple property <" + property + "> for <" + currentType + ">");
    }

    public static Object getPropertyValue(Object bean, String property) {
        try {
            return PropertyUtils.getProperty(bean, property);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void setPropertyValue(Object bean, String property, Object value) {
        try {
            PropertyUtils.setProperty(bean, property, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Failed to set property <" + property + "> to <" + bean + ">", e);
        }
    }
}
