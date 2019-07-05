/**
 * MIT License
 *
 * Copyright (c) 2018 Diego Costa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
				return isStateClass(superclassDescriptor.getXClass());
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
