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

package com.github.core.handlers;

import com.github.core.mux.Muxer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Netty Channel handler that muxes incoming http requests before handling them.
 */
public class RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Muxer muxer;

    /**
     * Creates a new instance of RequestHandler with the provided {@link com.github.core.mux.Muxer}
     * @param muxer The {@link com.github.core.mux.Muxer} to use for muxing incoming requests.
     */
    public RequestHandler(Muxer muxer) {
        this.muxer = muxer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (!req.getDecoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        HttpMethod method = req.getMethod();
        System.out.println(req.getDecoderResult().toString());
        String resource = req.getUri().split("\\?")[0];
        HttpHandler handler = muxer.mux(resource, method);
        if (handler == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        FullHttpResponse resp = handler.handle(req);
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext context, HttpResponseStatus status) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(String.valueOf(status), CharsetUtil.UTF_8));
        resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        context.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
