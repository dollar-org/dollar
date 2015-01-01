/*
 * Copyright (c) 2014-2015 Neil Ellis
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

package me.neilellis.dollar.api.js;

import me.neilellis.dollar.api.DollarStatic;
import me.neilellis.dollar.api.Pipeable;
import me.neilellis.dollar.api.collections.CollectionUtil;
import me.neilellis.dollar.api.types.DollarFactory;
import me.neilellis.dollar.api.var;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class JSFileScript implements Pipeable {
  private static
  @NotNull final
  ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");


  private final String script;

    public JSFileScript(@NotNull File file) throws IOException {
    byte[] bytes = Files.readAllBytes(file.toPath());
    script = new String(bytes);
  }

  public JSFileScript(InputStream stream) throws IOException {
    final String s;
    s = CollectionUtil.fromStream(stream);
    script= s;
  }

    @NotNull @Override
  public var pipe(var... in) throws Exception {
    SimpleScriptContext context = new SimpleScriptContext();
    nashorn.eval("var $=" + in[0].toJsonObject().toString() + ";", context);
    try {
      Object result = nashorn.eval(script, context);
      return DollarFactory.fromValue(result);
    } catch (Exception e) {
      return DollarStatic.handleError(e, in[0]);
    }
  }
}