/*
 * Copyright 2014 Jon Bonazza
 *
 * Jon Bonazza licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.core.mux;

import com.github.core.handlers.HttpHandler;
import com.google.common.annotations.VisibleForTesting;
import io.netty.handler.codec.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Default {@link com.github.core.mux.Muxer} implementation using Regex.
 */
public class DefaultMuxer  implements Muxer {

    private Map<HttpMethod, Map<String, HttpHandler>> methodMap = new HashMap<>();

    public DefaultMuxer() {
        methodMap.put(HttpMethod.CONNECT, new HashMap<>());
        methodMap.put(HttpMethod.DELETE, new HashMap<>());
        methodMap.put(HttpMethod.GET, new HashMap<>());
        methodMap.put(HttpMethod.HEAD, new HashMap<>());
        methodMap.put(HttpMethod.OPTIONS, new HashMap<>());
        methodMap.put(HttpMethod.PATCH, new HashMap<>());
        methodMap.put(HttpMethod.POST, new HashMap<>());
        methodMap.put(HttpMethod.PUT, new HashMap<>());
        methodMap.put(HttpMethod.TRACE, new HashMap<>());
    }

    @VisibleForTesting
    protected DefaultMuxer(Map<HttpMethod, Map<String, HttpHandler>> methodMap) {
        this.methodMap = methodMap;
    }

    /**
     * Registers handler for muxing at resource path and method method.
     * @param method The HTTP method that handler should be tied to.
     * @param path The resource that handler should be tied to.
     * @param handler The {@link com.github.core.handlers.HttpHandler} that should handle requests at method and path.
     */
    public void handle(HttpMethod method, String path, HttpHandler handler) {
        methodMap.get(method).put(path, handler);
    }

    /**
     * Uses Regex to mux a request with resource resource and method method.
     * @param resource The resource of the request to mux.
     * @param method The HTTP method of the request to mux.
     * @return
     */
    public HttpHandler mux(String resource, HttpMethod method) {
        Map<String, HttpHandler> handlerMap = methodMap.get(method);
        for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
            if (Pattern.matches(entry.getKey(), resource))
                return entry.getValue();
        }

        return null;
    }

    @VisibleForTesting
    protected Map<HttpMethod, Map<String, HttpHandler>> getMethodMap() {
        return methodMap;
    }
}
