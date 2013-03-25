import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** The main entry point for the program **/
public class Main 
{
	/* Public static member variables */
	
	//WHITE PAWN PROMOTION STRING: position startpos moves e2e4 a7a6 b2b4 a6a5 b4a5 a8a7 a5a6 a7a6 e4e5 a6b6 a2a4 b6c6 a4a5 b7b6 a5a6 b6b5 a6a7 b5b4 a7a8q
	//BLACK PAWN PROMOTION STRING: position startpos moves e2e3 a7a6 e3e4 a6a5 e4e5 a5a4 d2d3 a4a3 d3d4 a3b2 d4d5 b2c1q
	
	//KING-IN-CHECK TEST STRING: position startpos moves g2g4 a7a6 g4g5 a6a5 g5g6 a5a4 g6f7
	
	//Check error string: position startpos moves g1f3 c7c6 f3e5 h7h5 e5c6 d7d5 c6e5 g7g6 e5g6 h8h6 g6h8 f8g7 h8g6 c8g4 g6e5 d8c7 e5c6 a7a5 c6e5 c7c3 e5f7 c3d2 e1d2 h6h8 f7h8 b8a6 h8f7 a8d8 f7d6 e8d7 d6e8 a5a4 e8f6 d7c6 f6g8 d8e8 f2f3 c6c5 f3g4 g7c3 d2c3 b7b6 d1d4 c5b5 d4d5 a6c5 d5c5 b5a6 c5b6
	
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
