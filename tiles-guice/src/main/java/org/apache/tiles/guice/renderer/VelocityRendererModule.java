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

import java.util.Properties;

import org.apache.tiles.factory.TilesContainerFactoryException;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.velocity.render.VelocityToolsRenderer;
import org.apache.velocity.app.VelocityEngine;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class VelocityRendererModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, Renderer> mapbinder = MapBinder.newMapBinder(binder(), String.class, Renderer.class);
        mapbinder.addBinding("velocity").toProvider(VelocityRendererProvider.class);
    }
    public static class VelocityRendererProvider implements Provider<VelocityToolsRenderer> {
        private VelocityEngine velocityEngine;
        private ApplicationContext applicationContext;
        @Inject
        public VelocityRendererProvider(ApplicationContext applicationContext, VelocityEngine velocityEngine) {
            this.applicationContext = applicationContext;
            this.velocityEngine = velocityEngine;
        }
        
        public VelocityToolsRenderer get() {
            VelocityToolsRenderer renderer = new VelocityToolsRenderer(applicationContext, velocityEngine);
            applicationContext.getApplicationScope().put(VelocityToolsRenderer.class.getName(), renderer);
            return renderer;
        }
    }

    @Provides
    @Singleton
    public VelocityEngine createVelocityEngine(ApplicationContext applicationContext) {
        try {
            Properties velocityProperties = new Properties();
            velocityProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/velocity.properties"));
            return new VelocityEngine(velocityProperties);
        } catch (Exception e) {
            throw new TilesContainerFactoryException("Cannot initialize Velocity renderer", e);
        }
    }
}
