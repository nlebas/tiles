/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.spring;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.tiles.evaluator.AttributeEvaluator;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class TilesRegistrationPostProcessor implements BeanPostProcessor {
    @SuppressWarnings("unused")
    private static Logger                   logger     = LoggerFactory.getLogger(TilesRegistrationPostProcessor.class);

    private String                          defaultRendererName;
    private ChainedDelegateRenderer         defaultRenderer;
    private Renderer                        definitionRenderer;
    private ChainedDelegateRenderer         templateRenderer;
    private Renderer                        stringRenderer;
    private BasicRendererFactory            rendererFactory;
    private BasicAttributeEvaluatorFactory  evaluatorFactory;
    private Map<String, Renderer>           renderers  = new LinkedHashMap<String, Renderer>();
    private Map<String, AttributeEvaluator> evaluators = new LinkedHashMap<String, AttributeEvaluator>();

    public void setDefaultRendererName(String defaultRendererName) {
        this.defaultRendererName = defaultRendererName;
    }

    private void registerRenderer(String name, Renderer renderer) {
        if(name != null && renderer != null) {
            renderers.put(name, renderer);
        }
        if (defaultRenderer != null && definitionRenderer != null && templateRenderer != null && stringRenderer != null
                && rendererFactory != null) {
            defaultRenderer.addAttributeRenderer(definitionRenderer);
            defaultRenderer.addAttributeRenderer(templateRenderer);
            defaultRenderer.addAttributeRenderer(stringRenderer);
            rendererFactory.registerRenderer("definition", definitionRenderer);
            rendererFactory.registerRenderer("template", templateRenderer);
            rendererFactory.registerRenderer("string", stringRenderer);
            defaultRenderer = null;
            definitionRenderer = null;
            stringRenderer = null;
        }
        if (templateRenderer != null && rendererFactory != null) {
            for (Entry<String, Renderer> e : renderers.entrySet()) {
                templateRenderer.addAttributeRenderer(e.getValue());
                rendererFactory.registerRenderer(e.getKey(), e.getValue());
            }
            renderers.clear();
        }
    }

    private void registerAttributeEvaluator(String name, AttributeEvaluator evaluator) {
        if(name != null && evaluator != null) {
            evaluators.put(name, evaluator);
        }
        if (evaluatorFactory != null) {
            for (Entry<String, AttributeEvaluator> e : evaluators.entrySet()) {
                evaluatorFactory.registerAttributeEvaluator(e.getKey(), e.getValue());
            }
            evaluators.clear();
            evaluatorFactory.registerAttributeEvaluator(name, evaluator);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof Renderer) {
            if (defaultRendererName.equals(beanName)) {
                defaultRenderer = (ChainedDelegateRenderer) bean;
                registerRenderer(null, null);
            } else if ("definition".equals(beanName)) {
                definitionRenderer = (Renderer) bean;
                registerRenderer(null, null);
            } else if ("template".equals(beanName)) {
                templateRenderer = (ChainedDelegateRenderer) bean;
                registerRenderer(null, null);
            } else if ("string".equals(beanName)) {
                stringRenderer = (Renderer) bean;
                registerRenderer(null, null);
            } else {
                registerRenderer(beanName, (Renderer) bean);
            }
        }
        if (bean instanceof AttributeEvaluator) {
            registerAttributeEvaluator(beanName, (AttributeEvaluator) bean);
        }
        if (bean instanceof BasicAttributeEvaluatorFactory) {
            evaluatorFactory = (BasicAttributeEvaluatorFactory) bean;
            registerAttributeEvaluator(null, null);
        }
        if (bean instanceof BasicRendererFactory) {
            rendererFactory = (BasicRendererFactory) bean;
            registerRenderer(null, null);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
