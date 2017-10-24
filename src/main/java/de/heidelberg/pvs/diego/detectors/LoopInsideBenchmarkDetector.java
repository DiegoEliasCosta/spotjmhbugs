package de.heidelberg.pvs.diego.detectors;

import org.apache.bcel.Const;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

/**
 * Detector that identified a loop definition in the @Benchmark method
 * 
 * @author diego.costa
 *
 */
public class LoopInsideBenchmarkDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_LOOP_INSIDE_BENCHMARK = "JMH_LOOP_INSIDE_BENCHMARK";

	public LoopInsideBenchmarkDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {

		switch (seen) {

		/**
		 * This is a naive first approach for finding loops on code. GOTOs are
		 * used for loops in general but can also appear on some error handling
		 * mechanisms. So far, I have not fully understood SpotBugs way of
		 * dealing with loops but should update this method as soon as I find a
		 * more reliable way of identifying those.
		 * 
		 * A good link for more info on the subject
		 * https://stackoverflow.com/questions/6792305/identify-loops-in-java-byte-code
		 * 
		 */
		case Const.GOTO_W:
		case Const.GOTO:

			BugInstance bugInstance = new BugInstance(this, JMH_LOOP_INSIDE_BENCHMARK, LOW_PRIORITY)
					.addClassAndMethod(this).addSourceLine(this);

			super.bugReporter.reportBug(bugInstance);

		}

	}

}
