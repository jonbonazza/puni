package com.github;

import com.github.handlers.Muxer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 *
 * Created by bonazza on 12/2/14.
 */
public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;
    private Muxer muxer;

    public HttpInitializer(SslContext sslContext, Muxer muxer) {
        this.sslContext = sslContext;
        this.muxer = muxer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslContext != null)
            pipeline.addLast(sslContext.newHandler(ch.alloc()));

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(muxer);
    }
}
