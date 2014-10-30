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

import me.neilellis.dollar.types.DollarFactory;
import me.neilellis.dollar.var;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map2;

import java.util.function.Supplier;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class ScopedVarBinaryOperator implements Binary<var>, Operator {
    private final boolean immediate;
    private Map2<var, var, var> function;
    private ScriptScope scope;
    private Supplier<String> source;


    public ScopedVarBinaryOperator(Map2<var, var, var> function, ScriptScope scope) {
        this.function = function;
        this.scope = scope;
        this.immediate = false;
    }

    public ScopedVarBinaryOperator(boolean immediate, Map2<var, var, var> function, ScriptScope scope) {
        this.immediate = immediate;
        this.function = function;
        this.scope = scope;
    }

    @Override
    public var map(var var, var var2) {
        try {
            if (immediate) {
                return function.map(var, var2);
            }
            //Lazy evaluation
            return DollarFactory.fromLambda(v -> {
                try {
                    return function.map(var, var2);
                } catch (AssertionError e) {
                    throw new AssertionError(e + " at '" + source.get() + "'", e);
                }
            });
        } catch (AssertionError e) {
            throw new AssertionError(e + " at '" + source.get() + "'");
        }
    }

    @Override
    public void setSource(Supplier<String> source) {
        this.source = source;
    }
}
