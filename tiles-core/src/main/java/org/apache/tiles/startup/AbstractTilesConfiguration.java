package org.apache.tiles.startup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.definition.dao.ResolvingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.definition.pattern.BasicPatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.wildcard.WildcardDefinitionPatternMatcherFactory;
import org.apache.tiles.evaluator.AttributeEvaluator;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.BasicPreparerFactory;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.renderer.DefinitionRenderer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.RendererFactory;
import org.apache.tiles.request.render.StringRenderer;

public abstract class AbstractTilesConfiguration {

	public AbstractTilesConfiguration() {
		super();
	}

	protected abstract <T> Map<String, T> getContextInstances(Class<T> tClass);

	public TilesContainer tilesContainer(ApplicationContext applicationContext, AttributeEvaluatorFactory attributeEvaluatorFactory, DefinitionsFactory definitionsFactory,
			PreparerFactory preparerFactory) {
				BasicTilesContainer bean = new BasicTilesContainer();
				bean.setApplicationContext(applicationContext);
				bean.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
				bean.setDefinitionsFactory(definitionsFactory);
				bean.setPreparerFactory(preparerFactory);
				bean.setRendererFactory(rendererFactory(bean));
				TilesAccess.setContainer(applicationContext, bean);
				return bean;
			}

	public LocaleResolver localeResolver() {
		DefaultLocaleResolver bean = new DefaultLocaleResolver();
		return bean;
	}

	public DefinitionsFactory definitionsFactory(DefinitionDAO<Locale> definitionDao, LocaleResolver localeResolver) {
		UnresolvingLocaleDefinitionsFactory bean = new UnresolvingLocaleDefinitionsFactory();
		bean.setDefinitionDAO(definitionDao);
		bean.setLocaleResolver(localeResolver);
		return bean;
	}

	public DefinitionDAO<Locale> definitionDao(ApplicationContext applicationContext, PatternDefinitionResolver<Locale> definitionResolver, DefinitionsReader definitionsReader,
			Iterable<ApplicationResource> sources) {
				ResolvingLocaleUrlDefinitionDAO bean = new ResolvingLocaleUrlDefinitionDAO(
						applicationContext);
				bean.setPatternDefinitionResolver(definitionResolver);
				bean.setReader(definitionsReader);
				bean.setSources(sources);
				return bean;
			}

	public PatternDefinitionResolver<Locale> definitionResolver() {
		WildcardDefinitionPatternMatcherFactory factory = new WildcardDefinitionPatternMatcherFactory();
		BasicPatternDefinitionResolver<Locale> bean = new BasicPatternDefinitionResolver<Locale>(
				factory, factory
				);
		return bean;
	}

	public DefinitionsReader definitionsReader() {
		DigesterDefinitionsReader bean = new DigesterDefinitionsReader();
		return bean;
	}

	public Collection<ApplicationResource> tilesResources(ApplicationContext applicationContext) {
		return Arrays.asList(applicationContext.getResource("/WEB-INF/tiles.xml"));
	}

	public AttributeEvaluatorFactory attributeEvaluatorFactory() {
		BasicAttributeEvaluatorFactory bean = new BasicAttributeEvaluatorFactory(new DirectAttributeEvaluator());			
		for(Entry<String, AttributeEvaluator> evaluator: getContextInstances(AttributeEvaluator.class).entrySet()) {
			bean.registerAttributeEvaluator(evaluator.getKey(), evaluator.getValue());
		}
		return bean;
	}

	public PreparerFactory preparerFactory(ApplicationContext applicationContext) {
		BasicPreparerFactory bean = new BasicPreparerFactory();
		for(Entry<String, ViewPreparer> preparer: getContextInstances(ViewPreparer.class).entrySet()) {
			bean.register(preparer.getKey(), preparer.getValue());
		}
		return bean;
	}

	private RendererFactory rendererFactory(TilesContainer tilesContainer) {
		BasicRendererFactory bean = new BasicRendererFactory();
		ChainedDelegateRenderer defaultRenderer = new ChainedDelegateRenderer();
		DefinitionRenderer definitionRenderer = new DefinitionRenderer(tilesContainer);
		ChainedDelegateRenderer templateRenderer = new ChainedDelegateRenderer();
		StringRenderer stringRenderer = new StringRenderer();
		bean.setDefaultRenderer(defaultRenderer);
		bean.registerRenderer("default", defaultRenderer);
		defaultRenderer.addAttributeRenderer(definitionRenderer);
		bean.registerRenderer("definition", definitionRenderer);
		defaultRenderer.addAttributeRenderer(templateRenderer);
		bean.registerRenderer("template", templateRenderer);
		defaultRenderer.addAttributeRenderer(stringRenderer);
		bean.registerRenderer("string", stringRenderer);
		for(Entry<String, Renderer> renderer: getContextInstances(Renderer.class).entrySet()) {
			templateRenderer.addAttributeRenderer(renderer.getValue());
			bean.registerRenderer(renderer.getKey(), renderer.getValue());
		}
		return bean;
	}

}