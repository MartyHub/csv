package org.sweet.csv.mapper;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.sweet.csv.editor.factory.EditorFactory;
import org.sweet.csv.utils.BeanUtils;


public class MappedBean<T> implements Iterable<MappedProperty<T>>, InitializingBean {

    private final List<MappedProperty<T>> properties = new LinkedList<MappedProperty<T>>();

    private final Class<T> type;

    public MappedBean(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public void addProperty(EditorFactory editorFactory, String property, String header) {
        String[] properties = StringUtils.split(property, '.');
        Class<?> currentType = type;
        PropertyDescriptor currentDescriptor = null;

        for (String simpleProperty : properties) {
            currentDescriptor = BeanUtils.getSimplePropertyDescriptor(currentType, simpleProperty);
            currentType = currentDescriptor.getPropertyType();
        }

        if (currentType == null) {
            throw new MapperException("Unknown property <" + property + "> for <" + type + ">");
        }

        PropertyEditor editor = editorFactory.lookup(currentType);

        addProperty(new MappedProperty<T>(property, header, editor, currentType));
    }

    public void addProperties(EditorFactory editorFactory, String property, MappedBean<?> mappedBean) {
        for (MappedProperty<?> mappedProperty : mappedBean) {
            addProperty(editorFactory, property + "." + mappedProperty.getProperty(), mappedProperty.getHeader());
        }
    }

    public Iterator<MappedProperty<T>> iterator() {
        return properties.iterator();
    }

    public String[] getHeaders() {
        final int size = properties.size();
        String[] result = new String[size];

        for (int i = 0; i < size; ++i) {
            result[i] = properties.get(i).getHeader();
        }

        return result;
    }

    public final String getExpectedHeaders() {
        return StringUtils.join(getHeaders(), ",");
    }

    public T newInstance() {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new MapperException("Failed to instantiate <" + type + ">", e);
        } catch (IllegalAccessException e) {
            throw new MapperException("Failed to access <" + type + ">", e);
        }
    }

    public MappedBean<T> withHeaders(String[] headers) {
        MappedBean<T> result = new MappedBean<T>(type);
        Map<String, MappedProperty<T>> map = asMap();
        Set<String> knownHeaders = new TreeSet<String>();

        for (String header : headers) {
            final MappedProperty<T> mappedProperty = map.remove(header);

            if (mappedProperty == null) {
                throw new MapperException("Unknown header <" + header + "> for <" + type + ">");
            } else if (knownHeaders.contains(mappedProperty.getHeader())) {
                throw new MapperException("Duplicate header <" + header + ">");
            }

            result.addProperty(mappedProperty);
            knownHeaders.add(mappedProperty.getHeader());
        }

        for (MappedProperty<T> property : map.values()) {
            if (property.isMandatory()) {
                throw new MapperException("Header <" + property.getHeader() + "> is mandatory (" + getExpectedHeaders() + ")");
            }
        }

        return result;
    }

    public final void afterPropertiesSet() throws Exception {
        initProperties();
    }

    protected void initProperties() {
    }

    private Map<String, MappedProperty<T>> asMap() {
        Map<String, MappedProperty<T>> result = new TreeMap<String, MappedProperty<T>>();

        for (MappedProperty<T> mappedProperty : properties) {
            result.put(mappedProperty.getHeader(), mappedProperty);
        }

        return result;
    }

    private void addProperty(MappedProperty<T> mappedProperty) {
        properties.add(mappedProperty);
    }
}
