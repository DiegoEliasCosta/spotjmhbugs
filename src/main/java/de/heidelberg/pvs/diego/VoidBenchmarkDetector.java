package de.heidelberg.pvs.diego;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

public class VoidBenchmarkDetector extends BenchmarkDetector {

	private static final String VOID_BENCHMARK_ID = "JMH_VOID_BENCHMARK";

	public VoidBenchmarkDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	protected void analyzeBenchmark(Method method) {

		Type returnType = method.getReturnType();

		if (returnType.equals(Type.VOID)) {
			BugInstance bug = new BugInstance(this, VOID_BENCHMARK_ID, NORMAL_PRIORITY).addClassAndMethod(this)
					.addSourceLine(this, getPC());
			bugReporter.reportBug(bug);

		}

	}

	@Override
	public void sawOpcode(int seen) {
		// TODO Check how to proceed with this op code
	}

}
