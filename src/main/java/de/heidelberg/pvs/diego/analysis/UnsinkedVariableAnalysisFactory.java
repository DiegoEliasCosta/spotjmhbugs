package de.heidelberg.pvs.diego.analysis;

import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.IAnalysisCache;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import edu.umd.cs.findbugs.classfile.engine.bcel.AnalysisFactory;

public class UnsinkedVariableAnalysisFactory extends AnalysisFactory<UnsinkedVariableDataflow>{

	public UnsinkedVariableAnalysisFactory(String analysisName, Class<UnsinkedVariableDataflow> analysisClass) {
		super(analysisName, analysisClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public UnsinkedVariableDataflow analyze(IAnalysisCache analysisCache, MethodDescriptor descriptor)
			throws CheckedAnalysisException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
