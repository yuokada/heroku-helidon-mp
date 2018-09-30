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
import static io.helidon.config.ConfigSources.file;

import io.helidon.config.Config;
import io.helidon.microprofile.server.Server;
import io.helidon.webserver.ServerConfiguration;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.LogManager;

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
        Optional<Integer> port = config.get("PORT").asOptional(Integer.class);

        ServerConfiguration serverConfiguration = ServerConfiguration.builder()
            .port(port.get()).build();

        // Server will automatically pick up configuration from
        // microprofile-config.properties
        Server server = Server.builder().port(port.get()).build().start();
        return server;
//        Server server = Server.create();
//        server.start();
//        return server;
    }

    private static Config readConfig() {
        /*
            public static void main(String[] args) {
        Optional<String> optionalPort = Optional.ofNullable(System.getenv("PORT"));
        optionalPort.ifPresent(p -> {
            int port = Integer.parseInt(p);
            setPort(port);
        });
         */
        return Config.builder().sources(
            environmentVariables(),
            file("conf/dev.yaml").optional()
        ).build();
    }
}
