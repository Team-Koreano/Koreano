package org.ecommerce.orderapi.global.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DevProfileCondition implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String[] activeProfiles = context.getEnvironment().getActiveProfiles();
		for (String profile : activeProfiles) {
			if ("dev".equals(profile)) {
				return true;
			}
		}
		return false;
	}
}
