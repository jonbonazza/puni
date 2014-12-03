package com.github.handlers;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 *
 * Created by bonazza on 12/2/14.
 */
public interface HttpHandler {

    public void get(FullHttpRequest req, FullHttpResponse resp);

    public void post(FullHttpRequest req, FullHttpResponse resp);

    public void put(FullHttpRequest req, FullHttpResponse resp);

    public void delete(FullHttpRequest req, FullHttpResponse resp);

    public void patch(FullHttpRequest req, FullHttpResponse resp);
}
