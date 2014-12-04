package com.github.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * Created by bonazza on 12/2/14.
 */
public class Muxer extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Map<HttpMethod, Map<String, HttpHandler>> methodMap = new HashMap<>();

    public Muxer() {
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
    public void handle(HttpMethod method, String path, HttpHandler handler) {
        methodMap.get(method).put(path, handler);
    }

    private HttpHandler mux(String url, HttpMethod method) {
        Map<String, HttpHandler> handlerMap = methodMap.get(method);
        for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
            if (Pattern.matches(entry.getKey(), url))
                return entry.getValue();
        }

        return null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (req.getDecoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        HttpMethod method = req.getMethod();
        HttpHandler handler = mux(req.getUri(), method);
        if (handler == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        handler.handle(req, resp);

        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext context, HttpResponseStatus status) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(String.valueOf(status), CharsetUtil.UTF_8));
        resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        context.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
