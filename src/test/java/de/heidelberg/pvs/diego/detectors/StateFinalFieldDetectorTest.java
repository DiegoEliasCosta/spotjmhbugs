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
