package de.heidelberg.pvs.diego.detectors;

import java.util.Iterator;
import java.util.Set;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.BasicBlock;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysis;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.Hierarchy;
import edu.umd.cs.findbugs.ba.JavaClassAndMethod;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.SignatureConverter;
import edu.umd.cs.findbugs.ba.heap.FieldSet;
import edu.umd.cs.findbugs.ba.heap.StoreAnalysis;

public class DeadCodeOptimizationDetector extends BenchmarkDetector {

	public DeadCodeOptimizationDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void sawOpcode(int seen) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void analyzeBenchmark(Method method, ClassContext classContext) {

		CFG cfg;
		try {
			cfg = classContext.getCFG(method);

			for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();) {
				Location location = i.next();
				
				Instruction ins = location.getHandle().getInstruction();
				
				BasicBlock basicBlock = location.getBasicBlock();

				if (ins instanceof InvokeInstruction) {
					
					InvokeInstruction invoke = (InvokeInstruction) ins;
					
					Type returnType = invoke.getReturnType(classContext.getConstantPoolGen());
					
					if(!returnType.equals(Type.VOID)) {
						
						StoreAnalysis analysis = new StoreAnalysis(classContext.getDepthFirstSearch(method), classContext.getConstantPoolGen());
						
						FieldSet factAtLocation = analysis.getFactAtLocation(location);
						System.out.println("Fact At Location" + factAtLocation);
						
						FieldSet factAfterLocation = analysis.getFactAfterLocation(location);
						System.out.println("Fact After Location" + factAfterLocation);
						
						FieldSet resultFact = analysis.getResultFact(basicBlock);
						System.out.println("Result Fact " + resultFact);
						
						
						if(factAfterLocation.isEmpty()) {
							System.out.println("Empty location"+  ins);
							System.out.println("Method invocation: " + location.getHandle());
							System.out.println("\tInvoking: " + SignatureConverter
									.convertMethodSignature((InvokeInstruction) ins, classContext.getConstantPoolGen()));
							
							JavaClassAndMethod proto = Hierarchy.findInvocationLeastUpperBound((InvokeInstruction) ins,
									classContext.getConstantPoolGen());
							if (proto == null) {
								System.out.println("\tUnknown prototype method");
							} else {
								System.out.println("\tPrototype method: class=" + proto.getJavaClass().getClassName()
										+ ", method=" + proto.getMethod());
							}
							Set<JavaClassAndMethod> calledMethodSet = Hierarchy.resolveMethodCallTargets(
									(InvokeInstruction) ins, classContext.getTypeDataflow(method).getFactAtLocation(location),
									classContext.getConstantPoolGen());
							System.out.println("\tTarget method set: " + calledMethodSet);
						}
						
					}
					
				}
			}

		} catch (CFGBuilderException | DataflowAnalysisException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
