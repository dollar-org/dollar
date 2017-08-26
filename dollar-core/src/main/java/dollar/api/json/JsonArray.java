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

package dollar.api.json;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dollar.api.DollarException;
import dollar.api.json.impl.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class JsonArray extends JsonElement implements Iterable<Object> {

    @NotNull
    final List<Object> list;

    public JsonArray(@NotNull List list) {
        this(list, true);
    }

    protected JsonArray(@NotNull List list, boolean copy) {
        super();
        this.list = copy ? convertList(list) : list;
    }

    public JsonArray(@NotNull Object[] array) {
        this(new ArrayList<>(Arrays.asList(array)), true);
    }

    public JsonArray() {
        super();
        list = new ArrayList<>();
    }

    public JsonArray(@NotNull String jsonString) {
        super();
        list = Json.decodeValue(jsonString, List.class);
    }

    @NotNull
    public JsonArray add(@Nullable Object value) {
        if (value == null) {
            list.add(null);
        } else if (value instanceof JsonObject) {
            addObject((JsonObject) value);
        } else if (value instanceof JsonArray) {
            addArray((JsonArray) value);
        } else if (value instanceof String) {
            addString((String) value);
        } else if (value instanceof Number) {
            addNumber((Number) value);
        } else if (value instanceof Boolean) {
            addBoolean((Boolean) value);
        } else if (value instanceof byte[]) {
            addBinary((byte[]) value);
        } else if (value instanceof Character) {
            addString(value.toString());
        } else {
            throw new DollarException("Cannot add objects of class " + value.getClass() + " to JsonArray");
        }
        return this;
    }

    @NotNull
    public JsonArray addObject(@Nullable JsonObject value) {
        list.add((value == null) ? null : value.map);
        return this;
    }

    @NotNull
    private JsonArray addArray(@Nullable JsonArray value) {
        list.add((value == null) ? null : value.list);
        return this;
    }

    @NotNull
    private JsonArray addString(@NotNull String str) {
        list.add(str);
        return this;
    }

    @NotNull
    private JsonArray addNumber(@NotNull Number value) {
        list.add(value);
        return this;
    }

    @NotNull
    private JsonArray addBoolean(@NotNull Boolean value) {
        list.add(value);
        return this;
    }

    @NotNull
    private JsonArray addBinary(@Nullable byte[] value) {
        String encoded = (value == null) ? null : Base64.getEncoder().encodeToString(value);
        list.add(encoded);
        return this;
    }

    @NotNull
    public JsonArray addElement(@Nullable JsonElement value) {
        if (value == null) {
            list.add(null);
            return this;
        }
        if (value.isArray()) {
            return addArray(value.asArray());
        }
        return addObject(value.asObject());
    }

    public boolean contains(@NotNull Object value) {
        return list.contains(value);
    }

    /**
     * @return a copy of the JsonArray
     */
    @NotNull
    public JsonArray copy() {
        return new JsonArray(list, true);
    }

    @Nullable
    public <T> T get(final int index) {
        return convertObject(list.get(index));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private <T> T convertObject(@Nullable final Object obj) {
        Object retVal = obj;
        if (obj != null) {
            if (obj instanceof ImmutableList) {
                retVal = new JsonArray((((ImmutableList) obj)), false);
            } else if (obj instanceof ImmutableMap) {
                retVal = new JsonObject(((ImmutableMap) obj), false);
            } else if (obj instanceof List) {
                retVal = new JsonArray((List) obj, false);
            } else if (obj instanceof Map) {
                retVal = new JsonObject((Map<String, Object>) obj, false);
            }
        }
        return (T) retVal;
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) { return true; }
        if ((o == null) || (getClass() != o.getClass())) { return false; }
        JsonArray that = (JsonArray) o;
        return list.equals(that.list);
    }

    @NotNull
    @Override
    public String toString() {
        return encodePrettily();
    }

    @NotNull
    public String encodePrettily() throws EncodeException {
        return Json.encodePrettily(list);
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {

            @NotNull
            final Iterator<Object> iter = list.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Nullable
            @Override
            public Object next() {
                return convertObject(iter.next());
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    public int size() {
        return list.size();
    }

    @NotNull
    public Object[] toArray() {
        return convertList(list).toArray();
    }

    @NotNull
    public List toList() {
        return convertList(list);
    }

    @NotNull
    public List toStringList() {
        final ArrayList<Object> strings = new ArrayList<>();
        for (Object o : list) {
            strings.add(Json.encode(o));
        }
        return strings;
    }

    @NotNull
    String encode() throws EncodeException {
        return Json.encode(list);
    }
}
