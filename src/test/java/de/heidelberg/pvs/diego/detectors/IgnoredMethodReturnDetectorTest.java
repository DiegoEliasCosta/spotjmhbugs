package de.heidelberg.pvs.diego.detectors;

import static edu.umd.cs.findbugs.test.SpotBugsRule.containsExactly;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class IgnoredMethodReturnDetectorTest {

	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	@Test
	public void testIgnoreMethodReturnCheckerWithJMHSample_08() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_08_DeadCode.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_IGNORED_METHOD_RETURN").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}
	

	@Test
	public void testIgnoreMethodReturnCheckerWithJMHSample_09() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_09_Blackholes.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_IGNORED_METHOD_RETURN").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}
	
	
	@Test
	public void testIgnoreMethodReturnCheckerWithJMHSample_10() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_10_ConstantFold.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_IGNORED_METHOD_RETURN").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 0));
	}
	
	
	@Test
	public void testIgnoreMethodReturnCheckerWithJMHSample_08_09() throws Exception {
		Path path08 = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_08_DeadCode.class");
		Path path09 = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_09_Blackholes.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path08, path09);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_IGNORED_METHOD_RETURN").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 2));
	}
	
	@Test
	public void testIgnoreMethodReturnCheckerWithExample() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"IgnoreMethodReturnExample.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_IGNORED_METHOD_RETURN").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}
	
}