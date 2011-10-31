package org.sweet.csv.editor;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.sweet.csv.editor.BigDecimalEditor;

public class BigDecimalEditorTest {

    private BigDecimalEditor editor;

    @Before
    public void setUp() {
        editor = new BigDecimalEditor();
    }

    @Test
    public void testEmptyText() {
        editor.setAsText("");
        Object value = editor.getValue();
        Assert.assertNull(value);
    }

    @Test
    public void testNullText() {
        editor.setAsText(null);
        Object value = editor.getValue();
        Assert.assertNull(value);
    }

    @Test
    public void testIntegerText() {
        editor.setAsText("1");
        Object value = editor.getValue();
        Assert.assertEquals(BigDecimal.valueOf(1), value);
    }

    @Test
    public void testScientificNotationText() {
        editor.setAsText("1E-1");
        Object value = editor.getValue();
        Assert.assertEquals(BigDecimal.valueOf(0.1), value);
    }

    @Test
    public void testNullValue() {
        editor.setValue(null);
        Object value = editor.getValue();
        Assert.assertNull(value);
        String text = editor.getAsText();
        Assert.assertNull(text);
    }

    @Test
    public void testSetValue() {
        editor.setValue(BigDecimal.valueOf(3.14));
        Object value = editor.getValue();
        Assert.assertEquals(BigDecimal.valueOf(3.14), value);
        Assert.assertEquals("3.14", editor.getAsText());
    }
}
