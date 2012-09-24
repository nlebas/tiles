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

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
import org.apache.tiles.compat.definition.digester.CompatibilityDigesterDefinitionsReader;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PrefixedPatternDefinitionResolver;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.mustache.MustacheRenderer;
import org.apache.tiles.request.render.DispatchRenderer;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.request.servlet.wildcard.WildcardServletApplicationContext;
import org.apache.tiles.request.velocity.render.VelocityRenderer;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @author antonio
 *
 * @version $Rev$ $Date$
 */
public class CompleteAutoloadTilesInitializerTest {

    /**
     * The object to test.
     */
    private CompleteAutoloadTilesInitializer initializer;

    /**
     * Sets up the object to test.
     */
    @Before
    public void setUp() {
        initializer = new CompleteAutoloadTilesInitializer();
    }

    /**
     * Test method for {@link CompleteAutoloadTilesInitializer#createTilesApplicationContext(ApplicationContext)}.
     */
    @Test
    public void testCreateTilesApplicationContext() {
        ApplicationContext preliminaryContext = createMock(ApplicationContext.class);
        ServletContext servletContext = createMock(ServletContext.class);

        expect(preliminaryContext.getContext()).andReturn(servletContext);

        replay(preliminaryContext, servletContext);
        assertTrue(initializer
                .createTilesApplicationContext(preliminaryContext) instanceof WildcardServletApplicationContext);
        verify(preliminaryContext, servletContext);
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesInitializer
     * #createAttributeEvaluatorFactory(ApplicationContext, locale.LocaleResolver)}
     * .
     */
    @Test
    public void testCreateAttributeEvaluatorFactory() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ServletContext servletContext = createMock(ServletContext.class);
        JspFactory jspFactory = createMock(JspFactory.class);
        JspApplicationContext jspApplicationContext = createMock(JspApplicationContext.class);
        ExpressionFactory expressionFactory = createMock(ExpressionFactory.class);

        expect(applicationContext.getContext()).andReturn(servletContext);
        expect(jspFactory.getJspApplicationContext(servletContext)).andReturn(jspApplicationContext);
        expect(jspApplicationContext.getExpressionFactory()).andReturn(expressionFactory);

        replay(applicationContext, servletContext, jspFactory, jspApplicationContext, expressionFactory);
        JspFactory.setDefaultFactory(jspFactory);
        AttributeEvaluatorFactory attributeEvaluatorFactory = initializer.createAttributeEvaluatorFactory(
                applicationContext);
        assertTrue(attributeEvaluatorFactory instanceof BasicAttributeEvaluatorFactory);
        assertNotNull(attributeEvaluatorFactory.getAttributeEvaluator("EL"));
        assertNotNull(attributeEvaluatorFactory.getAttributeEvaluator("MVEL"));
        assertNotNull(attributeEvaluatorFactory.getAttributeEvaluator("OGNL"));
        verify(applicationContext, servletContext, jspFactory, jspApplicationContext, expressionFactory);
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesInitializer#createPatternDefinitionResolver(Class)}
     * .
     */
    @Test
    public void testCreatePatternDefinitionResolver() {
        PatternDefinitionResolver<Integer> resolver = initializer.createPatternDefinitionResolver(Integer.class);
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
     * {@link CompleteAutoloadTilesInitializer#getSources(ApplicationContext)}
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
        List<ApplicationResource> urls = initializer.getSources(applicationContext);
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
        List<ApplicationResource> resources = initializer.getSources(applicationContext);
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
        List<ApplicationResource> resources = initializer.getSources(applicationContext);
        assertEquals(1, resources.size());
        assertTrue(resources.contains(resource1));
        verify(applicationContext, resource1, resource2);
    }

    /**
     * Test method for
     * {@link CompleteAutoloadTilesInitializer
     * #createDefinitionsReader(ApplicationContext)}
     * .
     */
    @Test
    public void testCreateDefinitionsReader() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        replay(applicationContext);
        assertTrue(initializer.createDefinitionsReader(applicationContext) instanceof CompatibilityDigesterDefinitionsReader);
        verify(applicationContext);
    }

    @Test
    public void testGetRendererNames() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        replay(applicationContext);
        assertEquals(initializer.getRendererNames(applicationContext), Arrays.asList("velocity", "freemarker", "mustache", "dispatch"));
        verify(applicationContext);
    }
    @Test
    public void testGetRenderer() {
        ServletApplicationContext applicationContext = createMock(ServletApplicationContext.class);

        ServletContext servletContext = createMock(ServletContext.class);
        expect(applicationContext.getContext()).andStubReturn(servletContext);
        expect(servletContext.getInitParameter(EasyMock.<String>anyObject())).andStubReturn(null);
        expect(servletContext.getResourceAsStream(EasyMock.<String>anyObject())).andStubReturn(null);
        expect(servletContext.getAttribute(EasyMock.<String>anyObject())).andStubReturn(null);
        servletContext.log(EasyMock.<String>anyObject());
        expectLastCall().anyTimes();

        replay(applicationContext, servletContext);
        assertTrue(initializer.getRenderer(applicationContext, "velocity") instanceof VelocityRenderer);
        assertTrue(initializer.getRenderer(applicationContext, "freemarker") instanceof FreemarkerRenderer);
        assertTrue(initializer.getRenderer(applicationContext, "mustache") instanceof MustacheRenderer);
        assertTrue(initializer.getRenderer(applicationContext, "dispatch") instanceof DispatchRenderer);
        verify(applicationContext, servletContext);
    }
}
