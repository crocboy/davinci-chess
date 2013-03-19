/** The main entry point for the program **/
public class Main 
{
	/* Public static member variables */
	public static Board board = new Board();
	public static Evaluation eval = new Evaluation();
	
	public static void main(String[] args)
	{
		board.getPawnMoves(8);
		
		System.out.println("Done!");
	}

}
