package org.ecommerce.paymentapi.aop;

import java.util.LinkedList;
import java.util.List;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {
	private final static String getExpression = ".get()";
	private CustomSpringELParser() {
	}

	public static Object[] getDynamicValue(String[] parameterNames, Object[] args,
		String key) {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		List<Object> returnKeys = new LinkedList<>();

		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		try{
			int listSize = parser.parseExpression(
					key.substring(0, key.indexOf(getExpression)))
				.getValue(context, List.class)
				.size();

			for(int i = 0; i < listSize; i++){
				String newKey = key.replace(getExpression, ".get("+ i +")");
				returnKeys.add(parser.parseExpression(newKey).getValue(context, Object.class));
			}

		}catch(Exception e){
			returnKeys.add(parser.parseExpression(key).getValue(context, Object.class));
		}finally{
			return returnKeys.toArray();
		}
	}
}
