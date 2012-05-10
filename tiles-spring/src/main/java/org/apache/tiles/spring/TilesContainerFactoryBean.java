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
package org.apache.tiles.spring;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.spring.ApplicationResourceEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * Instantiates a TilesContainer as a Spring Bean.
 *
 *  Example usage for Spring MVC:
 *  <pre>
 *  &lt;bean id="tilesContainer"
 *      class="org.apache.tiles.spring.TilesContainerFactoryBean"&gt;
 *      &lt;property name="contextPath" value="/WEB-INF/tiles-context.xml"&gt;
 *  &lt;/bean&gt;
 *  &lt;bean id="tilesViewResolver"
 *      class="org.springframework.web.servlet.view.UrlBasedViewResolver"&gt;
 *      &lt;property name="viewClass" value="org.apache.tiles.spring.RendererView"&gt;
 *      &lt;property name="contentType" value="text/html"&gt;
 *      &lt;property name="attributesMap"&gt;
 *          &lt;map&gt;
 *              &lt;entry
 *                  key="org.apache.tiles.servlet.context.ServletTilesRequestContext.CURRENT_CONTAINER_KEY"
 *                  value-ref="tilesContainer" /&gt;
 *              &lt;entry
 *                  key="org.apache.tiles.request.ApplicationContext.ATTRIBUTE"
 *                  value-ref="#{@tilesContainer.applicationContext}" /&gt;
 *              &lt;entry key="org.apache.tiles.spring.RendererView.Renderer"
 *                  value-ref="definitionRenderer" /&gt;
 *          &lt;/map&gt;
 *      &lt;/property&gt;
 *  &lt;/bean&gt;
 *  </pre>
 *
 * The TilesContainer uses the components declared in the file "contextPath"
 * (/WEB-INF/tiles-context.xml in the example above).
 * org/apache/tiles/spring/BasicTilesContainer.xml provides sensible defaults, and
 * most singletons from Tiles' classes will be autowired into the beans declared
 * in the file "contextPath"
 *
 * @see TilesBeanFactoryPostProcessor
 * @see TilesRegistrationPostProcessor
 * @version $Rev$ $Date$
 * @since 3.0.0
 */
public class TilesContainerFactoryBean extends AbstractFactoryBean<TilesContainer> implements ApplicationContextAware {
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(TilesContainerFactoryBean.class);

    private ApplicationContext parent;
    private String contextPath;
    private GenericXmlApplicationContext springContext;
    private org.apache.tiles.request.ApplicationContext tilesContext;

    public void setTilesContext(org.apache.tiles.request.ApplicationContext tilesContext) {
        this.tilesContext = tilesContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.parent = applicationContext;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public Class<?> getObjectType() {
        return TilesContainer.class;
    }

    @Override
    protected TilesContainer createInstance() throws Exception {
        springContext = new GenericXmlApplicationContext();
        springContext.setParent(parent);
        if (parent instanceof ResourceLoader) {
            springContext.setResourceLoader((ResourceLoader) parent);
        }
        springContext.getBeanFactory().registerSingleton("tilesContext", tilesContext);
        springContext.getBeanFactory().registerResolvableDependency(org.apache.tiles.request.ApplicationContext.class,
                tilesContext);
        springContext.addBeanFactoryPostProcessor(new TilesBeanFactoryPostProcessor());
        springContext.getBeanFactory().addPropertyEditorRegistrar(new PropertyEditorRegistrar() {
            @Override
            public void registerCustomEditors(PropertyEditorRegistry registry) {
                registry.registerCustomEditor(ApplicationResource.class, new ApplicationResourceEditor(tilesContext));
            }
        });
        springContext.load(contextPath);
        springContext.refresh();
        String[] containers = springContext.getBeanFactory().getBeanNamesForType(TilesContainer.class, false, false);
        if (containers.length == 0) {
            throw new BeanCreationException("Couldn't find a TilesContainer in the specified context");
        }
        if (containers.length > 2) {
            throw new BeanCreationException("Found several TilesContainers in the specified context");
        }
        for (String container : containers) {
            if (containers.length == 1 || !container.endsWith("org.apache.tiles.TilesContainer#Default")) {
                springContext.registerAlias(container, TilesAccess.CONTAINER_ATTRIBUTE);
            }
        }
        return springContext.getBean(TilesAccess.CONTAINER_ATTRIBUTE, TilesContainer.class);
    }
}
