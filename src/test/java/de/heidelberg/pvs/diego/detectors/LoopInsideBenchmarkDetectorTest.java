/**
 * MIT License
 *
 * Copyright (c) 2018 Diego Costa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.heidelberg.pvs.diego.detectors;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.test.SpotBugsExtension;
import edu.umd.cs.findbugs.test.SpotBugsRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.heidelberg.pvs.diego.detectors.Util.countBugTypes;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpotBugsExtension.class})
class LoopInsideBenchmarkDetectorTest {

	@Test
	void testOnSimpleBenchmarkLoopExample(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"SimpleBenchmarkLoopExample.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(2, countBugTypes(bugCollection, "JMH_LOOP_INSIDE_BENCHMARK"));
	}
	
	@Test
	void testOnJMHSample_11(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_11_Loops.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(0, countBugTypes(bugCollection, "JMH_LOOP_INSIDE_BENCHMARK"));
	}
	
	@Test
	void testOnLog4JLoopInsideBenchmark(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"LoopInsideBenchmarkExample.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(1, countBugTypes(bugCollection, "JMH_LOOP_INSIDE_BENCHMARK"));
	}

}
