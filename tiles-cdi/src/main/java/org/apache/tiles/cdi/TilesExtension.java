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

package org.apache.tiles.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Scope;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class TilesExtension implements Extension {

    public static class SingletonBean<T> implements Bean<T> {
        private final InjectionTarget<T> it;
        private final Class<T> c;

        public SingletonBean(InjectionTarget<T> it, Class<T> c) {
            this.it = it;
            this.c = c;
        }

        @Override
        public Class<?> getBeanClass() {
            return c;
        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
            return it.getInjectionPoints();
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Set<Annotation> getQualifiers() {
            Set<Annotation> qualifiers = new HashSet<Annotation>();
            qualifiers.add(new AnnotationLiteral<Default>() {
            });
            qualifiers.add(new AnnotationLiteral<Any>() {
            });
            return qualifiers;
        }

        @Override
        public Class<? extends Annotation> getScope() {
            for (Annotation a : c.getAnnotations()) {
                Class<? extends Annotation> annotation = a.getClass();

                if (annotation.getAnnotation(Scope.class) != null) {
                    return annotation;
                }
            }
            return Dependent.class;
        }

        @Override
        public Set<Class<? extends Annotation>> getStereotypes() {
            return Collections.emptySet();
        }

        @Override
        public Set<Type> getTypes() {
            Set<Type> types = new HashSet<Type>();
            types.add(c);
            addSupertypes(types, c);
            return types;
        }
        
        private void addSupertypes(Set<Type> types, Class<?>c) {
            for(Class<?> i: c.getInterfaces()) {
                types.add(i);
                addSupertypes(types, i);
            }
            Class<?> superclass = c.getSuperclass();
            if(superclass != null) {
                types.add(superclass);
                addSupertypes(types, superclass);
            }
        }

        @Override
        public boolean isAlternative() {
            return false;
        }

        @Override
        public boolean isNullable() {
            return false;
        }

        @Override
        public T create(CreationalContext<T> ctx) {
            T instance = it.produce(ctx);
            it.inject(instance, ctx);
            it.postConstruct(instance);
            return instance;
        }

        @Override
        public void destroy(T instance, CreationalContext<T> ctx) {
            it.preDestroy(instance);
            it.dispose(instance);
            ctx.release();
        }
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        // use this to read annotations of the class
        AnnotatedType<CDITilesContainerFactory> at = beanManager.createAnnotatedType(CDITilesContainerFactory.class);

        // use this to create the class and inject dependencies
        final InjectionTarget<CDITilesContainerFactory> it = beanManager.createInjectionTarget(at);

        abd.addBean(new SingletonBean<CDITilesContainerFactory>(it, CDITilesContainerFactory.class));
    }

    public void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        Set<Bean<?>> contexts = beanManager.getBeans(ApplicationContext.class);
        Bean<?> contextBean = beanManager.resolve(contexts);
        // the call to toString() is a cheat to force the bean to be initialized
        ApplicationContext applicationContext = (ApplicationContext) beanManager.getReference(contextBean, contextBean.getBeanClass(),
                beanManager.createCreationalContext(contextBean));
        ApplicationAccess.register(applicationContext);
        
        Set<Bean<?>> tilesContainers = beanManager.getBeans(TilesContainer.class);
        Bean<?> bean = beanManager.resolve(tilesContainers);
        // the call to toString() is a cheat to force the bean to be initialized
        TilesContainer container = (TilesContainer) beanManager.getReference(bean, bean.getBeanClass(),
                beanManager.createCreationalContext(bean));
        TilesAccess.setContainer(applicationContext, container);
    }
}
