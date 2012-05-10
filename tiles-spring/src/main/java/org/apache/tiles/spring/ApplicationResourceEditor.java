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

import java.beans.PropertyEditorSupport;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * {@link java.beans.PropertyEditor Editor} for {@link ApplicationResource}
 * descriptors, to automatically convert <code>String</code> locations
 * e.g. <code>"file:C:/myfile.txt"</code> or
 * <code>"classpath:myfile.txt"</code>) to <code>ApplicationResource</code>
 * properties instead of using a <code>String</code> location property.
 *
 * <p>The path may contain <code>${...}</code> placeholders,
 * to be resolved as system properties: e.g. <code>${user.dir}</code>.
 * Unresolvable placeholder are ignored by default.
 *
 * <p>Delegates to a {@link ApplicationContext} to do the heavy lifting.
 *
 * @see ApplicationResource
 * @see ApplicationContext
 */
public class ApplicationResourceEditor extends PropertyEditorSupport {

    /** the <code>ApplicationResourceEditor</code> to use. */
    private ApplicationContext applicationContext;

    /**
     * whether to ignore unresolvable placeholders if no corresponding
     * system property could be found.
     */
    private boolean            ignoreUnresolvablePlaceholders;

    /**
     * Create a new instance of the {@link ApplicationResourceEditor} class
     * using the given {@link ApplicationContext}.
     * @param applicationContext the <code>ApplicationContext</code> to use
     */
    public ApplicationResourceEditor(ApplicationContext applicationContext) {
        this(applicationContext, true);
    }

    /**
     * Create a new instance of the {@link ApplicationResourceEditor} class
     * using the given {@link ApplicationResourceEditor}.
     * @param applicationContext the <code>ApplicationResourceEditor</code> to use
     * @param ignoreUnresolvablePlaceholders whether to ignore unresolvable placeholders
     * if no corresponding system property could be found
     */
    public ApplicationResourceEditor(ApplicationContext applicationContext, boolean ignoreUnresolvablePlaceholders) {
        this.applicationContext = applicationContext;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    @Override
    public void setAsText(String text) {
        if (StringUtils.hasText(text)) {
            String locationToUse = resolvePath(text).trim();
            setValue(this.applicationContext.getResource(locationToUse));
        } else {
            setValue(null);
        }
    }

    /**
     * Resolve the given path, replacing placeholders with corresponding system
     * property values if necessary.
     *
     * @param path the original file path
     * @return the resolved file path
     * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders
     */
    protected String resolvePath(String path) {
        return SystemPropertyUtils.resolvePlaceholders(path, this.ignoreUnresolvablePlaceholders);
    }

    @Override
    public String getAsText() {
        ApplicationResource value = (ApplicationResource) getValue();
        return (value != null ? value.getLocalePath() : "");
    }

}
