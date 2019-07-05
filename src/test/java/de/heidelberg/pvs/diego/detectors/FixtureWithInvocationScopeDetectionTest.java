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

public class FixtureWithInvocationScopeDetectionTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	@Test
	public void testIgnoreMethodReturnCheckerWithJMHSample_07() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_07_FixtureLevelInvocation.class");
		Path normalStateClassPath = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_07_FixtureLevelInvocation$NormalState.class");
		Path laggedStateClassPath = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_07_FixtureLevelInvocation$LaggingState.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path, normalStateClassPath, laggedStateClassPath);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_FIXTURE_USING_INVOCATION_SCOPE").build();
		assertThat(bugCollection, containsExactly(1, bugTypeMatcher));
	}
	
	@Test
	public void testIgnoreMethodReturnCheckerWithJMHSample_06() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_06_FixtureLevel.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_FIXTURE_USING_INVOCATION_SCOPE").build();
		assertThat(bugCollection, containsExactly(0, bugTypeMatcher));
	}

}
