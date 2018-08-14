package de.heidelberg.pvs.diego.detectors;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.Type;

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
	private static final String JMH_STATE_FINAL_PRIMITIVE = "JMH_STATE_FINAL_PRIMITIVE";

	public StateFinalFieldDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitField(Field obj) {

		// First check whether we are visiting a @State object
		if (super.isTargetStateClass()) {

			// RULE: Fields on @State object should not be declared final
			if (obj.isFinal() && !obj.isSynthetic()) {
				
				BugInstance bugInstance;
				
				Type type = obj.getType();
				
				
				if(isPrimitiveOrString(type)) {
					
					bugInstance = new BugInstance(this, JMH_STATE_FINAL_PRIMITIVE, HIGH_PRIORITY)
							.addClass(this)
							.addField(this);

					super.bugReporter.reportBug(bugInstance);
				}	
			}

		}

		super.visitField(obj);
	}

	private boolean isPrimitiveOrString(Type type) {
		return type == Type.FLOAT || type == Type.DOUBLE || type == Type.INT || type == Type.CHAR 
				|| type == Type.BOOLEAN || type.equals(Type.STRING) || type == Type.SHORT || type == Type.BYTE 
				|| type == Type.LONG;
	}


}
