package org.apache.tiles.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

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
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class TilesConfiguration {
	@Autowired
	private org.springframework.context.ApplicationContext parentContext;
	private GenericApplicationContext springContext;

	@Configuration
	public static class BasicTilesConfiguration extends AbstractTilesConfiguration {
		@Inject
		private org.springframework.context.ApplicationContext springContext;

		@Override
		@SuppressWarnings("unchecked")
		protected <T> Map<String, T> getContextInstances(Class<T> tClass) {
			Map<String, T> map = new HashMap<String, T> ();
			for(String name: BeanFactoryUtils.beanNamesForTypeIncludingAncestors(springContext, tClass)) {
				map.put(name, (T) springContext.getBean(name));
			}
			return map;
		}

		@Inject
		@Bean
		@Override
		public TilesContainer tilesContainer(ApplicationContext applicationContext,
				AttributeEvaluatorFactory attributeEvaluatorFactory,
				DefinitionsFactory definitionsFactory,
				PreparerFactory preparerFactory) {
			return super.tilesContainer(applicationContext, attributeEvaluatorFactory,
					definitionsFactory, preparerFactory);
		}

		@Inject
		@Bean
		@Override
		public LocaleResolver localeResolver() {
			return super.localeResolver();
		}

		@Inject
		@Bean
		@Override
		public DefinitionsFactory definitionsFactory(
				DefinitionDAO<Locale> definitionDao, LocaleResolver localeResolver) {
			return super.definitionsFactory(definitionDao, localeResolver);
		}

		@Inject
		@Bean
		public DefinitionDAO<Locale> definitionDao(
				ApplicationContext applicationContext,
				PatternDefinitionResolver<Locale> definitionResolver,
				DefinitionsReader definitionsReader) {
			return super.definitionDao(applicationContext, definitionResolver,
					definitionsReader, tilesResources(applicationContext));
		}

		@Inject
		@Bean
		@Override
		public PatternDefinitionResolver<Locale> definitionResolver() {
			return super.definitionResolver();
		}

		@Inject
		@Bean
		@Override
		public DefinitionsReader definitionsReader() {
			return super.definitionsReader();
		}

		@Inject
		@Bean
		@Override
		public Collection<ApplicationResource> tilesResources(
				ApplicationContext applicationContext) {
			return super.tilesResources(applicationContext);
		}

		@Inject
		@Bean
		@Override
		public AttributeEvaluatorFactory attributeEvaluatorFactory() {
			return super.attributeEvaluatorFactory();
		}

		@Inject
		@Bean
		@Override
		public PreparerFactory preparerFactory(ApplicationContext applicationContext) {
			return super.preparerFactory(applicationContext);
		}
	}
	
	public TilesConfiguration() {
		this(BasicTilesConfiguration.class);
	}

	public TilesConfiguration(Class<?> configurationClass) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(configurationClass);
		springContext = context;
	}

	public TilesConfiguration(String contextPath) {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.load(contextPath);
		springContext = context;
	}

	@Autowired
	@Bean 
	public ApplicationResourceEditor applicationResourceEditor(ApplicationContext applicationContext) {
		return new ApplicationResourceEditor(applicationContext);
	}
	
	@Autowired
	@Bean
	public TilesContainer tilesContainer() {
		springContext.setParent(parentContext);
		if (parentContext instanceof ResourceLoader) {
			springContext.setResourceLoader((ResourceLoader) parentContext);
		}
		springContext.refresh();
		TilesContainer tilesContainer = springContext.getBean(TilesContainer.class);
		return tilesContainer;
	}
}
