package org.apache.tiles.test.guice;

import org.apache.tiles.request.render.RendererFactory;
import org.apache.tiles.test.renderer.ReverseStringRenderer;

import com.google.inject.AbstractModule;

public class ReverseStringModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            bind(ReverseStringRenderer.class).toConstructor(
                    ReverseStringRenderer.class.getConstructor(RendererFactory.class)).asEagerSingleton();
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}