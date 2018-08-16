package de.heidelberg.pvs.diego.analysis;

import java.util.BitSet;

import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.Dataflow;

public class UnsinkedVariableDataflow extends Dataflow<BitSet, UnsinkedVariableAnalysis>{

	public UnsinkedVariableDataflow(CFG cfg, UnsinkedVariableAnalysis analysis) {
		super(cfg, analysis);
	}

}
