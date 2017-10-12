package de.heidelberg.pvs.diego.detectors;

import org.apache.bcel.classfile.Field;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

/**
 * Detector that identifies final variables defined in a JMH @State class
 * 
 * @author diego.costa
 *
 */
public class StateFinalFieldDetector extends AbstractJMHStateClassDetector {

	private static final String JMH_STATE_FINAL_FIELD = "JMH_STATE_FINAL_FIELD";

	public StateFinalFieldDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitField(Field obj) {

		// First check whether we are visiting a @State object
		if (super.isTargetStateClass()) {

			// RULE: Fields on @State object should not be declared final
			if (obj.isFinal() && !obj.isSynthetic()) {
				
				BugInstance bugInstance = new BugInstance(this, JMH_STATE_FINAL_FIELD, NORMAL_PRIORITY)
						.addClass(this) 
						.addField(this);

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
