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
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.classfile.analysis.AnnotationValue;

/**
 * Abstract class for JMH benchmark method analysis
 * 
 * @author diego.costa
 *
 */
public abstract class AbstractJMHBenchmarkMethodDetector extends OpcodeStackDetector {

	private static final String JMH_BENCHMARK_ANNOTATION = "org/openjdk/jmh/annotations/Benchmark";
	private static final String JMH_BENCHMARK_ANNOTATION_2 = "Lorg/openjdk/jmh/annotations/Benchmark;";
	

	protected final BugReporter bugReporter;
	protected Set<XMethod> targetBenchmarkMethods = new HashSet<XMethod>();

	public AbstractJMHBenchmarkMethodDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	

	@Override
	public void visitClassContext(ClassContext classContext) {
		
		XClass xClass = classContext.getXClass();

		List<? extends XMethod> methods = xClass.getXMethods();
		
		for (XMethod method : methods) {
			
			// Target methods that are declared with the @Benchmark annotation
			if (isMethodBenchmark(method)) {
				targetBenchmarkMethods.add(method);
			}

		}

		super.visitClassContext(classContext);
	}



	protected boolean isMethodBenchmark(XMethod method) {
		Collection<AnnotationValue> annotationEntries = method.getAnnotations();

		for (AnnotationValue annotation : annotationEntries) {
			
			String annotationType = annotation.getAnnotationClass().getClassName();
			
			if (Objects.equals(annotationType, JMH_BENCHMARK_ANNOTATION)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isMethodBenchmark(Method method) {
		AnnotationEntry[] annotationEntries = method.getAnnotationEntries();

		for (AnnotationEntry annotation : annotationEntries) {
			
			String annotationType = annotation.getAnnotationType();
			
			if (Objects.equals(annotationType, JMH_BENCHMARK_ANNOTATION_2)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public void sawOpcode(int seen) {

		// Before getMethod() we make sure that we are visiting a method at the moment
		if (visitingMethod()) {

			XMethod currentVisitingMethod = getXMethod();

			// Checks whether the method is a benchmark method
			if (targetBenchmarkMethods.contains(currentVisitingMethod)) {
				analyzeBenchmarkMethodOpCode(seen);
			}
		}
	}

	/**
	 * Main method to be implemented on each checker analyzing @Benchmark
	 * methods. This method will be called once per each byte code instruction.
	 * 
	 * @param seen
	 */
	protected abstract void analyzeBenchmarkMethodOpCode(int seen);

}
