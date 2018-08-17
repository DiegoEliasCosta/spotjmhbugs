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

public class UnsafeLoopInsideBenchmarkDetectorTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

//	@Test
//	public void testOnUnsafeLoopExample() throws Exception {
//		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
//				"UnsafeLoopInsideBenchmarkExample.class");
//		BugCollection bugCollection = spotbugs.performAnalysis(path);
//
//		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSAFELOOP_INSIDE_BENCHMARK").build();
//		assertThat(bugCollection, containsExactly(bugTypeMatcher, 4));
//	}
	
	@Test
	public void testOnJMHSample_11() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_11_Loops.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSAFELOOP_INSIDE_BENCHMARK").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 0));
	}
	
	@Test
	public void testOnLog4JLoopInsideBenchmark() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"LoopInsideBenchmarkExample.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSAFELOOP_INSIDE_BENCHMARK").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}
	
	@Test
	public void testOnSafeLoopInsideBenchmark() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"SafeLoopBenchmarkExample.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSAFELOOP_INSIDE_BENCHMARK").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 0));
	}
	
	@Test
	public void testOnUnsafeLoopInsideBenchmark2() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"UnsafeLoopInsideBenchmarkExample2.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNSAFELOOP_INSIDE_BENCHMARK").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}

}
