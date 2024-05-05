package org.ecommerce.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ecommerce.common.utils.EnumValidator;

import jakarta.validation.Constraint;

@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
	String message() default "Invalid Enum Value";
	Class<?>[] groups() default {};
	Class<?>[] payload() default {};
	Class<? extends Enum<?>> enumClass();
}
