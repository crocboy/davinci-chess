/** The main entry point for the program **/
public class Main 
{
	/* Public static member variables */
	public static Board board = new Board();
	public static Evaluation eval = new Evaluation();
	
	public static void main(String[] args)
	{
		board.getKnightMoves(1);
		
		System.out.println("Done!");
	}

}

/* To-Do list */

//pawn moves - done
//knight moves - done
