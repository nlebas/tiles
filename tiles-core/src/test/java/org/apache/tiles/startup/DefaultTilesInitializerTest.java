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

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.List;

import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.factory.BasicPreparerFactory;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.apache.tiles.request.render.DispatchRenderer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link DefaultTilesInitializer}.
 *
 * @version $Rev$ $Date$
 */
public class DefaultTilesInitializerTest {

    /**
     * The factory to test.
     */
    private DefaultTilesInitializer initializer;

    /**
     * The context object.
     */
    private ApplicationContext applicationContext;

    /**
     * The resource to load.
     */
    private ApplicationResource resource;

    /** {@inheritDoc} */
    @Before
    public void setUp() throws Exception {
        applicationContext = createMock(ApplicationContext.class);
        resource = new URLApplicationResource("/WEB-INF/tiles.xml", getClass().getResource(
                "/org/apache/tiles/config/tiles-defs.xml"));
        expect(applicationContext.getResource("/WEB-INF/tiles.xml")).andStubReturn(resource);
        replay(applicationContext);
        initializer = new DefaultTilesInitializer();
    }

    /**
     * Tests {@link DefaultTilesInitializer#createDefinitionsFactory(
     * ApplicationContext, LocaleResolver)}.
     */
    @Test
    public void testCreateDefinitionsFactory() {
        DefinitionsFactory defsFactory = initializer.createDefinitionsFactory(applicationContext);
        assertTrue("The class of the definitions factory is not correct",
                defsFactory instanceof UnresolvingLocaleDefinitionsFactory);
    }

    /**
     * Tests {@link DefaultTilesInitializer#createDefinitionsReader(
     * ApplicationContext)}.
     */
    @Test
    public void testCreateDefinitionsReader() {
        DefinitionsReader reader = initializer.createDefinitionsReader(applicationContext);
        assertTrue("The class of the reader is not correct", reader instanceof DigesterDefinitionsReader);
    }

    /**
     * Tests
     * {@link DefaultTilesInitializer#getSources(ApplicationContext)}.
     */
    @Test
    public void testGetSources() {
        List<ApplicationResource> resources = initializer.getSources(applicationContext);
        assertEquals("The urls list is not one-sized", 1, resources.size());
        assertEquals("The URL is not correct", resource, resources.get(0));
    }

    /**
     * Tests
     * {@link DefaultTilesInitializer#createAttributeEvaluatorFactory(
     * ApplicationContext, LocaleResolver)}.
     */
    @Test
    public void testCreateAttributeEvaluatorFactory() {
        AttributeEvaluatorFactory attributeEvaluatorFactory = initializer.createAttributeEvaluatorFactory(
                applicationContext);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator((String) null) instanceof DirectAttributeEvaluator);
    }

    /**
     * Tests
     * {@link DefaultTilesInitializer#createPreparerFactory(ApplicationContext)}.
     */
    @Test
    public void testCreatePreparerFactory() {
        PreparerFactory preparerFactory = initializer.createPreparerFactory(applicationContext);
        assertTrue("The class of the preparer factory is not correct", preparerFactory instanceof BasicPreparerFactory);
    }
    
    /**
     * Tests
     * {@link DefaultTilesInitializer#getRendererNames(ApplicationContext)} and 
     * {@link DefaultTilesInitializer#getRenderer(ApplicationContext,String)}.
     */
    @Test
    public void testRenderers() {
        List<String> names = initializer.getRendererNames(applicationContext);
        assertEquals("The renderers list is not one-sized", 1, names.size());
        assertTrue("The DispatchRenderer is not correct", initializer.getRenderer(applicationContext, names.get(0)) instanceof DispatchRenderer);
    }
    
    /**
     * Tests for unknown renderers.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testUnknownRenderer() {
        initializer.getRenderer(applicationContext, "unknown");
    }
}
