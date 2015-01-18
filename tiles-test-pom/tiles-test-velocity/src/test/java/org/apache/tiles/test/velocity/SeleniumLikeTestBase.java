package org.apache.tiles.test.velocity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Locale;

import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.CachingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.guice.GuiceTilesInitializer;
import org.apache.tiles.guice.definition.CompleteDefinitionsFactoryModule;
import org.apache.tiles.guice.preparer.BasicPreparerModule;
import org.apache.tiles.guice.renderer.BasicRendererModule;
import org.apache.tiles.guice.renderer.VelocityToolsRendererModule;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.basic.MapApplicationContext;
import org.apache.tiles.request.basic.StringRequest;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.spring.SpringResourceLocator;
import org.apache.tiles.request.velocity.render.VelocityToolsRenderer;
import org.apache.tiles.startup.TilesInitializer;
import org.apache.tiles.test.guice.JasperCompleteEvaluatorModule;
import org.apache.tiles.test.guice.ReverseStringModule;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class SeleniumLikeTestBase {

    private static MapApplicationContext applicationContext;
    private String output;

    @BeforeClass
    public static void setUp() {
        applicationContext = new MapApplicationContext();
        applicationContext.register(new SpringResourceLocator(new PathMatchingResourcePatternResolver()));
        GuiceTilesInitializer tilesAlternateInitializer = new GuiceTilesInitializer(
                //
                new CompleteDefinitionsFactoryModule(
                        "classpath:/org/apache/tiles/test/alt/defs/tiles-alt-freemarker-defs.xml") {

                    @Override
                    public DefinitionDAO<Locale> createLocaleDefinitionDao(ApplicationContext applicationContext,
                            LocaleResolver resolver, DefinitionsReader definitionsReader,
                            PatternDefinitionResolver<Locale> patternDefinitionResolver) {
                        CachingLocaleUrlDefinitionDAO definitionDao = new CachingLocaleUrlDefinitionDAO(
                                applicationContext);
                        definitionDao.setReader(definitionsReader);
                        definitionDao.setSources(getSources(applicationContext));
                        definitionDao.setPatternDefinitionResolver(patternDefinitionResolver);
                        return definitionDao;
                    }

                }, //
                new JasperCompleteEvaluatorModule(), new BasicPreparerModule(), new BasicRendererModule(),
                new VelocityToolsRendererModule(), new ReverseStringModule());
        tilesAlternateInitializer.setContainerKey("alternate");
        tilesAlternateInitializer.initialize(applicationContext);
        TilesInitializer tilesInititializer = new GuiceTilesInitializer(
                //
                new CompleteDefinitionsFactoryModule("/WEB-INF/**/tiles-defs*.xml",
                        "classpath*:/META-INF/**/tiles-defs*.xml", "classpath:/org/apache/tiles/classpath-defs.xml",
                        "classpath:/org/apache/tiles/freemarker-classpath-defs.xml",
                        "classpath:/org/apache/tiles/velocity-classpath-defs.xml"), //
                new JasperCompleteEvaluatorModule(), new BasicPreparerModule(), new BasicRendererModule(),
                new VelocityToolsRendererModule(), new ReverseStringModule());
        tilesInititializer.initialize(applicationContext);
    }

    protected final void clickAndWait(String path) throws IOException {
        String category = path.replace('/', '.');
        if (category.startsWith(".")) {
            category = category.substring(1);
        }
        Logger logger = LoggerFactory.getLogger(category);
        StringRequest request = new StringRequest(applicationContext);
        request.addRole("goodrole");
        output = "";
        Renderer renderer = (Renderer) applicationContext.getApplicationScope().get(VelocityToolsRenderer.class.getName());
        renderer.render(path, request);
        output = request.getOutput();
        logger.info(output);
    }

    protected final void assertTextPresent(String expected) {
        assertTrue(output.contains(expected));
    }

    protected final void assertTextNotPresent(String expected) {
        assertFalse(output.contains(expected));
    }

}