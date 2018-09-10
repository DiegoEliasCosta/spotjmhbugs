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

public class UnsinkedVariableBenchmarkDetectorTest {

	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	@Test
	public void testUnsinkedvariableWithDCE() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"DeadCodeEliminationExample.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSINKED_VARIABLE").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}
	
	@Test
	public void testUnsinkedvariableWithSimplestDCE() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"SimplestDeadCodeEliminationExample.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSINKED_VARIABLE").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}
	
	@Test
	public void testUnsinkedvariableWithFalsePositives() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"UnsinkVariableFalsePositive.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSINKED_VARIABLE").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 0));
	}
	
	@Test
	public void testUnsinkedvariableWithFalseNegatives() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"UnsinkVariableFalseNegativeExamples.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSINKED_VARIABLE").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	
	}
//	@Test
//	public void testUnsinkedvariableWithFalsePsitiveExamples() throws Exception {
//		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
//				"UnsinkedVariablesFalsePositiveExamples.class");
//		BugCollection bugCollection = spotbugs.performAnalysis(path);
//
//		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSINKED_VARIABLE").build();
//		assertThat(bugCollection, containsExactly(bugTypeMatcher, 0));
//	}
	
	
}
