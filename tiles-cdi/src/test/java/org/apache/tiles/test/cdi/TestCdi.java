package org.apache.tiles.test.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import junit.framework.Assert;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.cdi.TilesExtension;
import org.apache.tiles.freemarker.template.TilesFMModelRepository;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.basic.MapApplicationContext;
import org.apache.tiles.request.basic.StringRequest;
import org.apache.tiles.request.freemarker.RequestTemplateLoader;
import org.apache.tiles.request.freemarker.render.FreemarkerRenderer;
import org.apache.tiles.request.locale.ClasspathResourceLocator;
import org.apache.tiles.request.render.Renderer;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@RunWith(CdiRunner.class)
public class TestCdi {
	@Inject
	private TilesContainer tilesContainer;
	
	@Inject
	private ApplicationContext applicationContext;
	
	@Inject
	private TilesExtension extension;
	
	@Produces
	@Singleton
	public static ApplicationContext applicationContext() {
		MapApplicationContext applicationContext = new MapApplicationContext();
		applicationContext.register(new ClasspathResourceLocator());
		return applicationContext;
	}
	
	@Produces
	@Named("freemarker")
	public static Renderer freemarker() throws TemplateException {
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
	
	@Test
	public void testLoadTilesContainer() {
		Assert.assertNotNull(tilesContainer);
	}
	
	@Test
	public void testLoadApplicationContext() {
		Assert.assertNotNull("TilesContainer not found", tilesContainer);
	}
	
	@Test
	public void testRenderDefinition() {
		StringRequest request = new StringRequest(applicationContext);
		tilesContainer.render("test", request);
		System.out.println(request.getOutput());
	}
}
