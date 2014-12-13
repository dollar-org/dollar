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

package me.neilellis.dollar.uri.mapdb;

import me.neilellis.dollar.DollarException;
import me.neilellis.dollar.uri.URI;
import me.neilellis.dollar.uri.URIHandler;
import me.neilellis.dollar.uri.URIHandlerFactory;

import java.io.IOException;


/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class MapDBURIFactory implements URIHandlerFactory {


    @Override
    public URIHandlerFactory copy() {
        return this;
    }

    @Override
    public URIHandler forURI(String scheme, URI uri) throws IOException {
        if (uri.hasSubScheme()) {
            final URI sub = uri.sub();
            //noinspection ConstantConditions
            switch (sub.schemeString()) {
                case "map":
                    return new MapDBMapURI(scheme, sub);
                case "circular":
                    return new MapDBCircleURI(scheme, sub);
            }
        }
        throw new DollarException("Need to supply a sub scheme such as list, map, set");
    }

    @Override
    public boolean handlesScheme(String scheme) {
        return scheme.equals("db");
    }
}
