package de.heidelberg.pvs.diego.detectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.analysis.AnnotationValue;

/**
 * Abstract class for @State object analysis.
 * 
 * @author diego.costa
 *
 */
public abstract class AbstractJMHStateClassDetector extends BytecodeScanningDetector {

	private static final String JMH_STATE_ANNOTATION = "org/openjdk/jmh/annotations/State";
	private Set<ClassContext> targetStateClasses = new HashSet<ClassContext>();
	protected final BugReporter bugReporter;
	
	public AbstractJMHStateClassDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	
	/**
	 * This method is overridden to filter the future analysis only to JMH @State classes
	 */
	@Override
	public void visitClassContext(ClassContext classContext) {
		
		if(isStateClass(classContext.getXClass())) {
			targetStateClasses.add(classContext);
		}
		
		
		super.visitClassContext(classContext);
	}

	private boolean isStateClass(XClass xClass) {

		// Get annotations
		Collection<AnnotationValue> annotationEntries = xClass.getAnnotations();
		
		for(AnnotationValue annotation : annotationEntries) {
			
			String type = annotation.getAnnotationClass().getClassName();
			
			if(Objects.equals(type, JMH_STATE_ANNOTATION)) {
				return true;
			}
		}
		
		// Check super class
		ClassDescriptor superclassDescriptor = xClass.getSuperclassDescriptor();
		
		if(superclassDescriptor != null) {
			try {
				// Recursive call to the super class 
				isStateClass(superclassDescriptor.getXClass());
			} catch (CheckedAnalysisException e) {
				// FIXME: Handle this exception without stopping the analysis
				e.printStackTrace();
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

	/**
	 * Returns true whether we are currently visiting a @State class 
	 * 
	 * @return
	 */
	public boolean isTargetStateClass() {
		return this.targetStateClasses.contains(getClassContext());
	}
	

}
