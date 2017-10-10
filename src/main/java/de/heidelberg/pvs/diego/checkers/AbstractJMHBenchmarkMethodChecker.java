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

/**
 * Abstract class for JMH benchmark method analysis
 * 
 * @author diego.costa
 *
 */
public abstract class AbstractJMHBenchmarkMethodChecker extends OpcodeStackDetector {

	private static final String JMH_BENCHMARK_ANNOTATION = "Lorg/openjdk/jmh/annotations/Benchmark;";

	protected final BugReporter bugReporter;
	protected Set<Method> targetBenchmarkMethods = new HashSet<Method>();

	public AbstractJMHBenchmarkMethodChecker(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	@Override
	public void visitClassContext(ClassContext classContext) {

		List<Method> methods = classContext.getMethodsInCallOrder();

		for (Method method : methods) {

			// Target methods that are declared with the @Benchmark annotation
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

		// Before getMethod() we make sure that we are visiting a method at the moment
		if (visitingMethod()) {

			Method currentVisitingMethod = getMethod();

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
