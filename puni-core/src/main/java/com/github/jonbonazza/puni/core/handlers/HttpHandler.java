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

package com.github.jonbonazza.puni.core.handlers;

import com.github.jonbonazza.puni.core.requests.HttpRequest;
import com.github.jonbonazza.puni.core.requests.HttpResponse;

/**
 * Interface for handling HTTP requests. Developers should create implementations of HttpHandler
 * and pass them to a {@link com.github.jonbonazza.puni.core.mux.Muxer} for routing.
 */
public interface HttpHandler {

    /**
     * Handle an incoming HTTP request. This method should be overridden to provide application specific logic.
     * @param req The request to be processed.
     * @return The response that will be sent back to the client.
     */
    public HttpResponse handle(HttpRequest req);
}
