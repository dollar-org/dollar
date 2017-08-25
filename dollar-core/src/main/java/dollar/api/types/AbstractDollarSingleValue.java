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

package dollar.api.types;

import dollar.api.DollarStatic;
import dollar.api.collections.ImmutableList;
import dollar.api.collections.ImmutableMap;
import dollar.api.json.ImmutableJsonObject;
import dollar.api.json.JsonObject;
import dollar.api.var;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public abstract class AbstractDollarSingleValue<T> extends AbstractDollar {
    @NotNull
    final T value;

    AbstractDollarSingleValue(@NotNull ImmutableList<Throwable> errors, @NotNull T value) {
        super(errors);
        this.value = value;
    }

    @NotNull
    @Override
    public var $containsKey(@NotNull var value) {
        return DollarStatic.$(false);
    }

    @NotNull
    @Override
    public var $get(@NotNull var rhs) {
        if (equals(rhs)) {
            return DollarFactory.wrap(this);
        } else if (rhs.integer() && (rhs.toInteger() == 0)) {
            return DollarFactory.wrap(this);
        } else {
            return DollarFactory.failure(ErrorType.INVALID_SINGLE_VALUE_OPERATION,
                                         getClass().toString(), false);
        }
    }

    @NotNull
    @Override
    public var $append(@NotNull var value) {
        return DollarFactory.fromValue(Arrays.asList(this, value), errors(), value.errors());
    }

    @NotNull
    @Override
    public var $containsValue(@NotNull var value) {
        return DollarStatic.$(this.value.equals(value));
    }

    @NotNull
    public var $has(@NotNull var key) {
        return DollarStatic.$(equals(key));
    }

    @NotNull
    @Override
    public var $isEmpty() {
        return DollarStatic.$(false);
    }

    @NotNull
    @Override
    public var $size() {
        return DollarStatic.$(1);
    }

    @NotNull
    @Override
    public int size() {
        return 1;
    }

    @NotNull
    @Override
    public var $prepend(@NotNull var value) {
        return DollarFactory.fromValue(Arrays.asList(value, this), errors(), value.errors());
    }

    @NotNull
    @Override
    public var $insert(@NotNull var value, int position) {
        return DollarFactory.failure(ErrorType.INVALID_SINGLE_VALUE_OPERATION,
                                     getClass().toString(), false);
    }


    @NotNull
    public var $removeByKey(@NotNull String value) {
        return DollarFactory.failure(ErrorType.INVALID_SINGLE_VALUE_OPERATION,
                                     getClass().toString(), false);

    }

    @NotNull
    public var $set(@NotNull var key, Object value) {
        return DollarFactory.failure(ErrorType.INVALID_SINGLE_VALUE_OPERATION);
    }

    @NotNull
    @Override
    public var $remove(@NotNull var value) {
        return DollarFactory.failure(ErrorType.INVALID_SINGLE_VALUE_OPERATION,
                                     getClass().toString(), false);
    }

    @NotNull
    @Override
    public ImmutableList<var> toVarList() {
        return ImmutableList.of(this);
    }

    @Override
    public boolean collection() {
        return false;
    }

    @NotNull
    @Override
    public ImmutableMap<var, var> toVarMap() {
        return ImmutableMap.of($("value"), this);
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    @Override
    public ImmutableList<String> toStrings() {
        return ImmutableList.of(toHumanString());
    }

    @NotNull
    @Override
    public String toHumanString() {
        return value.toString();
    }


    @NotNull
    public ImmutableJsonObject toJsonObject() {
        return new ImmutableJsonObject(new JsonObject());
    }

    @NotNull
    @Override
    public ImmutableList<Object> toList() {
        return ImmutableList.of(value);
    }

    @NotNull
    public <K extends Comparable<K>, V> ImmutableMap<K, V> toJavaMap() {
        return ImmutableMap.copyOf(Collections.singletonMap((K) "value", (V) value));
    }

    @NotNull
    @Override
    public var $plus(@NotNull var rhs) {
        return DollarFactory.failure(ErrorType.INVALID_SINGLE_VALUE_OPERATION);
    }

    @NotNull
    @Override
    public Stream<var> $stream(boolean parallel) {
        return Stream.of(this);
    }

    @NotNull
    @Override
    public var $copy() {
        return DollarFactory.fromValue(value, errors());
    }

    @Override
    public boolean singleValue() {
        return true;
    }

    @Override
    public int hashCode() {
        return value.toString().hashCode();
    }

    @NotNull
    public Stream<String> keyStream() {
        return Stream.empty();

    }


    @NotNull
    @Override
    public String toJsonString() {
        return String.valueOf((Object) toJavaObject());
    }
}
