package de.heidelberg.pvs.diego.detectors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;

import de.heidelberg.pvs.diego.beans.LoopBranch;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack.Item;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.Location;

/**
 * Detector that identified a loop definition in the @Benchmark method
 * 
 * @author diego.costa
 *
 */
public class UnsafeLoopInsideBenchmarkDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_UNSAFELOOP_INSIDE_BENCHMARK = "JMH_UNSAFELOOP_INSIDE_BENCHMARK";

	State checkState;
	Set<Integer> storeRegister;
	Set<LoopBranch> registeredLoop;

	private boolean insideLoop;
	
	static boolean DEBUG = true;

	public UnsafeLoopInsideBenchmarkDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitMethod(Method obj) {
		super.visitMethod(obj);

		checkState = State.START;
		storeRegister = new HashSet<>();
		registeredLoop = new HashSet<>();
		
		try {
			CFG cfg = getClassContext().getCFG(obj);
			findLoops(cfg);
			
		} catch (CFGBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Loops found");
		System.out.println(registeredLoop);

	}

	private void findLoops(CFG cfg) {
		

		Iterator<Location> locationIterator = cfg.locationIterator();
		
		while(locationIterator.hasNext()) {
			
			Location location = locationIterator.next();
			InstructionHandle handle = location.getHandle();
			Instruction instruction = handle.getInstruction();
			
			short code = instruction.getOpcode();
			
			switch(code) {
			
			case Const.GOTO:
			case Const.GOTO_W:
				
				GotoInstruction gotoInstruction = ((GotoInstruction) instruction);
				int target = gotoInstruction.getTarget().getPosition();
				int position = handle.getPosition();
				
				LoopBranch loopBranch = new LoopBranch(position, target);
				registeredLoop.add(loopBranch);
			
			}
			
			
		}
		
		
	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {

		int pc = getPC();
		this.insideLoop = isInsideLoop(pc);
		System.out.println(String.format("%d - %s", pc, this.insideLoop));
		
		switch (seen) {
		
		case Const.ISTORE:
		case Const.ISTORE_1:
		case Const.ISTORE_2:
		case Const.ISTORE_3:
		case Const.LSTORE:
		case Const.LSTORE_1:
		case Const.LSTORE_2:
		case Const.LSTORE_3:
		case Const.FSTORE:
		case Const.FSTORE_1:
		case Const.FSTORE_2:
		case Const.FSTORE_3:
		case Const.DSTORE:
		case Const.DSTORE_1:
		case Const.DSTORE_2:
		case Const.DSTORE_3:
			registeringNumericalVariable();
			break;

		case Const.LADD:
		case Const.IADD:
		case Const.FADD:
		case Const.DADD:
			
			analyzeAdition();
			break;

		// FIXME: Find a way to infer getting out of the loop

		}

	}

	private boolean isInsideLoop(int pc) {
		
		for (LoopBranch loop : registeredLoop) {
			if(loop.isInsideLoop(pc)) {
				return true;
			}
		}
		
		return false;
	}

	private void analyzeAdition() {

		if (checkState == State.NUMERIC_CREATED_OUTSIDE_lOOP && this.insideLoop) {

			// Get both items from stack
			Item stackItem = stack.getStackItem(0);
			Item stackItem2 = stack.getStackItem(1);

			boolean wasItem1DefinedOutside = storeRegister.contains(stackItem.getRegisterNumber());
			boolean wasItem2DefinedOutside = storeRegister.contains(stackItem2.getRegisterNumber());

			if (wasItem1DefinedOutside || wasItem2DefinedOutside) {
				checkState = State.ADITION_MADE_IN_LOOP;
				if (DEBUG)
					System.out.println(String.format("CheckState %s", checkState));
			}

		}
	}

	private void registeringNumericalVariable() {

		if (checkState == State.START && getNextOpcode() == Const.GOTO) {
			// This means that the current numeric variable has been assigned for the loop
			// and should not be analyzed
			return;
		}

		if (!this.insideLoop) {
			checkState = State.NUMERIC_CREATED_OUTSIDE_lOOP;
			if (DEBUG)
				System.out.println(String.format("CheckState %s", checkState));
		}

		if (this.insideLoop && checkState == State.ADITION_MADE_IN_LOOP) {

			boolean isAccumulating = storeRegister.contains(getRegisterOperand());

			if (isAccumulating) {
				BugInstance bugInstance = new BugInstance(this, JMH_UNSAFELOOP_INSIDE_BENCHMARK, NORMAL_PRIORITY)
						.addClassAndMethod(this).addSourceLine(this);

				super.bugReporter.reportBug(bugInstance);

				// FIXME: This only allow us to get the first case
				checkState = State.DONE;
				if (DEBUG)
					System.out.println(String.format("CheckState %s", checkState));
			}
		}

		// Register the store
		storeRegister.add(getRegisterOperand());
	}

	enum State {
		START, NUMERIC_CREATED_OUTSIDE_lOOP, NUMERIC_ON_STACK, ADITION_MADE_IN_LOOP, DONE

	}

}
