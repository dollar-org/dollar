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

package dollar.internal.runtime.script.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SourceNodeOptions {
    @NotNull
    public static final SourceNodeOptions NEW_PARALLEL_SCOPE = new SourceNodeOptions(true, false, true);
    @NotNull
    public static final SourceNodeOptions NEW_SCOPE = new SourceNodeOptions(true, false, null);
    @NotNull
    public static final SourceNodeOptions NEW_SERIAL_SCOPE = new SourceNodeOptions(true, false, false);
    @NotNull
    public static final SourceNodeOptions NO_SCOPE = new SourceNodeOptions(false, false, null);
    @NotNull
    public static final SourceNodeOptions SCOPE_WITH_CLOSURE = new SourceNodeOptions(true, true, null);


    private final boolean newScope;
    @Nullable
    private final Boolean parallel;
    private final boolean scopeClosure;

    public SourceNodeOptions(boolean newScope, boolean scopeClosure, @Nullable Boolean parallel) {
        this.newScope = newScope;
        this.scopeClosure = scopeClosure;
        this.parallel = parallel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(newScope, scopeClosure, parallel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || (getClass() != o.getClass())) return false;
        SourceNodeOptions that = (SourceNodeOptions) o;
        return (newScope == that.newScope) &&
                       (scopeClosure == that.scopeClosure) &&
                       (parallel == that.parallel);
    }

    public boolean isNewScope() {
        return newScope;
    }

    @Nullable
    public Boolean isParallel() {
        return parallel;
    }

    public boolean isScopeClosure() {
        return scopeClosure;
    }
}
