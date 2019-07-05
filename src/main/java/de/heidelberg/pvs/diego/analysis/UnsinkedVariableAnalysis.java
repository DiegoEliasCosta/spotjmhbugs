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

package de.heidelberg.pvs.diego.analysis;

import java.util.BitSet;

import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.StoreInstruction;

import edu.umd.cs.findbugs.ba.BackwardDataflowAnalysis;
import edu.umd.cs.findbugs.ba.BasicBlock;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.Debug;
import edu.umd.cs.findbugs.ba.DepthFirstSearch;
import edu.umd.cs.findbugs.ba.Edge;
import edu.umd.cs.findbugs.ba.ReverseDepthFirstSearch;

public class UnsinkedVariableAnalysis extends BackwardDataflowAnalysis<BitSet> implements Debug {

	private int topBit;
	private int killedByStoreOffset;

	public UnsinkedVariableAnalysis(MethodGen methodGen, ReverseDepthFirstSearch rdfs, DepthFirstSearch dfs) {
		super(rdfs, dfs);
		this.topBit = methodGen.getMaxLocals() * 2;
		this.killedByStoreOffset = methodGen.getMaxLocals();
	}

	@Override
	public BitSet createFact() {
		return new BitSet();
	}

	@Override
	public void copy(BitSet source, BitSet dest) {
		dest.clear();
		dest.or(source);
	}

	@Override
	public void initEntryFact(BitSet result) throws DataflowAnalysisException {
		result.clear();

	}

	@Override
	public void makeFactTop(BitSet fact) {
		// Not sure how useful keeping track of the top is
		fact.clear();
		fact.set(topBit);
	}

	@Override
	public boolean isTop(BitSet fact) {
		return fact.get(topBit);
	}

	@Override
	public boolean same(BitSet fact1, BitSet fact2) {
		return fact1.equals(fact2);
	}

	@Override
	public void meetInto(BitSet fact, Edge edge, BitSet result) throws DataflowAnalysisException {
		// For now ths is a replication of the LiveLocalStore meetInto method.
		// I will adapt this to our case when I see fit

		verifyFact(fact);
		verifyFact(result);

		if (isTop(fact)) {
			// Nothing to do, result stays the same
		} else if (isTop(result)) {
			// Result is top, so it takes the value of fact
			copy(fact, result);
		} else {
			// Meet is union
			result.or(fact);
		}

	}

	/**
	 * @param fact
	 */
	private void verifyFact(BitSet fact) {
		if (VERIFY_INTEGRITY) {
			if (isTop(fact) && fact.nextSetBit(0) < topBit) {
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public void transferInstruction(InstructionHandle handle, BasicBlock basicBlock, BitSet fact)
			throws DataflowAnalysisException {
		// This is where we adapt our method

		if (!isFactValid(fact)) {
			return;
		}

		Instruction ins = handle.getInstruction();

		if (ins instanceof StoreInstruction) {
			// Local is stored
			LocalVariableInstruction store = (LocalVariableInstruction) ins;
			int local = store.getIndex();
			fact.set(local);
			fact.set(local + killedByStoreOffset);
		}

		if (ins instanceof LoadInstruction || ins instanceof IINC || ins instanceof RET) {
			// Local is loaded: it will be live on any path leading
			// to this instruction

			IndexedInstruction load = (IndexedInstruction) ins;
			int local = load.getIndex();
			fact.set(local);
		}

		if (!isFactValid(fact)) {
			throw new IllegalStateException("Fact become invalid");
		}

	}

	@Override
	public boolean isFactValid(BitSet fact) {
		verifyFact(fact);
		return !isTop(fact);
	}

}
