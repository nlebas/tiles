package org.apache.tiles.spring;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.freemarker.template.TilesFMModelRepository;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.basic.MapApplicationContext;
import org.apache.tiles.request.basic.StringRequest;
import org.apache.tiles.request.freemarker.RequestTemplateLoader;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.locale.ClasspathResourceLocator;
import org.apache.tiles.request.render.Renderer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSpring.Config.class)
public class TestSpring {
	@Inject
	private ApplicationContext applicationContext;
	@Inject	
	private TilesContainer tilesContainer;

	@Configuration
	@Import(TilesConfiguration.class)
	public static class Config {
		@Autowired TilesConfiguration tiles;
		@Bean
		public ApplicationContext applicationContext() {
			MapApplicationContext applicationContext = new MapApplicationContext();
			applicationContext.register(new ClasspathResourceLocator());
			return applicationContext;
		}

		@Bean
		public Renderer freemarker() throws TemplateException {
			freemarker.template.Configuration config = new freemarker.template.Configuration();
            config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            config.setObjectWrapper(ObjectWrapper.DEFAULT_WRAPPER);
            config.setTemplateLoader(new RequestTemplateLoader(applicationContext()));
            config.setDefaultEncoding("ISO-8859-1");
            config.setTemplateUpdateDelay(0);
            config.setSetting("number_format", "0.##########");
            config.setSharedVariable("tiles", new TilesFMModelRepository());
			FreemarkerRenderer renderer = new FreemarkerRenderer();
			renderer.setConfiguration(config);
			return renderer;
		}
	}
	
	 
	@Test
	public void testLoadApplicationContext() {
		Assert.assertNotNull("ApplicationContext not found", applicationContext);
	}
	
	@Test
	public void testLoadTilesContainer() {
		Assert.assertNotNull("TilesContainer not found", tilesContainer);
	}
	
	@Test
	public void testRenderDefinition() {
		StringRequest request = new StringRequest(applicationContext);
		tilesContainer.render("test", request);
		System.out.println(request.getOutput());
	}
}
