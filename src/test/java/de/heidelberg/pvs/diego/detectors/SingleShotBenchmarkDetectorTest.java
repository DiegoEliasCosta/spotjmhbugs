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

public class SingleShotBenchmarkDetectorTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	
	@Test
	public void testSingleShotDetectorWithJMHSample_02() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_02_BenchmarkModes.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARKMODE_SINGLESHOT").build();
		assertThat(bugCollection, containsExactly( 2, bugTypeMatcher));
	}
	
	@Test
	public void testSingleShotDetectorWithSingleShotBenchmarkClassExample() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"SingleShotBenchmarkClassExample.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_BENCHMARKMODE_SINGLESHOT").build();
		assertThat(bugCollection, containsExactly( 1, bugTypeMatcher));
	}

}
