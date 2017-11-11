package de.heidelberg.pvs.diego.detectors;

import java.util.HashSet;
import java.util.Set;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.XMethod;

public class BenchmarkMethodDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_BENCHMARK_FOUND = "JMH_BENCHMARK_METHOD_FOUND";

	public BenchmarkMethodDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	protected Set<XMethod> reportedBenchmarkMethods = new HashSet<XMethod>();
	
	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {
		
		XMethod method2 = this.getXMethod();
		
		if(!reportedBenchmarkMethods.contains(method2)) {
			reportedBenchmarkMethods.add(method2);
			
			BugInstance bug = new BugInstance(this, JMH_BENCHMARK_FOUND, NORMAL_PRIORITY)
					.addClassAndMethod(this)
					.addSourceLine(this);
			
			this.bugReporter.reportBug(bug);
			
		}
		
		
	}

}
