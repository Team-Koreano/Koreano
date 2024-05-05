package org.ecommerce.common.utils;

import org.ecommerce.common.aop.ValidEnum;

import jakarta.validation.ConstraintValidator;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

	private ValidEnum validEnum;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		this.validEnum = constraintAnnotation;
	}

	@Override
	public boolean isValid(Enum value, jakarta.validation.ConstraintValidatorContext context) {
		Object[] enumValues = this.validEnum.enumClass().getEnumConstants();
		if (enumValues == null) return false;

		for (Object enumValue : enumValues) {
			if (enumValue.equals(value)) return true;
		}

		return false;
	}
}
