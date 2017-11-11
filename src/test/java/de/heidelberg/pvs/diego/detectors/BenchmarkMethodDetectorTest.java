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

public class BenchmarkMethodDetectorTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_02() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_02_BenchmarkModes.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 6));
	}
	
	@Test
	public void testBenchmarkMethodDetectorWithJMHSample_08() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_08_DeadCode.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARK_METHOD_FOUND").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 3));
	}

}
