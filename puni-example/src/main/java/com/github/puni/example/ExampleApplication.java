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

package com.github.puni.example;

import com.github.core.Application;
import com.github.core.config.AppConfiguration;
import com.github.core.mux.Muxer;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;

public class ExampleApplication extends Application<AppConfiguration> {

    public ExampleApplication() {
        super(AppConfiguration.class);
    }

    @Override
    protected void configure(AppConfiguration configuration, Muxer muxer) throws Exception {
        muxer.handle(HttpMethod.GET, "/hello", new HelloWorldHandler());
    }

    public static void main(String[] args) {
        ExampleApplication app = new ExampleApplication();
        try {
            app.loadConfiguration(new File(args[0]));
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

