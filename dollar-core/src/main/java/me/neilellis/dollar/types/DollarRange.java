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

import com.google.common.collect.*;
import me.neilellis.dollar.AbstractDollar;
import me.neilellis.dollar.DollarStatic;
import me.neilellis.dollar.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class DollarRange extends AbstractDollar {

    private final Range<Long> range;

    public DollarRange(@NotNull List<Throwable> errors, long start, long finish) {
        super(errors);
        range = Range.closed(start, finish);
    }

    public DollarRange(@NotNull List<Throwable> errors, Range range) {
        super(errors);
        this.range = range;
    }

    @NotNull
    @Override
    public var $(@NotNull String age, long l) {
        return DollarFactory.failure(DollarFail.FailureType.INVALID_RANGE_OPERATION);

    }

    @NotNull @Override public var $(@NotNull String key, double value) {
        return DollarFactory.failure(DollarFail.FailureType.INVALID_RANGE_OPERATION);

    }

    @NotNull
    @Override
    public var $append(Object value) {
        return DollarFactory.failure(DollarFail.FailureType.INVALID_RANGE_OPERATION);
    }

    @NotNull
    @Override
    public Stream<var> $children() {
        return list().stream();
    }

    @NotNull
    @Override
    public Stream<var> $children(@NotNull String key) {
        return Stream.of(DollarFactory.failure(DollarFail.FailureType.INVALID_RANGE_OPERATION));
    }

    @Override
    public boolean $has(@NotNull String key) {
        return false;
    }

    @NotNull
    @Override
    public ImmutableList<var> list() {
        return ImmutableList.copyOf(ContiguousSet.create(range, DiscreteDomain.longs())
                                                 .stream()
                                                 .map(DollarStatic::$)
                                                 .collect(Collectors.toList()));
    }

    @NotNull
    @Override
    public ImmutableMap<String, var> $map() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String S(@NotNull String key) {
        return range.toString();
    }

    @NotNull
    @Override
    public var $rm(@NotNull String value) {
        return this;

    }

    @NotNull
    @Override
    public var $(@NotNull String key, Object value) {
        return DollarFactory.failure(DollarFail.FailureType.INVALID_RANGE_OPERATION);
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    @Override
    public Integer I() {
        return null;
    }

    @Override
    public Integer I(@NotNull String key) {
        return null;
    }

    @NotNull
    @Override
    public var decode() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public var $(@NotNull String key) {
        return DollarFactory.failure(DollarFail.FailureType.INVALID_RANGE_OPERATION);
    }

    @NotNull
    @Override
    public JsonObject json(@NotNull String key) {
        return new JsonObject();
    }

    @NotNull
    @Override
    public Range<Long> $() {
        return range;
    }

    @Override
    public Stream<String> keyStream() {
        return null;

    }

    @Override
    public Number number(@NotNull String key) {
        return null;
    }

    @NotNull
    @Override
    public JsonObject json() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.putArray("value", jsonArray());
        return jsonObject;

    }

    @Override
    public ImmutableList<String> strings() {
        return ImmutableList.copyOf(ContiguousSet.create(range, DiscreteDomain.longs())
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.toList()));
    }

    @Override
    public ImmutableMap<String, Object> toMap() {
        return null;
    }

    @Override
    public int hashCode() {
        return range.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof var) {
            var unwrapped = ((var) obj)._unwrap();
            if (unwrapped instanceof DollarRange) {
                return range.equals(((DollarRange) unwrapped).range);
            }
            if (unwrapped instanceof DollarList) {
                return unwrapped.list().equals(list());
            }
        }
        return false;

    }

    @Override
    public int size() {
        return (int) (range.upperEndpoint()-range.lowerEndpoint());
    }

    @Override
    public boolean containsValue(Object value) {
        if(value instanceof Number) {
            return range.contains(((Number) value).longValue());
        }
        return range.contains(Long.valueOf(value.toString()));
    }

    @NotNull
    @Override
    public var remove(Object value) {
        return DollarFactory.failure(DollarFail.FailureType.INVALID_RANGE_OPERATION);
    }

    @Override public String toString() {
        return String.format("%d..%d", range.lowerEndpoint(), range.upperEndpoint());
    }
}
