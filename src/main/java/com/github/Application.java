package com.github;

import com.github.config.AppConfiguration;
import com.github.exceptions.NoConfigurationException;
import com.github.handlers.Muxer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

/**
 * 
 * Created by bonazza on 12/2/14.
 */
public abstract class Application<T extends AppConfiguration> {

    private Class<T> configType;
    private T config;
    private Muxer muxer = new Muxer();

    public Application(Class<T> configType) {
        this.configType = configType;
    }

    private void bootstrap() throws Exception {
        SslContext sslContext = null;
        if (config.getSsl().isEnabled()) {
            sslContext = SslContext.newClientContext(new File(config.getSsl().getCert()));
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).
                    handler(new LoggingHandler(LogLevel.INFO)).
                    childHandler(new HttpInitializer(sslContext, muxer));

            Channel ch = b.bind(config.getPort()).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void loadConfiguration(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            config = yaml.loadAs(is, configType);
        }
    }

    public void start() throws Exception {
        if (config == null) {
            throw new NoConfigurationException();
        }

        configure(config, muxer);
        bootstrap();
    }

    protected abstract void configure(T configuration, Muxer muxer) throws Exception;
}
