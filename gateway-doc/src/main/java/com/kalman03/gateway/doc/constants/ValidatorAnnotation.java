package com.kalman03.gateway.doc.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public enum ValidatorAnnotation {

    NOT_EMPTY("NotEmpty"),

    NOT_BLANK("NotBlank"),

    NOT_NULL("NotNull"),

    NULL("Null"),

    ASSERT_TRUE("AssertTrue"),

    ASSERT_FALSE("AssertFalse"),

    MIN("Min"),

    MAX("Max"),

    DECIMAL_MIN("DecimalMin"),

    DECIMAL_MAX("DecimalMax"),

    SIZE("Size"),

    DIGITS("Digits"),

    PAST("Past"),

    FUTURE("Future"),

    PATTERN("Pattern"),

    EMAIL("Email"),

    LENGTH("Length"),

    RANGE("Range"),

    VALIDATED("Validated");

    private String value;

    ValidatorAnnotation(String value) {
        this.value = value;
    }

    public static List<String> getAllValidatorAnnotations() {
        List<String> annotations = new ArrayList<>();
        for (ValidatorAnnotation annotation : ValidatorAnnotation.values()) {
            annotations.add(annotation.value);
        }
        return annotations;
    }

	public String getValue() {
		return value;
	}
}