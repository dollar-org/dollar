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

package dollar;

import com.google.common.io.Files;
import org.junit.Test;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author hello@neilellis.me
 */
public class DocTest {

    @Test
    public void test() throws IOException {
        try {
            PegDownProcessor processor = new PegDownProcessor(Extensions.FENCED_CODE_BLOCKS);
            RootNode rootNode = processor.parseMarkdown(
                    Files.asCharSource(new File("README.md"), Charset.forName("utf-8")).read().toCharArray());
            rootNode.accept(new DocTestingVisitor());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}