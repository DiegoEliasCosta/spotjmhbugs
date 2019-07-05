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

import java.util.Objects;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.SourceLineAnnotation;

/**
 * Detector of badly configured benchmarks with fork = 0
 * 
 * @author diego.costa
 *
 */
public class NotForkedBenchmarkDetector extends BytecodeScanningDetector {

	private static final String JMH_NOTFORKED_BENCHMARK = "JMH_NOTFORKED_BENCHMARK";
	protected final BugReporter bugReporter;

	public NotForkedBenchmarkDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	@Override
	public void visitAnnotation(Annotations annotations) {

		AnnotationEntry[] annotationEntries = annotations.getAnnotationEntries();
		for (AnnotationEntry annotation : annotationEntries) {

			if (isForkAnnotation(annotation)) {
				validateForkValue(annotation);
			}
		}

		super.visitAnnotation(annotations);
	}

	private boolean isForkAnnotation(AnnotationEntry annotation) {
		String annotationType = annotation.getAnnotationType();
		return Objects.equals(annotationType, "Lorg/openjdk/jmh/annotations/Fork;");
	}

	private void validateForkValue(AnnotationEntry annotation) {

		ElementValuePair[] elementValuePairs = annotation.getElementValuePairs();

		for (ElementValuePair pair : elementValuePairs) {

			if (Objects.equals(pair.getNameString(), "value")) {
				ElementValue value = pair.getValue();

				/**
				 * Rule: Benchmarks should be executed with fork greater than
				 * zero
				 */
				if (Objects.equals(value.stringifyValue(), "0")) {

					reportBug();
				}

			}

		}

	}
	
	private void reportBug() {
		
		BugInstance bug = new BugInstance(this, JMH_NOTFORKED_BENCHMARK, NORMAL_PRIORITY);

		if(visitingMethod()) {
			bug.addClassAndMethod(this).addSourceLine(this);
		} else {
			// Workaround for not being able to add the source line
			bug.addClass(this.getClassDescriptor());
		}
		
		bugReporter.reportBug(bug);
	}

}
