package sokoban;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

public class AStarSearcher {
	PriorityQueue<Node> openList = new PriorityQueue<Node>();
	HashSet<Position> closedList = new HashSet<Position>();
	char[] dirs = {'U', 'D', 'L', 'R'};
	StringBuffer pathBuilder = new StringBuffer();

	public String findPath(Board b, BitSet state, Position source, Position goal) throws PathNotFoundException {
		if (!b.isPlayerAccessible(state, goal))
			throw new PathNotFoundException();
		openList.clear();
		closedList.clear();
		pathBuilder.delete(0, pathBuilder.length());
		openList.offer(new Node(source, goal));
		return search(b, state, goal);
	}

	private String search(Board b, BitSet state, Position goal) throws PathNotFoundException {
		while (openList.size() > 0) {
			Node current = openList.poll();
			if (current.pos.equals(goal)) {
				return reconstructPath(current);
			}
			for (char dir : dirs) {
				Position newNodePos = current.pos.move(dir);
				if (closedList.contains(newNodePos))
					continue;
				if(b.isPlayerAccessible(state, newNodePos)) {
					Node successor = new Node(current, dir, newNodePos, goal);
					addToOpenList(successor);
				}
			}
		}
		throw new PathNotFoundException();
	}

	private String reconstructPath(Node current) {
		if (current.parent == null) {
			return pathBuilder.reverse().toString();
		}
		else {
			pathBuilder.append(Character.toLowerCase(current.dir));
			return reconstructPath(current.parent);
		}
	}

	private void addToOpenList(Node successor) {
		Iterator<Node> iter = openList.iterator();
		while (iter.hasNext()) {
			Node match = iter.next();
			if (match.equals(successor)) {
				if (successor.g < match.g) {
					openList.remove(match);
					openList.offer(successor);
					return;
				}
				else return;
			}
		}
		openList.offer(successor);
	}

	private int calculateManhattanDistance(Position current, Position goal) {
		return Math.abs(goal.getI() - current.getI()) + Math.abs(goal.getJ() - current.getJ());
	}

	private class Node implements Comparable<Node> {
		Node parent;
		char dir;
		Position pos;
		int g, h, f;

		public Node(Position position, Position goal) {
			pos = position;
			g = 0;
			h = calculateManhattanDistance(position, goal);
			f = h;
		}

		public Node(Node parent, char dir, Position pos, Position goal) {
			this.parent = parent;
			this.dir = dir;
			this.pos = pos;
			g = parent.g + 1;
			h = calculateManhattanDistance(pos, goal);
			f = g + h;
		}

		@Override
		public int compareTo(Node arg0) {
			return this.f - arg0.f;
		}

		@Override
		public int hashCode() {
			return pos.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (pos == null) {
				if (other.pos != null)
					return false;
			} else if (!pos.equals(other.pos))
				return false;
			return true;
		}

		private AStarSearcher getOuterType() {
			return AStarSearcher.this;
		}	
	}
}

