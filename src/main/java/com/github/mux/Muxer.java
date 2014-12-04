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

package com.github.mux;

import com.github.handlers.HttpHandler;
import io.netty.handler.codec.http.HttpMethod;

/**
 * Interface for muxing incoming requests.
 */
public interface Muxer {

    /**
     * Registers handler with the muxer for requests to path, with method
     * @param method The HTTP method that handler should be tied to.
     * @param path The resource that handler should be tied to.
     * @param handler The {@link com.github.handlers.HttpHandler} that should handle requests at method and path.
     */
    public void handle(HttpMethod method, String path, HttpHandler handler);

    /**
     * Mux the request to resource with method method.
     * @param resource The resource of the request to mux.
     * @param method The HTTP method of the request to mux.
     * @return The HttpHandler that matches the request. If no handler matches the request, null should be returned.
     */
    public HttpHandler mux(String resource, HttpMethod method);
}
