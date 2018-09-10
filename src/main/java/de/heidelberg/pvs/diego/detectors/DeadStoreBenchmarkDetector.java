package de.heidelberg.pvs.diego.detectors;

import java.util.BitSet;
import java.util.Iterator;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.StoreInstruction;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.LocalVariableAnnotation;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Dataflow;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.LiveLocalStoreAnalysis;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.detect.DeadLocalStoreProperty;
import edu.umd.cs.findbugs.props.WarningProperty;
import edu.umd.cs.findbugs.props.WarningPropertySet;
import edu.umd.cs.findbugs.visitclass.PreorderVisitor;

/**
 * Detector for dead stores in a JMH benchmark. Heavily based on the
 * FindDeadLocalStores implementation by David Hovemeyer and Bill Pugh.
 * 
 * 
 * 
 * @author diego.costa
 *
 */
public class DeadStoreBenchmarkDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_DEAD_STORE_VARIABLE = "JMH_DEAD_STORE_VARIABLE";

	public DeadStoreBenchmarkDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitMethod(Method obj) {
		super.visitMethod(obj);

		if (isJMHBenchmark(obj)) {
			try {
				analyzeMethod(getClassContext(), getMethod());
			} catch (DataflowAnalysisException | CFGBuilderException e) {
				// FIXME: Treat the error here
				bugReporter.logError("Error analyzing " + obj.toString(), e);
			}
		}

	}

	private boolean isJMHBenchmark(Method obj) {
		// TODO: Check a better way of comparing XMethod with Method objects
		String signature = obj.getSignature();
		for (XMethod m : super.targetBenchmarkMethods) {
			if (m.getSignature().equals(signature)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Opcodes of instructions that load constant values that often indicate
	 * defensive programming.
	 */
	private static final BitSet defensiveConstantValueOpcodes = new BitSet();
	private static final boolean DEBUG = false;
	static {
		defensiveConstantValueOpcodes.set(Const.DCONST_0);
		defensiveConstantValueOpcodes.set(Const.DCONST_1);
		defensiveConstantValueOpcodes.set(Const.FCONST_0);
		defensiveConstantValueOpcodes.set(Const.FCONST_1);
		defensiveConstantValueOpcodes.set(Const.ACONST_NULL);
		defensiveConstantValueOpcodes.set(Const.ICONST_M1);
		defensiveConstantValueOpcodes.set(Const.ICONST_0);
		defensiveConstantValueOpcodes.set(Const.ICONST_1);
		defensiveConstantValueOpcodes.set(Const.ICONST_2);
		defensiveConstantValueOpcodes.set(Const.ICONST_3);
		defensiveConstantValueOpcodes.set(Const.ICONST_4);
		defensiveConstantValueOpcodes.set(Const.ICONST_5);
		defensiveConstantValueOpcodes.set(Const.LCONST_0);
		defensiveConstantValueOpcodes.set(Const.LCONST_1);
		defensiveConstantValueOpcodes.set(Const.LDC);
		defensiveConstantValueOpcodes.set(Const.LDC_W);
		defensiveConstantValueOpcodes.set(Const.LDC2_W);
	}

	private void analyzeMethod(ClassContext classContext, Method method)
			throws DataflowAnalysisException, CFGBuilderException {

		JavaClass javaClass = classContext.getJavaClass();
		BugAccumulator accumulator = new BugAccumulator(bugReporter);
		Dataflow<BitSet, LiveLocalStoreAnalysis> llsaDataflow = classContext.getLiveLocalStoreDataflow(method);

		int numLocals = method.getCode().getMaxLocals();
		int[] localStoreCount = new int[numLocals];
		int[] localLoadCount = new int[numLocals];
		int[] localIncrementCount = new int[numLocals];
		MethodGen methodGen = classContext.getMethodGen(method);
		CFG cfg = classContext.getCFG(method);
		if (cfg.isFlagSet(CFG.FOUND_INEXACT_UNCONDITIONAL_THROWERS)) {
			return;
		}

		// Get number of locals that are parameters.
		int localsThatAreParameters = PreorderVisitor.getNumberArguments(method.getSignature());
		if (!method.isStatic()) {
			localsThatAreParameters++;
		}

		// Scan method to determine number of loads, stores, and increments
		// of local variables.
		countLocalStoresLoadsAndIncrements(localStoreCount, localLoadCount, localIncrementCount, cfg);
		for (int i = 0; i < localsThatAreParameters; i++) {
			localStoreCount[i]++;
		}

		// For each source line, keep track of # times
		// the line was a live store. This can eliminate false positives
		// due to inlining of finally blocks.
		BitSet liveStoreSourceLineSet = new BitSet();

		// Scan method for
		// - dead stores
		// - stores to parameters that are dead upon entry to the method
		for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();) {
			Location location = i.next();

			try {
				WarningPropertySet<WarningProperty> propertySet = new WarningPropertySet<>();
				// Skip any instruction which is not a store
				if (!isStore(location)) {
					continue;
				}

				// Heuristic: exception handler blocks often contain
				// dead stores generated by the compiler.
				if (location.getBasicBlock().isExceptionHandler()) {
					continue; // Skip Exception clauses
				}
				IndexedInstruction ins = (IndexedInstruction) location.getHandle().getInstruction();

				int local = ins.getIndex();

				// Get live stores at this instruction.
				// Note that the analysis also computes which stores were
				// killed by a subsequent unconditional store.
				BitSet liveStoreSet = llsaDataflow.getAnalysis().getFactAtLocation(location);

				// Is store alive?
				boolean storeLive = llsaDataflow.getAnalysis().isStoreAlive(liveStoreSet, local);

				LocalVariableAnnotation lvAnnotation = LocalVariableAnnotation.getLocalVariableAnnotation(method,
						location, ins);

				String sourceFileName = javaClass.getSourceFileName();

				SourceLineAnnotation sourceLineAnnotation = SourceLineAnnotation.fromVisitedInstruction(classContext,
						methodGen, sourceFileName, location.getHandle());

				if (DEBUG) {
					System.out.println("    Store at " + sourceLineAnnotation.getStartLine() + "@"
							+ location.getHandle().getPosition() + " is " + (storeLive ? "live" : "dead"));
					System.out.println("Previous is: " + location.getHandle().getPrev());
				}

				// Note source lines of live stores.
				if (storeLive && sourceLineAnnotation.getStartLine() > 0) {
					liveStoreSourceLineSet.set(sourceLineAnnotation.getStartLine());
				}

				if(!storeLive) {
					
					// TODO: Check this
					String lvName = lvAnnotation.getName();
					if (lvName.charAt(0) == '$' || lvName.charAt(0) == '_') {
						propertySet.addProperty(DeadLocalStoreProperty.SYNTHETIC_NAME);
					}

					propertySet.setProperty(DeadLocalStoreProperty.LOCAL_NAME, lvName);
					
					BugInstance bugInstance = new BugInstance(this, JMH_DEAD_STORE_VARIABLE, HIGH_PRIORITY).addClassAndMethod(methodGen,
	                        sourceFileName).add(lvAnnotation);
					
					accumulator.accumulateBug(bugInstance, sourceLineAnnotation);
				
				}
			} finally {

			}

		}
		accumulator.reportAccumulatedBugs();
	}

	/**
	 * Is instruction at given location a store?
	 *
	 * @param location
	 *            the location
	 * @return true if instruction at given location is a store, false if not
	 */
	private boolean isStore(Location location) {
		Instruction ins = location.getHandle().getInstruction();
		return (ins instanceof StoreInstruction) || (ins instanceof IINC);
	}

	/**
	 * Is instruction at given location a load?
	 *
	 * @param location
	 *            the location
	 * @return true if instruction at given location is a load, false if not
	 */
	private boolean isLoad(Location location) {
		Instruction ins = location.getHandle().getInstruction();
		return (ins instanceof LoadInstruction) || (ins instanceof IINC);
	}

	/**
	 * Count stores, loads, and increments of local variables in method whose CFG is
	 * given.
	 *
	 * @param localStoreCount
	 *            counts of local stores (indexed by local)
	 * @param localLoadCount
	 *            counts of local loads (indexed by local)
	 * @param localIncrementCount
	 *            counts of local increments (indexed by local)
	 * @param cfg
	 *            control flow graph (CFG) of method
	 */
	private void countLocalStoresLoadsAndIncrements(int[] localStoreCount, int[] localLoadCount,
			int[] localIncrementCount, CFG cfg) {
		for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();) {
			Location location = i.next();

			if (location.getBasicBlock().isExceptionHandler()) {
				continue;
			}

			boolean isStore = isStore(location);
			boolean isLoad = isLoad(location);
			if (!isStore && !isLoad) {
				continue;
			}

			IndexedInstruction ins = (IndexedInstruction) location.getHandle().getInstruction();
			int local = ins.getIndex();
			if (ins instanceof IINC) {
				localStoreCount[local]++;
				localLoadCount[local]++;
				localIncrementCount[local]++;
			} else if (isStore) {
				localStoreCount[local]++;
			} else {
				localLoadCount[local]++;
			}
		}
	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {
		// TODO Auto-generated method stub

	}

}
