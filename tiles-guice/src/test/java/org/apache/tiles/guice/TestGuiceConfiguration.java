package org.apache.tiles.guice;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.freemarker.template.TilesFMModelRepository;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.basic.MapApplicationContext;
import org.apache.tiles.request.basic.StringRequest;
import org.apache.tiles.request.freemarker.RequestTemplateLoader;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.locale.ClasspathResourceLocator;
import org.apache.tiles.request.render.Renderer;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@RunWith(JukitoRunner.class)
@UseModules({ GuiceConfiguration.class, TestGuiceConfiguration.Config.class })
public class TestGuiceConfiguration {
	public static class Config extends AbstractModule {

		@Provides
		@Singleton
		public ApplicationContext applicationContext() {
			MapApplicationContext applicationContext = new MapApplicationContext();
			applicationContext.register(new ClasspathResourceLocator());
			return applicationContext;
		}

		@Provides
		@Named("freemarker")
		@Singleton
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

		@Override
		protected void configure() {
			
		}
	}
	
	@Inject
	private ApplicationContext applicationContext;

	@Inject
	private TilesContainer tilesContainer;

	@Test
	public void testLoadApplicationContext() {
		Assert.assertFalse("ApplicationContext not found",
				Mockito.mockingDetails(applicationContext).isMock());
	}
	
	@Test
	public void testLoadTilesContainer() {
		Assert.assertFalse("TilesContainer not found",
				Mockito.mockingDetails(tilesContainer).isMock());
	}

	@Test
	public void testRenderDefinition() {
		StringRequest request = new StringRequest(applicationContext);
		tilesContainer.render("test", request);
		System.out.println(request.getOutput());
	}
}
