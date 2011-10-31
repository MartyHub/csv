package org.sweet.csv.editor.factory;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.sweet.csv.editor.BigDecimalEditor;
import org.sweet.csv.editor.BooleanEditor;
import org.sweet.csv.editor.DateEditor;
import org.sweet.csv.editor.EnumEditor;
import org.sweet.date.BusinessDate;
import org.sweet.date.BusinessTime;
import org.sweet.date.TechnicalDate;
import org.sweet.date.duration.Duration;
import org.sweet.date.editor.BusinessDateEditor;
import org.sweet.date.editor.BusinessTimeEditor;
import org.sweet.date.editor.DurationEditor;
import org.sweet.date.editor.TechnicalDateEditor;

@Component
public class EditorFactory {

    private final Map<Class<?>, PropertyEditor> propertyEditors = new HashMap<Class<?>, PropertyEditor>();

    public EditorFactory() {
        this(null);
    }

    public EditorFactory(String defaultDatePattern) {
        BooleanEditor boolEditor = new BooleanEditor();

        this.propertyEditors.put(Boolean.class, boolEditor);
        this.propertyEditors.put(Boolean.TYPE, boolEditor);

        this.propertyEditors.put(BigDecimal.class, new BigDecimalEditor());

        this.propertyEditors.put(Date.class, new DateEditor(defaultDatePattern));
        this.propertyEditors.put(BusinessDate.class, new BusinessDateEditor());
        this.propertyEditors.put(BusinessTime.class, new BusinessTimeEditor());
        this.propertyEditors.put(TechnicalDate.class, new TechnicalDateEditor());
        this.propertyEditors.put(Duration.class, new DurationEditor());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PropertyEditor lookup(Class<?> type) {
        PropertyEditor editor = propertyEditors.get(type);

        if (editor == null) {
            editor = PropertyEditorManager.findEditor(type);

            if (editor != null) {
                propertyEditors.put(type, editor);
            } else if (type.isEnum()) {
                editor = new EnumEditor(type);

                propertyEditors.put(type, editor);
            }
        }

        if (editor == null) {
            throw new IllegalStateException("Could not find editor for class <" + type + ">");
        }

        return editor;
    }
}
