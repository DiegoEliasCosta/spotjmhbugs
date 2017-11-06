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
