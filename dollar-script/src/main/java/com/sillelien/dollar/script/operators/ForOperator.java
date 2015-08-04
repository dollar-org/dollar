/*
 * Copyright (c) 2014-2015 Neil Ellis
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

package com.sillelien.dollar.script.operators;

import com.sillelien.dollar.api.var;
import com.sillelien.dollar.script.DollarScriptSupport;
import com.sillelien.dollar.script.SourceSegmentValue;
import com.sillelien.dollar.script.api.DollarParser;
import com.sillelien.dollar.script.api.Scope;
import org.codehaus.jparsec.Token;
import org.codehaus.jparsec.functors.Map;
import org.jetbrains.annotations.NotNull;

import static com.sillelien.dollar.api.DollarStatic.fix;

public class ForOperator implements Map<Token, Map<? super var, ? extends var>> {
    private final Scope scope;
    private final DollarParser dollarParser;
    private final boolean pure;

    public ForOperator(DollarParser dollarParser, Scope scope, boolean pure) {
        this.dollarParser = dollarParser;
        this.scope = scope;
        this.pure = pure;
    }

    public Map<? super var, ? extends var> map(@NotNull Token token) {
        Object[] objects = (Object[]) token.value();
        String constraintSource = null;
        return rhs -> {
            return DollarScriptSupport.wrapReactive(scope, () -> {
                return dollarParser.inScope(pure, "for", scope, newScope -> {
                    return ((var) objects[3]).$each(i -> {
                        newScope.set(objects[1].toString(), fix(i[0], false), false, null, constraintSource, false,
                                     false,
                                     pure);
                        return rhs._fixDeep(false);
                    });
                });
            }, new SourceSegmentValue(scope, token), "for", rhs);
        };
    }
}