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

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tiles.Definition;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;
import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link AbstractTilesInitializer}.
 *
 * @version $Rev$ $Date$
 */
public class AbstractTilesInitializerTest {

    /**
     * The ApplicationContext.
     */
    private ApplicationContext applicationContext;
    
    /**
     * The Application scope.
     */
    private Map<String, Object> applicationScope;
    
    /**
     * The AttributeEvaluatorFactory.
     */
    private AttributeEvaluatorFactory attributeEvaluatorFactory;
    
    /**
     * The DefinitionsFactory.
     */
    private DefinitionsFactory definitionsFactory;
    
    /**
     * The PreparerFactory.
     */
    private PreparerFactory preparerFactory;
    
    /**
     * A renderer.
     */
    private Renderer renderer1;
    
    /**
     * Another renderer.
     */
    private Renderer renderer2;
    
    
    /**
     * The object to test.
     */
    private AbstractTilesInitializer initializer;

    /**
     * The tiles container.
     */
    private IAnswer<TilesContainer> tilesContainerAnswer;
    
    /**
     * Sets up the test.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        applicationScope = createMock(Map.class);
        expect(applicationContext.getApplicationScope()).andStubReturn(applicationScope);
        expect(applicationScope.put(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE, applicationContext)).andReturn(null);
        final Capture<TilesContainer> tilesContainerCapture = new Capture<TilesContainer>();        
        tilesContainerAnswer = new IAnswer<TilesContainer>() {

            @Override
            public TilesContainer answer() throws Throwable {
                return tilesContainerCapture.getValue();
            }
        };
        expect(applicationScope.put(eq(TilesAccess.CONTAINER_ATTRIBUTE), capture(tilesContainerCapture))).andReturn(null);
        expect(applicationScope.get(TilesAccess.CONTAINER_ATTRIBUTE)).andStubAnswer(tilesContainerAnswer);
        attributeEvaluatorFactory = createMock(AttributeEvaluatorFactory.class);
        definitionsFactory = createMock(DefinitionsFactory.class);
        preparerFactory = createMock(PreparerFactory.class);
        initializer = new AbstractTilesInitializer() {

            @Override
            protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext applicationContext) {
                return attributeEvaluatorFactory;
            }

            @Override
            protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext) {
                return definitionsFactory;
            }

            @Override
            protected PreparerFactory createPreparerFactory(ApplicationContext applicationContext) {
                return preparerFactory;
            }

            @Override
            protected List<String> getRendererNames(ApplicationContext applicationContext) {
                return Arrays.asList("test1", "test2");
            }

            @Override
            protected Renderer getRenderer(ApplicationContext applicationContext, String name) {
                if("test1".equals(name)) return renderer1;
                else if("test2".equals(name)) return renderer2;
                else throw new IllegalArgumentException("Unknown renderer");
            }
            
        };
    }

    /**
     * Test method for {@link AbstractTilesInitializer#initialize(ApplicationContext)}.
     */
    @Test
    public void testInitialize() {
        replay(applicationContext, applicationScope, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
        initializer.initialize(applicationContext);
        verify(applicationContext, applicationScope, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
    }

    /**
     * Test method for {@link AbstractTilesInitializer#initialize(ApplicationContext)}.
     */
    @Test
    public void testContainerGetDefinition() throws Throwable {
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andStubReturn(new HashMap<String, Object>());
        Definition definition = createMock(Definition.class);
        expect(definitionsFactory.getDefinition("testDefinition", request)).andReturn(definition);
        replay(applicationContext, applicationScope, request, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
        initializer.initialize(applicationContext);
        assertSame(definition, tilesContainerAnswer.answer().getDefinition("testDefinition", request));
        verify(applicationContext, applicationScope, request, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
    }

    /**
     * Test method for {@link AbstractTilesInitializer#initialize(ApplicationContext)}.
     */
    @Test
    public void testContainerPrepare() throws Throwable {
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andStubReturn(new HashMap<String, Object>());
        ViewPreparer preparer = createMock(ViewPreparer.class);
        expect(preparerFactory.getPreparer("testPreparer", request)).andReturn(preparer);
        replay(applicationContext, applicationScope, request, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
        initializer.initialize(applicationContext);
        tilesContainerAnswer.answer().prepare("testPreparer", request);
        verify(applicationContext, applicationScope, request, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
    }

    /**
     * Test method for {@link AbstractTilesInitializer#initialize(ApplicationContext)}.
     */
    @Test
    public void testDestroy() {
        expect(applicationScope.remove(TilesAccess.CONTAINER_ATTRIBUTE)).andAnswer(tilesContainerAnswer);
        replay(applicationContext, applicationScope, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
        initializer.initialize(applicationContext);
        initializer.destroy();
        verify(applicationContext, applicationScope, attributeEvaluatorFactory, definitionsFactory, preparerFactory);
    }
}
