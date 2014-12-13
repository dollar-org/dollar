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

package me.neilellis.dollar.types;

import me.neilellis.dollar.*;
import me.neilellis.dollar.collections.ImmutableList;
import me.neilellis.dollar.collections.ImmutableMap;
import me.neilellis.dollar.json.ImmutableJsonObject;
import me.neilellis.dollar.json.JsonArray;
import me.neilellis.dollar.json.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.neilellis.dollar.DollarStatic.fix;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class DollarList extends AbstractDollar {

    private final ImmutableList<var> list;

    DollarList(@NotNull ImmutableList<Throwable> errors, @NotNull JsonArray array) {
        this(errors, ImmutableList.copyOf(array.toList()));
    }


    DollarList(@NotNull ImmutableList<Throwable> errors, @NotNull ImmutableList<?> list) {
        super(errors);
        List<var> l = new ArrayList<>();
        for (Object value : list) {
            if ((value instanceof var)) {
                if (((var) value).isLambda() || !((var) value).isVoid()) {
                    l.add((var) value);
                }
            } else //noinspection StatementWithEmptyBody
                if (value == null) {
                    //Skip
                } else {
                    l.add(DollarFactory.fromValue(value, errors));
                }
        }
        this.list = ImmutableList.copyOf(l);
    }

    DollarList(@NotNull ImmutableList<Throwable> errors, @NotNull Object[] values) {
        super(errors);
        List<var> l = new ArrayList<>();
        for (Object value : values) {
            if ((value instanceof var)) {
                if (((var) value).isLambda() || !((var) value).isVoid()) {
                    l.add((var) value);
                }
            } else //noinspection StatementWithEmptyBody
                if (value == null) {
                    //Skip
                } else {
                    l.add(DollarFactory.fromValue(value, errors));
                }
        }
        this.list = ImmutableList.copyOf(l);
    }

    @NotNull
    @Override
    public var $abs() {
        return this;
    }

    @NotNull
    @Override
    public var $minus(@NotNull var v) {
        ArrayList<var> newVal = new ArrayList<>(list.mutable());
        newVal.remove(v);
        return DollarFactory.fromValue(newVal, errors());
    }

    @NotNull
    @Override
    public var $divide(@NotNull var v) {
        return DollarFactory.failure(FailureType.INVALID_LIST_OPERATION);
    }

    @NotNull
    @Override
    public var $plus(var v) {
        return DollarFactory.fromValue(ImmutableList.copyOf(list, v == null ? ImmutableList.of() : v.$list()), errors()
        );

    }

    @NotNull
    @Override
    public var $modulus(@NotNull var v) {
        return DollarFactory.failure(FailureType.INVALID_LIST_OPERATION);
    }

    @NotNull
    @Override
    public var $multiply(@NotNull var v) {
        return DollarFactory.failure(FailureType.INVALID_LIST_OPERATION);
    }

    @NotNull
    @Override
    public var $negate() {
        ArrayList<var> result = new ArrayList<var>(list.mutable());
        Collections.reverse(result);
        return DollarFactory.fromValue(result, errors());
    }


    @Override
    public var $listen(Pipeable pipe) {
        String key = UUID.randomUUID().toString();
        $listen(pipe, key);
        return DollarStatic.$(key);
    }

    @Override
    public var $listen(Pipeable pipe, String key) {
        for (var v : list) {
            //Join the children to this, so if the children change
            //listeners to this get the latest value of this.
            v.$listen(i -> this, key);
        }
        return DollarStatic.$(key);
    }

    @NotNull
    @Override
    public var $set(@NotNull var key, Object value) {
        ArrayList<var> newVal = new ArrayList<>(list.mutable());
        if (key.isInteger()) {
            newVal.set(key.I(), DollarFactory.fromValue(value));
        } else {
            return DollarFactory.failure(FailureType.INVALID_LIST_OPERATION);
        }
        return DollarFactory.fromValue(newVal, errors());
    }

    @NotNull
    @Override
    public <R> R $() {
        return (R) jsonArray();
    }

    @NotNull
    @Override
    public var $get(@NotNull var key) {
        if (key.isNumber()) {
            return list.get(key.I());
        }
        for (var var : list) {
            if (var.equals(key)) {
                return var;
            }
        }
        return DollarStatic.$void();
    }

    @NotNull @Override
    public var $containsValue(@NotNull var value) {
        return DollarStatic.$(list.contains(fix(value, false)));
    }

    @NotNull @Override
    public var $has(@NotNull var key) {
        return $containsValue(key);
    }

    @NotNull @Override
    public var $size() {
        return DollarStatic.$(list.size());
    }

    @NotNull
    @Override
    public var $removeByKey(@NotNull String value) {
        return DollarFactory.failure(FailureType.INVALID_LIST_OPERATION);
    }

    @NotNull
    @Override
    public ImmutableMap<String, var> $map() {
        return null;
    }

    @NotNull
    @Override
    public JSONObject orgjson() {
        return new JSONObject(json().toMap());
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public ImmutableJsonObject json() {
        JsonArray array = jsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.putArray("value", array);
        return new ImmutableJsonObject(jsonObject);
    }

    @NotNull
    @Override
    public var $remove(var value) {
        List<var> newList = list.stream().filter(val -> !val.equals(value)).collect(Collectors.toList());
        return DollarFactory.fromValue(newList, errors());
    }

    @Override public var $write(var value, boolean blocking, boolean mutating) {
        return $plus(value);
    }

    @Override
    public var $notify() {
        list.forEach(me.neilellis.dollar.var::$notify);
        return this;
    }

    @NotNull
    @Override
    public Stream<var> $stream(boolean parallel) {
        Stream<var> stream;
        if (parallel) {
            stream = list.stream().parallel();
        } else {
            stream = list.stream();
        }
        return stream;
    }

    @NotNull
    @Override
    public var $copy() {
        return DollarFactory.fromValue(list.stream().map(var::$copy).collect(Collectors.toList()), errors());
    }

    @Override public var _fix(int depth, boolean parallel) {
        if (depth <= 1) {
            return this;
        } else {
            ImmutableList<var> result;
            if (parallel) {
                try {
                    result =
                            ImmutableList.copyOf(Execution.forkJoinPool.submit(
                                    () -> $stream(parallel).map(v -> v._fix(depth - 1, parallel)).collect(
                                            Collectors.toList())).get());

                } catch (InterruptedException e) {
                    Thread.interrupted();
                    result = ImmutableList.<var>of(DollarFactory.failure(FailureType.INTERRUPTED, e));

                } catch (ExecutionException e) {
                    result =
                            ImmutableList.of(
                                    DollarFactory.failure(FailureType.EXECUTION_FAILURE, e.getCause()));

                }
                return new DollarList(errors(), result);
            } else {
                return new DollarList(errors(),
                                      ImmutableList.copyOf($stream(parallel).map(v -> v._fix(depth - 1, parallel))
                                                                            .collect(Collectors.toList())));
            }
        }

    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof List) {
            return list.equals(obj);
        }
        if (obj instanceof var) {
            return list.equals(((var) obj).$list());
        }
        return false;
    }

    @NotNull
    @Override
    public Stream<Map.Entry<String, var>> kvStream() {
        return null;
    }

    @Override
    public int compareTo(@NotNull var o) {
        //TODO: improve comparisons
        if (list.stream().allMatch(v -> v.compareTo(o) == -1)) {
            return -1;
        }
        if (list.stream().allMatch(v -> v.compareTo(o) == 1)) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    @Override
    public boolean isTruthy() {
        return !list.isEmpty();
    }

    @Override
    public boolean isFalse() {
        return false;
    }

    @Override
    public boolean isNeitherTrueNorFalse() {
        return true;
    }

    @NotNull
    @Override
    public ImmutableList<var> $list() {
        try {
            return ImmutableList.copyOf(
                    Execution.forkJoinPool.submit(() -> $stream(false).map(v -> v._fix(false)).collect(
                            Collectors.toList())).get());
        } catch (InterruptedException e) {
            Thread.interrupted();
            return ImmutableList.of(DollarFactory.failure(FailureType.INTERRUPTED, e));

        } catch (ExecutionException e) {
            return ImmutableList.of(DollarFactory.failure(FailureType.EXECUTION_FAILURE, e));

        }
    }


    @NotNull
    @Override
    public ImmutableList<Object> toList() {
        List<Object> newList = new ArrayList<>();
        for (var val : list) {
            newList.add(val.$());
        }
        return ImmutableList.copyOf(newList);

    }


    @Override
    public boolean isVoid() {
        return false;
    }


    @NotNull @Override
    public Integer I() {
        return $stream(false).collect(Collectors.summingInt(TypeAware::I));
    }








    @Override
    public ImmutableList<String> strings() {
        return ImmutableList.copyOf(list.stream().map(Object::toString).collect(Collectors.toList()));
    }


    @NotNull @Override
    public Map<String, Object> toMap() {
        return Collections.singletonMap("value", $list());
    }


    @NotNull
    @Override
    public Number N() {
        return 0;
    }

    @Override public boolean isCollection() {
        return true;
    }


    @Override
    public var $as(Type type) {
        switch (type) {
            case LIST:
                return this;
            case MAP:
                return DollarStatic.$(toMap());
            case STRING:
                return DollarFactory.fromStringValue(S());
            case VOID:
                return DollarStatic.$void();
            case BOOLEAN:
                return DollarStatic.$(!list.isEmpty());
            default:
                return DollarFactory.failure(FailureType.INVALID_CAST);

        }
    }


    @Override public Type $type() {
        return Type.LIST;
    }

    @NotNull
    @Override
    public String S() {
        return jsonArray().toString();
    }


    @Override
    public boolean is(@NotNull Type... types) {
        for (Type type : types) {
            if (type == Type.LIST) {
                return true;
            }
        }
        return false;
    }

}
