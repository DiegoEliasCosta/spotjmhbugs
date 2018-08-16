package de.heidelberg.pvs.diego.detectors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack.Item;
import edu.umd.cs.findbugs.visitclass.PreorderVisitor;

public class UnsinkedVariableBenchmarkDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final boolean DEBUG = true;

	private static final String JMH_UNSINKED_VARIABLE = "JMH_UNSINKED_VARIABLE";
	
	public UnsinkedVariableBenchmarkDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	Map<LocalVariable, VarStatus> monitoredVariables;

	LocalVariableTable varTable;

	class VarStatus {

		Set<LocalVariable> dependents;

		boolean sinkedIntoBlackhole;
		boolean sinkedIntoAField;
		boolean sinkedFromDependents;
		boolean sinkedByReturned;
		boolean sinkedByMethodCall;

		public VarStatus() {
			super();
			dependents = new HashSet<>();
		}

		public boolean isSinked() {
			// Add here the dependency
			return sinkedIntoBlackhole || sinkedIntoAField || sinkedByReturned || sinkedFromDependents || sinkedByMethodCall;
		}

		@Override
		public String toString() {
			String format = String.format("blackhole: %s | field: %s | return: %s | call: %s | dependent: %s", sinkedIntoBlackhole,
					sinkedIntoAField, sinkedByReturned, sinkedByMethodCall, sinkedFromDependents);
			return format;

		}

		public void resolveDependents() {
			// Only update if this variable has
			if (this.isSinked()) {
				for (LocalVariable local : dependents) {
					VarStatus varStatus = monitoredVariables.get(local);
					varStatus.sinkedFromDependents = true;

				}
			}
		}

		public void addDependent(LocalVariable localVariable) {
			dependents.add(localVariable);
		}

	}

	@Override
	public void visit(Code code) {

		
		varTable = code.getLocalVariableTable();
		monitoredVariables = new HashMap<>();

		LocalVariable[] localVariableTable = varTable.getLocalVariableTable();
		for (LocalVariable var : localVariableTable) {
			// Do not analyze parameters (startPC == 0)
			if(var.getStartPC() > 0) {
				monitoredVariables.put(var, new VarStatus());
			}
			
		}

		// Perform the Byte-code analysis
		super.visit(code);

		// Pos analysis
		for (VarStatus status : monitoredVariables.values()) {
			status.resolveDependents();
		}
		
		for (Entry<LocalVariable, VarStatus> el: monitoredVariables.entrySet()) {
			
			LocalVariable key = el.getKey();
			VarStatus status = el.getValue();
			
			if(!status.isSinked()) {
				
				BugInstance bugInstance = new BugInstance(this, JMH_UNSINKED_VARIABLE, HIGH_PRIORITY)
						.addClassAndMethod(getMethodDescriptor());

				super.bugReporter.reportBug(bugInstance);
			}
		}

		debugResult();

	}

	private void debugResult() {
		if (DEBUG) {
			System.out.println("Detailed Result");
			for (Entry<LocalVariable, VarStatus> varStatus : monitoredVariables.entrySet()) {

				VarStatus value = varStatus.getValue();
				String toPrint = String.format("%s - %s - %s", varStatus.getKey().getName(), value.isSinked(), value);
				System.out.println(toPrint);

			}
		}
	}


	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {

		switch (seen) {

		case Const.AASTORE:
		case Const.ASTORE:
		case Const.DSTORE:
		case Const.FSTORE:
		case Const.ISTORE:
		case Const.LSTORE:

		case Const.ASTORE_0:
		case Const.DSTORE_0:
		case Const.FSTORE_0:
		case Const.ISTORE_0:
		case Const.LSTORE_0:

		case Const.ASTORE_1:
		case Const.DSTORE_1:
		case Const.FSTORE_1:
		case Const.ISTORE_1:
		case Const.LSTORE_1:

		case Const.ASTORE_2:
		case Const.DSTORE_2:
		case Const.FSTORE_2:
		case Const.ISTORE_2:
		case Const.LSTORE_2:

		case Const.ASTORE_3:
		case Const.DSTORE_3:
		case Const.FSTORE_3:
		case Const.ISTORE_3:
		case Const.LSTORE_3:

		case Const.BASTORE:
		case Const.CASTORE:
		case Const.DASTORE:
		case Const.SASTORE:

			analyzeStore();
			break;

		// FIELD
		case Const.PUTFIELD:
		case Const.PUTSTATIC:
			analyzeFieldStore();

			break;

		// METHOD CALL
		case Const.INVOKEINTERFACE:
		case Const.INVOKESPECIAL:
		case Const.INVOKEVIRTUAL:
		case Const.INVOKESTATIC:

			// debugMethodCall();

			if (isBlackholeConsume()) {
				analyzeBlackholeConsume();

			} else {
				analyzeMethodCall();
			}

			break;

		// RETURN
		// case Const.RETURN: VOID RETURN - Nothing to analyze
		case Const.ARETURN:
		case Const.DRETURN:
		case Const.FRETURN:
		case Const.IRETURN:
		case Const.LRETURN:

			analyzeReturn();
			break;

		}

	}

	private void analyzeReturn() {

		Item stackItem = stack.getStackItem(0);
		LocalVariable localVariable = varTable.getLocalVariable(stackItem.getRegisterNumber(), getPC());

		if (monitoredVariables.containsKey(localVariable)) {
			VarStatus varStatus = monitoredVariables.get(localVariable);
			varStatus.sinkedByReturned = true;
		}

	}

	private void analyzeStore() {

		// Get the variable in stack
		Item stackItem = stack.getStackItem(0);
		LocalVariable local = varTable.getLocalVariable(stackItem.getRegisterNumber(), getPC());

		// Get the variable where it will be stored
		int registerOperand = getRegisterOperand();
		LocalVariable newLocal = varTable.getLocalVariable(registerOperand, getNextPC()); // we have to use next PC here

		// Add dependency of local -> new local
		if (monitoredVariables.containsKey(newLocal) && local != null) {
			VarStatus varStatus = monitoredVariables.get(newLocal);
			varStatus.addDependent(local);
		}

	}

	private void analyzeMethodCall() {

		String signature = this.getSigConstantOperand();
		int referenceOffset = PreorderVisitor.getNumberArguments(signature);

		// Find the reference variable
		LocalVariable reference = varTable.getLocalVariable(referenceOffset, getPC());
		if (reference != null) {

			VarStatus refStatus = monitoredVariables.get(reference);

			// Assign dependency between REF -> PARAMETERS
			for (int index = 0; index < referenceOffset; index++) {

				Item stackItem = stack.getStackItem(index);
				LocalVariable localVariable = varTable.getLocalVariable(stackItem.getRegisterNumber(), getPC());
				if (localVariable != null) {
					refStatus.addDependent(localVariable);
				}
			}

		}

		// Assign dependency between REF -> PARAMETERS
		for (int index = 0; index < referenceOffset; index++) {

			Item stackItem = stack.getStackItem(index);
			LocalVariable localVariable = varTable.getLocalVariable(stackItem.getRegisterNumber(), getPC());
			VarStatus varStatus = monitoredVariables.get(localVariable);
			if (varStatus != null) {
				varStatus.sinkedByMethodCall = true;
			}
		}

	}

	private void analyzeBlackholeConsume() {
		// Get the variable that has be sent to the blackhole
		Item stackItem = stack.getStackItem(0);
		LocalVariable toBeConsumedVar = varTable.getLocalVariable(stackItem.getRegisterNumber(), getPC());

		// Mark the variable as sinked by a blackhole
		if (monitoredVariables.containsKey(toBeConsumedVar)) {
			VarStatus varStatus = monitoredVariables.get(toBeConsumedVar);
			varStatus.sinkedIntoBlackhole = true;
		}

	}

	private void analyzeFieldStore() {

		if (DEBUG) {
			System.out.println("Analyze field stack" + stack);
		}

		Item stackItem2 = stack.getStackItem(0);
		LocalVariable local = varTable.getLocalVariable(stackItem2.getRegisterNumber(), getPC());

		if (monitoredVariables.containsKey(local)) {
			VarStatus varStatus2 = monitoredVariables.get(local);
			varStatus2.sinkedIntoAField = true;

		}

	}

	private boolean isBlackholeConsume() {

		String clazz = getClassConstantOperand();
		String methodName = getNameConstantOperand();

		return clazz.equals("org/openjdk/jmh/infra/Blackhole") && methodName.equals("consume");
	}

	private void debugMethodCall() {

		if (DEBUG) {

			String signature = getSigConstantOperand();
			String clazz = getClassConstantOperand();
			String methodName = getNameConstantOperand();

			int numberArguments = getNumberArguments(signature);

			System.out.println(String.format("Call: %s # Arg: %d Class: %s Name: %s", signature, numberArguments, clazz,
					methodName));
		}

	}

}
