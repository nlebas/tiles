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

package org.apache.tiles.test.db;

import java.util.Locale;

import javax.sql.DataSource;

import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.LocaleDefinitionsFactory;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.startup.DefaultTilesInitializer;

/**
 * Test Tiles initializer for Tiles initialization of the db-based container.
 *
 * @version $Rev$ $Date$
 */
public class TestDbTilesInitializer extends DefaultTilesInitializer {

    /** {@inheritDoc} */
    @Override
    protected String getContainerKey(
            ApplicationContext applicationContext) {
        return "db";
    }

    /** {@inheritDoc} */
    @Override
    protected DefinitionDAO<Locale> createLocaleDefinitionDao(ApplicationContext applicationContext) {
        LocaleDbDefinitionDAO definitionDao = new LocaleDbDefinitionDAO();
        definitionDao.setDataSource((DataSource) applicationContext
                .getApplicationScope().get("dataSource"));
        return definitionDao;
    }

    @Override
    protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext) {
        LocaleDefinitionsFactory factory = new LocaleDefinitionsFactory();
        factory.setLocaleResolver(new DefaultLocaleResolver());
        factory.setDefinitionDAO(createLocaleDefinitionDao(applicationContext));
        return factory;
    }
}