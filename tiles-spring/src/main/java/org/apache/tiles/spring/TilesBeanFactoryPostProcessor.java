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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.definition.pattern.BasicPatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.render.RendererFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * 
 */
public class TilesBeanFactoryPostProcessor implements BeanFactoryPostProcessor, BeanDefinitionRegistryPostProcessor {
    private static Logger           logger           = LoggerFactory.getLogger(TilesBeanFactoryPostProcessor.class);

    private static final Class<?>[] INJECTABLES      = { TilesContainer.class,
            ApplicationContext.class,
            LocaleResolver.class,
            DefinitionsReader.class,
            BasicPatternDefinitionResolver.class,
            PatternDefinitionResolver.class,
            DefinitionDAO.class,
            DefinitionsFactory.class,
            AttributeEvaluatorFactory.class,
            PreparerFactory.class,
            RendererFactory.class                   };

    private HashSet<String>         defaultInstances = new HashSet<String>();

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#
     * postProcessBeanFactory
     * (org.springframework.beans.factory.config.ConfigurableListableBeanFactory
     * )
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (Class<?> type : INJECTABLES) {
            String[] availableNames = beanFactory.getBeanNamesForType(type);
            ArrayList<String> actualNames = new ArrayList<String>(availableNames.length);
            if (availableNames.length == 1) {
                actualNames.add(availableNames[0]);
            } else {
                for (String name : availableNames) {
                    if (!defaultInstances.contains(name)) {
                        actualNames.add(name);
                    }
                }
            }
            if (actualNames.size() == 1) {
                String injectableName = actualNames.get(0);
                injectMissing(beanFactory, type, injectableName);
            } else {
                logger.warn("Multiple beans found for {}, not autowiring", type);
            }
        }
    }

    private void injectMissing(ConfigurableListableBeanFactory beanFactory, Class<?> type, String injectableName) {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            try {
                Class<?> beanClass = beanFactory.getBeanClassLoader().loadClass(bd.getBeanClassName());
                for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(beanClass)) {
                    if (pd.getPropertyType().equals(type)) {
                        if (!bd.getPropertyValues().contains(pd.getName())) {
                            bd.getPropertyValues().addPropertyValue(pd.getName(), new RuntimeBeanReference(injectableName));
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new CannotLoadBeanClassException("TilesBeanFactoryPostProcessor", beanName, bd.getBeanClassName(), e);
            }
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // create default instances
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(new DefaultsRegistry(registry));
        reader.loadBeanDefinitions("classpath:org/apache/tiles/spring/BasicTilesContainer.xml");
    }

    private class DefaultsRegistry implements BeanDefinitionRegistry {
        private BeanDefinitionRegistry delegate;

        public DefaultsRegistry(BeanDefinitionRegistry delegate) {
            super();
            this.delegate = delegate;
        }

        public void registerAlias(String name, String alias) {
            delegate.registerAlias(name, alias);
        }

        public void removeAlias(String alias) {
            delegate.removeAlias(alias);
        }

        public boolean isAlias(String beanName) {
            return delegate.isAlias(beanName);
        }

        public String[] getAliases(String name) {
            return delegate.getAliases(name);
        }

        public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
            if (!delegate.containsBeanDefinition(beanName)) {
                defaultInstances.add(beanName);
                delegate.registerBeanDefinition(beanName, beanDefinition);
            }
        }

        public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
            defaultInstances.remove(beanName);
            delegate.removeBeanDefinition(beanName);
        }

        public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
            return delegate.getBeanDefinition(beanName);
        }

        public boolean containsBeanDefinition(String beanName) {
            return delegate.containsBeanDefinition(beanName);
        }

        public String[] getBeanDefinitionNames() {
            return delegate.getBeanDefinitionNames();
        }

        public int getBeanDefinitionCount() {
            return delegate.getBeanDefinitionCount();
        }

        public boolean isBeanNameInUse(String beanName) {
            return delegate.isBeanNameInUse(beanName);
        }
    }
}
