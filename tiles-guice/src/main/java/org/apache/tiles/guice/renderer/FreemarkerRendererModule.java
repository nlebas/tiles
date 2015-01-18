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

package org.apache.tiles.guice.renderer;

import org.apache.tiles.freemarker.template.TilesFMModelRepository;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.freemarker.RequestTemplateLoader;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.startup.TilesInitializerException;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class FreemarkerRendererModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, Renderer> mapbinder = MapBinder.newMapBinder(binder(), String.class, Renderer.class);
        mapbinder.addBinding("freemarker").toProvider(FreemarkerRendererProvider.class);
    }

    public static class FreemarkerRendererProvider implements Provider<FreemarkerRenderer> {
        private ApplicationContext applicationContext;
        private Configuration config;

        @Inject
        public FreemarkerRendererProvider(ApplicationContext applicationContext, Configuration config) {
            this.applicationContext = applicationContext;
            this.config = config;
        }

        public FreemarkerRenderer get() {
            FreemarkerRenderer freemarkerRenderer = (FreemarkerRenderer) applicationContext.getApplicationScope().get(
                    FreemarkerRenderer.class.getName());
            if (freemarkerRenderer == null) {
                freemarkerRenderer = new FreemarkerRenderer();
                freemarkerRenderer.setConfiguration(config);
                applicationContext.getApplicationScope()
                        .put(FreemarkerRenderer.class.getName(), freemarkerRenderer);
            }
            return freemarkerRenderer;
        }
    }

    @Provides
    @Singleton
    public Configuration createConfiguration(ApplicationContext applicationContext) {
        try {
            Configuration config = new Configuration();
            config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            config.setObjectWrapper(ObjectWrapper.DEFAULT_WRAPPER);
            config.setTemplateLoader(new RequestTemplateLoader(applicationContext));
            config.setDefaultEncoding("ISO-8859-1");
            config.setTemplateUpdateDelay(0);
            config.setSetting("number_format", "0.##########");
            config.setSharedVariable("tiles", new TilesFMModelRepository());
            return config;
        } catch (TemplateException e) {
            throw new TilesInitializerException("Cannot initialize Freemarker renderer", e);
        }
    }
}
