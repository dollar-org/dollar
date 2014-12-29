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

package me.neilellis.dollar.script;

import com.beust.jcommander.JCommander;
import me.neilellis.dollar.Configuration;
import me.neilellis.dollar.DollarStatic;

import java.io.File;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class ParserMain {

    public static void main(String[] args) throws Throwable {

        final ParserOptions options = new ParserOptions();
        new JCommander(options, args);
        File file = options.getFile();
        DollarStatic.config = new ParserConfiguration(options);
        DollarParser parser = new DollarParser(options);
        try {
            parser.parse(file, false);
        } catch (Throwable t) {
            parser.getErrorHandler().handleTopLevel(t);
        }
    }

    private static class ParserConfiguration implements Configuration {
        private final ParserOptions options;

        public ParserConfiguration(ParserOptions options) {this.options = options;}

        @Override public boolean debugScope() {
            return options.isDebugScope();
        }

        @Override public boolean failFast() {
            return !options.isTolerateErrors();
        }

        @Override public void failFast(boolean failFast) {
            options.setTolerateErrors(!failFast);
        }

        @Override public boolean production() {
            return options.isProduction();
        }

        @Override public boolean wrapForGuards() {
            return options.isSafe();
        }

        @Override public boolean wrapForMonitoring() {
            return options.isMonitor();
        }
    }
}
