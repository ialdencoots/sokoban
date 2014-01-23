package sokoban;


import java.io.*;
import java.net.*;
import java.util.Date;

public class Client {

	public static void main(String[] pArgs) 
	{
		if(pArgs.length<3)
		{
			System.out.println("usage: java Client host port boardnum");
			return;
		}
	
		try
		{
			Socket lSocket=new Socket(pArgs[0],Integer.parseInt(pArgs[1]));
			PrintWriter lOut=new PrintWriter(lSocket.getOutputStream());
			BufferedReader lIn=new BufferedReader(new InputStreamReader(lSocket.getInputStream()));
	
            lOut.println(pArgs[2]);
            lOut.flush();

            String lLine =lIn.readLine();

            //read number of rows
            //int lNumRows=Integer.parseInt(lLine);

			Board b = new Board(lIn);
			b.printState(b.getInitialState());
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

            //now, we should find a path from the player to any goal

            //we've found our solution
            //String lMySol="U R R U U L D L L U L L D R R R R L D D R U R U D L L U R";
            //these formats are also valid:
            //String lMySol="URRUULDLLULLDRRRRLDDRURUDLLUR";
            //String lMySol="0 3 3 0 0 2 1 2 2 0 2 2 1 3 3 3 3 2 1 1 3 0 3 0 1 2 2 0 3";

            //send the solution to the server
            lOut.println(lMySol);
            lOut.flush();
    
            //read answer from the server
            lLine=lIn.readLine();
    
            System.out.println(lLine);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
