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

package dollar.test;

import dollar.api.DollarStatic;
import dollar.api.Type;
import dollar.api.Value;
import dollar.api.types.DollarFactory;
import dollar.api.types.DollarInfinity;
import dollar.api.types.DollarNull;
import dollar.api.types.DollarVoid;
import dollar.api.types.ErrorType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.singletonMap;

public final class TestGenerator {

    @NotNull
    public static List<Value> allValues() {
        List<Value> all = new ArrayList<>();
        all.addAll(intValues());
        all.addAll(
                toVarValues(Float.MIN_VALUE, Float.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, 0.0, 1.0, -1.0, 100.0,
                            -100.0, 0.5, -0.5, -0.1, 0.1));
        all.addAll(dateValues());
        all.addAll(stringValues());
        all.addAll(booleanValues());
        all.addAll(listValues());
        all.addAll(rangeValues());
        all.addAll(mapValues());
        all.addAll(infinities());
        all.addAll(emptyValues());
        return all;
    }

    @NotNull
    public static List<Value> booleanValues() {
        return toVarValues(true, false);
    }

    @NotNull
    public static List<Value> dateValues() {
        return toVarValues(Instant.ofEpochSecond(0), Instant.ofEpochSecond(-1), Instant.ofEpochSecond(1_000_000),
                           Instant.ofEpochSecond(2_000_000),
                           Instant.ofEpochSecond(3_000_000));
    }

    @NotNull
    public static List<Value> emptyValues() {
        return Arrays.asList(new DollarNull(Type._STRING), new DollarNull(Type._INTEGER), new DollarNull(Type._ANY),
                             new DollarVoid(), DollarFactory.failure(ErrorType.EXCEPTION,
                                                                     "Test Exception", true));
    }

    @NotNull
    public static List<Value> infinities() {
        return Arrays.asList(new DollarInfinity(true), new DollarInfinity(false));
    }

    @NotNull
    public static List<Value> intValues() {
        return toVarValues(Integer.MIN_VALUE, Integer.MAX_VALUE, -Long.MAX_VALUE, Long.MAX_VALUE, 0, 1, -1, 100, -100);
    }

    @NotNull
    public static List<Value> largeDateValues() {
        return toVarValues(Instant.ofEpochSecond(1_000_000), Instant.ofEpochSecond(2_000_000),
                           Instant.ofEpochSecond(3_000_000));
    }

    @NotNull
    public static List<Value> largeDecimalValues() {
        return toVarValues(-Float.MAX_VALUE, Float.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0, -1.0,
                           100.0,
                           -100.0, 0.5, -0.5, -0.1, 0.1);
    }

    @NotNull
    public static List<Value> largeIntValues() {
        return intValues();
    }

    @NotNull
    public static List<Value> largeListValues() {
        return toVarValues(largeIntValues(), largeDecimalValues(), largeDateValues(), stringValues(), booleanValues(),
                           Collections.emptyList());
    }

    @NotNull
    public static List<Value> largeMapValues() {
        return mapValues();
    }

    @NotNull
    public static List<Value> largeRangeValues() {
        return toVarValues(DollarStatic.$range(Instant.ofEpochSecond(1_000_000), Instant.ofEpochSecond(1_100_000)),
                           DollarStatic.$range(-1, 1),
                           DollarStatic.$range(0,
                                               1), DollarStatic.$range(
                        0, 0), DollarStatic.$range(1, 2), DollarStatic.$range(0, Long.MAX_VALUE),
                           DollarStatic.$range(-Long.MAX_VALUE, Long.MAX_VALUE),
                           DollarStatic.$range("a", "z"), DollarStatic.$range("@", "\uF8FF"),
                           DollarStatic.$range(-0.1, -0.05));
    }

    @NotNull
    public static List<Value> listValues() {
        return toVarValues(intValues(),
                           toVarValues(Float.MIN_VALUE, Float.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, 0.0, 1.0,
                                       -1.0, 100.0,
                                       -100.0, 0.5, -0.5, -0.1, 0.1), dateValues(), stringValues(), booleanValues(),
                           Collections.emptyList());
    }

    @NotNull
    public static List<Value> mapValues() {
        HashMap<Object, Object> mixedMap = new HashMap<>();
        mixedMap.put(1, 0.1);
        mixedMap.put(true, 10);
        mixedMap.put("value1", true);
        mixedMap.put("value2", true);
        mixedMap.put("value3", true);
        mixedMap.put(true, "true");
        return toVarValues(Collections.emptyMap(), singletonMap(1, "one"), singletonMap(true, "true"),
                           singletonMap(Instant.ofEpochSecond(1_000_000), true), singletonMap(0.1, "0.1"),
                           singletonMap("string", "string"),
                           mixedMap);
    }

    @NotNull
    public static List<Value> minimal() {
        List<Value> all = new ArrayList<>();
        all.addAll(toVarValues(0, 1, -1));
        all.addAll(toVarValues(0.1));
        all.addAll(toVarValues(Instant.ofEpochSecond(1)));
        all.addAll(toVarValues("", "@"));
        all.addAll(booleanValues());
        all.addAll(toVarValues(toVarValues(0, 1, -1)));
        all.addAll(toVarValues(DollarStatic.$range(-1, 1)));
        all.addAll(minimalEmptyValues());
        HashMap<Object, Object> mixedMap = new HashMap<>();
        mixedMap.put(1, 0.1);
        mixedMap.put(true, 5);
        mixedMap.put("value1", true);
        all.addAll(toVarValues(mixedMap));
        return all;
    }

    @NotNull
    public static List<Value> minimalEmptyValues() {
        return Arrays.asList(new DollarNull(Type._ANY), new DollarVoid(), DollarFactory.failure(ErrorType.EXCEPTION,
                                                                                                "Test Exception", true));
    }

    @NotNull
    public static List<Value> noSmallDecimals() {
        List<Value> all = new ArrayList<>();
        all.addAll(largeIntValues());
        all.addAll(largeDecimalValues());
        all.addAll(largeDateValues());
        all.addAll(stringValues());
        all.addAll(booleanValues());
        all.addAll(largeListValues());
        all.addAll(largeRangeValues());
        all.addAll(largeMapValues());
        return all;
    }

    @NotNull
    public static List<Value> rangeValues() {
        return toVarValues(DollarStatic.$range(Instant.ofEpochSecond(1_000_000),
                                               Instant.ofEpochSecond(2_000_000)),
                           DollarStatic.$range(-1, 1),
                           DollarStatic.$range(0, 0),
                           DollarStatic.$range(0L, Long.MAX_VALUE),
                           DollarStatic.$range(-Long.MAX_VALUE, Long.MAX_VALUE),
                           DollarStatic.$range("a", "z"),
                           DollarStatic.$range("@", "\uF8FF"),
                           DollarStatic.$range(-0.1, -0.05));
    }

    @NotNull
    public static List<Value> small() {
        List<Value> all = new ArrayList<>();
        all.addAll(smallIntValues());
        all.addAll(smallDecimalValues());
        all.addAll(smallDateValues());
        all.addAll(stringValues());
        all.addAll(booleanValues());
        all.addAll(smallListValues());
        all.addAll(smallRangeValues());
        all.addAll(smallMapValues());
        all.addAll(emptyValues());
        return all;
    }

    @NotNull
    public static List<Value> smallDateValues() {
        return toVarValues(Instant.ofEpochSecond(0), Instant.ofEpochSecond(-1));
    }

    @NotNull
    public static List<Value> smallDecimalValues() {
        return toVarValues(0.0, 1.0, -1.0, 10.0,
                           -10.0, 0.5, -0.5, -0.1, 0.1);
    }

    @NotNull
    public static List<Value> smallIntValues() {
        return toVarValues(0, 1, -1, 10, -10);
    }

    @NotNull
    public static List<Value> smallListValues() {
        return toVarValues(smallIntValues(), smallDecimalValues(), smallDateValues(), stringValues(), booleanValues(),
                           smallRangeValues(),
                           Collections.emptyList());
    }

    @NotNull
    public static List<Value> smallMapValues() {
        HashMap<Object, Object> mixedMap = new HashMap<>();
        mixedMap.put(1, 0.1);
        mixedMap.put(true, 5);
        mixedMap.put("value1", true);
        mixedMap.put("value2", true);
        mixedMap.put("value3", true);
        mixedMap.put(true, "true");
        return toVarValues(Collections.emptyMap(), singletonMap(1, "one"), singletonMap(true, "true"),
                           singletonMap(Instant.ofEpochSecond(1_000_000), true), singletonMap(0.1, "0.1"),
                           singletonMap("string", "string"),
                           mixedMap);
    }

    @NotNull
    public static List<Value> smallRangeValues() {
        return toVarValues(DollarStatic.$range(Instant.ofEpochSecond(1), Instant.ofEpochSecond(1)), DollarStatic.$range(-1, 1),
                           DollarStatic.$range(0,
                                               1), DollarStatic.$range(
                        0, 0), DollarStatic.$range(0, 5), DollarStatic.$range(-5, 5),
                           DollarStatic.$range("a", "d"), DollarStatic.$range(-0.1, -0.05));
    }

    @NotNull
    public static List<Value> stringValues() {
        return toVarValues("", "@", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@", "@@ @@ @@@ @@ @@ @@", " ",
                           "ﬂ‡°·‚ÏÌÓÔ\uF8FF^øπåß∂ƒ©˙∆˚¬…æ…;…",
                           "\n\t\r", "1", "true", "false", "0");
    }

    @NotNull
    public static List<Value> test1Param(@NotNull List<Value> values, @NotNull Function<Value, Value> oneParam) {
        List<Value> result = new ArrayList<>();
        for (Value value : values) {
            result.add(oneParam.apply(value));
        }
        return result;
    }

    @NotNull
    public static List<List<Value>> testBinary(@NotNull List<Value> values1, @NotNull List<Value> values2,
                                               @NotNull Function<List<Value>, Value> twoParam) {
        List<List<Value>> result = new ArrayList<>();
        for (Value value1 : values1) {
            for (Value value2 : values2) {
                try {
                    final Value resultValue = twoParam.apply(Arrays.asList(value1, value2));
                    result.add(Arrays.asList(value1, value2, resultValue));
                } catch (Exception e) {
                    result.add(Arrays.asList(value1, value2,
                                             DollarFactory.failure(ErrorType.EXCEPTION, e, true)));
                }
            }
        }
        return result;
    }

    @NotNull
    private static List<Value> toVarValues(@NotNull Object... values) {
        ArrayList<Value> result = new ArrayList<>();
        for (Object value : values) {
            result.add(DollarStatic.$(value));

        }
        return result;
    }
}
