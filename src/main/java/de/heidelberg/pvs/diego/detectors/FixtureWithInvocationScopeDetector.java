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
