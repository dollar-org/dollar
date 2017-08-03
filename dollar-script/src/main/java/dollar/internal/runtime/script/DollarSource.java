/*
 *    Copyright (c) 2014-2017 Neil Ellis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package dollar.internal.runtime.script;

import com.sillelien.dollar.api.DollarException;
import com.sillelien.dollar.api.Pipeable;
import com.sillelien.dollar.api.Scope;
import com.sillelien.dollar.api.TypePrediction;
import com.sillelien.dollar.api.plugin.Plugins;
import com.sillelien.dollar.api.script.SourceSegment;
import com.sillelien.dollar.api.script.TypeLearner;
import com.sillelien.dollar.api.types.DollarLambda;
import com.sillelien.dollar.api.var;
import dollar.internal.runtime.script.api.DollarParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class DollarSource extends DollarLambda {
    @Nullable
    public static final TypeLearner typeLearner = Plugins.sharedInstance(TypeLearner.class);

    private static final Logger log = LoggerFactory.getLogger("DollarSource");


    private final SourceSegment source;
    @Nullable
    private Scope scope;
    private List<var> inputs;
    @Nullable
    private String operation;
    private volatile TypePrediction prediction;
    private DollarParser parser;

    public DollarSource(Pipeable lambda, @Nullable Scope scope, SourceSegment source, @NotNull List<var> inputs,
                        @Nullable String operation, DollarParser parser) {
        super(vars -> lambda.pipe(vars));
        this.scope = scope;
        this.parser = parser;
        if (operation == null) {
            throw new NullPointerException();
        }
        if (inputs == null) {
            throw new NullPointerException();
        }
        this.inputs = inputs;
        this.operation = operation;
        this.source = source;
        setScopes(inputs);
    }

    private void setScopes(List<var> inputs) {
        meta.put("scope", scope);
        meta.put("children", inputs);

        for (var input : inputs) {
            correctScope(input);
        }
    }

    private void correctScope(var input) {
        if (input.getMetaObject("scope") == null) {
            input.setMetaObject("scope", scope);
        } else if (input.getMetaObject("scope") != scope) {
            if (scope.getParent() != null && scope.hasParent((Scope) input.getMetaObject("scope"))) {
                System.err.println("Correcting scope " + input.getMetaObject("scope") + " to parent " + scope);
                input.setMetaObject("scope", scope);
            } else {
                System.err.println("Setting parent scope of " + input.getMetaObject("scope") + " to " + scope);
                ((Scope) input.getMetaObject("scope")).setParent(scope);
            }
        }
        input.setMetaObject("parentScope", scope);
    }

    @Nullable
    @Override
    public Object invoke(Object proxy, @NotNull Method method, Object[] args) throws Throwable {
        Scope useScope = (Scope) meta.get("scope");
        if (useScope == null) {
            useScope = this.scope;
        }
        try {
            if (Objects.equals(method.getName(), "_source")) {
                return source;
            }

            if (Objects.equals(method.getName(), "_constrain")) {
                return _constrain((var) proxy, (var) args[0], String.valueOf(args[1]));
            }
            if (Objects.equals(method.getName(), "_predictType")) {
                if (this.prediction == null) {
                    this.prediction = typeLearner.predict(operation, source, inputs);
                }
                return this.prediction;
            }
            final Object result;
            if (method.getName().startsWith("_fix")) {
                log.debug("FIXING SCOPE TO " + useScope + " for " + source.getSourceMessage());
                log.debug("meta.scope=  " + meta.get("scope"));
//                new Exception().printStackTrace(System.err);
                result = DollarScriptSupport.inScope(useScope, newScope -> {
                    try {
                        return super.invoke(proxy, method, args);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        throw new DollarException(throwable);
                    }
                });
            } else {
                result = super.invoke(proxy, method, args);
            }
            if(result instanceof var) {
                    correctScope((var) result);
            }
            if (method.getName().startsWith("_fixDeep")) {
                typeLearner.learn(operation, source, inputs, ((var) result).$type());
            }
            return result;
        } catch (AssertionError e) {
            log.warn(e.getMessage(), e);
            return parser.getErrorHandler().handle(useScope, source, e);
        } catch (DollarException e) {
            log.warn(e.getMessage(), e);
            return parser.getErrorHandler().handle(useScope, source, e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return parser.getErrorHandler().handle(useScope, source, e);
        }
    }


}
