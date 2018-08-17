package de.heidelberg.pvs.diego.detectors;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.StoreInstruction;
import org.dom4j.io.STAXEventReader;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.BasicBlock;
import edu.umd.cs.findbugs.ba.BasicBlock.InstructionIterator;
import sun.security.provider.NativePRNG.Blocking;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.LiveLocalStoreAnalysis;
import edu.umd.cs.findbugs.ba.LiveLocalStoreDataflow;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.XMethod;

public class UnsinkedVariableBenchmarkDetector_Block extends AbstractJMHBenchmarkMethodDetector {

	private static final boolean DEBUG = false;

	BitSet sinkSources;
	Map<Integer, BitSet> blockReadIndexMap;
	
	
	class SinkSource {
		
		int blockId; // Redundant for now
		int sinkIndex;

		public SinkSource(int blockId, int sinkIndex) {
			super();
			this.blockId = blockId;
			this.sinkIndex = sinkIndex;
		}
		
	}

	public UnsinkedVariableBenchmarkDetector_Block(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitMethod(Method obj) {
		super.visitMethod(obj);

		if (isJMHBenchmark(obj)) {

			sinkSources = new BitSet();
			blockReadIndexMap = new HashMap<>();

			try {
				// FIXME: The method is not been used here... find
				// a better way to implement this
				analyzeMethod(getClassContext(), getMethod());
			} catch (DataflowAnalysisException | CFGBuilderException e) {
				// FIXME: Treat the error here
				bugReporter.logError("Error analyzing " + obj.toString(), e);
			}
		}

	}

	/**
	 * Analyze method
	 * 
	 * 1 - Categorize every sinking basic block and variable BasicBlock Label (IDs)
	 * 
	 * 2 - Get every local variable LocalVariableGen object
	 * 
	 * 3 - Get the READ dependency between blocks and variables index1 -> {Block 3,
	 * Block 5, Block 10}
	 * 
	 * 4 - Determine if a sinking block reads any of the blocks in the set
	 * 
	 * 
	 * @param classContext
	 * @param method
	 * @throws DataflowAnalysisException
	 * @throws CFGBuilderException
	 */
	private void analyzeMethod(ClassContext classContext, Method method)
			throws DataflowAnalysisException, CFGBuilderException {

		LiveLocalStoreDataflow loadDataflow = classContext.getLiveLocalStoreDataflow(method);
		LiveLocalStoreAnalysis analysis = loadDataflow.getAnalysis();

		CFG cfg = loadDataflow.getCFG();
		MethodGen methodGen = classContext.getMethodGen(method);

		debugCFG(cfg, methodGen);


		// STEP 1: Determine the sinking blocks
		determineSinkingBlocks(cfg);

		// STEP 3: Analyze Read Dependency
		analyzeReadDependency(cfg);
		
		// STEP : Determine if all variables
		LocalVariableGen[] localVariables = methodGen.getLocalVariables();
		
		debugLocals(methodGen);
		System.out.println("Sink Sources - Block");
		System.out.println(sinkSources);

		System.out.println("Block read Idx");
		System.out.println(blockReadIndexMap);

		for (Iterator<BasicBlock> i = cfg.blockIterator(); i.hasNext();) {
			// N Locations -> 1 Block
			BasicBlock block = i.next();

			BitSet resultFact = analysis.getResultFact(block);
			System.out.println(String.format("Block %d: %s", block.getLabel(), resultFact));
		}

	}

	private void analyzeVariableBlocks(CFG cfg) {
		// TODO Auto-generated method stub
		
	}

	private void determineSinkingBlocks(CFG cfg) {

		for (Iterator<BasicBlock> i = cfg.blockIterator(); i.hasNext();) {

			// N Locations -> 1 Block
			BasicBlock block = i.next();

			int id = block.getLabel();

			// CASE 1 - Store in a field
			if (isAFieldStore(block)) {
				sinkSources.set(id);
			}

			// CASE 2 - Blackhole
			if (isABlackholeSink(block)) {
				sinkSources.set(id);
			}

			// CASE 3 - Return
			if (isReturn(block)) {
				sinkSources.set(id);
			}

			// CASE 4 (Heuristic) - Cases where the Blackhole is passed as a parameter
			// TODO: Implement it here

		}

	}

	private boolean isReturn(BasicBlock block) {
		InstructionIterator it = block.instructionIterator();

		while (it.hasNext()) {
			Instruction instruction = it.next().getInstruction();
			if (isReturn(instruction)) {
				return true;
			}
		}

		return false;
	}

	private boolean isABlackholeSink(BasicBlock block) {
		InstructionIterator it = block.instructionIterator();

		while (it.hasNext()) {
			Instruction instruction = it.next().getInstruction();
			if (isABlackholeSink(instruction)) {
				return true;
			}
		}

		return false;
	}

	private boolean isAFieldStore(BasicBlock block) {

		InstructionIterator it = block.instructionIterator();

		while (it.hasNext()) {
			Instruction instruction = it.next().getInstruction();
			if (isAFieldStore(instruction)) {
				return true;
			}
		}

		return false;
	}

	private void debugLocals(MethodGen methodGen) {

		int maxLocals = methodGen.getMaxLocals();
		System.out.println("Max Locals = " + maxLocals);

		int maxStack = methodGen.getMaxStack();
		System.out.println("Max Stack = " + maxStack);

		LocalVariableGen[] localVariables = methodGen.getLocalVariables();
		for (int i = 0; i < localVariables.length; i++) {
			LocalVariableGen localVariableGen = localVariables[i];
			localVariableGen.getIndex();
			System.out.println(localVariableGen.getName());
			System.out.println(localVariableGen.getStart());
			System.out.println(localVariableGen.getEnd());
			System.out.println(localVariableGen.getIndex());
		}

	}

	private void debugCFG(CFG cfg, MethodGen methodGen) {
		Collection<Location> orderedLocations = cfg.orderedLocations();

		for (Location location : orderedLocations) {
			LineNumberTable table = methodGen.getLineNumberTable(methodGen.getConstantPool());
			int lineNumber = table.getSourceLine(location.getHandle().getPosition());
			int position = location.getHandle().getPosition();
			int label = location.getBasicBlock().getLabel();
			System.out.println(String.format("Line %d -- Position %d -- ID %d -- ", lineNumber, position, label));

		}
	}

	private void analyzeReadDependency(CFG cfg) {
		
		Iterator<BasicBlock> blockIterator = cfg.blockIterator();
		while(blockIterator.hasNext()) {
			BasicBlock block = blockIterator.next();
			analyzeReadDependency(block);
		}
	}

	private void analyzeReadDependency(BasicBlock block) {
		InstructionIterator it = block.instructionIterator();

		while (it.hasNext()) {

			Instruction instruction = it.next().getInstruction();

			if (instruction instanceof LoadInstruction) {

				int index = ((LoadInstruction) instruction).getIndex();
				BitSet dependency;
				if (blockReadIndexMap.containsKey(index)) {
					dependency = blockReadIndexMap.get(index);
				} else {
					dependency = new BitSet();
					blockReadIndexMap.put(index, dependency);
				}
				dependency.set(block.getLabel());

			}
			
			if(instruction instanceof StoreInstruction) {
				
			}

		}

	}

	private boolean isVariableDeclaration(Instruction instruction) {

		if ((instruction instanceof StoreInstruction) || (instruction instanceof IINC)) {
			return true;
		}

		return false;
	}

	private boolean isReturn(Instruction instruction) {

		if (instruction instanceof org.apache.bcel.generic.ARETURN
				|| instruction instanceof org.apache.bcel.generic.IRETURN
				|| instruction instanceof org.apache.bcel.generic.LRETURN
				|| instruction instanceof org.apache.bcel.generic.DRETURN
				|| instruction instanceof org.apache.bcel.generic.FRETURN) {
			return true;
		}

		return false;
	}

	private boolean isABlackholeSink(Instruction instruction) {

		if (instruction instanceof org.apache.bcel.generic.INVOKEVIRTUAL) {

			ConstantPoolGen cpg = this.getClassContext().getConstantPoolGen();
			INVOKEVIRTUAL invoke = (org.apache.bcel.generic.INVOKEVIRTUAL) instruction;

			// Maybe there is a better way to check this but it works pretty fine by now...
			if (invoke.getClassName(cpg).equals("org.openjdk.jmh.infra.Blackhole")
					&& invoke.getMethodName(cpg).equals("consume")) {
				return true;
			}

		}

		return false;
	}

	private boolean isAFieldStore(Instruction instruction) {

		if (instruction instanceof org.apache.bcel.generic.PUTFIELD
				|| instruction instanceof org.apache.bcel.generic.PUTSTATIC) {
			return true;
		}
		return false;
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

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {
		// Not used
	}

}