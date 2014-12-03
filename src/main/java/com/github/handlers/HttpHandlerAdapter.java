package com.github.handlers;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 *
 * Created by bonazza on 12/2/14.
 */
public class HttpHandlerAdapter implements HttpHandler {
    @Override
    public void get(FullHttpRequest req, FullHttpResponse resp) {
        resp.setStatus(HttpResponseStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void post(FullHttpRequest req, FullHttpResponse resp) {
        resp.setStatus(HttpResponseStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void put(FullHttpRequest req, FullHttpResponse resp) {
        resp.setStatus(HttpResponseStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void delete(FullHttpRequest req, FullHttpResponse resp) {
        resp.setStatus(HttpResponseStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void patch(FullHttpRequest req, FullHttpResponse resp) {
        resp.setStatus(HttpResponseStatus.NOT_IMPLEMENTED);
    }
}
