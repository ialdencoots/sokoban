package sokoban;


import java.io.*;
import java.net.*;
import java.util.Date;

public class SokobanSolver {

	public static void main(String[] pArgs) 
	{
		if(pArgs.length<1)
		{
			System.out.println("usage: java Client file");
			return;
		}
	
		try
		{
			BufferedReader lIn=new BufferedReader(new FileReader(pArgs[0]));
	
            //String lLine =lIn.readLine();

			Board b = new Board(lIn);
			//b.printState(b.getInitialState());
			b.printMap();
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
			System.out.println("Number of visited states: " + b.getNumVisitedStates());
			String lMySol = b.reconstructPath();
			System.out.println(lMySol);

		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
