/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yuokada.samples.helidon.mp;

import static io.helidon.config.ConfigSources.environmentVariables;

import io.helidon.config.Config;
import io.helidon.microprofile.server.Server;
import io.helidon.security.Security;
import io.helidon.security.SecurityContext;
import io.helidon.security.SecurityEnvironment;
import io.helidon.security.provider.httpauth.HttpBasicAuthProvider;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.LogManager;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main method simulating trigger of main method of the server.
 */
public final class Main {

    private Main() {
    }

    /**
     * Application main entry point.
     *
     * @param args command line arguments
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
        startServer();
    }

    /**
     * Start the server.
     *
     * @return the created {@link Server} instance
     * @throws IOException if there are problems reading logging properties
     */
    protected static Server startServer() throws IOException {

        // load logging configuration
        LogManager.getLogManager().readConfiguration(
            Main.class.getResourceAsStream("/logging.properties"));

        Config config = readConfig();
        int port = config.get("PORT").asInt(8080);

        // Add security Provider
        Security security = Security.builder()
            .config(config)
            .addProvider(HttpBasicAuthProvider.builder().build(), "foo")
            .build();
        // create a security context
        SecurityContext context = security.contextBuilder(UUID.randomUUID().toString())
            .env(SecurityEnvironment.builder()
                .method("get")
                .path("/test")
                .transport("http")
                .header("Authorization", "Bearer abcdefgh")
                .build())
            .build();

        SecurityEnvironment securityEnvironment = SecurityEnvironment.builder()
            .method("GET")
            .path("/greet")
            .transport("http")
            .build();

        ResourceConfig resourceConfig = new ResourceConfig()
            // register JAX-RS resource
            .register(GreetResource.class)
            // integrate security
            .register(new io.helidon.security.jersey.SecurityFeature(security));

        // Server will automatically pick up configuration from
        // microprofile-config.properties
        Server server = Server.builder()
            .resourceConfig(resourceConfig)
            .port(port)
            .build();
        server.start();
        return server;
    }

    private static Config readConfig() {
        return Config.builder().sources(
            environmentVariables()
        ).build();
    }
}
