/** This class represents our board.
 * It also generates possible moves.
 * @author Joey Freeland
 *
 */
public class Board 
{
	/* These are all of the static variables used by this class and other classes */
	
	/** These bytes define piece values **/
	public static final byte WHITE_PAWN = 1;
	public static final byte WHITE_BISHOP = 2;
	public static final byte WHITE_KNIGHT = 3;
	public static final byte WHITE_ROOK = 4;
	public static final byte WHITE_QUEEN = 5;
	public static final byte WHITE_KING = 6;
	
	public static final byte BLACK_PAWN = 7;
	public static final byte BLACK_BISHOP = 8;
	public static final byte BLACK_KNIGHT = 9;
	public static final byte BLACK_ROOK = 10;
	public static final byte BLACK_QUEEN = 11;
	public static final byte BLACK_KING = 12;
	
	/** Empty square **/
	public static final byte EMPTY = 0;
	
	/** Out of bounds square **/
	public static final byte OOB = -1;
	
	
	/* These are instance variables */
	
	
	/** Defines starting position **/
	public static final byte[][] init = 
			
		{{10,9,8,11,12,8,9,10},
		{7,7,7,7,7,7,7,7},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0},
		{1,1,1,1,1,1,1,1},
		{4,3,2,5,6,2,3,4}};

	
	
	/** Defines our trimmed and padded board **/
	byte[][] fullBoard = new byte[12][12];
	byte[][] board = new byte[8][8];
	
	/** Public constructor: Empty **/
	public Board()
	{
		//
		int i = board[0][0];
		board[1][1] = (byte) i;
	}
	
	/** Take a full 12x12 array and trim it to an 8x8 array **/
	public static byte[][] trimBoard(byte[][] board)
	{
		/* Size check */
		if(board[0].length != 12 || board.length != 12)
			return null;

		//Stuff
		return board;
		
	}
	
	
	/** Take an 8x8 array and add 2 layers of padding on each side **/
	public static byte[][] padBoard(byte[][] board)
	{
		/* Size check */
		if(board[0].length != 8 || board.length != 8)
			return null;
		
		return board;
	}
}
