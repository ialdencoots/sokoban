package sokoban;


public class Path {

		String path;
		int playerRow;
		int playerCol;
		boolean isForward;

		public Path(String path, int row, int col, boolean isFoward) {
			this.path = path;
			playerRow = row;
			playerCol = col;
			this.isForward = isFoward;
		}
	
		public int getPlayerRow() {
			return playerRow;
		}

		public int getPlayerCol() {
			return playerCol;
		}

		public String getPath() {
			return path;
		}

		public boolean isForward() {
			return isForward;
		}
}

