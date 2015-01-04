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

package org.apache.tiles.guice.container;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.renderer.DefinitionRenderer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.StringRenderer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class CachingTilesContainerModule extends AbstractModule {

    private static final String STRING_RENDERER_NAME = "string";
    private static final String TEMPLATE_RENDERER_NAME = "template";
    private static final String DEFINITION_RENDERER_NAME = "definition";

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public TilesContainer createTilesContainer(ApplicationContext applicationContext,
            AttributeEvaluatorFactory attributeEvaluatorFactory, DefinitionsFactory definitionsFactory,
            PreparerFactory preparerFactory, Map<String, Renderer> renderers) {
        BasicTilesContainer tilesContainer = new BasicTilesContainer();
        tilesContainer.setApplicationContext(applicationContext);
        tilesContainer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        tilesContainer.setDefinitionsFactory(definitionsFactory);
        tilesContainer.setPreparerFactory(preparerFactory);
        BasicRendererFactory rendererFactory = new BasicRendererFactory();
        tilesContainer.setRendererFactory(rendererFactory);
        TilesContainer result = new CachingTilesContainer(tilesContainer);
        populateRendererFactory(result, rendererFactory, renderers);
        return result;
    }

    protected void populateRendererFactory(TilesContainer container, BasicRendererFactory factory,
            Map<String, Renderer> renderers) {
        ChainedDelegateRenderer defaultRenderer = new ChainedDelegateRenderer();
        factory.setDefaultRenderer(defaultRenderer);

        // register the actual renderers first, so they do not override the
        // standard names
        for (Entry<String, Renderer> entry : renderers.entrySet()) {
            factory.registerRenderer(entry.getKey(), entry.getValue());
        }

        // first renderer in standard chain: definition
        DefinitionRenderer definitionRenderer = new DefinitionRenderer(container);
        factory.registerRenderer(DEFINITION_RENDERER_NAME, definitionRenderer);
        defaultRenderer.addAttributeRenderer(definitionRenderer);

        // second renderer in standard chain: template
        ChainedDelegateRenderer templateRenderer = new ChainedDelegateRenderer();
        factory.registerRenderer(TEMPLATE_RENDERER_NAME, templateRenderer);
        defaultRenderer.addAttributeRenderer(templateRenderer);

        // insert the actual renderers into the template chain
        for (Renderer renderer : renderers.values()) {
            templateRenderer.addAttributeRenderer(renderer);
        }

        // third renderer in standard chain: string
        StringRenderer stringRenderer = new StringRenderer();
        factory.registerRenderer(STRING_RENDERER_NAME, stringRenderer);
        defaultRenderer.addAttributeRenderer(stringRenderer);
    }
}
