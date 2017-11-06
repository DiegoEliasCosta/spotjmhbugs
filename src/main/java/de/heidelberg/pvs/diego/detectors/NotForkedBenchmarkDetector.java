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
