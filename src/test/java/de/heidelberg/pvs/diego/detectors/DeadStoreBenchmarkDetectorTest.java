package de.heidelberg.pvs.diego.detectors;

import static edu.umd.cs.findbugs.test.SpotBugsRule.containsExactly;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class DeadStoreBenchmarkDetectorTest {

	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	@Test
	public void testUnsinkedvariableWithDCE() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"DeadCodeEliminationExample.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);
		
		Collection<BugInstance> collection = bugCollection.getCollection();
		
		for(BugInstance b : collection) {
			System.out.println(b);
		}
		

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_DEAD_STORE_VARIABLE").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 2));
	}
	
	@Test
	public void testFalsePositives() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"DeadCodeFalsePositives.class");
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_DEAD_STORE_VARIABLE").build();
		assertThat(bugCollection, containsExactly(bugTypeMatcher, 0));
	}
}
