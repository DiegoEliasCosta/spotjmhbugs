package de.heidelberg.pvs.diego.beans;

public class LoopBranch {

	int start;
	int end;

	public LoopBranch(int pc, int target) {
		super();
		// Swap
		if (pc > target) {
			int tmp = pc;
			pc = target;
			target = tmp;
		}

		this.start = pc;
		this.end = target;
	}
	
	public boolean isInsideLoop(int pc) {
		return pc >= start && pc <= end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoopBranch other = (LoopBranch) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LoopBranch [start=" + start + ", end=" + end + "]";
	}

}
