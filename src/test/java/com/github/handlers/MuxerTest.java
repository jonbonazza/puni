package com.github.handlers;

import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * Created by bonazza on 12/3/14.
 */
public class MuxerTest {

    private Muxer muxer;

    @Before
    public void setupTest() {
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        Map<HttpMethod, Map<String, HttpHandler>> methodMap = new HashMap<>();
        Map<String, HttpHandler> handlerMap = new HashMap<>();
        handlerMap.put("/test/.*", handler);
        methodMap.put(HttpMethod.GET, handlerMap);
        muxer = new Muxer(methodMap);
    }
    @Test
    public void testMux() {
        HttpHandler handler = muxer.mux("/test/test", HttpMethod.GET);
        assertNotNull(handler);

        handler = muxer.mux("/test", HttpMethod.GET);
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
