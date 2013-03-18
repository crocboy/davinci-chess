/** This class represents our board.
 * It also generates possible moves.
 * @author Joey Freeland
 *
 */
public class Board 
{
	/** These bytes define piece values **/
	public static final byte WHITE_PAWN = 0;
	public static final byte WHITE_BISHOP = 1;
	public static final byte WHITE_KNIGHT = 2;
	public static final byte WHITE_ROOK = 3;
	public static final byte WHITE_QUEEN = 4;
	public static final byte WHITE_KING = 5;
	
	public static final byte BLACK_PAWN = 6;
	public static final byte BLACK_BISHOP = 7;
	public static final byte BLACK_KNIGHT = 8;
	public static final byte BLACK_ROOK = 9;
	public static final byte BLACK_QUEEN = 10;
	public static final byte BLACK_KING = 11;
	
	
	/** Defines starting position **/
	//public static final byte[][] init = {{

	
	
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
