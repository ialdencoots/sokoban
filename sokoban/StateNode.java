package sokoban;

import java.util.BitSet;

public class StateNode {

	BitSet state;
	StateNode parent;
	boolean isForward;

	public StateNode(BitSet state, StateNode parent, boolean isForward) {
		this.state = state;
		this.parent = parent;
		this.isForward = isForward;
	}

	public StateNode getParent() {
		return parent;
	}

	public BitSet getState() {
		return state;
	}

	public boolean isForward() {
		return isForward;
	}
}
