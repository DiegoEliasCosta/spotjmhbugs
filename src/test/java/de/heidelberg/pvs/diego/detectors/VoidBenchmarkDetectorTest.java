package de.heidelberg.pvs.diego.detectors;

import static edu.umd.cs.findbugs.test.SpotBugsRule.containsExactly;
import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class VoidBenchmarkDetectorTest {
	
    @Rule
    public SpotBugsRule spotbugs = new SpotBugsRule();
	
	@Test
	public void testBenchmarkAnnotationDetection() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego".replace('.', '/'), "BenchmarkTestClass.class");
        BugCollection bugCollection = spotbugs.performAnalysis(path);

        BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder()
                .bugType("JMH_VOID_BENCHMARK").build();
        assertThat(bugCollection, containsExactly(bugTypeMatcher, 1));
	}

	
}
