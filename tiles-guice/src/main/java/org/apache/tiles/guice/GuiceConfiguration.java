package org.apache.tiles.guice;

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

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

public class GuiceConfiguration extends AbstractModule {

	@Inject
	@Provides
	public AbstractTilesConfiguration config(final Injector injector) {
		return new AbstractTilesConfiguration() {
			@Override
			@SuppressWarnings("unchecked")
			protected <T> Map<String, T> getContextInstances(Class<T> tClass) {
				Map<String, T> map = new HashMap<String, T>();
				for (Binding<T> binding : injector
						.findBindingsByType(TypeLiteral.get(tClass))) {
					Key<T> key = binding.getKey();
					if (Named.class.equals(key.getAnnotationType())) {
						Named named = (Named) key.getAnnotation();
						map.put(named.value(), binding.getProvider().get());
					}
				}
				return map;
			}
		};
	}

	@Inject
	@Provides
	@Singleton
	public TilesContainer tilesContainer(AbstractTilesConfiguration config, ApplicationContext applicationContext,
			AttributeEvaluatorFactory attributeEvaluatorFactory,
			DefinitionsFactory definitionsFactory,
			PreparerFactory preparerFactory) {
		return config.tilesContainer(applicationContext,
				attributeEvaluatorFactory, definitionsFactory, preparerFactory);
	}

	@Inject
	@Provides
	public LocaleResolver localeResolver(AbstractTilesConfiguration config) {
		return config.localeResolver();
	}

	@Inject
	@Provides
	public DefinitionsFactory definitionsFactory(AbstractTilesConfiguration config, 
			DefinitionDAO<Locale> definitionDao, LocaleResolver localeResolver) {
		return config.definitionsFactory(definitionDao, localeResolver);
	}

	@Inject
	@Provides
	public DefinitionDAO<Locale> definitionDao(AbstractTilesConfiguration config,
			ApplicationContext applicationContext,
			PatternDefinitionResolver<Locale> definitionResolver,
			DefinitionsReader definitionsReader,
			Iterable<ApplicationResource> sources) {
		return config.definitionDao(applicationContext, definitionResolver,
				definitionsReader, sources);
	}

	@Inject
	@Provides
	public PatternDefinitionResolver<Locale> definitionResolver(AbstractTilesConfiguration config) {
		return config.definitionResolver();
	}

	@Inject
	@Provides
	public DefinitionsReader definitionsReader(AbstractTilesConfiguration config) {
		return config.definitionsReader();
	}

	@Inject
	@Provides
	public Iterable<ApplicationResource> tilesResources(AbstractTilesConfiguration config,
			ApplicationContext applicationContext) {
		return config.tilesResources(applicationContext);
	}

	@Inject
	@Provides
	public AttributeEvaluatorFactory attributeEvaluatorFactory(AbstractTilesConfiguration config) {
		return config.attributeEvaluatorFactory();
	}

	@Inject
	@Provides
	public PreparerFactory preparerFactory(AbstractTilesConfiguration config,ApplicationContext applicationContext) {
		return config.preparerFactory(applicationContext);
	}

	@Override
	protected void configure() {
	}
}
