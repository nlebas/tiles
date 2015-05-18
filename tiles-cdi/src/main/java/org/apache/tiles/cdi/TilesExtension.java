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

package org.apache.tiles.cdi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.startup.AbstractTilesConfiguration;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class TilesExtension implements Extension {
	@Named
	@Singleton
	public static class BasicTilesConfiguration extends AbstractTilesConfiguration {
		@Inject
		private BeanManager beanManager;

		@SuppressWarnings("unchecked")
		protected <T> Map<String, T> getContextInstances(Class<T> tClass) {
			Map<String, T> map = new HashMap<String, T>();
			for (Bean<?> tBean : beanManager.getBeans(tClass)) {
				Bean<T> bean = (Bean<T>) tBean;
				map.put(bean.getName(), (T) beanManager.getReference(bean, tClass, beanManager
						.createCreationalContext(bean)));
			}
			return map;
		}

		@Produces
		@Singleton
		@Override
		public TilesContainer tilesContainer(
				ApplicationContext applicationContext,
				AttributeEvaluatorFactory attributeEvaluatorFactory,
				DefinitionsFactory definitionsFactory,
				PreparerFactory preparerFactory) {
			return super.tilesContainer(applicationContext,
					attributeEvaluatorFactory, definitionsFactory,
					preparerFactory);
		}

		@Produces
		@Singleton
		@Override
		public LocaleResolver localeResolver() {
			return super.localeResolver();
		}

		@Produces
		@Singleton
		@Override
		public DefinitionsFactory definitionsFactory(
				DefinitionDAO<Locale> definitionDao,
				LocaleResolver localeResolver) {
			return super.definitionsFactory(definitionDao, localeResolver);
		}

		@Produces
		@Singleton
		public DefinitionDAO<Locale> definitionDao(
				ApplicationContext applicationContext,
				PatternDefinitionResolver<Locale> definitionResolver,
				DefinitionsReader definitionsReader,
				Collection<ApplicationResource> sources) {
			return super.definitionDao(applicationContext, definitionResolver,
					definitionsReader, sources);
		}

		@Produces
		@Singleton
		@Override
		public PatternDefinitionResolver<Locale> definitionResolver() {
			return super.definitionResolver();
		}

		@Produces
		@Singleton
		@Override
		public DefinitionsReader definitionsReader() {
			return super.definitionsReader();
		}

		@Produces
		@Singleton
		@Override
		public Collection<ApplicationResource> tilesResources(
				ApplicationContext applicationContext) {
			return super.tilesResources(applicationContext);
		}

		@Produces
		@Singleton
		@Override
		public AttributeEvaluatorFactory attributeEvaluatorFactory() {
			return super.attributeEvaluatorFactory();
		}

		@Produces
		@Singleton
		@Override
		public PreparerFactory preparerFactory(
				ApplicationContext applicationContext) {
			return super.preparerFactory(applicationContext);
		}
	}

    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager bm) {

    	bbd.addAnnotatedType(bm.createAnnotatedType(BasicTilesConfiguration.class));
    }
}
