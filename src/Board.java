import java.util.ArrayList;

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
	
	
	/** Defines starting position.  (0,0) is a1 **/
	public static final byte[][] init = 
			
		{{4,1,0,0,0,0,7,10},
		{3,1,0,0,0,0,7,9},
		{2,1,0,0,0,0,7,8},
		{5,1,0,0,0,0,7,11},
		{6,1,0,0,0,0,7,12},
		{2,1,0,0,0,0,7,8},
		{3,1,0,0,0,0,7,9},
		{4,1,0,0,0,0,7,10}};

	
	
	/** Defines our trimmed and padded board **/
	byte[][] fullBoard = new byte[12][12];
	byte[][] board = new byte[8][8];
	
	/** Public constructor: Empty **/
	public Board()
	{
		this.board = init;
		this.fullBoard = Board.padBoard(board);
	}
	
	
	/** Return a list of all possible moves, in square-notation **/
	public int[][] getAllPossibleMoves()
	{
		return new int[0][0];
	}
	
	
	/** Return all moves for the piece on the given square.
	 * Moves are given as one number, the destination square.
	 * Because the piece location is given, you already know the origin. **/
	public int[] getMoves(int square)
	{
		if(square == -1 || square == 0)
			return null;
		
		int[] loc = Board.numberToArray(square);
		int x = loc[0];
		int y = loc[1];
		
		byte piece = board[loc[0]][loc[1]]; //Get the piece code
		
		return null;
	}
	
	
	/** Return a board that is the result of playing the given move on the given board **/
	public static byte[][] getResultBoard(int[] move, byte[][] board)
	{
		return new byte[0][0];
	}
	
	
	/* These methods generate possible positions for each piece, given the starting location */
	/* All methods return int[], where destination squares are given in square notation */
	
	/** Generate pawn moves **/
	public int[] getPawnMoves(int square)
	{
		byte piece = getPiece(square);
		boolean isBlocked = false;
		
		if(piece != Board.WHITE_PAWN && piece != Board.BLACK_PAWN) //Invalid piece code
			return null;
		
		int[][] allMoves = new int[4][2]; //Holds all possible moves 
		ArrayList<Integer> moves = new ArrayList<Integer>(); //Holds all valid moves
		
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];

		
		if(piece == Board.WHITE_PAWN)
		{
			isBlocked = !(getPiece(square+8) == Board.EMPTY || getPiece(square+8) == Board.OOB);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square+8); //Up one
				
				if(y == 1)
				{
					moves.add(square+16); //Up 2
				}
			}
			
			if(getPiece(x+1, y+1) != Board.EMPTY || getPiece(x+1, y+1) != Board.OOB)
			{
				moves.add(square + 9); //atack up right
			}
			
			if(getPiece(x-1, y+1) != Board.EMPTY || getPiece(x-1, y+1) != Board.OOB)
			{
				moves.add(square + 7); //attack up left
			}
		}
		else if(piece == Board.BLACK_PAWN) 
		{
			isBlocked = !(getPiece(square-8) == Board.EMPTY || getPiece(square-8) == Board.OOB);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square - 8); //1 down
				
				if(y == 6)
				{
					moves.add(square - 16); //2 down
				}
			}
			
			if(getPiece(x+1, y-1) != Board.EMPTY || getPiece(x+1, y-1) != Board.OOB)
			{
				moves.add(square - 9); //Down right attack
			}
			
			if(getPiece(x-1, y-1) != Board.EMPTY || getPiece(x-1, y-1) != Board.OOB)
			{
				moves.add(square - 7); //Down left attack
			}
		}
		
		/* Eliminate moves that result in invalid destinations */
		/*for(int i = 0; i < allMoves.length; i++)
		{
			int[] newLoc = allMoves[i];
			
			if(getPiece(newLoc) != Board.OOB)
			{
				moves.add(Board.arrayToNumber(newLoc));
			}
		}*/
		
		return Board.toArray(moves);
	}
	
	/** Generate knight moves **/
	public static int[] getKnightMoves(int square)
	{
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		return null;
	}
	
	/** Generate bishop moves **/
	public static int[] getBishopMoves(int square)
	{
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		return null;
	}
	
	/** Generate rook moves **/
	public static int[] getRookMoves(int square)
	{
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		return null;
	}
	
	/** Generate queen moves **/
	public static int[] getQueenMoves(int square)
	{
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		return null;
	}
	
	/** Generate king moves **/
	public static int[] getKingMoves(int square)
	{
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		return null;
	}
	
	/* End of move generation methods */
	
	
	/** Return the value at the given square.  Retrieves value from the full, padded board **/
	public byte getPiece(int square)
	{
		if(square > 63 || square < 0)
			return Board.OOB;
		
		int[] location = Board.numberToArray(square);
		return fullBoard[location[0]+2][location[1]+2];
	}
	
	
	/** Return the value at the given coordinates **/
	public byte getPiece(int... loc)
	{
		return fullBoard[loc[0]+2][loc[1]+2];
	}
	
	
	/** Take a full 12x12 array and trim it to an 8x8 array **/
	public static byte[][] trimBoard(byte[][] board)
	{
		/* Size check */
		if(board[0].length != 12 || board.length != 12)
			return null;

		byte[][] newBoard = new byte[8][8];
		
		for(int x = 0; x < board.length; x++)
		{
			for(int y= 0; y < board[0].length; y++)
			{
				if((x > 1 && x < 10) && (y > 1 && y < 10)) //If the coordinate is in the 8x8 region of the board, add it to our new board!
					newBoard[x][y] = board[x][y];
			}
		}
		
		return newBoard;
		
	}
	
	
	/** Take an 8x8 array and add 2 layers of padding on each side **/
	public static byte[][] padBoard(byte[][] board)
	{
		/* Size check */
		if(board[0].length != 8 || board.length != 8)
			return null;
		
		byte[][] newBoard = new byte[12][12];
		
		for(int x = 0; x < newBoard.length; x++)
		{
			for(int y= 0; y < newBoard[0].length; y++)
			{
				if((x > 1 && x < 10) && (y > 1 && y < 10)) //If the coordinate is in the 8x8 region of the board, add it to our new board!
					newBoard[x][y] = board[x-2][y-2];
				else
					newBoard[x][y] = Board.OOB; //the coordinate is not a valid chess suqare, make this coord a OOB
			}
		}
		
		return newBoard;
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
    
    
    /** Convert ArrayList<Integer> to int[] **/
    public static int[] toArray(ArrayList<Integer> data)
    {
    	int[] newData = new int[data.size()];
    	
    	for(int i = 0; i < data.size(); i++)
    		newData[i] = data.get(i);
    	
    	return newData;
    }
}
