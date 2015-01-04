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

package org.apache.tiles.guice.definition;

import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */
public class DefinitionsFactoryModule extends AbstractModule {

    private String[] sources;

    public DefinitionsFactoryModule(String... sources) {
        this.sources = sources;
    }

    /** {@inheritDoc} */
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public LocaleResolver createLocaleResolver() {
        return new DefaultLocaleResolver();
    }
    
    @Provides
    @Singleton
    public DefinitionsReader createDefinitionsReader() {
        return new DigesterDefinitionsReader();
    }
    
    @Provides
    @Singleton
    public DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext, LocaleResolver resolver,
            DefinitionDAO<Locale> dao) {
        UnresolvingLocaleDefinitionsFactory factory = new UnresolvingLocaleDefinitionsFactory();
        factory.setLocaleResolver(resolver);
        factory.setDefinitionDAO(dao);
        return factory;
    }

    @Provides
    @Singleton
    public DefinitionDAO<Locale> createLocaleDefinitionDao(ApplicationContext applicationContext,
            LocaleResolver resolver, DefinitionsReader definitionsReader,
            PatternDefinitionResolver<Locale> patternDefinitionResolver) {
        ResolvingLocaleUrlDefinitionDAO definitionDao = new ResolvingLocaleUrlDefinitionDAO(applicationContext);
        definitionDao.setReader(definitionsReader);
        definitionDao.setSources(getSources(applicationContext));
        definitionDao.setPatternDefinitionResolver(patternDefinitionResolver);
        return definitionDao;
    }

    protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
        List<ApplicationResource> retValue = new ArrayList<ApplicationResource>();
        for (String source : sources) {
            Collection<ApplicationResource> resources = applicationContext.getResources(source);
            for (ApplicationResource resource : resources) {
                if (Locale.ROOT.equals(resource.getLocale())) {
                    retValue.add(resource);
                }
            }
        }
        return retValue;
    }

    @Provides
    @Singleton
    public PatternDefinitionResolver<Locale> createPatternDefinitionResolver() {
        WildcardDefinitionPatternMatcherFactory definitionPatternMatcherFactory = new WildcardDefinitionPatternMatcherFactory();
        return new BasicPatternDefinitionResolver<Locale>(definitionPatternMatcherFactory, definitionPatternMatcherFactory);
    }
}
