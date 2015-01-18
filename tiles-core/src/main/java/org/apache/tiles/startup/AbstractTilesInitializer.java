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

package org.apache.tiles.startup;

import java.util.List;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.renderer.DefinitionRenderer;
import org.apache.tiles.renderer.RelativeDelegateRenderer;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.StringRenderer;

/**
 * Default Tiles initialization delegate implementation under a servlet
 * environment. It uses init parameters to create the
 * {@link ApplicationContext} and the {@link TilesContainer}.
 *
 * @version $Rev$ $Date$
 * @since 2.2.0
 */
public abstract class AbstractTilesInitializer implements TilesInitializer {

    /**
     * Init parameter to define the key under which the container will be
     * stored.
     *
     * @since 2.1.2
     */
    public static final String CONTAINER_KEY_INIT_PARAMETER = "org.apache.tiles.startup.AbstractTilesInitializer.CONTAINER_KEY";

    /**
     * The string renderer name.
     */
    public static final String STRING_RENDERER_NAME = "string";

    /**
     * The template renderer name.
     */
    public static final String TEMPLATE_RENDERER_NAME = "template";

    /**
     * The definition renderer name.
     */
    public static final String DEFINITION_RENDERER_NAME = "definition";

    /**
     * The initialized application context.
     */
    private ApplicationContext applicationContext;

    /** {@inheritDoc} */
    public void initialize(ApplicationContext applicationContext) {
        this.applicationContext = createTilesApplicationContext(applicationContext);
        ApplicationAccess.register(this.applicationContext);
        BasicTilesContainer tilesContainer = new BasicTilesContainer();
        TilesContainer mutableContainer = new CachingTilesContainer(tilesContainer);
        registerContainer(mutableContainer);
        populateContainer(tilesContainer);
    }

    /** {@inheritDoc} */
    public void destroy() {
        TilesAccess.setContainer(applicationContext, null, getContainerKey(applicationContext));
    }

    /**
     * Creates the Tiles application context, to be used across all the
     * Tiles-based application. If you override this class, please override this
     * method or
     * {@link #createAndInitializeTilesApplicationContextFactory(ApplicationContext)}
     * .<br>
     * This implementation returns the preliminary context passed as a parameter
     *
     * @param preliminaryContext The preliminary application context to use.
     * @return The Tiles application context.
     * @since 2.2.0
     */
    protected ApplicationContext createTilesApplicationContext(ApplicationContext preliminaryContext) {
        return preliminaryContext;
    }

    /**
     * Returns the container key under which the container will be stored.
     * This implementation returns <code>null</code> so that the container will
     * be the default one.
     *
     * @param applicationContext The Tiles application context to use.
     * @return The container key.
     * @since 2.2.0
     */
    protected String getContainerKey(ApplicationContext applicationContext) {
        return null;
    }

    /**
     * Creates a Tiles container. If you override this class, please override
     * this method or {@link #createContainerFactory(ApplicationContext)}.
     *
     * @param context The servlet context to use.
     * @return The created container.
     * @since 2.2.0
     */
    protected void populateContainer(BasicTilesContainer tilesContainer) {
        tilesContainer.setApplicationContext(applicationContext);
        BasicRendererFactory rendererFactory = new BasicRendererFactory();
        tilesContainer.setRendererFactory(rendererFactory);
        populateRendererFactory(rendererFactory);
        tilesContainer.setAttributeEvaluatorFactory(createAttributeEvaluatorFactory(applicationContext));
        tilesContainer.setDefinitionsFactory(createDefinitionsFactory(applicationContext));
        tilesContainer.setPreparerFactory(createPreparerFactory(applicationContext));
    }

    protected void registerContainer(TilesContainer tilesContainer) {
        TilesAccess.setContainer(this.applicationContext, tilesContainer, getContainerKey(this.applicationContext));
    }

    protected abstract AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext applicationContext);

    protected abstract DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext);

    protected abstract PreparerFactory createPreparerFactory(ApplicationContext applicationContext);

    protected abstract List<String> getRendererNames(ApplicationContext applicationContext);

    protected abstract Renderer getRenderer(ApplicationContext applicationContext, String name);

    protected List<String> getTemplateNames(ApplicationContext applicationContext) {
        return getRendererNames(applicationContext);
    }
    
    protected void populateRendererFactory(BasicRendererFactory factory) {
        ChainedDelegateRenderer defaultRenderer = new ChainedDelegateRenderer();
        factory.setDefaultRenderer(defaultRenderer);

        // register the actual renderers first, so they do not override the
        // standard names
        for (String name : getRendererNames(applicationContext)) {
            factory.registerRenderer(name, getRenderer(applicationContext, name));
        }

        // first renderer in standard chain: definition
        TilesContainer container = TilesAccess.getContainer(applicationContext, getContainerKey(applicationContext));
        DefinitionRenderer definitionRenderer = new DefinitionRenderer(container);
        factory.registerRenderer(DEFINITION_RENDERER_NAME, definitionRenderer);
        defaultRenderer.addAttributeRenderer(definitionRenderer);

        // second renderer in standard chain: template
        ChainedDelegateRenderer templateRenderer = new ChainedDelegateRenderer();
        RelativeDelegateRenderer relativeTemplateRenderer = new RelativeDelegateRenderer(templateRenderer);
        factory.registerRenderer(TEMPLATE_RENDERER_NAME, relativeTemplateRenderer);
        defaultRenderer.addAttributeRenderer(templateRenderer);

        // insert the actual renderers into the template chain
        for (String name : getTemplateNames(applicationContext)) {
            templateRenderer.addAttributeRenderer(getRenderer(applicationContext, name));
        }

        // third renderer in standard chain: string
        StringRenderer stringRenderer = new StringRenderer();
        factory.registerRenderer(STRING_RENDERER_NAME, stringRenderer);
        defaultRenderer.addAttributeRenderer(stringRenderer);
    }

}
