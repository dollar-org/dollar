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

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public enum FailureType {
    INVALID_LIST_OPERATION, INVALID_MAP_OPERATION, INVALID_SINGLE_VALUE_OPERATION, EXCEPTION,
    INVALID_RANGE_OPERATION, INVALID_BOOLEAN_VALUE_OPERATION, ASSERTION, INVALID_STRING_OPERATION,
    INVALID_URI_OPERATION,

    INVALID_OPERATION, EVALUATION_FAILURE, METADATA_IMMUTABLE, INVALID_CAST, EXECUTION_FAILURE, INTERRUPTED,
    VOID_FAILURE
}
