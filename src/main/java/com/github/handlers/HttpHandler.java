package com.github.handlers;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 *
 * Created by bonazza on 12/2/14.
 */
public interface HttpHandler {

    public void handle(FullHttpRequest req, FullHttpResponse resp);
}
