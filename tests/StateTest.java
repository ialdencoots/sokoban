package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import sokoban.Board;

public class StateTest {
	BufferedReader br;

	@Test
	public final void testSuccessor() throws IOException {
		br = new BufferedReader(new FileReader("tests/maps/map2.txt"));
		Board b = new Board(br);
		BitSet initialState = b.getInitialState();
		BitSet newState = new BitSet(2*12);
		for (int i=0; i<12; i++) {
			if (i!=1) 
				newState.set(i);
		}
		newState.set(1+12);
		b.printState(initialState);
		b.printStateString(initialState);
		BitSet successor = b.getSuccessorState(initialState, 12+5, 'U');
		b.printState(successor);
		b.printStateString(successor);
		assertEquals(newState, b.getSuccessorState(initialState, 12+5, 'U'));
	}

	@Test
	public final void testPrecessor() throws IOException {
		br = new BufferedReader(new FileReader("tests/maps/map2.txt"));
		Board b = new Board(br);
		BitSet initialState = b.getInitialState();
		BitSet newState = new BitSet(2*12);
		for (int i=0; i<12; i++) {
			if (i!=6) 
				newState.set(i);
		}
		newState.set(6+12);
		b.printState(initialState);
		b.printStateString(initialState);
		BitSet precessor = b.getPrecessorState(initialState, 12+5, 'R');
		b.printState(precessor);
		b.printStateString(precessor);
		assertEquals(newState, b.getPrecessorState(initialState, 12+5, 'R'));
	}

	@Test
	public final void testExists() throws IOException {
		br = new BufferedReader(new FileReader("tests/maps/map2.txt"));
		Board b = new Board(br);
		BitSet initialState = b.getInitialState();
		assertEquals(true, b.precessorExists(initialState, 5+12, 'R'));
		assertEquals(false, b.precessorExists(initialState, 5+12, 'L'));
		assertEquals(false, b.precessorExists(initialState, 5+12, 'D'));
		assertEquals(false, b.precessorExists(initialState, 5+12, 'U'));

		assertEquals(true, b.successorExists(initialState, 5+12, 'U'));
		assertEquals(true, b.successorExists(initialState, 5+12, 'D'));
		assertEquals(true, b.successorExists(initialState, 5+12, 'L'));
		assertEquals(true, b.successorExists(initialState, 5+12, 'R'));
	}
}
