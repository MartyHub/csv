package org.sweet.csv.editor;

import java.math.BigDecimal;

public class BigDecimalEditor extends SafePropertyEditor<BigDecimal> {

	@Override
	protected String doGetAsText(BigDecimal value) {
		// The format returned by the toString() method of a BigDecimal
		// is well defined in the javadocs, thus we use it here
		return value.toString();
	}

	@Override
	protected BigDecimal fromText(String text) {
		return new BigDecimal(text);
	}
}
