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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.servlet.ServletContext;

import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.apache.tiles.compat.definition.digester.CompatibilityDigesterDefinitionsReader;
import org.apache.tiles.context.TilesRequestContextHolder;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.pattern.DefinitionPatternMatcherFactory;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PrefixedPatternDefinitionResolver;
import org.apache.tiles.definition.pattern.regexp.RegexpDefinitionPatternMatcherFactory;
import org.apache.tiles.definition.pattern.wildcard.WildcardDefinitionPatternMatcherFactory;
import org.apache.tiles.el.ELAttributeEvaluator;
import org.apache.tiles.el.JspExpressionFactoryFactory;
import org.apache.tiles.el.ScopeELResolver;
import org.apache.tiles.el.TilesContextBeanELResolver;
import org.apache.tiles.el.TilesContextELResolver;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.freemarker.template.TilesFMModelRepository;
import org.apache.tiles.mvel.MVELAttributeEvaluator;
import org.apache.tiles.mvel.ScopeVariableResolverFactory;
import org.apache.tiles.mvel.TilesContextBeanVariableResolverFactory;
import org.apache.tiles.mvel.TilesContextVariableResolverFactory;
import org.apache.tiles.ognl.AnyScopePropertyAccessor;
import org.apache.tiles.ognl.DelegatePropertyAccessor;
import org.apache.tiles.ognl.NestedObjectDelegatePropertyAccessor;
import org.apache.tiles.ognl.OGNLAttributeEvaluator;
import org.apache.tiles.ognl.PropertyAccessorDelegateFactory;
import org.apache.tiles.ognl.ScopePropertyAccessor;
import org.apache.tiles.ognl.TilesApplicationContextNestedObjectExtractor;
import org.apache.tiles.ognl.TilesContextPropertyAccessorDelegateFactory;
import org.apache.tiles.request.AbstractApplicationContext;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.RequestTemplateLoader;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.freemarker.servlet.SharedVariableLoaderFreemarkerServlet;
import org.apache.tiles.request.mustache.MustacheRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.request.spring.SpringResourceLocator;
import org.apache.tiles.request.velocity.render.VelocityRendererBuilder;
import org.apache.tiles.startup.DefaultTilesInitializer;
import org.apache.tiles.startup.TilesInitializerException;
import org.mvel2.integration.VariableResolverFactory;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * This initializer uses {@link WildcardServletApplicationContext} to
 * retrieve resources using Ant-style patterns and creates a
 * {@link CompleteAutoloadTilesContainerFactory} to load all new features of
 * Tiles at once.
 *
 * @version $Rev$ $Date$
 * @since 2.2.0
 */
public class CompleteAutoloadTilesInitializer extends DefaultTilesInitializer {

    /**
     * The freemarker renderer name.
     */
    private static final String FREEMARKER_RENDERER_NAME = "freemarker";

    /**
     * The velocity renderer name.
     */
    private static final String VELOCITY_RENDERER_NAME = "velocity";

    /**
     * The mustache renderer name.
     */
    private static final String MUSTACHE_RENDERER_NAME = "mustache";

    /** {@inheritDoc} */
    @Override
    protected <T> PatternDefinitionResolver<T> createPatternDefinitionResolver(Class<T> customizationKeyClass) {
        DefinitionPatternMatcherFactory wildcardFactory = new WildcardDefinitionPatternMatcherFactory();
        DefinitionPatternMatcherFactory regexpFactory = new RegexpDefinitionPatternMatcherFactory();
        PrefixedPatternDefinitionResolver<T> resolver = new PrefixedPatternDefinitionResolver<T>();
        resolver.registerDefinitionPatternMatcherFactory("WILDCARD", wildcardFactory);
        resolver.registerDefinitionPatternMatcherFactory("REGEXP", regexpFactory);
        return resolver;
    }

    @Override
    protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext applicationContext) {
        BasicAttributeEvaluatorFactory attributeEvaluatorFactory = new BasicAttributeEvaluatorFactory(
                createELEvaluator(applicationContext));
        attributeEvaluatorFactory.registerAttributeEvaluator("MVEL", createMVELEvaluator());
        attributeEvaluatorFactory.registerAttributeEvaluator("OGNL", createOGNLEvaluator());

        return attributeEvaluatorFactory;
    }

    /**
     * Creates the EL evaluator.
     *
     * @param applicationContext The Tiles application context.
     * @return The EL evaluator.
     */
    private ELAttributeEvaluator createELEvaluator(ApplicationContext applicationContext) {
        ELAttributeEvaluator evaluator = new ELAttributeEvaluator();
        JspExpressionFactoryFactory efFactory = new JspExpressionFactoryFactory();
        efFactory.setApplicationContext(applicationContext);
        evaluator.setExpressionFactory(efFactory.getExpressionFactory());
        ELResolver elResolver = new CompositeELResolver() {
            {
                BeanELResolver beanElResolver = new BeanELResolver(false);
                add(new ScopeELResolver());
                add(new TilesContextELResolver(beanElResolver));
                add(new TilesContextBeanELResolver());
                add(new ArrayELResolver(false));
                add(new ListELResolver(false));
                add(new MapELResolver(false));
                add(new ResourceBundleELResolver());
                add(beanElResolver);
            }
        };
        evaluator.setResolver(elResolver);
        return evaluator;
    }

    /**
     * Creates the MVEL evaluator.
     *
     * @return The MVEL evaluator.
     */
    private MVELAttributeEvaluator createMVELEvaluator() {
        TilesRequestContextHolder requestHolder = new TilesRequestContextHolder();
        VariableResolverFactory variableResolverFactory = new ScopeVariableResolverFactory(requestHolder);
        variableResolverFactory.setNextFactory(new TilesContextVariableResolverFactory(requestHolder));
        variableResolverFactory.setNextFactory(new TilesContextBeanVariableResolverFactory(requestHolder));
        MVELAttributeEvaluator mvelEvaluator = new MVELAttributeEvaluator(requestHolder, variableResolverFactory);
        return mvelEvaluator;
    }

    /**
     * Creates the OGNL evaluator.
     *
     * @return The OGNL evaluator.
     */
    private OGNLAttributeEvaluator createOGNLEvaluator() {
        try {
            PropertyAccessor objectPropertyAccessor = OgnlRuntime.getPropertyAccessor(Object.class);
            PropertyAccessor applicationContextPropertyAccessor = new NestedObjectDelegatePropertyAccessor<Request>(
                    new TilesApplicationContextNestedObjectExtractor(), objectPropertyAccessor);
            PropertyAccessor anyScopePropertyAccessor = new AnyScopePropertyAccessor();
            PropertyAccessor scopePropertyAccessor = new ScopePropertyAccessor();
            PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
                    objectPropertyAccessor, applicationContextPropertyAccessor, anyScopePropertyAccessor,
                    scopePropertyAccessor);
            PropertyAccessor tilesRequestAccessor = new DelegatePropertyAccessor<Request>(factory);
            OgnlRuntime.setPropertyAccessor(Request.class, tilesRequestAccessor);
            return new OGNLAttributeEvaluator();
        } catch (OgnlException e) {
            throw new TilesInitializerException("Cannot initialize OGNL evaluator", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
        Collection<ApplicationResource> webINFSet = applicationContext.getResources("/WEB-INF/**/tiles*.xml");
        Collection<ApplicationResource> metaINFSet = applicationContext
                .getResources("classpath*:META-INF/**/tiles*.xml");

        List<ApplicationResource> filteredResources = new ArrayList<ApplicationResource>();
        if (webINFSet != null) {
            for (ApplicationResource resource : webINFSet) {
                if (Locale.ROOT.equals(resource.getLocale())) {
                    filteredResources.add(resource);
                }
            }
        }
        if (metaINFSet != null) {
            for (ApplicationResource resource : metaINFSet) {
                if (Locale.ROOT.equals(resource.getLocale())) {
                    filteredResources.add(resource);
                }
            }
        }
        return filteredResources;
    }

    @Override
    protected List<String> getRendererNames(ApplicationContext applicationContext) {
        List<String> result = new ArrayList<String>();
        result.add(VELOCITY_RENDERER_NAME);
        result.add(FREEMARKER_RENDERER_NAME);
        result.add(MUSTACHE_RENDERER_NAME);
        result.addAll(super.getRendererNames(applicationContext));
        return result;
    }

    @Override
    protected Renderer getRenderer(ApplicationContext applicationContext, String name) {
        if (FREEMARKER_RENDERER_NAME.equals(name)) {
            try {
                Configuration config = new Configuration();
                config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                config.setObjectWrapper(ObjectWrapper.DEFAULT_WRAPPER);
                config.setTemplateLoader(new RequestTemplateLoader(applicationContext));
                config.setDefaultEncoding("ISO-8859-1");
                config.setTemplateUpdateDelay(0);
                config.setSetting("number_format", "0.##########");
                config.setSharedVariable("tiles", new TilesFMModelRepository());
                FreemarkerRenderer freemarkerRenderer = new FreemarkerRenderer();
                freemarkerRenderer.setConfiguration(config);
                return freemarkerRenderer;
            } catch (TemplateException e) {
                throw new TilesInitializerException("Cannot initialize Freemarker renderer", e);
            }
        } else if (VELOCITY_RENDERER_NAME.equals(name)) {
            return VelocityRendererBuilder.createInstance().setApplicationContext(applicationContext).build();
        } else if (MUSTACHE_RENDERER_NAME.equals(name)) {
            MustacheRenderer mustacheRenderer = new MustacheRenderer();
            mustacheRenderer.setAcceptPattern(Pattern.compile(".+\\.mustache"));
            return mustacheRenderer;
        } else {
            return super.getRenderer(applicationContext, name);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected ApplicationContext createTilesApplicationContext(ApplicationContext preliminaryContext) {
    	ServletContext servletContext = (ServletContext) preliminaryContext.getContext();
    	AbstractApplicationContext result = new ServletApplicationContext(servletContext);
    	result.register(new SpringResourceLocator(new ServletContextResourcePatternResolver(servletContext)));
    	return result;
    }

    /** {@inheritDoc} */
    @Override
    protected DefinitionsReader createDefinitionsReader(ApplicationContext applicationContext) {
        return new CompatibilityDigesterDefinitionsReader();
    }

}
