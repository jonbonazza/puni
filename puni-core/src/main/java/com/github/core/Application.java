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

package com.github.core;

import com.github.core.config.AppConfiguration;
import com.github.core.exceptions.NoConfigurationException;
import com.github.core.mux.DefaultMuxer;
import com.github.core.mux.Muxer;
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
 * Abstract base class for puni applications. Developers should subclass this class to provide application specific
 * logic for {@link Application#configure}.
 *
 */
public abstract class Application<T extends AppConfiguration> {

    private Class<T> configType;
    private T config;
    private Muxer muxer = new DefaultMuxer();

    private boolean bootstrapped = false;

    /**
     * Creates a new Application instance using the configType class for configuration.
     * @param configType The class to unmarshal configuration to.
     */
    public Application(Class<T> configType) {
        this.configType = configType;
    }

    private void bootstrap() throws Exception {
        SslContext sslContext = null;
        if (config.getSsl().isEnabled()) {
            sslContext = SslContext.newClientContext(new File(config.getSsl().getCert()));
        }

        EventLoopGroup eventGroup = new NioEventLoopGroup(config.getEventLoopThreadCount());

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(eventGroup).channel(NioServerSocketChannel.class).
                    handler(new LoggingHandler(LogLevel.INFO)).
                    childHandler(new HttpInitializer(sslContext, muxer));
            Channel ch = b.bind(config.getPort()).sync().channel();
            ch.closeFuture().sync();
            bootstrapped = true;
        } finally {
            eventGroup.shutdownGracefully();
        }
    }

    /**
     * Loads YAML configuration from file, unmarshalling it into the provided class type.
     * @param file The file to load the configuration from. This must be called before
     * {@link Application#start} is called.
     * @throws IOException if something went wrong during loading.
     */
    public void loadConfiguration(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            config = yaml.loadAs(is, configType);
        }
    }

    /**
     * Starts the application server. {@link Application#loadConfiguration} must already have been called
     * or a {@link com.github.core.exceptions.NoConfigurationException} will be thrown.
     * @throws Exception if something goes wrong during the startup process. If an exception is thrown, the server was
     * not started.
     */
    public void start() throws Exception {
        if (config == null) {
            throw new NoConfigurationException();
        }

        configure(config, muxer);
        bootstrap();
    }

    /**
     * Should be overridden by subclasses to further configure the application. For example, regeristing handlers with
     * the muxer or changing the muxer all together is done here.
     * @param configuration The configuration that was loaded from file.
     * @param muxer The default muxer is passed in for convenience. Is a different muxer is to be used,
     *              this parameter can be ignored.
     * @throws Exception Implementation should throw an exception is something goes wrong.
     */
    protected abstract void configure(T configuration, Muxer muxer) throws Exception;

    /**
     * Sets the muxer to use with the application. This can only be done before the application has been bootstrapped.
     * @param muxer The new muxer that should be used by the application
     * @throws Exception if called after the application has been bootstrapped.
     */
    public void setMuxer(Muxer muxer) throws Exception {
        if (bootstrapped)
            throw new Exception("Cannot change muxer once the application has been bootstrapped");

        this.muxer = muxer;
    }
}
