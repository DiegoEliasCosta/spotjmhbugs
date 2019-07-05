/**
 * MIT License
 *
 * Copyright (c) 2018 Diego Costa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.heidelberg.pvs.diego.detectors;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.StoreInstruction;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.BasicBlock;
import edu.umd.cs.findbugs.ba.BlockOrder;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.LiveLocalStoreAnalysis;
import edu.umd.cs.findbugs.ba.LiveLocalStoreDataflow;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.ReturnPath;
import edu.umd.cs.findbugs.ba.ReturnPathAnalysis;
import edu.umd.cs.findbugs.ba.ReturnPathDataflow;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.ba.ca.CallList;
import edu.umd.cs.findbugs.ba.ca.CallListAnalysis;
import edu.umd.cs.findbugs.ba.ca.CallListDataflow;
import edu.umd.cs.findbugs.ba.heap.FieldSet;
import edu.umd.cs.findbugs.ba.heap.StoreAnalysis;
import edu.umd.cs.findbugs.ba.heap.StoreDataflow;
import edu.umd.cs.findbugs.ba.type.TypeAnalysis;
import edu.umd.cs.findbugs.ba.type.TypeDataflow;
import edu.umd.cs.findbugs.ba.type.TypeFrame;

public class UnsinkedVariableBenchmarkDetector3 extends AbstractJMHBenchmarkMethodDetector {

	private static final boolean DEBUG = false;

	BitSet sinkSources;
	BitSet varSources;
	BitSet varIndex;
	Map<Integer, BitSet> blockLoadMap;
	Map<Integer, BitSet> blockStoreMap;
	Map<Integer, Integer> stack;

	public UnsinkedVariableBenchmarkDetector3(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitMethod(Method obj) {
		super.visitMethod(obj);

		if (isJMHBenchmark(obj)) {

			sinkSources = new BitSet();
			varSources = new BitSet();
			varIndex = new BitSet();
			blockLoadMap = new HashMap<>();
			blockStoreMap = new HashMap<>();

			stack = new HashMap<>();

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
	 * 1 - Categorize every sinking instruction
	 * 
	 * 2 - Determine the dependency between blocks based on LOAD
	 * 
	 * 3 - For every local variable: Find every block that loads the variable (per
	 * index)
	 * 
	 * 4 - Determine if a block that loads the variable reaches the sinking block
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

		TypeDataflow typeDataflow = classContext.getTypeDataflow(method);
		TypeAnalysis typeAnalysis = typeDataflow.getAnalysis();

		StoreDataflow storeDataflow = classContext.getStoreDataflow(method);
		StoreAnalysis storeAnalysis = storeDataflow.getAnalysis();

		ReturnPathDataflow returnPathDataflow = classContext.getReturnPathDataflow(method);
		ReturnPathAnalysis returnPathAnalysis = returnPathDataflow.getAnalysis();

		CallListDataflow callListDataflow = classContext.getCallListDataflow(method);
		CallListAnalysis callListanalysis = callListDataflow.getAnalysis();

		CFG cfg = loadDataflow.getCFG();
		MethodGen methodGen = classContext.getMethodGen(method);

		debugCFG(cfg, methodGen);

		for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();) {

			// N Locations -> 1 Block
			Location location = i.next();
			categorizeLocation(location);
			analyzeLocation(location);
		}

		if (DEBUG) {

			debugLocations(cfg);

			debugLocals(methodGen);

			System.out.println("Sink Sources - Block");
			System.out.println(sinkSources);

			System.out.println("Variable Sources - Block");
			System.out.println(varSources);

			System.out.println("Variable Indexes - Block");
			System.out.println(varIndex);

			System.out.println("Dependency (Load)");
			System.out.println(blockLoadMap);

			System.out.println("Dependency (Store)");
			System.out.println(blockStoreMap);

			System.out.println("\n --- Live fact --- ");
			for (Iterator<BasicBlock> i = cfg.blockIterator(); i.hasNext();) {
				// N Locations -> 1 Block
				BasicBlock block = i.next();

				BitSet startFact = analysis.getStartFact(block);
				BitSet resultFact = analysis.getResultFact(block);
				System.out.println(String.format("Block %d: %s - %s", block.getLabel(), startFact, resultFact));
			}

			System.out.println("\n --- Location Fact --- ");
			for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();) {
				// N Locations -> 1 Block
				Location location = i.next();

				BitSet startFact = analysis.getFactAtLocation(location);
				BitSet resultFact = analysis.getFactAfterLocation(location);
				System.out.println(String.format("Location %s: %s - %s", location, startFact, resultFact));
			}

			System.out.println("\n --- Type Analysis ---");
			for (Iterator<BasicBlock> i = cfg.blockIterator(); i.hasNext();) {
				// N Locations -> 1 Block
				BasicBlock block = i.next();

				TypeFrame startFact = typeAnalysis.getStartFact(block);
				TypeFrame resultFact = typeAnalysis.getResultFact(block);
				System.out.println(String.format("Block %d: %s - %s", block.getLabel(), startFact, resultFact));
			}

			System.out.println("\n --- Store Analysis ---");
			for (Iterator<BasicBlock> i = cfg.blockIterator(); i.hasNext();) {
				// N Locations -> 1 Block
				BasicBlock block = i.next();

				FieldSet startFact = storeAnalysis.getStartFact(block);
				FieldSet resultFact = storeAnalysis.getResultFact(block);
				System.out.println(String.format("Block %d: %s - %s", block.getLabel(), startFact, resultFact));
			}

			System.out.println("\n --- Return Path Analysis ---");
			for (Iterator<BasicBlock> i = cfg.blockIterator(); i.hasNext();) {
				// N Locations -> 1 Block
				BasicBlock block = i.next();

				ReturnPath resultFact = returnPathAnalysis.getResultFact(block);
				System.out.println(String.format("Block %d: %s", block.getLabel(), resultFact));
			}

			System.out.println("\n --- Call List Analysis ---");
			for (Iterator<BasicBlock> i = cfg.blockIterator(); i.hasNext();) {
				// N Locations -> 1 Block
				BasicBlock block = i.next();

				CallList resultFact = callListanalysis.getResultFact(block);
				System.out.println(String.format("Block %d: %s", block.getLabel(), resultFact));
			}

		}

	}

	private void debugLocations(CFG cfg) {

		Collection<Location> orderedLocations = cfg.orderedLocations();

		System.out.println("\n --- Instructions --- ");

		for (Location location : orderedLocations) {

			Instruction instruction = location.getHandle().getInstruction();
			int label = location.getBasicBlock().getLabel();
			int position = location.getHandle().getPosition();
			System.out.println(String.format("%d - %d - %s", label, position, instruction));

		}
		System.out.println("\n");

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

		if (DEBUG) {

			Collection<Location> orderedLocations = cfg.orderedLocations();

			for (Location location : orderedLocations) {
				LineNumberTable table = methodGen.getLineNumberTable(methodGen.getConstantPool());
				int lineNumber = table.getSourceLine(location.getHandle().getPosition());
				int position = location.getHandle().getPosition();
				int label = location.getBasicBlock().getLabel();
				System.out.println(String.format("Line %d -- Position %d -- ID %d -- ", lineNumber, position, label));

			}
		}
	}

	private void analyzeLocation(Location location) {

		int locationID = location.getBasicBlock().getLabel();
		Instruction instruction = location.getHandle().getInstruction();

		if (instruction instanceof StoreInstruction || instruction instanceof IINC) {
			int index = ((IndexedInstruction) instruction).getIndex();
			stack.put(index, locationID);

			BitSet dependency = new BitSet();
			dependency.set(index);
			blockStoreMap.put(locationID, dependency);

		}

		if (instruction instanceof LoadInstruction) {

			int index = ((LoadInstruction) instruction).getIndex();
			if (stack.containsKey(index)) {
				int dependencyBlock = stack.get(index);

				BitSet dependency;
				if (blockLoadMap.containsKey(dependencyBlock)) {
					dependency = blockLoadMap.get(dependencyBlock);
				} else {
					dependency = new BitSet();
					blockLoadMap.put(dependencyBlock, dependency);
				}
				dependency.set(locationID);
			}
		}
	}

	private void categorizeLocation(Location location) {

		if (location.getHandle().getPrev() != null) {

			int blockID = location.getBasicBlock().getLabel();

			// CASE 1 - Store in a field
			if (isAFieldStore(location)) {
				sinkSources.set(blockID);
			}

			// CASE 2 - Blackhole
			if (isABlackholeSink(location)) {
				sinkSources.set(blockID);
			}

			// CASE 3 - Return
			if (isReturn(location)) {
				sinkSources.set(blockID);
			}

			if (isVariableDeclaration(location)) {
				varSources.set(blockID);
			}
		}
	}

	private boolean isVariableDeclaration(Location location) {

		Instruction instruction = location.getHandle().getInstruction();
		if ((instruction instanceof StoreInstruction || instruction instanceof IINC)) {
			varIndex.set(((IndexedInstruction) instruction).getIndex());
			return true;
		}

		return false;
	}

	private boolean isReturn(Location location) {

		Instruction instruction = location.getHandle().getInstruction();

		if (instruction instanceof org.apache.bcel.generic.ARETURN
				|| instruction instanceof org.apache.bcel.generic.IRETURN
				|| instruction instanceof org.apache.bcel.generic.LRETURN
				|| instruction instanceof org.apache.bcel.generic.DRETURN
				|| instruction instanceof org.apache.bcel.generic.FRETURN) {
			return true;
		}

		return false;
	}

	private boolean isABlackholeSink(Location location) {

		Instruction instruction = location.getHandle().getInstruction();

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

	private boolean isAFieldStore(Location location) {

		Instruction instruction = location.getHandle().getInstruction();

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
