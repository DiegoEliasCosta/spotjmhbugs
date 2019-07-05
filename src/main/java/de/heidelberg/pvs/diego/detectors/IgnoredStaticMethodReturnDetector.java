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

import org.apache.bcel.Const;
import org.apache.commons.lang.StringUtils;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import edu.umd.cs.findbugs.classfile.analysis.AnnotationValue;

/**
 * Checker for identifying static methods call with ignored return values on JMH benchmarks.
 * 
 * @author diego.costa
 *
 */
public class IgnoredStaticMethodReturnDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_IGNORED_STATIC_METHOD_RETURN = "JMH_IGNORED_STATIC_METHOD_RETURN";
	private static final String JMH_IGNORED_STATIC_PRIMITIVE_METHOD_RETURN = "JMH_IGNORED_STATIC_PRIMITIVE_METHOD_RETURN";

	/**
	 * Operation codes related to a method call
	 */
	private static final BitSet INVOKE_OPCODE_SET = new BitSet();
	static {
		INVOKE_OPCODE_SET.set(Const.INVOKESTATIC);
	}

	public IgnoredStaticMethodReturnDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	private boolean isDiscardOperation(int nextOpcode) {
		return nextOpcode == Const.POP || nextOpcode == Const.POP2;
	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {
		
		// Investigate the method calls (invoke op code)
		if (INVOKE_OPCODE_SET.get(seen)) {

			int nextOpcode = getNextOpcode();
			if (isDiscardOperation(nextOpcode)) {
				
				BugInstance bugInstance = null;
				
				if(parametersOnlyPrimitives()) {
					
					// Create the bug
					bugInstance = new BugInstance(this, JMH_IGNORED_STATIC_PRIMITIVE_METHOD_RETURN, HIGH_PRIORITY)
							.addClassAndMethod(this).addSourceLine(this);
					
				} else {
					
					// Create the bug
					bugInstance = new BugInstance(this, JMH_IGNORED_STATIC_METHOD_RETURN, NORMAL_PRIORITY)
							.addClassAndMethod(this).addSourceLine(this);
				}
				
				super.bugReporter.reportBug(bugInstance);
			}

		}

	}

	private boolean parametersOnlyPrimitives() {
		
		XMethod xMethodOperand = getXMethodOperand();
		String signature = xMethodOperand.getSignature();
		String parameters = StringUtils.substringBetween(signature, "(", ")");
		
		// Any object has the following signature L(.+?);
		boolean matches = parameters.matches(".*L(.+?);.*");
		// Arrays of primitives has the signature [J -> array of longs 
		boolean arrayMatches = parameters.matches(".*\\[.*");
		
		return !matches && !arrayMatches;
	}

}
