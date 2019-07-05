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
import java.util.Iterator;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.StoreInstruction;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.BasicBlock;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Dataflow;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.LiveLocalStoreAnalysis;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.ba.heap.LoadAnalysis;
import edu.umd.cs.findbugs.ba.heap.LoadDataflow;

public class UnsinkedVariableBenchmarkDetector2 extends AbstractJMHBenchmarkMethodDetector {

	public UnsinkedVariableBenchmarkDetector2(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitMethod(Method obj) {
		super.visitMethod(obj);

		if (isJMHBenchmark(obj)) {
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

	private void analyzeMethod(ClassContext classContext, Method method)
			throws DataflowAnalysisException, CFGBuilderException {
		
		
		JavaClass javaClass = classContext.getJavaClass();
		
		LoadDataflow loadDataflow = classContext.getLoadDataflow(method);
		
		LoadAnalysis analysis = loadDataflow.getAnalysis();
		
		CFG loadCfg = loadDataflow.getCFG();
		
		
		Dataflow<BitSet, LiveLocalStoreAnalysis> llsaDataflow = classContext.getLiveLocalStoreDataflow(method);

		CFG cfg = llsaDataflow.getCFG();
		
		for (Iterator<Location> i = loadCfg.locationIterator(); i.hasNext();) {
			
			Location location = i.next();
			BasicBlock block = location.getBasicBlock();
			
			InstructionHandle handle = location.getHandle();
			System.out.println(handle.getInstruction().getName());
			System.out.println(block.toString());
			
//			if (!isStore(location)) {
//				continue;
//			}
//			
//			
//			IndexedInstruction ins = (IndexedInstruction) location.getHandle().getInstruction();
//			int local = ins.getIndex();
//
//			// Get live stores at this instruction.
//			// Note that the analysis also computes which stores were
//			// killed by a subsequent unconditional store.
//			BitSet liveStoreSet = llsaDataflow.getAnalysis().getFactAtLocation(location);
//
//			// Is store alive?
//			boolean storeLive = llsaDataflow.getAnalysis().isStoreAlive(liveStoreSet, local);
//			

		}

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

	}

}
