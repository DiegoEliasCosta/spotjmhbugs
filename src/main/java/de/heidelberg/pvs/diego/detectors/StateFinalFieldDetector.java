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

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

/**
 * Detector that identifies final variables defined in a JMH @State class
 * 
 * @author diego.costa
 *
 */
public class StateFinalFieldDetector extends AbstractJMHStateClassDetector {

	private static final String JMH_STATE_FINAL_PRIMITIVE = "JMH_STATE_FINAL_PRIMITIVE";
	private static final String JMH_STATE_FINAL_STATIC_PRIMITIVE = "JMH_STATE_FINAL_STATIC_PRIMITIVE";

	public StateFinalFieldDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitField(Field obj) {

		// First check whether we are visiting a @State object
		if (super.isTargetStateClass()) {

			// RULE: Fields on @State object should not be declared final
			if (obj.isFinal() && !obj.isSynthetic()) {

				BugInstance bugInstance;

				Type type = obj.getType();

				if (isPrimitiveOrString(type)) {

					// Heuristic: Declared static fields tend to be constants
					if (obj.isStatic()) {

						bugInstance = new BugInstance(this, JMH_STATE_FINAL_STATIC_PRIMITIVE, LOW_PRIORITY)
								.addClass(this).addField(this);

						super.bugReporter.reportBug(bugInstance);

					} else {
						bugInstance = new BugInstance(this, JMH_STATE_FINAL_PRIMITIVE, HIGH_PRIORITY).addClass(this)
								.addField(this);

						super.bugReporter.reportBug(bugInstance);

					}
				}
			}

		}

		super.visitField(obj);
	}

	private boolean isPrimitiveOrString(Type type) {
		return type == Type.FLOAT || type == Type.DOUBLE || type == Type.INT || type == Type.CHAR
				|| type == Type.BOOLEAN || type.equals(Type.STRING) || type == Type.SHORT || type == Type.BYTE
				|| type == Type.LONG;
	}

}
