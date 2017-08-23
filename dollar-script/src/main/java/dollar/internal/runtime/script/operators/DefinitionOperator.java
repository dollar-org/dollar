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

package dollar.internal.runtime.script.operators;

import com.sillelien.dollar.api.Type;
import com.sillelien.dollar.api.var;
import dollar.internal.runtime.script.Func;
import dollar.internal.runtime.script.SourceSegmentValue;
import dollar.internal.runtime.script.api.DollarParser;
import dollar.internal.runtime.script.api.Scope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jparsec.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.function.Function;

import static com.sillelien.dollar.api.DollarStatic.$;
import static com.sillelien.dollar.api.types.meta.MetaConstants.CONSTRAINT_SOURCE;
import static dollar.internal.runtime.script.DollarScriptSupport.*;
import static dollar.internal.runtime.script.SourceNodeOptions.NEW_SCOPE;
import static dollar.internal.runtime.script.SourceNodeOptions.NO_SCOPE;
import static dollar.internal.runtime.script.parser.Symbols.DEFINITION;
import static java.util.Collections.singletonList;

public class DefinitionOperator implements Function<Token, Function<? super var, ? extends var>> {
    @NotNull
    private static final Logger log = LoggerFactory.getLogger("DefinitionOperator");
    private final boolean pure;
    @NotNull
    private final DollarParser parser;
    private final boolean def;

    public DefinitionOperator(boolean pure, @NotNull DollarParser parser, boolean def) {
        this.pure = pure;
        this.parser = parser;
        this.def = def;
    }

    @Nullable
    public Function<? super var, ? extends var> apply(@NotNull Token token) {
        Object[] objects = (Object[]) token.value();
        Scope scope = currentScope();

        final Object exportObj = objects[1];

        return (Function<var, var>) rhs -> {
            var value;
            var variableName;
            final var typeConstraintObj;
            @Nullable var constraint;
            @Nullable String constraintSource;
            boolean readonly;

            if (def) {
                variableName = (var) objects[3];
                value = rhs;
                typeConstraintObj = (var) objects[1];
                readonly = true;

            } else {
                variableName = (var) objects[3];
                value = rhs;
                typeConstraintObj = (var) objects[2];
                readonly = objects[1] != null;
            }


            if (pure) {
                log.info("Creating pure variable {}", variableName);
            } else {
                log.info("Creating impure variable {}", variableName);
            }

            if (typeConstraintObj != null) {
                constraint = node(DEFINITION, "definition-constraint", pure, NEW_SCOPE,
                                  token, new ArrayList<>(), parser,
                                  i -> $(scope.parameter("it").is(Type.of(typeConstraintObj))));
                SourceSegmentValue meta = typeConstraintObj.meta(CONSTRAINT_SOURCE);
                if (meta != null) {
                    constraintSource = meta.getSourceSegment();
                } else {
                    constraintSource = null;
                }
            } else {
                constraint = null;
                constraintSource = null;
            }

            boolean finalReadonly = readonly;
            var node = node(DEFINITION, pure, NO_SCOPE,
                            singletonList(constrain(scope, value, constraint, constraintSource)), token, parser,
                            i -> Func.definitionFunc(token, (exportObj != null), value, variableName, constraint, constraintSource,
                                                     parser, pure, finalReadonly)
            );

            node.$listen(i -> scope.notify(variableName.$S()));
            return node;
        };
    }
}
