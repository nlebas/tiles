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

package org.apache.tiles.definition;

import java.util.Locale;
import java.util.Map;

import org.apache.tiles.Definition;
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.awareness.TilesApplicationContextAware;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.locale.LocaleResolver;

/**
 * {@link DefinitionsFactory DefinitionsFactory} implementation that manages
 * Definitions configuration data from URLs, without resolving definition
 * inheritance when a definition is returned.<p/>
 * <p>
 * The Definition objects are read from the
 * {@link org.apache.tiles.definition.digester.DigesterDefinitionsReader DigesterDefinitionsReader}
 * class unless another implementation is specified.
 * </p>
 *
 * @version $Rev$ $Date$
 * @since 2.2.1
 */
public class UnresolvingLocaleDefinitionsFactory implements DefinitionsFactory,
        TilesApplicationContextAware {

    /**
     * The definition DAO that extracts the definitions from the sources.
     *
     * @since 2.2.1
     */
    protected DefinitionDAO<Locale> definitionDao;

    /**
     * The application context.
     *
     * @since 2.2.1
     */
    protected TilesApplicationContext applicationContext;

    /**
     * The locale resolver object.
     *
     * @since 2.2.1
     */
    protected LocaleResolver localeResolver;

    /** {@inheritDoc} */
    public void setApplicationContext(TilesApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Sets the locale resolver to use.
     *
     * @param localeResolver The locale resolver.
     * @since 2.2.1
     */
    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    /**
     * Sets the definition DAO to use. It must be locale-based.
     *
     * @param definitionDao The definition DAO.
     * @since 2.2.1
     */
    public void setDefinitionDAO(DefinitionDAO<Locale> definitionDao) {
        this.definitionDao = definitionDao;
    }

    /** {@inheritDoc} */
    public Definition getDefinition(String name,
            TilesRequestContext tilesContext) {
        Locale locale = null;

        if (tilesContext != null) {
            locale = localeResolver.resolveLocale(tilesContext);
        }

        return definitionDao.getDefinition(name, locale);
    }

    /** {@inheritDoc} */
    @Deprecated
    public void addSource(Object source) {
        throw new UnsupportedOperationException(
                "The addSource method is not supported");
    }

    /** {@inheritDoc} */
    @Deprecated
    public Definitions readDefinitions() {
        throw new UnsupportedOperationException(
                "The readDefinitions method is not supported");
    }

    /** {@inheritDoc} */
    @Deprecated
    public void init(Map<String, String> params) {
        // It's here only for binary compatibility reasons.
    }
}
