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

package org.apache.tiles.extras.complete;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.compat.definition.digester.CompatibilityDigesterDefinitionsReader;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PrefixedPatternDefinitionResolver;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.renderer.DefinitionRenderer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.mustache.MustacheRenderer;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.DispatchRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.StringRenderer;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.request.velocity.render.VelocityRenderer;
import org.apache.velocity.tools.view.VelocityView;
import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link CompleteAutoloadTilesContainerFactory}.
 *
 * @version $Rev$ $Date$
 */
public class CompleteAutoloadTilesContainerFactoryTest {

    /**
     * The object to test.
     */
    private CompleteAutoloadTilesContainerFactory factory;

    /**
     * Initializes the object.
     */
    @Before
    public void setUp() {
        factory = new CompleteAutoloadTilesContainerFactory();
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesContainerFactory#createDecoratedContainer(TilesContainer, ApplicationContext)
     * .
     */
    @Test
    public void testCreateDecoratedContainer() {
        ApplicationContext applicationContext = createMock(ServletApplicationContext.class);
        TilesContainer wrapped = createMock(TilesContainer.class);

        replay(applicationContext, wrapped);
        assertSame(wrapped,
                ((CachingTilesContainer) factory.createDecoratedContainer(wrapped, applicationContext))
                        .getWrappedContainer());
        verify(applicationContext, wrapped);
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesContainerFactory
     * #registerAttributeRenderers(BasicRendererFactory, ApplicationContext,
     * TilesContainer, evaluator.AttributeEvaluatorFactory)}
     * .
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testRegisterAttributeRenderers() {
        BasicRendererFactory rendererFactory = createMock(BasicRendererFactory.class);
        ServletApplicationContext applicationContext = createMock(ServletApplicationContext.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> applicationScope = (Map<String, Object>) createMock(Map.class);
        TilesContainer container = createMock(TilesContainer.class);
        AttributeEvaluatorFactory attributeEvaluatorFactory = createMock(AttributeEvaluatorFactory.class);
        ServletContext servletContext = createMock(ServletContext.class);

        rendererFactory.registerRenderer(eq("string"), isA(StringRenderer.class));
        rendererFactory.registerRenderer(eq("template"), isA(ChainedDelegateRenderer.class));
        rendererFactory.registerRenderer(eq("definition"), isA(DefinitionRenderer.class));
        Capture<FreemarkerRenderer> freemarker = new Capture<FreemarkerRenderer>();
        rendererFactory.registerRenderer(eq("freemarker"), and(isA(FreemarkerRenderer.class), capture(freemarker)));
        expect(rendererFactory.getRenderer("freemarker")).andAnswer(new CapturedAnswer<Renderer>(freemarker));
        Capture<VelocityRenderer> velocity = new Capture<VelocityRenderer>();
        rendererFactory.registerRenderer(eq("velocity"), and(isA(VelocityRenderer.class), capture(velocity)));
        expect(rendererFactory.getRenderer("velocity")).andAnswer(new CapturedAnswer<Renderer>(velocity));
        Capture<MustacheRenderer> mustache = new Capture<MustacheRenderer>();
        rendererFactory.registerRenderer(eq("mustache"), and(isA(MustacheRenderer.class), capture(mustache)));
        expect(rendererFactory.getRenderer("mustache")).andAnswer(new CapturedAnswer<Renderer>(mustache));
        Capture<DispatchRenderer> dispatch = new Capture<DispatchRenderer>();
        rendererFactory.registerRenderer(eq("dispatch"), and(isA(DispatchRenderer.class), capture(dispatch)));
        expect(rendererFactory.getRenderer("dispatch")).andAnswer(new CapturedAnswer<Renderer>(dispatch));

        expect(applicationContext.getContext()).andReturn(servletContext).anyTimes();
        expect(applicationContext.getApplicationScope()).andReturn(applicationScope).anyTimes();
        expect(applicationScope.get(FreemarkerRenderer.class.getName())).andReturn(null);
        expect(servletContext.getInitParameter(VelocityView.PROPERTIES_KEY)).andReturn(null);
        expect(servletContext.getInitParameter(VelocityView.TOOLS_KEY)).andReturn(null);
        expect(servletContext.getAttribute(VelocityView.TOOLS_KEY)).andReturn(null);
        expect(servletContext.getResourceAsStream("/WEB-INF/velocity.properties")).andReturn(
                getClass().getResourceAsStream("/velocity.properties"));
        expect(servletContext.getResourceAsStream("/WEB-INF/VM_global_library.vm")).andReturn(
                getClass().getResourceAsStream("/VM_global_library.vm"));
        expect(servletContext.getResourceAsStream("/WEB-INF/tools.xml")).andReturn(
                getClass().getResourceAsStream("/tools.xml"));
        expect(servletContext.getResourceAsStream(VelocityView.DEPRECATED_USER_TOOLS_PATH)).andReturn(null);
        servletContext.log((String) anyObject());
        expectLastCall().anyTimes();
        expect(applicationScope.put(eq(FreemarkerRenderer.class.getName()), isA(FreemarkerRenderer.class))).andReturn(null);
        expect(servletContext.getRealPath("/")).andReturn(null);

        replay(rendererFactory, applicationContext, container, attributeEvaluatorFactory, servletContext, applicationScope);
        factory.registerAttributeRenderers(rendererFactory, applicationContext, container, attributeEvaluatorFactory);
        verify(rendererFactory, applicationContext, container, attributeEvaluatorFactory, servletContext, applicationScope);
    }

    /**
     * Tests
     * {@link CompleteAutoloadTilesContainerFactory#createDefaultAttributeRenderer(BasicRendererFactory,
     * ApplicationContext, TilesContainer, AttributeEvaluatorFactory)}.
     */
    @Test
    public void testCreateTemplateAttributeRenderer() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        TilesContainer container = createMock(TilesContainer.class);
        AttributeEvaluatorFactory attributeEvaluatorFactory = createMock(AttributeEvaluatorFactory.class);
        BasicRendererFactory rendererFactory = createMock(BasicRendererFactory.class);
        Renderer velocityRenderer = createMock(Renderer.class);
        Renderer freemarkerRenderer = createMock(Renderer.class);
        Renderer mustacheRenderer = createMock(Renderer.class);
        Renderer dispatchRenderer = createMock(Renderer.class);

        expect(rendererFactory.getRenderer("velocity")).andReturn(velocityRenderer);
        expect(rendererFactory.getRenderer("freemarker")).andReturn(freemarkerRenderer);
        expect(rendererFactory.getRenderer("mustache")).andReturn(mustacheRenderer);
        expect(rendererFactory.getRenderer("dispatch")).andReturn(dispatchRenderer);

        replay(container, attributeEvaluatorFactory, rendererFactory, applicationContext);
        Renderer renderer = factory.createTemplateAttributeRenderer(rendererFactory, applicationContext, container,
                attributeEvaluatorFactory);
        assertTrue("The default renderer class is not correct", renderer instanceof ChainedDelegateRenderer);
        verify(container, attributeEvaluatorFactory, rendererFactory, applicationContext);
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesContainerFactory
     * #createAttributeEvaluatorFactory(ApplicationContext, locale.LocaleResolver)}
     * .
     */
    @Test
    public void testCreateAttributeEvaluatorFactory() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        LocaleResolver resolver = createMock(LocaleResolver.class);
        ServletContext servletContext = createMock(ServletContext.class);
        JspFactory jspFactory = createMock(JspFactory.class);
        JspApplicationContext jspApplicationContext = createMock(JspApplicationContext.class);
        ExpressionFactory expressionFactory = createMock(ExpressionFactory.class);

        expect(applicationContext.getContext()).andReturn(servletContext);
        expect(jspFactory.getJspApplicationContext(servletContext)).andReturn(jspApplicationContext);
        expect(jspApplicationContext.getExpressionFactory()).andReturn(expressionFactory);

        replay(applicationContext, resolver, servletContext, jspFactory, jspApplicationContext, expressionFactory);
        JspFactory.setDefaultFactory(jspFactory);
        AttributeEvaluatorFactory attributeEvaluatorFactory = factory.createAttributeEvaluatorFactory(
                applicationContext, resolver);
        assertTrue(attributeEvaluatorFactory instanceof BasicAttributeEvaluatorFactory);
        assertNotNull(attributeEvaluatorFactory.getAttributeEvaluator("EL"));
        assertNotNull(attributeEvaluatorFactory.getAttributeEvaluator("MVEL"));
        assertNotNull(attributeEvaluatorFactory.getAttributeEvaluator("OGNL"));
        verify(applicationContext, resolver, servletContext, jspFactory, jspApplicationContext, expressionFactory);
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesContainerFactory#createPatternDefinitionResolver(Class)}
     * .
     */
    @Test
    public void testCreatePatternDefinitionResolver() {
        PatternDefinitionResolver<Integer> resolver = factory.createPatternDefinitionResolver(Integer.class);
        assertTrue(resolver instanceof PrefixedPatternDefinitionResolver);
        Definition definitionWildcard = new Definition("WILDCARD:blah*", (Attribute) null, null);
        Definition definitionRegexp = new Definition("REGEXP:what(.*)", (Attribute) null, null);
        Map<String, Definition> definitionMap = new HashMap<String, Definition>();
        definitionMap.put("WILDCARD:blah*", definitionWildcard);
        definitionMap.put("REGEXP:what(.*)", definitionRegexp);
        resolver.storeDefinitionPatterns(definitionMap, 1);
        Definition result = resolver.resolveDefinition("blahX", 1);
        assertEquals("blahX", result.getName());
        result = resolver.resolveDefinition("whatX", 1);
        assertEquals("whatX", result.getName());
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesContainerFactory#getSources(ApplicationContext)}
     * .
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testGetSources() throws IOException {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        ApplicationResource resource1 = createMock(ApplicationResource.class);
        expect(resource1.getLocale()).andReturn(Locale.ROOT);
        ApplicationResource resource2 = createMock(ApplicationResource.class);
        expect(resource2.getLocale()).andReturn(Locale.ITALY);
        ApplicationResource resource3 = createMock(ApplicationResource.class);
        expect(resource3.getLocale()).andReturn(Locale.ROOT);

        Collection<ApplicationResource> resourceSet1 = new HashSet<ApplicationResource>();
        resourceSet1.add(resource1);
        resourceSet1.add(resource2);

        Collection<ApplicationResource> resourceSet2 = new HashSet<ApplicationResource>();
        resourceSet2.add(resource3);

        expect(applicationContext.getResources("/WEB-INF/**/tiles*.xml")).andReturn(resourceSet1);
        expect(applicationContext.getResources("classpath*:META-INF/**/tiles*.xml")).andReturn(resourceSet2);

        replay(applicationContext, resource1, resource2, resource3);
        List<ApplicationResource> urls = factory.getSources(applicationContext);
        assertEquals(2, urls.size());
        assertTrue(urls.contains(resource1));
        assertTrue(urls.contains(resource3));
        verify(applicationContext, resource1, resource2, resource3);
    }

    /**
     * Regression test for TILES-484 issue.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testTILES484first() throws IOException {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        ApplicationResource resource = createMock(ApplicationResource.class);
        expect(resource.getLocale()).andReturn(Locale.ROOT);

        Collection<ApplicationResource> resourceSet = new HashSet<ApplicationResource>();
        resourceSet.add(resource);

        expect(applicationContext.getResources("/WEB-INF/**/tiles*.xml")).andReturn(null);
        expect(applicationContext.getResources("classpath*:META-INF/**/tiles*.xml")).andReturn(resourceSet);

        replay(applicationContext, resource);
        List<ApplicationResource> resources = factory.getSources(applicationContext);
        assertEquals(1, resources.size());
        assertTrue(resources.contains(resource));
        verify(applicationContext, resource);
    }

    /**
     * Regression test for TILES-484 issue.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    public void testTILES484second() throws IOException {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        ApplicationResource resource1 = createMock(ApplicationResource.class);
        expect(resource1.getLocale()).andReturn(Locale.ROOT);
        ApplicationResource resource2 = createMock(ApplicationResource.class);
        expect(resource2.getLocale()).andReturn(Locale.ITALY);

        Collection<ApplicationResource> resourceSet = new HashSet<ApplicationResource>();
        resourceSet.add(resource1);
        resourceSet.add(resource2);

        expect(applicationContext.getResources("/WEB-INF/**/tiles*.xml")).andReturn(resourceSet);
        expect(applicationContext.getResources("classpath*:META-INF/**/tiles*.xml")).andReturn(null);

        replay(applicationContext, resource1, resource2);
        List<ApplicationResource> resources = factory.getSources(applicationContext);
        assertEquals(1, resources.size());
        assertTrue(resources.contains(resource1));
        verify(applicationContext, resource1, resource2);
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesContainerFactory
     * #createDefinitionsReader(ApplicationContext)}
     * .
     */
    @Test
    public void testCreateDefinitionsReader() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        replay(applicationContext);
        assertTrue(factory.createDefinitionsReader(applicationContext) instanceof CompatibilityDigesterDefinitionsReader);
        verify(applicationContext);
    }

    private static class CapturedAnswer<T> implements IAnswer<T> {
        private Capture<? extends T> capture;

        public CapturedAnswer(Capture<? extends T> capture) {
            this.capture = capture;
        }

        @Override
        public T answer() throws Throwable {
            return capture.getValue();
        }

    }
}
