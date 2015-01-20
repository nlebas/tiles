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

package org.apache.tiles.test.factory;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer;
import org.apache.tiles.request.AbstractApplicationContext;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.ClasspathResourceLocator;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.request.spring.SpringResourceLocator;
import org.apache.tiles.test.renderer.ReverseStringRenderer;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

/**
 * Test Tiles initializer for Tiles initialization of the default container.
 *
 * @version $Rev$ $Date$
 */
public class TestTilesInitializer extends CompleteAutoloadTilesInitializer {

    /** {@inheritDoc} */
    @Override
    protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
        List<ApplicationResource> urls = new ArrayList<ApplicationResource>();
        urls.addAll(applicationContext
                .getResources("/WEB-INF/**/tiles-defs*.xml"));
        urls.add(applicationContext.getResource(
                "classpath:/org/apache/tiles/classpath-defs.xml"));
        urls.add(applicationContext.getResource(
                "classpath:/org/apache/tiles/freemarker-classpath-defs.xml"));
        urls.add(applicationContext.getResource(
            "classpath:/org/apache/tiles/velocity-classpath-defs.xml"));
        return urls;
    }
    
    /** {@inheritDoc} */
    @Override
    protected ApplicationContext createTilesApplicationContext(ApplicationContext preliminaryContext) {
        ServletContext servletContext = (ServletContext) preliminaryContext.getContext();
        AbstractApplicationContext result = new ServletApplicationContext(servletContext);
        result.register(new SpringResourceLocator(new ServletContextResourcePatternResolver(servletContext)));
        result.register(new ClasspathResourceLocator());
        return result;
    }

    @Override
    protected List<String> getRendererNames(ApplicationContext applicationContext) {
        ArrayList<String> renderers = new ArrayList<String>();
        renderers.addAll(super.getRendererNames(applicationContext));
        renderers.add("reversed");
        return renderers;
    }

    @Override
    protected List<String> getTemplateNames(ApplicationContext applicationContext) {
        return super.getRendererNames(applicationContext);
    }

    @Override
    protected Renderer getRenderer(ApplicationContext applicationContext, String name) {
        if("reversed".equals(name)) {
            return new ReverseStringRenderer();
        }
        return super.getRenderer(applicationContext, name);
    }
}
