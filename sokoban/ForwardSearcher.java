package sokoban;


import java.util.BitSet;
import java.util.LinkedList;


public class ForwardSearcher implements Runnable {

	Board board;	
	LinkedList<StateNode> stateNodeQueue;
	char[] dirs = {'U', 'D', 'L', 'R'};

	public ForwardSearcher(Board b, LinkedList<StateNode> queue) {
		board = b;
		stateNodeQueue = queue;
	}

	public void run() {
		while (stateNodeQueue.size() > 0) {
			StateNode currentStateNode = stateNodeQueue.poll();
			for (int i=board.getStateSize(); i<currentStateNode.getState().length(); i++) {
				for (int j=0; j<dirs.length; j++) {
					if (board.successorExists(currentStateNode.getState(), i, dirs[j])) {
						BitSet newState = board.getSuccessorState(currentStateNode.getState(), i, dirs[j]);
						StateNode newStateNode = new StateNode(newState, currentStateNode, true);
						if (board.shouldAddToQueue(newStateNode)) {
							stateNodeQueue.offer(newStateNode);
						}
					}
				}
			}
		}
	}
}
