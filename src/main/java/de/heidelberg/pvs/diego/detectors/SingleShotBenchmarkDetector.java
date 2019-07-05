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
import org.apache.bcel.classfile.ElementValuePair;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.SourceLineAnnotation;

public class SingleShotBenchmarkDetector extends BytecodeScanningDetector {

	private static final String JMH_BENCHMARKMODE_SINGLESHOT = "JMH_BENCHMARKMODE_SINGLESHOT";
	private static final String BENCHMARK_MODE = "Lorg/openjdk/jmh/annotations/BenchmarkMode;";
	private BugReporter bugReporter;

	public SingleShotBenchmarkDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	@Override
	public void visitAnnotation(Annotations annotations) {
		
		AnnotationEntry[] annotationDescriptors = annotations.getAnnotationEntries();

		for (AnnotationEntry annotation : annotationDescriptors) {
			
			String simpleName = annotation.getAnnotationType();

			if (Objects.equals(simpleName, BENCHMARK_MODE)) {
				validateBenchmarkMode(annotation);
			}
		}

		super.visitAnnotation(annotations);
	}

	private void validateBenchmarkMode(AnnotationEntry annotation) {

		ElementValuePair[] elementValuePairs = annotation.getElementValuePairs();

		for (ElementValuePair element : elementValuePairs) {
			String name = element.getNameString();

			if (Objects.equals(name, "value")) {
				String value = element.getValue().stringifyValue();
				
				// Rule: SingleShotTime should be avoided in the JMH benchmark
				if (value.contains("SingleShotTime")) {
					reportBug();
				}
			}

		}

	}

	private void reportBug() {
		
		BugInstance bug = new BugInstance(this, JMH_BENCHMARKMODE_SINGLESHOT, NORMAL_PRIORITY);

		if(visitingMethod()) {
			bug.addClassAndMethod(this).addSourceLine(this);
		} else {
			// When visiting a class we cannot call addSourceLine
			bug.addClass(getClassDescriptor());
		}
		
		bugReporter.reportBug(bug);
	}

}
