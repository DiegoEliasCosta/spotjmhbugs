package de.heidelberg.pvs.diego.detectors;

import java.util.List;
import java.util.Objects;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public abstract class BenchmarkDetector extends OpcodeStackDetector {

	private static final String JMH_BENCHMARK_ANNOTATION = "Lorg/openjdk/jmh/annotations/Benchmark;";
	protected final BugReporter bugReporter;

	public BenchmarkDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	/**
	 * Main method for analyzing the benchmark code. This method is only called
	 * when a method with a @Benchmark annotation is encountered.
	 * 
	 * @param method
	 */
	protected abstract void analyzeBenchmark(Method method, ClassContext classContext);
	
	@Override
	public void visitClassContext(ClassContext classContext) {
		
		List<Method> methods = classContext.getMethodsInCallOrder();
		
		for(Method method : methods) {
			
			if(isMethodBenchmark(method)) {
				analyzeBenchmark(method, classContext);
			}
			
		}
		
		super.visitClassContext(classContext);
	}

	private boolean isMethodBenchmark(Method method) {
		AnnotationEntry[] annotationEntries = method.getAnnotationEntries();

		for (AnnotationEntry annotation : annotationEntries) {
			if (Objects.equals(annotation.getAnnotationType(), JMH_BENCHMARK_ANNOTATION)) {
				return true;
			}
		}
		return false;

	}

}
