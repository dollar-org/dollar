/*
 * Copyright (c) 2014 Neil Ellis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.neilellis.dollar.http;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.neilellis.dollar.DollarStatic;
import me.neilellis.dollar.Pipeable;
import me.neilellis.dollar.types.DollarFactory;
import me.neilellis.dollar.types.SerializedType;
import me.neilellis.dollar.uri.URI;
import me.neilellis.dollar.uri.URIHandler;
import me.neilellis.dollar.var;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.neilellis.dollar.DollarStatic.$;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class HttpURIHandler implements URIHandler {
    public static final int BLOCKING_TIMEOUT = 10;
    private static final ConcurrentHashMap<String, RouteableNanoHttpd> servers = new ConcurrentHashMap<>();
    private final URI uri;
    private final ConcurrentHashMap<String, String> subscriptions = new ConcurrentHashMap<>();
    private RouteableNanoHttpd httpd;
    private String method = "GET";

    public HttpURIHandler(String scheme, me.neilellis.dollar.uri.URI uri) throws URISyntaxException, IOException {
        if (uri.hasSubScheme()) {
            this.uri = URI.parse(scheme + ":" + uri.sub().sub().asString());
            this.method = this.uri.sub().scheme();
        } else {
            this.uri = uri;
        }
    }

    @Override
    public var all() {
        throw new UnsupportedOperationException();
    }

    @Override public void destroy() {
        //TODO
    }

    @Override
    public var drain() {
        throw new UnsupportedOperationException();
    }

    @Override
    public var get(var key) {
        throw new UnsupportedOperationException();
    }

    @Override public void init() {
        //TODO
    }

    @Override public void pause() {
        //TODO
    }

    @Override
    public var write(var value, boolean blocking, boolean mutating) {
        throw new UnsupportedOperationException();
    }

    @Override
    public var read(boolean blocking, boolean mutating) {
        try {
            return DollarFactory.fromStream(SerializedType.JSON, Unirest.get(uri.toString())
                                                                        .asJson().getRawBody());
        } catch (UnirestException e) {
            return DollarStatic.handleError(e, null);
        } catch (IOException e) {
            return DollarStatic.handleError(e, null);
        }
    }

    @Override
    public var remove(var key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public var removeValue(var v) {
        throw new UnsupportedOperationException();
    }

    public var set(var key, var value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override public void start() {
        //TODO
    }

    @Override public void stop() {
        //TODO
    }

    @Override
    public void subscribe(Pipeable consumer, String id) throws IOException {
        httpd = getHttpServerFor(this.uri.host(), this.uri.port() > 0 ? this.uri.port() : 80);
        final String path = this.uri.path();
        httpd.handle(path, new RequestHandler(consumer));
        subscriptions.put(id, path);
    }

    @Override public void unpause() {
        //TODO
    }

    @Override public void unsubscribe(String subId) {
        httpd.remove(subscriptions.get(subId));
    }

    private static RouteableNanoHttpd getHttpServerFor(String hostname, int port) throws IOException {
        String key = hostname + ":" + port;
        if (servers.containsKey(key)) {
            return servers.get(key);
        } else {
            RouteableNanoHttpd nanoHttpd = new RouteableNanoHttpd(hostname, port);
            servers.putIfAbsent(key, nanoHttpd);
            nanoHttpd.start();
            return nanoHttpd;
        }
    }

    public static class RouteableNanoHttpd extends NanoHttpdServer {

        private final Map<String, RequestHandler> handlers = new HashMap<>();

        public RouteableNanoHttpd(String hostname, int port) {
            super(hostname, port);
        }

        public void handle(String key, RequestHandler handler) {
            handlers.put(key, handler);
        }

        public void remove(String key) {
            handlers.remove(key);
        }

        @Override
        public Response serve(IHTTPSession session) {
            URI uri;
            uri = URI.parse(session.getUri());
            RequestHandler requestHandler = handlers.get(uri.path());
            if (requestHandler == null) {
                return new Response(Response.Status.NOT_FOUND, "text/plain", "");
            }
            return requestHandler.invoke(session);
        }

    }

    public class RequestHandler {
        private final Pipeable consumer;

        public RequestHandler(Pipeable consumer) {
            this.consumer = consumer;
        }

        public NanoHttpdServer.Response invoke(NanoHttpdServer.IHTTPSession session) {
            try {
                var in = $()
                        .$set($("headers"), session.getHeaders())
                        .$set($("params"), session.getParms())
                        .$set($("uri"), session.getUri())
                        .$set($("query"), session.getQueryParameterString())
                        .$set($("method"), session.getMethod().name())
                        .$set($("body"), "");
//                session.getInputStream().close();
                var out = consumer.pipe(in);
                var body = out.$("body");
                NanoHttpdServer.Response
                        response =
                        new NanoHttpdServer.Response(new NanoHttpdServer.Response.IStatus() {
                    @Override
                    public String getDescription() {
                        return out.$("reason").$default($("")).S();
                    }

                    @Override
                    public int getRequestStatus() {
                        return out.$("status").$default($(200)).I();
                    }
                        }, body.$mimeType().$S(), body.toStream());
                out.$("headers").$map().forEach((s, v) -> response.addHeader(s, v.$S()));
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return new NanoHttpdServer.Response(NanoHttpdServer.Response.Status.INTERNAL_ERROR, "text/plain",
                                                    e.getMessage());
            }
        }
    }
}
