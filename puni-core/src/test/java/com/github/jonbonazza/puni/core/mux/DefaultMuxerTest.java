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

package com.github.jonbonazza.puni.core.mux;

import com.github.jonbonazza.puni.core.handlers.HttpHandler;
import com.github.jonbonazza.puni.core.requests.HttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DefaultMuxerTest {

    private DefaultMuxer muxer;

    @Before
    public void setupTest() {
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        Map<HttpMethod, Map<String, HttpHandler>> methodMap = new HashMap<>();
        Map<String, HttpHandler> handlerMap = new HashMap<>();
        handlerMap.put("/test/.*", handler);
        methodMap.put(HttpMethod.GET, handlerMap);
        muxer = new DefaultMuxer(methodMap);
    }
    @Test
    public void testMux() {
        HttpRequest request = new HttpRequest(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                HttpMethod.GET, "/test/test"));
        HttpHandler handler = muxer.mux(request);
        assertNotNull(handler);

        request.setUri("/test");
        handler = muxer.mux(request);
        assertNull(handler);
    }

    @Test
    public void testHandle() {
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        muxer.handle(HttpMethod.GET, "/test", handler);

        Map<String, HttpHandler> getMap = muxer.getMethodMap().get(HttpMethod.GET);
        assertNotNull(getMap.get("/test"));
    }
}
