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

package com.github;

import com.github.config.AppConfiguration;
import com.github.exceptions.NoConfigurationException;
import com.github.mux.DefaultMuxer;
import com.github.mux.Muxer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * Created by bonazza on 12/2/14.
 */
public abstract class Application<T extends AppConfiguration> {

    private Class<T> configType;
    private T config;
    private Muxer muxer = new DefaultMuxer();

    private boolean bootstrapped = false;

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
            bootstrapped = true;
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

    public void setMuxer(Muxer muxer) throws Exception {
        if (bootstrapped)
            throw new Exception("Cannot change muxer once the application has been bootstrapped");

        this.muxer = muxer;
    }
}
