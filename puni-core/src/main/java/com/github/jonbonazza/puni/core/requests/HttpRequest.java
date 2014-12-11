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

package com.github.jonbonazza.puni.core.requests;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for extending the functionality of DefaultFullHttpRequest.
 * This class adds the ability to retrieve an http request's form data and uri components.
 */
public class HttpRequest extends DefaultFullHttpRequest {

    protected Map<String, String> formData = new HashMap<>();
    protected String[] uriComponents;

    public HttpRequest(FullHttpRequest req) {
        super(req.getProtocolVersion(), req.getMethod(),
                req.getUri(), req.content().copy());
        String[] uriParts = getUri().split("\\?");
        uriComponents = uriParts[0].split("/");
        uriComponents = Arrays.copyOfRange(uriComponents, 1, uriComponents.length);
        if (uriParts.length > 1)
            parseFormData(uriParts[1]);
    }

    protected void parseFormData(String queryParamSection) {
        String[] queryParams = queryParamSection.split("&");
        for (String param : queryParams) {
            String[] paramComponents = param.split("=");
            if (paramComponents.length == 2)
                formData.put(paramComponents[0], paramComponents[1]);
        }
    }

    public Map<String, String> getFormData() {
        return formData;
    }

    public String[] getUriComponents() {
        return uriComponents;
    }
}
