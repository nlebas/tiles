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

package org.apache.tiles.renderer;

import java.io.IOException;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;

/**
 *
 * @version $Rev$ $Date$
 * @since 3.1.0
 */

public class RelativeDelegateRenderer implements Renderer {

    private static final String ROOT_PATH_ATTRIBUTE = RelativeDelegateRenderer.class.getName() + ".ROOT_PATH";
    private Renderer delegate;

    /**
     * @param delegate
     */
    public RelativeDelegateRenderer(Renderer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void render(String value, Request request) throws IOException {
        if (value == null) {
            throw new NullPointerException("The attribute value is null");
        }

        String path = getAbsolutePath(value, request);
        String oldRootPath = getRootPath(request);
        setRootPath(path, request);
        try {
            delegate.render(path, request);
        } finally {
            setRootPath(oldRootPath, request);
        }
    }

    /** {@inheritDoc} */
    public boolean isRenderable(String value, Request request) {
        String path = getAbsolutePath(value, request);
        return delegate.isRenderable(path, request);
    }

    private String getAbsolutePath(String value, Request request) {
        if (!value.startsWith("/")) {
            String rootPath = getRootPath(request);
            if (rootPath != null) {
                return rootPath + value;
            }
        }
        return value;
    }

    private void setRootPath(String path, Request request) {
        String rootPath = (path == null) ? null : path.substring(0, path.lastIndexOf('/'));
        request.getContext(Request.REQUEST_SCOPE).put(ROOT_PATH_ATTRIBUTE, rootPath);
    }

    private String getRootPath(Request request) {
        String rootPath = (String) request.getContext(Request.REQUEST_SCOPE).get(ROOT_PATH_ATTRIBUTE);
        return (rootPath == null) ? null : (rootPath + "/");
    }
}
