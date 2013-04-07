import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** The main entry point for the program **/

/* BENCHMARKS:
 * Check-checking takes up: 69% (4) 65% (3) as of 4/4/13, no optimizations
 * Move-gen takes up: 24% (4) 22% (3) as of 4/4/13, no optimizations
 */
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
            	
            	if(hasMoved == false)
            	{
            		board.gameSide = Board.SIDE_BLACK;
            		hasMoved = true;
            	}
            	
				if(cmd.indexOf(("startpos"))!= -1) 
				{
					int mstart = cmd.indexOf("moves");
					if(mstart>-1) 
					{
						String moves = cmd.substring(mstart+5);
						board.gameBoard = Board.playMoves(moves);
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
        	 		board.gameSide = Board.SIDE_WHITE;
        	 	
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
