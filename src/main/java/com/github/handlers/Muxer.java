package com.github.handlers;

import com.github.handlers.HttpHandler;
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

    private Map<String, HttpHandler> handlerMap = new HashMap<>();

    public void handle(String path, HttpHandler handler) {
        handlerMap.put(path, handler);
    }

    private HttpHandler mux(String url) {
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
        HttpHandler handler = mux(req.getUri());
        if (handler == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        if (method == HttpMethod.GET) {
            handler.get(req, resp);
        } else if (method == HttpMethod.POST) {
            handler.post(req, resp);
        } else if (method == HttpMethod.PUT) {
            handler.put(req, resp);
        } else if (method == HttpMethod.PATCH) {
            handler.patch(req, resp);
        } else if (method == HttpMethod.DELETE) {
            handler.delete(req, resp);
        } else {
            sendError(ctx, HttpResponseStatus.NOT_IMPLEMENTED);
            return;
        }

        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext context, HttpResponseStatus status) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(String.valueOf(status), CharsetUtil.UTF_8));
        resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        context.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
