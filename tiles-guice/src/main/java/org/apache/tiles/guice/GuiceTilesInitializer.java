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

package org.apache.tiles.guice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.RendererFactory;
import org.apache.tiles.startup.AbstractTilesInitializer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.util.Types;


/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class GuiceTilesInitializer extends AbstractTilesInitializer {
    private ApplicationContext applicationContext;
    private TilesContainer tilesContainer;
    private RendererFactory rendererFactory;
    private Module[] modules;
    private Injector injector;
    private String containerKey;
    
    private final class ContextModule extends AbstractModule {
        
        @Override
        protected void configure() {
            bind(ApplicationContext.class).toProvider(new Provider<ApplicationContext>() {
                @Override
                public ApplicationContext get() {
                    return applicationContext;
                }
            });
            bind(TilesContainer.class).toProvider(new Provider<TilesContainer>() {
                @Override
                public TilesContainer get() {
                    return tilesContainer;
                }
            });
            bind(RendererFactory.class).toProvider(new Provider<RendererFactory>() {
                @Override
                public RendererFactory get() {
                    return rendererFactory;
                }
            });
        }
    }

    public GuiceTilesInitializer(Module... modules) {
        Module[] mods = Arrays.copyOf(modules, modules.length + 1);
        mods[modules.length] = new ContextModule();
        this.modules = mods;
    }

    @Override
    protected ApplicationContext createTilesApplicationContext(ApplicationContext preliminaryContext) {
        applicationContext = super.createTilesApplicationContext(preliminaryContext);
        return applicationContext;
    }

    @Override
    protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext applicationContext) {
        return injector.getInstance(AttributeEvaluatorFactory.class);
    }

    @Override
    protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext) {
        return injector.getInstance(DefinitionsFactory.class);
    }

    @Override
    protected PreparerFactory createPreparerFactory(ApplicationContext applicationContext) {
        return injector.getInstance(PreparerFactory.class);
    }

    @Override
    protected List<String> getRendererNames(ApplicationContext applicationContext) {
        @SuppressWarnings("unchecked")
        Map<String, Renderer> renderers = (Map<String, Renderer>) injector.getInstance(Key.get(Types.mapOf(String.class, Renderer.class)));
        ArrayList<String> names = new ArrayList<String>(renderers.size());
        names.addAll(renderers.keySet());
        return names;
    }

    @Override
    protected Renderer getRenderer(ApplicationContext applicationContext, String name) {
        @SuppressWarnings("unchecked")
        Map<String, Renderer> renderers = (Map<String, Renderer>) injector.getInstance(Key.get(Types.mapOf(String.class, Renderer.class)));
        return renderers.get(name);
    }

    public void setContainerKey(String containerKey) {
        this.containerKey = containerKey;
    }

    @Override
    protected String getContainerKey(ApplicationContext applicationContext) {
        return containerKey;
    }

    @Override
    protected void populateRendererFactory(BasicRendererFactory factory) {
        this.rendererFactory = factory;
        injector = Guice.createInjector(modules);
        super.populateRendererFactory(factory);
    }

    @Override
    protected void registerContainer(TilesContainer tilesContainer) {
        this.tilesContainer = tilesContainer;
        super.registerContainer(tilesContainer);
    }

}
