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

package com.github.jonbonazza.puni.core.config;

/**
 * Base application configuration. Apps should subclass this class and provide further,
 * application specific configuration options.
 */
public class AppConfiguration {

    /**
     * The address to bind the application to. Defaults to 0.0.0.0
     */
    private String bindAddress = "0.0.0.0";

    /**
     * The port to bind the application to. Defaults to 8080
     */
    private Integer port = 8080;

    /**
     * The number of threads to use for the EventLoopGroup. Defaults to 5.
     */
    private Integer eventLoopThreadCount = 5;

    /**
     * {@link SSLConfiguration} instance containing SSL related configuration options.
     */
    private SSLConfiguration ssl = new SSLConfiguration();


    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getEventLoopThreadCount() {
        return eventLoopThreadCount;
    }

    public void setEventLoopThreadCount(Integer eventLoopThreadCount) {
        this.eventLoopThreadCount = eventLoopThreadCount;
    }

    public SSLConfiguration getSsl() {
        return ssl;
    }

    public void setSsl(SSLConfiguration ssl) {
        this.ssl = ssl;
    }
}
