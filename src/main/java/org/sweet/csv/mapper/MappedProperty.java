package org.sweet.csv.mapper;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.sweet.csv.utils.BeanUtils;


public class MappedProperty<T> {

    private final String property;

    private final String header;

    private final PropertyEditor editor;

    private final Class<?> type;

    private final boolean mandatory;

    MappedProperty(String property, String header, PropertyEditor editor, Class<?> type) {
        this(property, header, editor, type, true);
    }

    MappedProperty(String property, String header, PropertyEditor editor, Class<?> type, final boolean mandatory) {
        this.property = property;
        this.header = header;
        this.editor = editor;
        this.type = type;
        this.mandatory = mandatory;
    }

    public String getHeader() {
        return header;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getProperty() {
        return property;
    }

    public Object getValue(T bean) {
        try {
            return PropertyUtils.getProperty(bean, property);
        } catch (IllegalAccessException e) {
            throw new MapperException(e);
        } catch (InvocationTargetException e) {
            return null;
        } catch (NoSuchMethodException e) {
            throw new MapperException(e);
        }
    }

    public void setValue(T bean, Object value) {
        String[] properties = StringUtils.split(property, '.');
        final int length = properties.length;
        Object current = bean;

        try {
            for (int i = 0; i < length - 1; ++i) {
                final String currentProperty = properties[i];
                Object o = PropertyUtils.getProperty(current, currentProperty);

                if (o == null) {
                    final PropertyDescriptor descriptor = BeanUtils.getSimplePropertyDescriptor(current.getClass(), currentProperty);

                    o = descriptor.getPropertyType().newInstance();

                    PropertyUtils.setProperty(current, currentProperty, o);
                }

                current = o;
            }

            PropertyUtils.setProperty(current, properties[length - 1], value);
        } catch (IllegalAccessException e) {
            throw new MapperException(e);
        } catch (InvocationTargetException e) {
            throw new MapperException("Failed to set value <" + value + "> on <" + bean + "> for <" + property + ">", e);
        } catch (NoSuchMethodException e) {
            throw new MapperException(e);
        } catch (InstantiationException e) {
            throw new MapperException(e);
        }
    }

    public void setValueFromString(T bean, String str) {
        editor.setAsText(str);

        Object value = editor.getValue();

        setValue(bean, value);
    }

    public String asString(T bean) {
        Object value = getValue(bean);

        if (value == null) {
            return StringUtils.EMPTY;
        } else {
            editor.setValue(value);

            return editor.getAsText();
        }
    }

    public Class<?> getType() {
        return type;
    }
}
