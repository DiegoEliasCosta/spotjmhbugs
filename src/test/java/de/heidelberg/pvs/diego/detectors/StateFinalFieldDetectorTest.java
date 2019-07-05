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

import static edu.umd.cs.findbugs.test.CountMatcher.containsExactly;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class StateFinalFieldDetectorTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	@Test
	public void testOnJMHSample_08() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_10_ConstantFold.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_FIELD").build();
		assertThat(bugCollection, containsExactly(0, bugTypeMatcher));
		
		BugInstanceMatcher primitiveBugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_PRIMITIVE").build();
		assertThat(bugCollection, containsExactly(1, primitiveBugTypeMatcher ));
		
		BugInstanceMatcher staticBugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_STATIC_PRIMITIVE").build();
		assertThat(bugCollection, containsExactly(0, staticBugTypeMatcher));
	}
	
	@Test
	public void testOnJMHSample_07() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_07_FixtureLevelInvocation.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_FIELD").build();
		assertThat(bugCollection, containsExactly(0, bugTypeMatcher)); // No cases
	}
	
	
	@Test
	public void testOnStatePrimitiveFinalFieldsExample() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"StateMultipleFinalFieldsExample.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);
		
		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_FIELD").build();
		assertThat(bugCollection, containsExactly(0, bugTypeMatcher));

		BugInstanceMatcher primitiveBugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_PRIMITIVE").build();
		assertThat(bugCollection, containsExactly(3, primitiveBugTypeMatcher));
	}
	
	
	@Test
	public void testOnStateInnerClassExample() throws Exception {
		Path outerClassPath = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"StateInnerClassExample.class");
		Path innerClassPath = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"StateInnerClassExample$MyState.class");
		
		
		BugCollection bugCollection = spotbugs.performAnalysis(outerClassPath, innerClassPath);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_FIELD").build();
		assertThat(bugCollection, containsExactly(0, bugTypeMatcher));
		
		BugInstanceMatcher primitiveBugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_PRIMITIVE").build();
		assertThat(bugCollection, containsExactly(1, primitiveBugTypeMatcher));
		
		BugInstanceMatcher staticBugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_STATE_FINAL_STATIC_PRIMITIVE").build();
		assertThat(bugCollection, containsExactly(1, staticBugTypeMatcher));
	}

}
