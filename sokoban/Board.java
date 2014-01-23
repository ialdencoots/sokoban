package sokoban;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.LinkedList;

public class Board {

	int[][] bitMap;  //bitMap[i][j] is the index of Position i,j in a given state bit string
	int[] rowMap;	//rowMap[i] is the row index in the map for bit i in a state BitSet
	int[] colMap;	//colMap[i] is the column index in the map for a bit i in a state BitSet
	char[][] map;
	char[][] solutionMap;
	char[][] ghostMap;
	int stateSize = 0;
	int initialPlayerRow, initialPlayerCol;
	BitSet initialState;
	ArrayList<StateNode> possibleFinalStates;
	Hashtable<BitSet, StateNode> visitedStates = new Hashtable<BitSet, StateNode>(10000); //K: BitSet, V: StateNode
	StateNode forwardLeaf;
	StateNode backwardRoot;
	boolean solutionFound;
	Position initialPlayerPositionAtMerge;
	int numVisitedStates = 0;
	int numForwardStates = 0;
	int numBackwardStates = 0;

	public Board(BufferedReader br) throws IOException {
		int maxRowSize = 0;
		int numRows = 0;

		ArrayList<String> lines = new ArrayList<String>();
		while (br.ready()) {
			numRows++;
			String line = br.readLine();
			if (line.length() > maxRowSize)
				maxRowSize = line.length();
			lines.add(line);
		}
		map = new char[numRows][maxRowSize];
		solutionMap = new char[numRows][maxRowSize];
		ghostMap = new char[numRows][maxRowSize];
		bitMap = new int[numRows][maxRowSize];
		ArrayList<Integer> rowIndices = new ArrayList<Integer>();
		ArrayList<Integer> colIndices = new ArrayList<Integer>();

		for (int i=0; i<lines.size(); i++) {
			String line = lines.get(i);
			for (int j=0; j<maxRowSize; j++) {
				bitMap[i][j] = -1;
				if (j < line.length()) {
					map[i][j] = line.charAt(j);
					solutionMap[i][j] = getSolutionChar(map[i][j]);
					ghostMap[i][j] = getGhostChar(map[i][j]);
					if (map[i][j] != '#') {
						bitMap[i][j] = stateSize;
						rowIndices.add(i);
						colIndices.add(j);
						stateSize++;
					}
					if (map[i][j] == '@' || map[i][j] == '+') {
						initialPlayerRow = i;
						initialPlayerCol = j;
					}

				}
				else map[i][j] = ' ';
			}
		}

		rowMap = new int[stateSize];
		colMap = new int[stateSize];
		for (int i=0; i<stateSize; i++) {
			rowMap[i] = rowIndices.get(i);
			colMap[i] = colIndices.get(i);
		}

		initialState = getInitialStateFromMap();
		possibleFinalStates = getPossibleFinalStates();

		System.out.println("State Size: " + stateSize);
	}

	public synchronized boolean shouldAddToQueue(StateNode sn) {
		if (solutionFound) return false;
		if (!visitedStates.containsKey(sn.getState())) {
			numVisitedStates++;
			if (sn.isForward())
				numForwardStates++;
			else numBackwardStates++;

			visitedStates.put(sn.getState(), sn);
			return true;
		}
		else {
			StateNode exploredStateNode = (StateNode)visitedStates.get(sn.getState());
			if (exploredStateNode.isForward() == sn.isForward()) {
				return false;
			}
			else {
//				printStateString(sn.getState());
				System.out.println("Solution found?");
				if (sn.isForward()) {
					forwardLeaf = sn;
					backwardRoot = exploredStateNode;
				}
				else {
					forwardLeaf = exploredStateNode;
					backwardRoot = sn;
				}
				solutionFound = true;
			}
		}
		return false;
	}

	public String reconstructPath() throws PathNotFoundException {
		StringBuffer path = new StringBuffer();
		AStarSearcher pathFinder = new AStarSearcher();

		Position finalPlayerPos = null;
		Position initialPlayerPos;
		StateNode current = forwardLeaf;
		while (current.getParent() != null) {
			StateNode previous = current.getParent();
			BitSet boxFinder = current.getState().get(stateSize, current.getState().length());
			boxFinder.xor(previous.getState().get(stateSize, previous.getState().length()));
			int box1Indx = boxFinder.nextSetBit(0);
			int box2Indx = boxFinder.nextSetBit(box1Indx+1);
			int fromIndx = (previous.getState().get(box1Indx + stateSize)) ? box1Indx : box2Indx;
			int toIndx = (box1Indx == fromIndx) ? box2Indx : box1Indx;
			char boxPushDir = getBoxPushChar(fromIndx, toIndx);

			initialPlayerPos = new Position(rowMap[fromIndx], colMap[fromIndx]);
			if (finalPlayerPos != null) {
				path.insert(0, pathFinder.findPath(this, current.getState(), initialPlayerPos, finalPlayerPos));
			}

			path.insert(0, boxPushDir);

			finalPlayerPos = (new Position(rowMap[fromIndx], colMap[fromIndx]).oppositeMove(boxPushDir));

			if (initialPlayerPositionAtMerge == null) {
				initialPlayerPositionAtMerge = initialPlayerPos;
			}
			current = previous;
		}
		//Prepend path from initial player position to position before first box push
		if (!forwardLeaf.getState().equals(initialState)) {
			path.insert(0, pathFinder.findPath(this, current.getState(), new Position(initialPlayerRow, initialPlayerCol), finalPlayerPos));
			initialPlayerPos = null;
		}
		else {
			initialPlayerPos = new Position(initialPlayerRow, initialPlayerCol);
		}

		current = backwardRoot;
		while (current.getParent() != null) {
			StateNode next = current.getParent();
			BitSet boxFinder = current.getState().get(stateSize, current.getState().length());
			boxFinder.xor(next.getState().get(stateSize, next.getState().length()));
			int box1Indx = boxFinder.nextSetBit(0);
			int box2Indx = boxFinder.nextSetBit(box1Indx+1);
			int fromIndx = (current.getState().get(box1Indx + stateSize)) ? box1Indx : box2Indx;
			int toIndx = (box1Indx == fromIndx) ? box2Indx : box1Indx;
			char boxPushDir = getBoxPushChar(fromIndx, toIndx);

			Position initialBoxPos = new Position(rowMap[fromIndx], colMap[fromIndx]);
			finalPlayerPos = initialBoxPos.oppositeMove(boxPushDir);
			if (initialPlayerPos == null) {
				path.append(pathFinder.findPath(this, current.getState(), initialPlayerPositionAtMerge, finalPlayerPos));
			}
			else {
				path.append(pathFinder.findPath(this, current.getState(), initialPlayerPos, finalPlayerPos));
			}
			path.append(boxPushDir);

			initialPlayerPos = initialBoxPos;

			current = next;
		}

		return path.toString();
	}


	//Indices are ints between 0 and stateSize
	private char getBoxPushChar(int fromIndx, int toIndx) {
		int fromRow = rowMap[fromIndx];
		int toRow = rowMap[toIndx];
		if (fromRow > toRow)
			return 'U';
		if (toRow > fromRow)
			return 'D';
		int fromCol = colMap[fromIndx];
		int toCol = colMap[toIndx];
		if (fromCol > toCol)
			return 'L';
		if (toCol > fromCol)
			return 'R';
		return 'X';
	}

	private BitSet getInitialStateFromMap() {
		BitSet state = new BitSet(2*stateSize);
		setBoxBitsFromMap(state);
		setPlayerAccessibleBits(state, initialPlayerRow, initialPlayerCol);
		return state;
	}

	public LinkedList<StateNode> getInitialForwardList() {
		LinkedList<StateNode> forwardList = new LinkedList<StateNode>();
		StateNode initialForwardStateNode = new StateNode(initialState, null, true);
		forwardList.add(initialForwardStateNode);
		visitedStates.put(initialState, initialForwardStateNode);
		return forwardList;
	}

	public LinkedList<StateNode> getInitialBackwardList() {
		LinkedList<StateNode> backwardList = new LinkedList<StateNode>();
		for (StateNode sn : possibleFinalStates) {
			backwardList.add(sn);
			visitedStates.put(sn.getState(), sn);
		}
		return backwardList;
	}

	private void setBoxBitsFromFinalMap(BitSet state) {
		for (int i=0; i<solutionMap.length; i++) {
			for (int j=0; j<solutionMap[0].length; j++) {
				if (isBox(solutionMap[i][j]))
					state.set(bitMap[i][j] + stateSize);
			}
		}
	}

	//Would be good to add deadlock checks here
	public boolean successorExists(BitSet state, int boxIndx, char dir) {
		//Not a box
		if (!state.get(boxIndx))
			return false;

		Position boxPos = new Position(rowMap[boxIndx-stateSize], colMap[boxIndx-stateSize]);
		return (isPlayerAccessible(state, boxPos.oppositeMove(dir)) && isBoxAccessible(state, boxPos.move(dir)));
	}

	public BitSet getSuccessorState(BitSet state, int boxIndx, char dir) {
		//Clone state and clear player accessibility bits
		BitSet successor = (BitSet)state.clone();
		successor.clear(0, stateSize);

		//Clear current box pos and set target box pos bits
		successor.clear(boxIndx);
		Position boxPos = new Position(rowMap[boxIndx-stateSize], colMap[boxIndx-stateSize]);
		Position target = boxPos.move(dir);
		Position playerPos = boxPos.oppositeMove(dir);
		successor.set(bitMap[target.getI()][target.getJ()]+stateSize);
		setPlayerAccessibleBits(successor, playerPos.getI(), playerPos.getJ());
		return successor;
	}

	public boolean precessorExists(BitSet state, int boxIndx, char dir) {
		//Not a box
		if (!state.get(boxIndx))
			return false;

		Position boxTarget = (new Position(rowMap[boxIndx-stateSize], colMap[boxIndx-stateSize])).move(dir);
		return (isPlayerAccessible(state, boxTarget) && isPlayerAccessible(state, boxTarget.move(dir)));
	}

	public BitSet getPrecessorState(BitSet state, int boxIndx, char dir) {
		//Clone state and clear player accessibility bits
		BitSet precessor = (BitSet)state.clone();
		precessor.clear(0, stateSize);

		precessor.clear(boxIndx);
		Position boxPos = new Position(rowMap[boxIndx-stateSize], colMap[boxIndx-stateSize]);
		Position target = boxPos.move(dir);
		Position playerPos = target.move(dir);
		precessor.set(bitMap[target.getI()][target.getJ()]+stateSize);
		setPlayerAccessibleBits(precessor, playerPos.getI(), playerPos.getJ());
		return precessor;
	}

	private ArrayList<StateNode> getPossibleFinalStates() {
		ArrayList<StateNode> finals = new ArrayList<StateNode>();
		BitSet accessibility = new BitSet(stateSize);

		BitSet boxSol = new BitSet(2*stateSize);
		setBoxBitsFromFinalMap(boxSol);
		accessibility = boxSol.get(stateSize, 2*stateSize);
		System.out.println("Num Boxes: " + accessibility.cardinality());

		while (accessibility.cardinality() < stateSize) {
			BitSet sol = (BitSet)boxSol.clone();
			int firstClearSpot = accessibility.nextClearBit(0);
			setPlayerAccessibleBits(sol, rowMap[firstClearSpot], colMap[firstClearSpot]);
			finals.add(new StateNode(sol, null, false));
			for (int i=0; i<stateSize; i++) {
				if (sol.get(i)) {
					accessibility.set(i);
				}
			}
		}
		return finals;
	}

	private void setBoxBitsFromMap(BitSet state) {
		for (int i=0; i<map.length; i++) {
			for (int j=0; j<map[0].length; j++) {
				if (isBox(map[i][j]))
					state.set(bitMap[i][j] + stateSize);
			}
		}
	}

	private void setPlayerAccessibleBit(BitSet state, int row, int col) {
			state.set(bitMap[row][col]);
	}

	//To be called on an incomplete state where box bits have been filled
	private void setPlayerAccessibleBits(BitSet state, int row, int col) {
		setPlayerAccessibleBit(state, row, col);
			//Up
			if (playerCanOccupy(state, row-1, col) && !isPlayerAccessible(state, row-1, col)) {
				setPlayerAccessibleBits(state, row-1, col);
			}
			//Down
			if (playerCanOccupy(state, row+1, col) && !isPlayerAccessible(state, row+1, col)) {
				setPlayerAccessibleBits(state, row+1, col);
			}
			//Left
			if (playerCanOccupy(state, row, col-1) && !isPlayerAccessible(state, row, col-1)) {
				setPlayerAccessibleBits(state, row, col-1);
			}
			//Right
			if (playerCanOccupy(state, row, col+1) && !isPlayerAccessible(state, row, col+1)) {
				setPlayerAccessibleBits(state, row, col+1);
			}
	}

	//To be called on a completed state
	public boolean isPlayerAccessible(BitSet state, int row, int col) {
		if (row < 0 || col < 0 || row > map.length || col > map[0].length || bitMap[row][col] == -1)
			return false;
		return state.get(bitMap[row][col]);
	}

	//To be called on a completed state
	public boolean isPlayerAccessible(BitSet state, Position pos) {
		int row = pos.getI();
		int col = pos.getJ();
		if (row < 0 || col < 0 || row > map.length || col > map[0].length || bitMap[row][col] == -1)
			return false;
		return state.get(bitMap[row][col]);
	}

	public boolean isBoxAccessible(BitSet state, Position pos) {
		int row = pos.getI();
		int col = pos.getJ();
		if (row < 0 || col < 0 || row > map.length || col > map[0].length || bitMap[row][col] == -1)
			return false;
		return (bitMap[row][col] != -1 && !state.get(bitMap[row][col]+stateSize));

	}

	private boolean isBox(char c) {
		switch (c) {
			case '$':
			case '*':
			case 'X':
				return true;
			default:
				return false;
		}
	}

	//To be called on a state where only box bits have been set
	private boolean playerCanOccupy(BitSet state, int row, int col) {
		if (row < map.length && row > -1 && col < map[0].length && col > -1) {
			if (state.get(bitMap[row][col] + stateSize))
				return false;
			char type = ghostMap[row][col];
			switch (type) {
				case '.':
				case ' ':
					return true;
				default:
					return false;
			}
		}
		return false;
	}

	private char getSolutionChar(char c) {
		switch(c) {
			case '.':
			case '+':
			case '*':
				return 'X';
			case '#':
				return '#';
			default:
				return ' ';
		}
	}

	private char getGhostChar(char c) {
		switch(c) {
			case '$':
			case ' ':
			case '@':
				return ' ';
			case '.':
			case '+':
			case '*':
				return '.';
			case '#':
				return '#';
			default:
				return 'å';
		}
	}

	public String toString() {
		String output = new String();
		for (int i=0; i<map.length; i++) {
			output += String.valueOf(map[i]);
			output += "\n";
		}
		return output;
	}

	public void printMap() {
		printState(initialState);
	}

	public void printSolutionMap() {
		String output = new String();
		for (int i=0; i<solutionMap.length; i++) {
			output += String.valueOf(solutionMap[i]);
			output += "\n";
		}
		System.out.println(output);
	}

	public void printGhostMap() {
		String output = new String();
		for (int i=0; i<ghostMap.length; i++) {
			output += String.valueOf(ghostMap[i]);
			output += "\n";
		}
		System.out.println(output);
	}

	public synchronized void printStateString(BitSet state) {
		for (int i=stateSize; i<2*stateSize; i++) {
			if (i == stateSize)
				System.out.println();
			if (state.get(i))
				System.out.print("1 ");
			else System.out.print("0 ");
		}
		System.out.println();
	}

	public synchronized void printState(BitSet state) {
		for (int i=0; i<ghostMap.length; i++) {
			for (int j=0; j<ghostMap[0].length; j++) {
				System.out.print(getCharInState(state, i, j));
			}
			System.out.println();
		}
		System.out.println();
	}

	public synchronized void printState(StateNode state) {
		String dir;
		if (state.isForward)
			dir = "Forward state:";
		else dir = "Backward state:";
		System.out.println(dir);
		printState(state.getState());
	}

	private char getCharInState(BitSet state, int row, int col) {
		switch (ghostMap[row][col]) {
			case '#':
				return '#';
			case '.':
				if (state.get(bitMap[row][col] + stateSize))
					return '*';
				else return '.';
			case ' ':
				if (bitMap[row][col] != -1 && state.get(bitMap[row][col] + stateSize))
					return '$';
				else return ' ';
			default:
				return ' ';
		}
	}

	public BitSet getInitialState() {
		return initialState;
	}

	public int getStateSize() {
		return stateSize;
	}

	public boolean solutionFound() {
		return solutionFound;
	}

	public int getNumVisitedStates() {
		return numVisitedStates;
	}

	public void printInitialStateString() {
		System.out.println("Initial State:");
		for (int i=0; i<2*stateSize; i++) {
			if (i == stateSize)
				System.out.println();
			if (initialState.get(i))
				System.out.print("1 ");
			else System.out.print("0 ");
		}
		System.out.println();
	}


	public void printPossibleFinalStates() {
		for (int i=0; i<possibleFinalStates.size(); i++) {
			System.out.println("Final State " + i + ":");
			printStateString(possibleFinalStates.get(i).getState());
			System.out.println();
		}
	}

}
