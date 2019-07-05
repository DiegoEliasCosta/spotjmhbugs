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
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

public class FixtureWithInvocationScopeDetector extends AbstractJMHStateClassDetector {

	private static final String LEVEL_INVOCATION = "Invocation";
	private static final String JMH_FIXTURE_USING_INVOCATION_SCOPE = "JMH_FIXTURE_USING_INVOCATION_SCOPE";
	private static final String SETUP_ANNOTATION = "Lorg/openjdk/jmh/annotations/Setup;";
	private static final String TEAR_DOWN_ANNOTATION = "Lorg/openjdk/jmh/annotations/TearDown;";

	public FixtureWithInvocationScopeDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitMethod(Method obj) {

		if (super.isTargetStateClass()) {
			AnnotationEntry[] annotationEntries = obj.getAnnotationEntries();

			for (AnnotationEntry annotation : annotationEntries) {

				String annotationType = annotation.getAnnotationType();

				// Find the @Setup or @TearDown annotations
				if (Objects.equals(annotationType, SETUP_ANNOTATION)
						|| Objects.equals(annotationType, TEAR_DOWN_ANNOTATION)) {

					validateFixtureScope(annotation);
				}
			}
		}

		super.visitMethod(obj);
	}

	private void validateFixtureScope(AnnotationEntry annotation) {

		ElementValuePair[] elementValuePairs = annotation.getElementValuePairs();

		for (ElementValuePair element : elementValuePairs) {

			String name = element.getNameString();

			if (Objects.equals(name, "value")) {
				ElementValue value = element.getValue();

				String stringifiedValue = value.stringifyValue();

				// Rule: Avoid using Invocation scope on Fixture methods for
				// Benchmarks
				if (Objects.equals(stringifiedValue, LEVEL_INVOCATION)) {

					BugInstance bug = new BugInstance(this, JMH_FIXTURE_USING_INVOCATION_SCOPE, NORMAL_PRIORITY)
							.addClassAndMethod(this).addSourceLine(this);

					super.bugReporter.reportBug(bug);

				}
			}
		}

	}

}
