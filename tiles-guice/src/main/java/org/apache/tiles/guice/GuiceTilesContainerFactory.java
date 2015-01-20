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

import java.util.Arrays;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.request.ApplicationContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class GuiceTilesContainerFactory extends AbstractTilesContainerFactory {

    private static final class ContextModule extends AbstractModule {
        private ApplicationContext applicationContext;
        
        private ContextModule(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }
        
        @Override
        protected void configure() {
            bind(ApplicationContext.class).toInstance(applicationContext);
        }
    }

    private Module[] modules;
    
    public GuiceTilesContainerFactory(Module... modules) {
        this.modules = modules;
    }

    /** {@inheritDoc} */
    @Override
    public TilesContainer createContainer(ApplicationContext applicationContext) {
        Module[] mods = Arrays.copyOf(modules, modules.length + 1);
        mods[modules.length] = new ContextModule(applicationContext);
        Injector injector = Guice.createInjector(mods);
        TilesContainer container = injector.getInstance(TilesContainer.class);
        TilesAccess.setContainer(applicationContext, container);
        return container;
    }
}
