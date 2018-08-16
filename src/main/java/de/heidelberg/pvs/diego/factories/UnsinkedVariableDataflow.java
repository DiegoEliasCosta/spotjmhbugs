package de.heidelberg.pvs.diego.factories;

import java.util.BitSet;

import de.heidelberg.pvs.diego.analysis.UnsinkedVariableAnalysis;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.Dataflow;

public class UnsinkedVariableDataflow extends Dataflow<BitSet, UnsinkedVariableAnalysis>{

	public UnsinkedVariableDataflow(CFG cfg, UnsinkedVariableAnalysis analysis) {
		super(cfg, analysis);
	}

}
