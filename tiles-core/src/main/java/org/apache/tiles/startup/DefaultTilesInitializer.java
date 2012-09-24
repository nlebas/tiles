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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.definition.dao.ResolvingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.definition.pattern.BasicPatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.wildcard.WildcardDefinitionPatternMatcherFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.preparer.factory.BasicPreparerFactory;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.render.DispatchRenderer;
import org.apache.tiles.request.render.Renderer;

/**
 * Loads Tiles with the default settings.
 *
 * @version $Rev$ $Date$
 * @since 2.2.0
 */
public class DefaultTilesInitializer extends AbstractTilesInitializer {

    public static final String DISPATCH_RENDERER_NAME = "dispatch";

    @Override
    protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext applicationContext) {
        return new BasicAttributeEvaluatorFactory(new DirectAttributeEvaluator());
    }

    @Override
    protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext) {
        UnresolvingLocaleDefinitionsFactory factory = new UnresolvingLocaleDefinitionsFactory();
        factory.setLocaleResolver(new DefaultLocaleResolver());
        factory.setDefinitionDAO(createLocaleDefinitionDao(applicationContext));
        return factory;
    }

    /**
     * Creates a Locale-based definition DAO.
     * @param applicationContext The Tiles application context.
     * @param resolver The locale resolver.
     * @return The definition DAO.
     * @since 2.1.1
     */
    protected DefinitionDAO<Locale> createLocaleDefinitionDao(ApplicationContext applicationContext) {
        ResolvingLocaleUrlDefinitionDAO definitionDao = new ResolvingLocaleUrlDefinitionDAO(applicationContext);;
        definitionDao.setReader(createDefinitionsReader(applicationContext));
        definitionDao.setSources(getSources(applicationContext));
        definitionDao.setPatternDefinitionResolver(createPatternDefinitionResolver(Locale.class));
        return definitionDao;
    }
    
    /**
     * Creates the definitions reader. By default it creates a
     * {@link DigesterDefinitionsReader}.
     * @param applicationContext The Tiles application context.
     * @return The definitions reader.
     * @since 2.1.1
     */
    protected DefinitionsReader createDefinitionsReader(
            ApplicationContext applicationContext) {
        return new DigesterDefinitionsReader();
    }

    /**
     * Returns a list containing the resources to be parsed. By default, it returns a
     * list containing the resource at "/WEB-INF/tiles.xml".
     * @param applicationContext The Tiles application context.
     * @return The resources.
     * @since 2.1.1
     */
    protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
        List<ApplicationResource> retValue = new ArrayList<ApplicationResource>(1);
        retValue.add(applicationContext.getResource("/WEB-INF/tiles.xml"));
        return retValue;
    }

    /**
     * Creates the preparer factory to use. By default it returns a
     * {@link BasicPreparerFactory}.
     * @param applicationContext The Tiles application context.
     * @return The preparer factory.
     * @since 2.1.1
     */
    @Override
    protected PreparerFactory createPreparerFactory(ApplicationContext applicationContext) {
        return new BasicPreparerFactory();
    }

    /**
     * Creates a new pattern definition resolver. By default, it instantiate a
     * {@link BasicPatternDefinitionResolver} with
     * {@link WildcardDefinitionPatternMatcherFactory} to manage wildcard
     * substitution.
     *
     * @param <T> The type of the customization key.
     * @param customizationKeyClass The customization key class.
     * @return The pattern definition resolver.
     * @since 2.2.0
     */
    protected <T> PatternDefinitionResolver<T> createPatternDefinitionResolver(
            Class<T> customizationKeyClass) {
        WildcardDefinitionPatternMatcherFactory definitionPatternMatcherFactory =
            new WildcardDefinitionPatternMatcherFactory();
        return new BasicPatternDefinitionResolver<T>(
                definitionPatternMatcherFactory,
                definitionPatternMatcherFactory);
    }

    @Override
    protected List<String> getRendererNames(ApplicationContext applicationContext) {
        return Arrays.asList(DISPATCH_RENDERER_NAME);
    }

    @Override
    protected Renderer getRenderer(ApplicationContext applicationContext, String name) {
        if (DISPATCH_RENDERER_NAME.equals(name)) {
            return new DispatchRenderer();
        }
        throw new IllegalArgumentException("Unknown renderer " + name);
    }

}
