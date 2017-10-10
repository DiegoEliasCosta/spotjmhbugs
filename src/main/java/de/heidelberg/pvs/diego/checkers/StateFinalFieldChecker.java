package de.heidelberg.pvs.diego.checkers;

import org.apache.bcel.classfile.Field;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

public class StateFinalFieldChecker extends AbstractJMHStateClassChecker {

	private static final String JMH_STATE_FINAL_FIELD = "JMH_STATE_FINAL_FIELD";

	public StateFinalFieldChecker(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitField(Field obj) {

		// First check whether we are visiting a @State object
		if (super.isTargetStateClass()) {

			// RULE: Fields on @State object should not be declared final
			if (obj.isFinal() && !obj.isSynthetic()) {
				
				
				BugInstance bugInstance = new BugInstance(this, JMH_STATE_FINAL_FIELD, NORMAL_PRIORITY).addField(this)
						.addClass(this);

				super.bugReporter.reportBug(bugInstance);
			}

		}

		super.visitField(obj);
	}

	@Override
	protected void analyzeStateClassOpCode(int seen) {
		// We cannot use the opcode stack to analyze field variables.
		// For this analysis this needs to be ignored
	}

}
