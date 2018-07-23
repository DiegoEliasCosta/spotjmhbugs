package de.heidelberg.pvs.diego.detectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
