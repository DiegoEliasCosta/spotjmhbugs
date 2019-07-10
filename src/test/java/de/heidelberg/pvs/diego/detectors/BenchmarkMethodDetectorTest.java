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
class BenchmarkMethodDetectorTest {

	@Test
	void testBenchmarkMethodDetectorWithJMHSample_02(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_02_BenchmarkModes.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(6, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}

	@Test
	void testBenchmarkMethodDetectorWithJMHSample_04(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_04_DefaultState.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(1, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}

	@Test
	void testBenchmarkMethodDetectorWithJMHSample_05(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_05_StateFixtures.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(2, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}

	@Test
	void testBenchmarkMethodDetectorWithJMHSample_06(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_06_FixtureLevel.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(2, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}

	@Test
	void testBenchmarkMethodDetectorWithJMHSample_07(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_07_FixtureLevelInvocation.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(2, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}


	@Test
	void testBenchmarkMethodDetectorWithJMHSample_08(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_08_DeadCode.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(3, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}

	@Test
	void testBenchmarkMethodDetectorWithJMHSample_09(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_09_Blackholes.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(4, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}

	@Test
	void testBenchmarkMethodDetectorWithJMHSample_10(SpotBugsRunner spotbugs) {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_10_ConstantFold.class");

		BugCollection bugCollection = spotbugs.performAnalysis(path);
		assertEquals(4, countBugTypes(bugCollection, "JMH_BENCHMARK_METHOD_FOUND"));
	}
}
