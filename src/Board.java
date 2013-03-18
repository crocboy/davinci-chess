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
	
	/** Used to convert Algebraic to Coordinates (and vice-versa) **/
	public static final String[] LETTER_ARRAY = {"a","b","c","d","e","f","g","h"};
	
	
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
	
	
	/** Return a list of all possible moves, in square-notation **/
	public int[][] getAllPossibleMoves()
	{
		return new int[0][0];
	}
	
	
	/** Return a board that is the result of playing the given move on the given board **/
	public static byte[][] getResultBoard(int[] move, byte[][] board)
	{
		return new byte[0][0];
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
	
	
	/** Convert coordinate notation to algebraic notation **/
    public static String numberToLetter(int startX, int startY, int endX, int endY)
    {
            String s = LETTER_ARRAY[startX] + String.valueOf(startY+1) + LETTER_ARRAY[endX] + String.valueOf(endY+1);
            return s;
    }
    
    /** Convert algebraic notation to coordinate location, as an int[] (Square notation) **/
    public static int[] letterToNumber(String s)
    {
            if(s.length() != 4) //Length check
            {
                    return null;
            }
            
            int[] start = new int[2];
            int[] end = new int[2];
            
            start[0] = Util.indexOf(LETTER_ARRAY, String.valueOf(s.charAt(0)));
            start[1] = Integer.parseInt(String.valueOf(s.charAt(1))) - 1;
            end[0] = Util.indexOf(LETTER_ARRAY, String.valueOf(s.charAt(2)));
            end[1] = Integer.parseInt(String.valueOf(s.charAt(3))) - 1;
            
            int[] square = {arrayToNumber(start), arrayToNumber(end)};
            
            return square;
    }
    
    
    /** Convert a single square number (12) to it's coordinate equivalent (4,1) **/
    public static int[] numberToArray(int square)
    {
    	int[] pos = new int[2];
    	pos[0] = square % 8; //Get x-coord
    	pos[1] = square / 8; //Get y-coord
    	return pos;
    }
    
    /** Convert an array coordinate (4,1) to it's single number equivalent (12)**/
    public static int arrayToNumber(int[] coord)
    {
    	int square = coord[1] * 8;
    	square += coord[0];
    	
    	return square;
    }
}
