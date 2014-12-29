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

package me.neilellis.dollar.script.operators;

import me.neilellis.dollar.Type;
import me.neilellis.dollar.TypePrediction;
import me.neilellis.dollar.script.*;
import me.neilellis.dollar.script.exceptions.DollarScriptException;
import me.neilellis.dollar.var;
import org.codehaus.jparsec.Token;
import org.codehaus.jparsec.functors.Map;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static me.neilellis.dollar.DollarStatic.*;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class AssignmentOperator implements Map<Token, Map<? super var, ? extends var>> {
    private final Scope scope;
    private boolean pure;

    public AssignmentOperator(Scope scope, boolean push, boolean pure) {
        this.scope = scope;
        this.pure = pure;
    }

    public Map<? super var, ? extends var> map(Token token) {
        Type type;
        Object[] objects = (Object[]) token.value();
        var constraint;
        final String constraintSource;
        if (objects[3] instanceof var) {
            constraintSource = ((var) objects[3])._source().getSourceSegment();
        } else {
            constraintSource = null;
        }
        final SourceSegmentValue source = new SourceSegmentValue(scope, token);
        if (objects[2] != null) {
            type = Type.valueOf(objects[2].toString().toUpperCase());
            constraint = DollarScriptSupport.wrapLambda(source, scope, i -> {
                return $(scope.getDollarParser().currentScope().getParameter("it")
                              .is(type) &&
                         (objects[3] == null || ((var) objects[3]).isTrue()));
            }, Arrays.asList(), "constraint");
        } else {
            type = null;
            constraint = (var) objects[3];

        }
        boolean constant;
        boolean isVolatile;
        final Object mutability = objects[1];
        constant = mutability != null && mutability.toString().equals("const");
        isVolatile = mutability != null && mutability.toString().equals("volatile");
        if (((var) objects[4]).getMetaAttribute("__builtin") != null) {
            throw new DollarParserException("The variable '" +
                                            objects[4] +
                                            "' cannot be assigned as this name is the name of a builtin function.");
        }
        final String varName = objects[4].toString();

        return new Map<var, var>() {

            public var map(var rhs) {
                final TypePrediction prediction = rhs._predictType();
                if (type != null && prediction != null) {
                    final Double probability = prediction.probability(type);
                    if (probability < 0.5 && !prediction.empty()) {
                        System.err.println("Type assertion may fail, expected " +
                                           type +
                                           " most likely type is " +
                                           prediction.probableType() +
                                           " (" +
                                           (int) (prediction.probability(prediction.probableType()) * 100) +
                                           "%) at " +
                                           source.getSourceMessage());
                    }
                }

                final String operator = objects[5].toString();

                if (operator.equals("?=") || operator.equals("*=")) {
                    var useConstraint;
                    if (constraint != null) {
                        useConstraint = constraint;
                    } else {
                        useConstraint = scope.getConstraint(varName);
                    }
                    if (operator.equals("?=")) {
                        scope.set(varName, $void(), false, null, constraintSource, isVolatile, false, pure);
                        Callable<var> callable = () -> $($(rhs.$listen(
                                i -> scope.set(varName, fix(i[0], false), false,
                                               useConstraint, constraintSource, isVolatile, false, pure))));
                        return DollarScriptSupport.toLambda(scope, callable, source, Arrays.asList(rhs),
                                                            "listen-assign");

                    } else if (operator.equals("*=")) {
                        scope.set(varName, $void(), false, null, constraintSource, true, true, pure);
                        Callable<var> callable = () -> $(rhs.$subscribe(
                                i -> scope.set(varName, fix(i[0], false), false,
                                               useConstraint, constraintSource, true, false, pure)));
                        return DollarScriptSupport.toLambda(scope, callable, source, Arrays.asList(rhs),
                                                            "subscribe-assign");
                    }
                }
                return assign(rhs, objects, constraint, constant, isVolatile, source, constraintSource);
            }
        };
    }

    private var assign(var rhs, Object[] objects, var constraint, boolean constant, boolean isVolatile,
                       SourceSegment source,
                       String constraintSource) {

        final String varName = objects[4].toString();
        Callable<var> callable = () -> {
            var useConstraint;
            if (constraint != null) {
                useConstraint = constraint;
            } else {
                useConstraint = scope.getConstraint(varName);
            }
            return scope.getDollarParser().inScope(pure, "assignment-constraint", scope, newScope -> {
                final var rhsFixed = rhs._fix(1, false);
                if (useConstraint != null) {
                    newScope.setParameter("it", rhsFixed);
                    newScope.setParameter("previous", scope.get(varName));
                    if (useConstraint.isFalse()) {
//                        System.out.println(rhsFixed.toDollarScript());
//                        System.out.println(useConstraint.isFalse());
                        newScope.handleError(new DollarScriptException(
                                "Constraint failed for variable " + varName + ""));
                    }
                }
                if (objects[0] != null) {
                    scope.getDollarParser().export(varName, rhsFixed);
                }
                return scope.set(varName, rhsFixed, constant,
                                 constraint, constraintSource, isVolatile, constant, pure);
            });
        };
        return DollarScriptSupport.toLambda(scope, callable, source, Arrays.asList(rhs), "assignment");
    }
}
