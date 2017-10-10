package de.heidelberg.pvs.diego.detectors;

import java.util.BitSet;

import org.apache.bcel.Const;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

/**
 * Checker for identifying methods call with ignored return values on JMH benchmarks.
 * 
 * @author diego.costa
 *
 */
public class IgnoredMethodReturnDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_IGNORED_METHOD_RETURN = "JMH_IGNORED_METHOD_RETURN";

	/**
	 * Operation codes related to a method call
	 */
	private static final BitSet INVOKE_OPCODE_SET = new BitSet();
	static {
		INVOKE_OPCODE_SET.set(Const.INVOKEINTERFACE);
		INVOKE_OPCODE_SET.set(Const.INVOKESPECIAL);
		INVOKE_OPCODE_SET.set(Const.INVOKESTATIC);
		INVOKE_OPCODE_SET.set(Const.INVOKEVIRTUAL);
	}

	public IgnoredMethodReturnDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	private boolean isDiscardOperation(int nextOpcode) {
		return nextOpcode == Const.POP || nextOpcode == Const.POP2;
	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {
		
		// Investigate the method calls (invoke op code)
		if (INVOKE_OPCODE_SET.get(seen)) {

			int nextOpcode = getNextOpcode();
			if (isDiscardOperation(nextOpcode)) {

				// Create the bug
				BugInstance bugInstance = new BugInstance(this, JMH_IGNORED_METHOD_RETURN, NORMAL_PRIORITY)
						.addClassAndMethod(this).addSourceLine(this);

				super.bugReporter.reportBug(bugInstance);
			}

		}

	}

}
