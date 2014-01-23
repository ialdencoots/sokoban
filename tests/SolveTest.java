package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import sokoban.AStarSearcher;
import sokoban.BackwardSearcher;
import sokoban.Board;
import sokoban.ForwardSearcher;
import sokoban.PathNotFoundException;
import sokoban.Position;

public class SolveTest {
	BufferedReader br;

	@Test
	public final void testSolve() throws IOException, PathNotFoundException {
		br = new BufferedReader(new FileReader("tests/maps/level21.txt"));
		Board b = new Board(br);
		String solution;
		Thread forwardThread = new Thread(new ForwardSearcher(b, b.getInitialForwardList()));
		Thread backwardThread = new Thread(new BackwardSearcher(b, b.getInitialBackwardList()));
		forwardThread.start();
		backwardThread.start();
		while (!b.solutionFound()) {
			System.out.print("");
		}
		System.out.println("Solution found!");
		forwardThread.interrupt();
		backwardThread.interrupt();
		solution = b.reconstructPath();
		System.out.println("Number of visited states: " + b.getNumVisitedStates());
		System.out.println(solution);
	}
}
