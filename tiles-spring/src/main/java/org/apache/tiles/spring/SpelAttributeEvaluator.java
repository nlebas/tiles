package org.apache.tiles.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tiles.evaluator.AbstractAttributeEvaluator;
import org.apache.tiles.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelAttributeEvaluator extends AbstractAttributeEvaluator implements ApplicationContextAware {
    @SuppressWarnings("unused")
    private static Logger             logger = LoggerFactory.getLogger(SpelAttributeEvaluator.class);

    private BeanResolver              beanResolver;
    private StandardEvaluationContext context;

    @Override
    public Object evaluate(String expression, Request request) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression, new ParserContext() {

            @Override
            public boolean isTemplate() {
                return true;
            }

            @Override
            public String getExpressionSuffix() {
                return "}";
            }

            @Override
            public String getExpressionPrefix() {
                return "${";
            }
        });
        Map<String, Object> scopes = new HashMap<String, Object>();
        List<String> availableScopes = request.getAvailableScopes();
        // insert scopes from the more general to the more specific
        for (int i = availableScopes.size() - 1; i >= 0; --i) {
            Map<String, Object> data = request.getContext(availableScopes.get(i));
            scopes.putAll(data);
            scopes.put(availableScopes.get(i) + "Scope", data);
        }
        return exp.getValue(context, scopes, String.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        beanResolver = new BeanFactoryResolver(applicationContext);
        context = new StandardEvaluationContext();
        context.setBeanResolver(beanResolver);
        context.addPropertyAccessor(new MapAccessor());
    }
}
