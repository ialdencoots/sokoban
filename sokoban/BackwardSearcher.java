package sokoban;


import java.util.BitSet;
import java.util.LinkedList;


public class BackwardSearcher implements Runnable {

	Board board;
	LinkedList<StateNode> stateNodeQueue;
	char[] dirs = {'U', 'D', 'L', 'R'};

	public BackwardSearcher(Board b, LinkedList<StateNode> queue) {
		board = b;
		stateNodeQueue = queue;
	}

	public void run() {
		while (stateNodeQueue.size() > 0) {
			StateNode currentStateNode = stateNodeQueue.poll();
			//System.out.println("Current:");
			//board.printState(currentStateNode.getState());
			for (int i=board.getStateSize(); i<currentStateNode.getState().length(); i++) {
				for (int j=0; j<dirs.length; j++) {
					if (board.precessorExists(currentStateNode.getState(), i, dirs[j])) {
					//	System.out.println("A precessor exists going " + dirs[j] +" from box " + i);
						BitSet newState = board.getPrecessorState(currentStateNode.getState(), i, dirs[j]);
					//	board.printState(newState);
						StateNode newStateNode = new StateNode(newState, currentStateNode, false);
						if (board.shouldAddToQueue(newStateNode)) {
							stateNodeQueue.offer(newStateNode);
					//		System.out.println("Adding to Q\n");
						}
					//	else
					//		System.out.println("Not deemed worthy\n");
					}
				}
			}
		}
	}
}
