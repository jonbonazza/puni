package com.github.config;

/**
 *
 * Created by bonazza on 12/2/14.
 */
public class AppConfiguration {

    private String host = "0.0.0.0";
    private Integer port = 8080;
    private SSLConfiguration ssl = new SSLConfiguration();


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public SSLConfiguration getSsl() {
        return ssl;
    }

    public void setSsl(SSLConfiguration ssl) {
        this.ssl = ssl;
    }
}
