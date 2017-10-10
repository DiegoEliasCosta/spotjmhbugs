package de.heidelberg.pvs.diego.checkers;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.bcel.classfile.AnnotationEntry;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/**
 * Abstract class for @State object analysis.
 * 
 * @author diego.costa
 *
 */
public abstract class AbstractJMHStateClassChecker extends OpcodeStackDetector {

	private Set<ClassContext> targetStateClasses = new HashSet<ClassContext>();
	protected final BugReporter bugReporter;
	
	public AbstractJMHStateClassChecker(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	
	/**
	 * This method is overridden to filter the future analysis only to JMH @State classes
	 */
	@Override
	public void visitClassContext(ClassContext classContext) {
		
		if(isStateClass(classContext)) {
			targetStateClasses.add(classContext);
		}
		
		
		super.visitClassContext(classContext);
	}

	private boolean isStateClass(ClassContext classContext) {

		// Get annotations
		AnnotationEntry[] annotationEntries = classContext.getJavaClass().getAnnotationEntries();
		
		for(AnnotationEntry annotation : annotationEntries) {
			
			String type = annotation.getAnnotationType();
			
			if(Objects.equals(type, "Lorg/openjdk/jmh/annotations/State;")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Overriding the beforeOpCode to allow the analysis of TOP byte code segments
	 */
	@Override
	public boolean beforeOpcode(int seen) {
		return true;
	}

	@Override
	public void sawOpcode(int seen) {
		
		ClassContext currentClassContext = getClassContext();
		
		// Only analyzes @State classes  
		if(targetStateClasses.contains(currentClassContext)) {
			analyzeStateClassOpCode(seen);
		}
	}
	
	/**
	 * Returns true whether we are currently visiting a @State class 
	 * 
	 * @return
	 */
	public boolean isTargetStateClass() {
		return this.targetStateClasses.contains(getClassContext());
	}
	
	/**
	 * Main method to be implemented on each checker analyzing @State classes. 
	 * This method will be called once per each byte code instruction.
	 * 
	 * @param seen
	 */
	protected abstract void analyzeStateClassOpCode(int seen);

	

}
