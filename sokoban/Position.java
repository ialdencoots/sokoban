package sokoban;


public class Position {
	int i, j;

	public Position(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public Position move(char dir) {
		char c = Character.toUpperCase(dir);
		int newI = i;
		int newJ = j;
		switch (c) {
			case 'U':
				newI--;
				break;
			case 'D':
				newI++;
				break;
			case 'L':
				newJ--;
				break;
			case 'R':
				newJ++;	
				break;
		}
		return new Position(newI, newJ);
	}

	public Position oppositeMove(char dir) {
		char c = Character.toUpperCase(dir);
		int newI = i;
		int newJ = j;
		switch (c) {
			case 'D':
				newI--;
				break;
			case 'U':
				newI++;
				break;
			case 'R':
				newJ--;
				break;
			case 'L':
				newJ++;	
				break;
		}
		return new Position(newI, newJ);
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		final int prime2 = 107;
		int result = 1;
		result = prime * result + i;
		result = prime2 * result + j;
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
		Position other = (Position) obj;
		if (i != other.i)
			return false;
		if (j != other.j)
			return false;
		return true;
	}
}
