import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** The main entry point for the program **/

/* BENCHMARKS:
 * Check-checking takes up: 69% (4) 65% (3) as of 4/4/13, no optimizations
 * Move-gen takes up: 24% (4) 22% (3) as of 4/4/13, no optimizations
 */

//King gone String: position startpos moves e2e4 a7a6 b1c3 b7b5 c3b5 a6a5 b5a7 b8a6 a7c8 a5a4 c8d6 c7d6 e4e5 g8f6 e5f6 a4a3 b2a3 a6b4 a1b1

//Other error string: position startpos moves e2e3 b8c6 d1f3

//Pawn promotion error string: position startpos moves g1f3 b8c6 b1c3 e7e6 h2h4 f8b4 a2a3 b4a5 d2d3 a5c3 b2c3 d8f6 c1b2 g8e7 g2g3 c6e5 f3e5 f6e5 f1g2 e5b5 a1b1 e6e5 c3c4 b5c5 e2e3 d7d6 d3d4 c5c4 d4e5 c8g4 f2f3 g4f5 e3e4 f5e6 h4h5 c4c5 f3f4 d6e5 f4e5 c5e3 d1e2 e3g3 e2f2 g3f2 e1f2 a8d8 b1d1 d8d1 h1d1 e6c4 b2c3 f7f6 e5f6 g7f6 c3f6 h8f8 e4e5 e7c6 d1e1 c4f1 f2f1 c6d4 e1c1 b7b6 c2c3 d4f5 a3a4 f5e3 f1f2 e3g4 f2g1 g4f6 e5f6 e8d8 c1f1 d8c8 g2e4 f8g8 g1f2 g8f8 f1g1 c8b8 f2e3 f8e8 h5h6 a7a6 g1f1 e8g8 f1f2 g8g3 f2f3 g3g4 f6f7 g4h4 f7f8q
public class Main 
{
	/* Public static member variables */
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
            	
            	/*if(hasMoved == false)
            	{
            		board.gameSide = Board.SIDE_BLACK;
            		hasMoved = true;
            	}*/
            	
				if(cmd.indexOf(("startpos"))!= -1) 
				{
					int mstart = cmd.indexOf("moves");
					if(mstart>-1) 
					{
						String moves = cmd.substring(mstart+5);
						board.gameBoard = Board.playMoves(moves);
						moves = moves.trim();
						String[] strArr = moves.split("\\s");
						if( strArr.length % 2 == 1)
						{
							board.gameSide = Board.SIDE_BLACK;
						}
						else
						{
							board.gameSide = Board.SIDE_WHITE;
						}
					}
				} 
				else 
				{
					board.gameSide = Board.SIDE_WHITE;
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
				
        	 	//if(!hasMoved)
        	 		//board.gameSide = Board.SIDE_WHITE;
        	 	
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
