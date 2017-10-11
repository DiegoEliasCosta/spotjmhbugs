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

public class UnforkedBenchmarkDetectorTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	@Test
	public void testOnJMHSample_12() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.jmh".replace('.', '/'),
				"JMHSample_12_Forking.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_UNFORKED_BENCHMARK").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 3));
	}

}
