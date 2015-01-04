package org.apache.tiles.test.guice;

import org.apache.tiles.el.ExpressionFactoryFactory;
import org.apache.tiles.guice.el.CompleteEvaluatorModule;
import org.apache.tiles.request.ApplicationContext;

public class JasperCompleteEvaluatorModule extends CompleteEvaluatorModule {
    @Override
    public ExpressionFactoryFactory createExpressionFactoryFactory(ApplicationContext applicationContext) {
        return new JasperExpressionFactoryFactory();
    }
}