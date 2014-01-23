package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import sokoban.AStarSearcher;
import sokoban.Board;
import sokoban.PathNotFoundException;
import sokoban.Position;

public class MapTest {
	BufferedReader br;

	@Test
	public final void testConstructor() throws IOException {
		br = new BufferedReader(new FileReader("tests/maps/level6.txt"));
		Board b = new Board(br);
	//	System.out.println(b);
	//	b.printMap();
	//	b.printGhostMap();
	//	b.printSolutionMap();
	//	b.printInitialStateString();
	//	b.printPossibleFinalStates();
	}

	@Test
	public final void testSearch() throws IOException, PathNotFoundException {
		br = new BufferedReader(new FileReader("tests/maps/map1.txt"));
		Board b = new Board(br);
		AStarSearcher ass = new AStarSearcher();
		System.out.println(ass.findPath(b, b.getInitialState(), new Position(3,2), new Position(1,12)));
	}

	@Test
	public final void testSearchToNowhere() throws IOException, PathNotFoundException {
		br = new BufferedReader(new FileReader("tests/maps/map1.txt"));
		Board b = new Board(br);
		AStarSearcher ass = new AStarSearcher();
		System.out.println(ass.findPath(b, b.getInitialState(), new Position(3,2), new Position(3,2)));
	}

	@Test
	public final void testSearchBadGoal() throws IOException, PathNotFoundException {
		br = new BufferedReader(new FileReader("tests/maps/map1.txt"));
		Board b = new Board(br);
		AStarSearcher ass = new AStarSearcher();
		System.out.println(ass.findPath(b, b.getInitialState(), new Position(3,2), new Position(1,11)));
	}

	@Test
	public final void testSearchFail() throws IOException, PathNotFoundException {
		br = new BufferedReader(new FileReader("tests/maps/map1.txt"));
		Board b = new Board(br);
		AStarSearcher ass = new AStarSearcher();
		System.out.println(ass.findPath(b, b.getInitialState(), new Position(3,2), new Position(3,5)));
	}
}
