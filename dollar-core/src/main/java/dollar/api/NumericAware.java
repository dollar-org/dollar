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

package dollar.api;

import dollar.api.guard.ChainGuard;
import dollar.api.guard.Guarded;
import dollar.api.guard.NotNullGuard;
import dollar.api.guard.NotNullParametersGuard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NumericAware {


    /**
     * Computes the absolute value for this object. Currently the result is only defined for numeric values.
     *
     * @return the absolute value
     */
    @NotNull
    @Guarded(NotNullGuard.class)
    @Guarded(ChainGuard.class)
    var $abs();

    /**
     * Decrements this value, decrementing is the same as  {@code $minus($(1))} for numeric values but may be
     * different behaviour for non-numeric values.
     *
     * @return the new decremented value
     */
    @Guarded(NotNullGuard.class)
    @Guarded(ChainGuard.class)
    @NotNull
    default var $dec() {
        return $minus(DollarStatic.$(1));
    }

    /**
     * Deducts from var, for toStrings this means remove all occurrence of and for collections it means remove the value
     * from the collection. For numbers it is standard numeric arithmetic.
     *
     * @param rhs the value to deduct from this
     * @return the new value
     */
    @NotNull
    @Guarded(NotNullParametersGuard.class)
    default @Guarded(ChainGuard.class)
    var $minus(@NotNull var rhs) {
        return $plus(rhs.$negate());
    }

    /**
     * Returns a new {@link var} with this value added to it. Like {@link #$minus(var)} the actual behaviour varies with
     * types. So for toStrings this is concatenation for collections it is adding a new element.
     *
     * @param rhs the value to add
     * @return a new object with the value supplied added
     */

    @NotNull
    @Guarded(ChainGuard.class)
    var $plus(@NotNull var rhs);

    /**
     * Negate the value, for lists, toStrings and maps this means reversing the elements. For numbers it is the usual
     * numeric negation.
     *
     * @return the negated value
     */
    @NotNull
    @Guarded(NotNullGuard.class)
    @Guarded(ChainGuard.class)
    var $negate();

    /**
     * Divide the value. For toStrings this means splitting. For numbers this is the usual numerical division.
     *
     * @param rhs the value to divide by
     * @return the divided value
     */
    @NotNull
    @Guarded(NotNullGuard.class)
    @Guarded(ChainGuard.class)
    var $divide(@NotNull var rhs);

    /**
     * Incrementing is the same as {@code $plus($(1))} for numerical values, it has type dependent behaviour for
     * the other types.
     *
     * @return the incremented value
     */
    @NotNull
    @Guarded(NotNullGuard.class)
    @Guarded(ChainGuard.class)
    default var $inc() {
        return $plus(DollarStatic.$(1));
    }

    /**
     * Subtract the value from this object. For toStrings and collections this means remove all occurrences of the RHS
     * argument.
     *
     * @param rhs the value to subtract
     * @return the new value
     */
    @NotNull
    default var $minus(int rhs) {
        return $minus(DollarStatic.$(rhs));
    }

    /**
     * Returns the remainder after a division.
     *
     * @param rhs the value to divide by
     * @return the remainder
     */
    @NotNull
    @Guarded(NotNullGuard.class)
    @Guarded(ChainGuard.class)
    var $modulus(@NotNull var rhs);

    /**
     * $ multiply.
     *
     * @param v the v
     * @return the var
     */
    @NotNull
    @Guarded(NotNullGuard.class)
    @Guarded(ChainGuard.class)
    var $multiply(@NotNull var v);

    /**
     * Convenience method for the Java api which is the same as {@link #$plus(var)}
     *
     * @param rhs the value to add
     * @return the new value
     */
    @NotNull
    default var $plus(int rhs) {
        return $plus(DollarStatic.$(rhs));
    }

    /**
     * Is negative.
     *
     * @return the boolean
     */
    default boolean negative() {
        return sign() <= 0;
    }

    /**
     * Sign int.
     *
     * @return the int
     */
    default int sign() {
        if (toDouble() == null) {
            return 0;
        }
        return (int) Math.signum(toDouble());
    }

    /**
     * toDouble double.
     *
     * @return the double
     */
    @Nullable
    Double toDouble();

    /**
     * toDouble double.
     *
     * @return the double
     */
    @Nullable
    default Double D() {
        return toDouble();
    }


    /**
     * Is positive.
     *
     * @return the boolean
     */
    default boolean positive() {
        return sign() >= 0;
    }

    /**
     * toInteger integer.
     *
     * @return the integer
     */
    @NotNull
    Integer toInteger();


    /**
     * toInteger integer.
     *
     * @return the integer
     */
    @NotNull
    default Integer I() {
        return toInteger();
    }

    /**
     * toLong long.
     *
     * @return the long
     */
    @NotNull
    Long toLong();

    /**
     * toNumber number.
     *
     * @return the number
     */
    @Nullable
    default Number toNumber() {
        return toDouble();
    }

    /**
     * Is zero.
     *
     * @return the boolean
     */
    default boolean zero() {
        return sign() == 0;
    }

}
