package de.heidelberg.pvs.diego.checkers;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public abstract class AbstractJMHBenchmarkChecker extends OpcodeStackDetector {

	private static final String JMH_BENCHMARK_ANNOTATION = "Lorg/openjdk/jmh/annotations/Benchmark;";
	
	protected final BugReporter bugReporter;
	protected Set<Method> targetBenchmarkMethods = new HashSet<>();

	public AbstractJMHBenchmarkChecker(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	@Override
	public void visitClassContext(ClassContext classContext) {

		List<Method> methods = classContext.getMethodsInCallOrder();

		for (Method method : methods) {

			if (isMethodBenchmark(method)) {
				targetBenchmarkMethods.add(method);
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

	@Override
	public void sawOpcode(int seen) {

		if (visitingMethod()) {

			Method currentVisitingMethod = getMethod();

			// Checks whether the method is one of the benchmarks
			if (targetBenchmarkMethods.contains(currentVisitingMethod)) {
				
				analyzeBenchmarkMethodOpCode(seen);
				
			}

		}

	}

	/**
	 * 
	 * 
	 * @param seen
	 */
	protected abstract void analyzeBenchmarkMethodOpCode(int seen);

}
