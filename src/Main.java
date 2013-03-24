import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** The main entry point for the program **/
public class Main 
{
	/* Public static member variables */
	
	//TEST MOVE STRING (Pawn move error): position startpos moves e2e3 a7a6 e3e4 a6a5 d2d3 a5a4 e4e5 a4a3 d3d4
	//WHITE PAWN PROMOTION STRING: position startpos moves e2e4 a7a6 b2b4 a6a5 b4a5 a8a7 a5a6 a7a6 e4e5 a6b6 a2a4 b6c6 a4a5 b7b6 a5a6 b6b5 a6a7 b5b4 a7a8q
	//BLACK PAWN PROMOTION STRING: position startpos moves e2e3 a7a6 e3e4 a6a5 e4e5 a5a4 d2d3 a4a3 d3d4 a3b2 d4d5 b2c1q
	
	//KING-IN-CHECK TEST STRING: position startpos moves g2g4 a7a6 g4g5 a6a5 g5g6 a5a4 g6f7
	
	//Check error string: position startpos moves e2e4 a7a6 d2d4 a6a5 d1g4 a5a4 g4e6 a4a3 e6e7 d8e7 b2a3 a8a7 a3a4 a7a6 a4a5 a6b6 a5a6 b6c6 a6b7 b8a6 b7c8q e7d8 c8d8 e8d8 e4e5 a6b4 e5e6 b4c2 e1d1 c2d4 c1e3 c6d6 e3d4 c7c6 f1d3 c6c5 d4c5 d6e6 c5d6 d8c8 d3a6
	
	public static Board board = new Board();
	public static Evaluation eval = new Evaluation();
	
	public static BufferedReader reader;
    public static String cmd;
    
    static boolean hasMoved = false;
	
	/** Main entry point **/
	public static void main(String args[]) throws IOException 
	{	
        try 
        {
        	reader = new BufferedReader(new InputStreamReader(System.in));
            printGreeting();
            getCmd();
        } 
        catch(Exception ex) 
        {
            System.out.print("info string ");
            System.out.println(ex);
            ex.printStackTrace();
		}
	}
	
    /*
     * method printGreeting()
     * 
     * prints a simple greeting message
     */ 
    public static void printGreeting() {
        System.out.println("*****************DAVINCI CHESS***************");
        System.out.println("*****************Version 1.00***************");
        System.out.println("to play in UCI mode type \"uci\"");
        //System.out.println("to launch GUI type \"launch\"");
		
    }
	/*
     * method uci
     * 
     * enters a while loop and processes input from the user
     * 
     */ 
    public static void uci() throws IOException{
		int movetime;
		int maxMoveTime;
      int searchDepth;
		int wtime=0;
		int btime=0;
		int winc=0;
		int binc=0;
		int togo = 0;
        
		boolean infinite = false;				//infinite time controls
		System.out.println("id name DaVinci");
		System.out.println("id author Joey Freeland");
		
        
		System.out.println("uciok");
		while(true) {
			cmd = reader.readLine();
			if(cmd.startsWith("quit"))
                System.exit(0);
         else if(cmd.equals("eval_dump")) 
         {
         }
         else if ("isready".equals( cmd ))
				System.out.println("readyok");

            if(cmd.startsWith("perft")) {

            }
            if(cmd.startsWith("divide")) {

            }
            if(cmd.startsWith("position")) {
            	
            	if(hasMoved == false)
            	{
            		board.side = Board.SIDE_BLACK;
            		hasMoved = true;
            	}
            	
				if(cmd.indexOf(("startpos"))!= -1) 
				{
					int mstart = cmd.indexOf("moves");
					if(mstart>-1) 
					{
						String moves = cmd.substring(mstart+5);
						board.playMoves(moves);
					}
				} 
				else 
				{
				}
			}	
         else if(cmd.startsWith("setoption")) 
         {
				int index = cmd.indexOf("Hash");
				if(index != -1)  {
					index = cmd.indexOf("value");
					cmd = cmd.substring(index+5);
					cmd = cmd.trim();
					int hashSize = Integer.parseInt(cmd.substring(0));
					System.out.println("info string hashsize is "+hashSize);
				} else if(cmd.indexOf("Evaluation Table")!= -1) {
                    index = cmd.indexOf("value");
                    cmd = cmd.substring(index+5);
					cmd = cmd.trim();
					int evalSize = Integer.parseInt(cmd.substring(0));
                    System.out.println("info string evalHash is "+evalSize);
                } else if(cmd.indexOf("Pawn Table") != -1) {
                    index = cmd.indexOf("value");
                    cmd = cmd.substring(index+5);
					cmd = cmd.trim();
					int evalSize = Integer.parseInt(cmd.substring(0));
                    System.out.println("info string pawnHash is "+evalSize);
                }  else {
                    System.out.println("info string command not recognized");
                }
			}
			
         else if(cmd.startsWith("go")) {
				
        	 	if(!hasMoved)
        	 		board.side = Board.SIDE_WHITE;
        	 	
        	 	String bestMove = board.getBestMove();
				System.out.println("bestmove "+ bestMove);
				//board.playMove(bestMove);
			}	
         else if(cmd.equals("ucinewgame")) 
         {
				board.newGame();
				hasMoved = false;
		 }
		}		
	}	
	/*
     * method getCmd()
     * 
     * gets users commands when program is first launched
     * 
     */ 
    public static void getCmd() throws IOException{
		
		while(true) {
			cmd = reader.readLine();
			if(cmd.equals("uci")) {
				uci();
				break;
			}
            if(cmd.startsWith("quit"))
                System.exit(0);
		}		
	}	

}

/* To-Do list */

//move generation - done
//Check checking - 
//Castling - 
//Pawn promotion - done
