package org.apache.tiles.cdi;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.renderer.DefinitionRenderer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.StringRenderer;

@Singleton
public class CDITilesContainerFactory extends AbstractTilesContainerFactory {

    private static final String STRING_RENDERER_NAME = "string";
    private static final String TEMPLATE_RENDERER_NAME = "template";
    private static final String DEFINITION_RENDERER_NAME = "definition";

    @Inject
    private AttributeEvaluatorFactory attributeEvaluatorFactory;
    @Inject
    private DefinitionsFactory definitionsFactory;
    @Inject
    private PreparerFactory preparerFactory;
    @Inject
    private Instance<Renderer> renderers;

    /** {@inheritDoc} */
    @Override
    @Produces
    public TilesContainer createContainer(ApplicationContext applicationContext) {
        BasicTilesContainer tilesContainer = new BasicTilesContainer();
        tilesContainer.setApplicationContext(applicationContext);
        tilesContainer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        tilesContainer.setDefinitionsFactory(definitionsFactory);
        tilesContainer.setPreparerFactory(preparerFactory);
        BasicRendererFactory rendererFactory = new BasicRendererFactory();
        tilesContainer.setRendererFactory(rendererFactory);
        TilesContainer result = new CachingTilesContainer(tilesContainer);
        populateRendererFactory(result, rendererFactory);
        TilesAccess.setContainer(applicationContext, result);
        return result;
    }

    protected void populateRendererFactory(TilesContainer container, BasicRendererFactory factory) {
        ChainedDelegateRenderer defaultRenderer = new ChainedDelegateRenderer();
        factory.setDefaultRenderer(defaultRenderer);

        // register the actual renderers first, so they do not override the
        // standard names
        for (Renderer renderer : renderers) {
            Named named = renderer.getClass().getAnnotation(Named.class);
            if (named != null) {
                factory.registerRenderer(named.value(), renderer);
            }
        }

        // first renderer in standard chain: definition
        DefinitionRenderer definitionRenderer = new DefinitionRenderer(container);
        factory.registerRenderer(DEFINITION_RENDERER_NAME, definitionRenderer);
        defaultRenderer.addAttributeRenderer(definitionRenderer);

        // second renderer in standard chain: template
        ChainedDelegateRenderer templateRenderer = new ChainedDelegateRenderer();
        factory.registerRenderer(TEMPLATE_RENDERER_NAME, templateRenderer);
        defaultRenderer.addAttributeRenderer(templateRenderer);

        // insert the actual renderers into the template chain
        for (Renderer renderer : renderers) {
            templateRenderer.addAttributeRenderer(renderer);
        }

        // third renderer in standard chain: string
        StringRenderer stringRenderer = new StringRenderer();
        factory.registerRenderer(STRING_RENDERER_NAME, stringRenderer);
        defaultRenderer.addAttributeRenderer(stringRenderer);
    }
}
