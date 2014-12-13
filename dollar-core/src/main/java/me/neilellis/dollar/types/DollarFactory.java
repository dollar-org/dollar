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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import me.neilellis.dollar.*;
import me.neilellis.dollar.collections.*;
import me.neilellis.dollar.exceptions.DollarFailureException;
import me.neilellis.dollar.json.DecodeException;
import me.neilellis.dollar.json.ImmutableJsonObject;
import me.neilellis.dollar.json.JsonArray;
import me.neilellis.dollar.json.JsonObject;
import me.neilellis.dollar.json.impl.Json;
import me.neilellis.dollar.monitor.DollarMonitor;
import me.neilellis.dollar.script.SourceAware;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.QueryParamsMap;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;

import static me.neilellis.dollar.DollarStatic.$;
import static me.neilellis.dollar.DollarStatic.$void;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class DollarFactory {
    public static final String VALUE_KEY = "value";
    public static final String TYPE_KEY = "$type";
    public static final String LOWERBOUND_KEY = "lower";
    public static final String UPPERBOUND_KEY = "upper";
    public static final String TEXT_KEY = "text";
    public static final String MILLISECOND_KEY = "millis";
    static DollarMonitor monitor = DollarStatic.monitor();
    @NotNull
    static StateTracer tracer = DollarStatic.tracer();

    @NotNull
    public static var fromField(@NotNull ImmutableList<Throwable> errors, Object field) {
//            return new DollarWrapper(DollarNull.INSTANCE, monitor, tracer);
//        }
//        if (field instanceof String) {
//            return new DollarWrapper(new DollarString((String) field), monitor, tracer);
//        }
//        if (field instanceof Number) {
//            return new DollarWrapper(new DollarNumber((Number) field), monitor, tracer);
//        }
//        if (field instanceof JsonObject) {
//            return DollarStatic.$(field);
//        }
//        return new DollarWrapper(DollarStatic.$(field.toString()), monitor, tracer);
        return create(errors, field);
    }


    @NotNull
    public static var fromValue(Object o, @NotNull ImmutableList<Throwable>... errors) {
        return create(ImmutableList.copyOf(errors), o);
    }

    @NotNull
    public static var fromValue() {
        return create(ImmutableList.of(), new JsonObject());
    }


    @NotNull
    private static var create(@NotNull ImmutableList<Throwable> errors, @Nullable Object o) {
        if (o == null) {
            return wrap(new DollarVoid(errors));
        }
        if (o instanceof var) {
            return (var) o;
        }
        if (o instanceof Pipeable) {
            return wrap((var) java.lang.reflect.Proxy.newProxyInstance(
                    DollarStatic.class.getClassLoader(),
                    new Class<?>[]{var.class},
                    new DollarLambda((Pipeable) o)));
        }
        if (o instanceof JsonArray) {
            return wrap(new DollarList(errors, (JsonArray) o));
        }
        if (o instanceof JsonObject) {
            return wrap(new DollarMap(errors, new ImmutableJsonObject((JsonObject) o)));
        }
        if (o instanceof JSONObject || o instanceof ObjectNode || o instanceof com.fasterxml.jackson.databind.node.ObjectNode) {
            return wrap(new DollarMap(errors, new JsonObject(o.toString())));
        }
        if (o instanceof JSONArray || o instanceof ArrayNode || o instanceof com.fasterxml.jackson.databind.node.ArrayNode) {
            return wrap(new DollarList(errors, new JsonArray(o.toString())));
        }
        if (o instanceof Map) {
            return wrap(new DollarMap(errors, (Map<String, Object>) o));
        }
        if (o instanceof QueryParamsMap) {
            return create(errors, DollarStatic.paramMapToJson(((QueryParamsMap) o).toMap()));
        }
        if (o instanceof ImmutableList) {
            return wrap(new DollarList(errors, (ImmutableList<?>) o));
        }
        if (o instanceof List) {
            return wrap(new DollarList(errors, ImmutableList.copyOf((List<Object>) o)));
        }
        if (o instanceof Collection) {
            return wrap(new DollarList(errors, ImmutableList.copyOf(new ArrayList<>((Collection<Object>) o))));
        }
        if (o.getClass().isArray()) {
            return wrap(new DollarList(errors, (Object[]) o));
        }
        if (o instanceof Boolean) {
            return wrap(new DollarBoolean(errors, (Boolean) o));
        }
        if (o instanceof Date) {
            return wrap(new DollarDate(errors, ((Date) o).getTime()));
        }
        if (o instanceof LocalDateTime) {
            return wrap(new DollarDate(errors, (LocalDateTime) o));
        }
        if (o instanceof Double) {
            return wrap(new DollarDecimal(errors, (Double) o));
        }
        if (o instanceof Float) {
            return wrap(new DollarDecimal(errors, ((Float) o).doubleValue()));
        }
        if (o instanceof Long) {
            return wrap(new DollarInteger(errors, (Long) o));
        }
        if (o instanceof Integer) {
            return wrap(new DollarInteger(errors, ((Integer) o).longValue()));
        }
        if (o instanceof Short) {
            return wrap(new DollarInteger(errors, ((Short) o).longValue()));
        }
        if (o instanceof Range) {
            return wrap(new DollarRange(errors, (Range) o));
        }
        if (o instanceof ImmutableJsonObject) {
            return wrap(new DollarMap(errors, (ImmutableJsonObject) o));
        }
        if (o instanceof InputStream) {
            try {
                return create(errors, CollectionUtil.fromStream((InputStream) o));
            } catch (IOException e) {
                return failure(e);
            }
        }
        if (o instanceof String) {
            if (((String) o).matches("^[a-zA-Z0-9]+$")) {
                return wrap(new DollarString(errors, (String) o));
            } else {
                try {
                    return wrap(new DollarMap(errors, new JsonObject((String) o)));
                } catch (DecodeException de) {
                    return wrap(new DollarString(errors, (String) o));
                }
            }
        }
        JsonObject json;
        if (o instanceof MultiMap) {
            json = DollarStatic.mapToJson((MultiMap) o);
        } else {
            json = Json.fromJavaObject(o);
        }
        return wrap(new DollarMap(errors, json));
    }


    @NotNull
    public static var fromGsonObject(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return create(ImmutableList.<Throwable>of(), json);
    }

    @NotNull
    public static var fromValue(Object o) {
        return fromValue(o, ImmutableList.of());
    }

    @NotNull
    public static var failure(FailureType failureType) {
        if (DollarStatic.config.failFast()) {
            throw new DollarFailureException(failureType);
        } else {
            return wrap(new DollarFail(failureType));
        }
    }

    @NotNull
    public static var wrap(var value) {
        return wrap(value, DollarStatic.monitor(), DollarStatic.tracer(), DollarStatic.errorLogger());
    }

    @NotNull
    private static var wrap(var value, DollarMonitor monitor, StateTracer tracer, ErrorLogger errorLogger) {
        final var val;
        if (DollarStatic.config.wrapForMonitoring()) {
            val = new DollarWrapper(value, monitor, tracer, errorLogger);
        } else {
            val = value;
        }
        if (DollarStatic.config.wrapForGuards()) {
            return (var) java.lang.reflect.Proxy.newProxyInstance(
                    DollarStatic.class.getClassLoader(),
                    new Class<?>[]{var.class},
                    new DollarGuard(val));
        } else {
            return val;
        }


    }

    @NotNull
    public static var failure(FailureType failureType, Throwable t) {
        if (DollarStatic.config.failFast()) {
            throw new DollarFailureException(t, failureType);
        } else {
            return wrap(new DollarFail(ImmutableList.of(t), failureType));
        }
    }

    public static var failure(FailureType failureType, String message, boolean quiet) {
        if (DollarStatic.config.failFast() && !quiet) {
            throw new DollarFailureException(failureType, message);
        } else {
            return wrap(new DollarFail(ImmutableList.of(new DollarException(message)), failureType));
        }
    }

    public static var failure(Throwable throwable) {
        return failure(FailureType.EXCEPTION, throwable);
    }

    public static var newVoid() {
        return wrap(new DollarVoid());
    }

    public static var fromStringValue(String body) {
        return create(ImmutableList.<Throwable>of(), body);
    }

    public static var fromLambda(Pipeable pipeable) {
        return fromValue(pipeable);
    }

    public static var fromURI(var from) {
        if (from.isUri()) {
            return from;
        } else {
            return fromURI(from.$S());
        }
    }

    public static var fromURI(String uri) {
        try {
            return wrap(new DollarURI(ImmutableList.of(), uri));
        } catch (Exception e) {
            return DollarStatic.handleError(e, null);
        }
    }

    public static var fromStream(SerializedType type, InputStream rawBody) throws IOException {
        if (type == SerializedType.JSON) {
            ObjectMapper mapper = new ObjectMapper();
            final JsonNode jsonNode = mapper.readTree(rawBody);
            if (jsonNode.isArray()) {
                return create(ImmutableList.<Throwable>of(), jsonNode);
            } else if (jsonNode.isObject()) {
                return create(ImmutableList.<Throwable>of(), jsonNode);
            } else {
                throw new DollarException("Could not deserialize JSON, not array or object");
            }
        } else {
            throw new DollarException("Could not deserialize " + type);
        }
    }

    public static var fromFuture(Future<var> future) {
        return wrap((var) java.lang.reflect.Proxy.newProxyInstance(
                DollarStatic.class.getClassLoader(),
                new Class<?>[]{var.class},
                new DollarLambda(i -> future.get(), false)));
    }

    public static var failureWithSource(FailureType failureType, Throwable throwable, SourceAware source) {
        if (source == null) {
            throw new NullPointerException();
        }
        if (DollarStatic.config.failFast()) {
            final DollarFailureException dollarFailureException = new DollarFailureException(throwable, failureType);
            dollarFailureException.addSource(source);
            throw dollarFailureException;
        } else {
            return wrap(new DollarFail(ImmutableList.of(throwable), failureType));
        }
    }

    public static var blockCollection(List<var> var) {
        return wrap(new DollarBlockCollection(var));
    }

    //    public static var _old`deserialize(Type type, String s) {
//        switch(type) {
//            case VOID:
//                return $void();
//            case INTEGER:
//                return wrap(new DollarInteger(Arrays.asList(), Long.parseLong(s)));
//            case BOOLEAN:
//                return wrap(new DollarBoolean(Arrays.asList(), Boolean.parseBoolean(s)));
//            case DATE:
//                return wrap(new DollarDate(Arrays.asList(), LocalDateTime.parse(s)));
//            case DECIMAL:
//                return wrap(new DollarDecimal(Arrays.asList(), Double.parseDouble(s)));
//            case LIST:
//                final JsonArray array = new JsonArray(s);
//                ArrayList<Object> arrayList= new ArrayList<>();
//                for (Object o : array) {
//                    JsonObject jsonObject= (JsonObject) o;
//                    final Type arrayElementType = Type.valueOf(jsonObject.getString("type"));
//                    arrayList.add(deserialize(arrayElementType, jsonObject.getString("value")));
//                }
//                return wrap(new DollarList(Arrays.asList(), arrayList));
//            case MAP:
//                final JsonObject json = new JsonObject(s);
//                LinkedHashMap<String,Object> map= new LinkedHashMap<>();
//                final Set<String> fieldNames = json.getFieldNames();
//                for (String fieldName : fieldNames) {
//                    final JsonObject fieldObject = json.getObject(fieldName);
//                    final Type arrayElementType = Type.valueOf(fieldObject.getString("type"));
//                    map.put(fieldName, deserialize(arrayElementType, fieldObject.getString("value")));
//                }
//                return wrap(new DollarMap(Arrays.asList(), map));
//            case RANGE:
//                final JsonObject jsonObject = new JsonObject(s);
//                final Type rangeType = Type.valueOf(jsonObject.getString("type"));
//                final var lower = deserialize(rangeType, jsonObject.getString("lower"));
//                final var upper = deserialize(rangeType, jsonObject.getString("upper"));
//                return wrap(new DollarRange(Arrays.asList(), lower, upper));
//            case URI:
//                return wrap(new DollarURI(Arrays.asList(), s));
//            case STRING:
//                return wrap(new DollarString(Arrays.asList(), s));
//            default:
//                throw new DollarException("Unrecognized type " + type);
//        }
//    }
//
    public static var deserialize(String s) {
        JsonObject jsonObject = new JsonObject(s);
        return fromJson(jsonObject);
    }

    private static var fromJson(JsonObject jsonObject) {
        final Type type;
        if (!jsonObject.containsField(TYPE_KEY)) {
            type = Type.MAP;
        } else {
            type = Type.valueOf(jsonObject.getString(TYPE_KEY));
        }
        switch (type) {
            case VOID:
                return $void();
            case INTEGER:
                return fromValue(jsonObject.getLong(VALUE_KEY));
            case BOOLEAN:
                return fromValue(jsonObject.getBoolean(VALUE_KEY));
            case ERROR:
                return $void();
            case DATE:
                return wrap(new DollarDate(ImmutableList.of(), LocalDateTime.parse(jsonObject.getString(TEXT_KEY))));
            case DECIMAL:
                return fromValue(jsonObject.getNumber(VALUE_KEY));
            case LIST:
                final JsonArray array = jsonObject.getArray(VALUE_KEY);
                ArrayList<Object> arrayList = new ArrayList<>();
                for (Object o : array) {
                    arrayList.add(fromJson(o));
                }
                return wrap(new DollarList(ImmutableList.of(), ImmutableList.copyOf(arrayList)));
            case MAP:
                final JsonObject json;
                if (jsonObject.containsField(VALUE_KEY)) {
                    json = jsonObject.getObject(VALUE_KEY);
                } else {
                    json = jsonObject;
                }
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                final Set<String> fieldNames = json.getFieldNames();
                for (String fieldName : fieldNames) {
                    map.put(fieldName, fromJson(json.get(fieldName)));
                }
                return wrap(new DollarMap(ImmutableList.of(), map));
            case RANGE:
                final var lower = fromJson(jsonObject.get(LOWERBOUND_KEY));
                final var upper = fromJson(jsonObject.get(UPPERBOUND_KEY));
                return wrap(new DollarRange(ImmutableList.of(), lower, upper));
            case URI:
                return wrap(new DollarURI(ImmutableList.of(), jsonObject.getString(VALUE_KEY)));
            case STRING:
                if (!(jsonObject.get(VALUE_KEY) instanceof String)) {
                    System.out.println(jsonObject.get(VALUE_KEY));
                }
                return wrap(new DollarString(ImmutableList.of(), jsonObject.getString(VALUE_KEY)));
            default:
                throw new DollarException("Unrecognized type " + type);
        }
    }

    private static var fromJson(Object value) {
        if (value == null) {
            return $void();
        } else if (value instanceof LinkedHashMap) {
            JsonObject json = new JsonObject((Map<String, Object>) value);
            if (json.containsField(TYPE_KEY)) {
                return fromJson(json);
            } else {
                return fromValue(value);
            }
        } else if (value instanceof ArrayList) {
            ArrayList list = (ArrayList) value;
            ArrayList<var> result = new ArrayList<>();
            for (Object o : list) {
                result.add(fromJson(o));
            }
            return fromValue(result);

        } else if (value instanceof JsonObject) {
            if (((JsonObject) value).containsField(TYPE_KEY)) {
                return fromJson((JsonObject) value);
            } else {
                return fromValue(value);
            }
        } else if (value instanceof JsonArray) {
            return fromValue(value);
        } else if (value instanceof String) {
            return fromValue(value);
        } else if (value instanceof Number) {
            return fromValue(value);
        } else if (value instanceof Boolean) {
            return fromValue(value);
        } else if (value instanceof byte[]) {
            return fromValue(value);
        } else {
            throw new DollarException("Unrecognized type " + value.getClass() + " for " + value);
        }
    }

    public static String serialize(var value) {
        final Object jsonObject = toJson(value._fixDeep());
        return jsonObject.toString();
    }

    private static JsonObject valueToJson(var value) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.putString(TYPE_KEY, value.$type().name());
        jsonObject.putValue(VALUE_KEY, value.$());
        return jsonObject;
    }

    public static Object toJson(var value) {
        switch (value.$type()) {
            case VOID:
            case INTEGER:
            case BOOLEAN:
            case DECIMAL:
            case STRING:
                return value.$();
            case DATE:
                final JsonObject jsonObject = new JsonObject();
                jsonObject.putValue(TYPE_KEY, value.$type().name());
                jsonObject.putValue(TEXT_KEY, value.$S());
                jsonObject.putValue(MILLISECOND_KEY, (long) (value.D() * 24 * 60 * 60 * 1000));
                return jsonObject;
            case URI:
                final JsonObject uriJsonObject = new JsonObject();
                uriJsonObject.putValue(TYPE_KEY, value.$type().name());
                uriJsonObject.putValue(VALUE_KEY, value.$S());
                return uriJsonObject;
            case LIST:
                final JsonArray array = new JsonArray();
                ImmutableList<var> arrayList = value.$list();
                for (var v : arrayList) {
                    array.add(toJson(v));
                }

                return array;
            case MAP:
                final JsonObject json = new JsonObject();
                ImmutableMap<String, var> map = value.$map();
                final Set<String> fieldNames = map.keySet();
                for (String fieldName : fieldNames) {
                    var v = map.get(fieldName);
                    json.putValue(fieldName, toJson(v));
                }
                final JsonObject containerObject = new JsonObject();
                return json;
            case RANGE:
                final JsonObject rangeObject = new JsonObject();
                rangeObject.putString(TYPE_KEY, value.$type().name());
                final Range range = (Range) value.$();
                rangeObject.put(LOWERBOUND_KEY, toJson($(range.lowerEndpoint())));
                rangeObject.put(UPPERBOUND_KEY, toJson($(range.upperEndpoint())));
                return rangeObject;
            default:
                throw new DollarException("Unrecognized type " + value.$type());
        }
    }

}
