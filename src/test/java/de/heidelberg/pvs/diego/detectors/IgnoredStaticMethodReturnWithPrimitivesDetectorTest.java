package de.heidelberg.pvs.diego.detectors;

import static edu.umd.cs.findbugs.test.CountMatcher.containsExactly;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.dom4j.Document;
import org.junit.Rule;
import org.junit.Test;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class IgnoredStaticMethodReturnWithPrimitivesDetectorTest {
	
	@Rule
	public SpotBugsRule spotbugs = new SpotBugsRule();

	@Test
	public void testIgnoredStaticMethodReturnWithPrimitivesExample() throws Exception {
		Path path = Paths.get("target/test-classes", "de.heidelberg.pvs.diego.examples".replace('.', '/'),
				"IgnoredStaticMethodReturnWithPrimitivesExample.class");
		
		BugCollection bugCollection = spotbugs.performAnalysis(path);

		BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("JMH_IGNORED_STATIC_PRIMITIVE_METHOD_RETURN").build();
		assertThat(bugCollection, containsExactly(4, bugTypeMatcher));
		
		BugInstanceMatcher bugTypeMatcher2 = new BugInstanceMatcherBuilder().bugType("JMH_IGNORED_STATIC_METHOD_RETURN").build();
		assertThat(bugCollection, containsExactly( 3, bugTypeMatcher2));
	}
	

}
