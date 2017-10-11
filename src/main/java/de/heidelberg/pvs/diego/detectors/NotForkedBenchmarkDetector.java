package de.heidelberg.pvs.diego.detectors;

import java.util.Objects;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;

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
	public void visitJavaClass(JavaClass javaClass) {
		
		AnnotationEntry[] annotationEntries = javaClass.getAnnotationEntries();

		for (AnnotationEntry annotation : annotationEntries) {

			if (isForkAnnotation(annotation)) {
				validateForkValue(annotation, javaClass);
			}
		}

		super.visitJavaClass(javaClass);
	}

	@Override
	public void visitMethod(Method obj) {
		
		AnnotationEntry[] annotationEntries = obj.getAnnotationEntries();

		for (AnnotationEntry annotation : annotationEntries) {

			if (isForkAnnotation(annotation)) {
				validateForkValue(annotation, getThisClass());
			}
		}
		super.visitMethod(obj);
	}

	private boolean isForkAnnotation(AnnotationEntry annotation) {
		String annotationType = annotation.getAnnotationType();
		return Objects.equals(annotationType, "Lorg/openjdk/jmh/annotations/Fork;");
	}

	private void validateForkValue(AnnotationEntry annotation, JavaClass javaClass) {

		ElementValuePair[] elementValuePairs = annotation.getElementValuePairs();

		for (ElementValuePair pair : elementValuePairs) {

			if (Objects.equals(pair.getNameString(), "value")) {
				ElementValue value = pair.getValue();

				/**
				 * Rule: Benchmarks should be executed with fork greater than
				 * zero
				 */
				if (Objects.equals(value.stringifyValue(), "0")) {

					BugInstance bugInstance = new BugInstance(this, JMH_NOTFORKED_BENCHMARK, NORMAL_PRIORITY);
					
					if(visitingMethod()) {
						bugInstance.addClassAndMethod(this).addSourceLine(this);
					} else {
						bugInstance.addClass(javaClass);
					}

					bugReporter.reportBug(bugInstance);
				}

			}

		}

	}

}
