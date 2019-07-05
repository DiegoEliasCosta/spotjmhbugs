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

public class BenchmarkMethodDetectorTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_02() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_02_BenchmarkModes.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(6, bugTypeMatcher));
	}
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_04() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_04_DefaultState.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(1, bugTypeMatcher));
	}
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_05() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_05_StateFixtures.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(2, bugTypeMatcher));
	}
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_06() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_06_FixtureLevel.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(2, bugTypeMatcher));
	}
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_07() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_07_FixtureLevelInvocation.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(2, bugTypeMatcher));
	}
	
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_08() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_08_DeadCode.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(3, bugTypeMatcher));
	}
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_09() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_09_Blackholes.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(4, bugTypeMatcher));
	}
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_10() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_10_ConstantFold.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(4, bugTypeMatcher));
	}


}
