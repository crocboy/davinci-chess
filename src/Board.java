import java.util.ArrayList;
import java.util.Random;

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
	
	public static final byte BLACK_PAWN = -1;
	public static final byte BLACK_BISHOP = -2;
	public static final byte BLACK_KNIGHT = -3;
	public static final byte BLACK_ROOK = -4;
	public static final byte BLACK_QUEEN = -5;
	public static final byte BLACK_KING = -6;
	
	public static final byte SIDE_WHITE = 0;
	public static final byte SIDE_BLACK = 1;
	
	
	/** Empty square **/
	public static final byte EMPTY = 0;
	
	/** Out of bounds square **/
	public static final byte OOB = 99;
	
	/** Used to convert Algebraic to Coordinates (and vice-versa) **/
	public static final String[] LETTER_ARRAY = {"a","b","c","d","e","f","g","h"};
	
	
	/* These are instance variables */
	public int side = Board.SIDE_BLACK;
	
	/** Defines starting position.  (0,0) is a1 **/
	public static final byte[][] init = 
			
	   {{4,1,0,0,0,0,-1,-4},
		{3,1,0,0,0,0,-1,-3},
		{2,1,0,0,0,0,-1,-2},
		{5,1,0,0,0,0,-1,-5},
		{6,1,0,0,0,0,-1,-6},
		{2,1,0,0,0,0,-1,-2},
		{3,1,0,0,0,0,-1,-3},
		{4,1,0,0,0,0,-1,-4}};

	
	
	/** Defines our trimmed and padded board **/
	byte[][] fullBoard = new byte[12][12];
	byte[][] board = new byte[8][8];
	
	
	/** Private variables */
	private long startTime;
	
	/** Public constructor: Empty **/
	public Board()
	{
		this.board = clone(init);
		this.fullBoard = Board.padBoard(clone(init));
	}
	
	public String getBestMove()
	{
		int[][] moves = getAllPossibleMoves();
		Random r = new Random();
		int num = r.nextInt(moves.length);
		
		int[] start = numberToArray(moves[num][0]);
		int[] end = numberToArray(moves[num][1]);
		
		String move = numberToLetter(start[0],start[1],end[0],end[1]);
		return move;
	}
	
	
	/** Play multiple moves **/
	public void playMoves(String s)
	{
		board = clone(init);
		fullBoard = padBoard(clone(init));
		
		s = s.trim();
		
		if(s.length() > 4) //More than one move
		{
			String[] moves = s.split(" ");
			
			for(String move : moves)
			{
				playMove(move);
			}
			System.out.print("");
		}
		
		else //Only one move
			playMove(s);
		
	}
	
	/** Play a single move **/
	public void playMove(String s)
	{
		if(s.equals("e1g1") && this.side == Board.SIDE_BLACK) //White castling!
		{
			board[5][0] = Board.WHITE_ROOK;
			board[6][0] = Board.WHITE_KING;
			board[4][0] = Board.EMPTY;
			board[7][0] = Board.EMPTY;
			return;
		}
		
		if(s.length() == 5) //Pawn promotion
		{
			int[] move = letterToNumber(s.substring(0, 4));
			int[] start = numberToArray(move[0]);
			int[] end = numberToArray(move[1]);
			
			byte piece = getPiece(move[0]);
			
			if(piece == Board.WHITE_PAWN || piece == Board.BLACK_PAWN) //Check for pawn promotion 
			{
				if(promotePawn(start, end, piece)) //If the pawn was promoted, return because we're done
					return;
			}
		}
		else if(s.length() == 4) //Regular move
		{
			int[] move = letterToNumber(s);
			int[] start = numberToArray(move[0]);
			int[] end = numberToArray(move[1]);
			
			byte piece = getPiece(move[0]);
			
			board[start[0]][start[1]] = Board.EMPTY;
			board[end[0]][end[1]] = piece;
			
			fullBoard = Board.padBoard(clone(board));
		}
	}
	
	
	/** Checks if the given side is in check **/
	public boolean isCheck(int checkSide)
	{
		int[][] moves = getAllPossibleMoves(checkSide);
		int kingSquare = getKingLocation(this.side);
		
		for(int[] pos : moves) //Look through all moves, see if our king's square is one of the destinations
		{
			int dest = pos[1];
			if(dest == kingSquare)
				return true;
		}
		
		return false;
	}
	
	
	/** Return a list of all possible moves, in square-notation **/
	public int[][] getAllPossibleMoves()
	{
		start();
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		
		/* Find the locations of all white pieces */
		int[] locs = getLocations();
		
		/* Add all moves for all pieces */
		for(int square : locs)
		{
			int[] moves = getMoves(square);
			for(int move : moves)
			{
				if(!causesCheck(new int[] {square, move}, this.side)) //Make sure it doesn't cause check 
				{
					String s = moveToString(square,move);
					System.out.println(s);
					allMoves.add(new int[] {square,move});
				}
			}
		}
		
		end("All moves");

		return to2DArray(allMoves);
	}
	
	
	/** Return all moves for the piece on the given square.
	 * Moves are given as one number, the destination square.
	 * Because the piece location is given, you already know the origin. **/
	public int[][] getAllPossibleMoves(int newSide)
	{
		int tempSide = this.side; //Store our side 
		this.side = newSide;
		
		int[][] moves = getAllPossibleMoves();
		
		this.side = tempSide; //Restore our old side and return our new moves
		return moves;
	}
	
	
	/** Return all moves for the given side **/
	/** Return all moves for the piece on the given square.
	 * Moves are given as one number, the destination square.
	 * Because the piece location is given, you already know the origin. **/
	public int[] getMoves(int square)
	{
		if(getPiece(square) == Board.OOB || getPiece(square) == Board.EMPTY)
			return null;
		
		ArrayList<Integer> allMoves = new ArrayList<Integer>();
		
		byte piece = getPiece(square); //Get the piece code
		
		/* Return the moves for the appropriate piece */
		switch(piece)
		{
			case Board.WHITE_PAWN:
				return getPawnMoves(square);
			case Board.WHITE_KNIGHT:
				return getKnightMoves(square);
			case Board.WHITE_BISHOP:
				return getBishopMoves(square);
			case Board.WHITE_ROOK:
				return getRookMoves(square);
			case Board.WHITE_QUEEN:
				return getQueenMoves(square);
			case Board.WHITE_KING:
				return getKingMoves(square);
				
			case Board.BLACK_PAWN:
				return getPawnMoves(square);
			case Board.BLACK_KNIGHT:
				return getKnightMoves(square);
			case Board.BLACK_BISHOP:
				return getBishopMoves(square);
			case Board.BLACK_ROOK:
				return getRookMoves(square);
			case Board.BLACK_QUEEN:
				return getQueenMoves(square);
			case Board.BLACK_KING:
				return getKingMoves(square);
		}
		
		return toArray(allMoves);
	}
	
	
	/** Start a new game **/
	public void newGame()
	{
		this.board = clone(init);
		this.fullBoard = Board.padBoard(clone(init));
	}
	
	
	/** Return a board that is the result of playing the given move on the given board **/
	public static byte[][] getResultBoard(int[] move, byte[][] board)
	{
		return new byte[0][0];
	}
	
	
	/** Get locations of all pieces on our side **/
	public int[] getLocations()
	{
		ArrayList<Integer> pos = new ArrayList<Integer>();
		
		for(int x = 0; x < 8; x++)
		{
			for(int y = 0; y < 8; y++)
			{
				if(side == Board.SIDE_BLACK)
				{
					if(board[x][y] < 0 && board[x][y] != Board.OOB)
						pos.add(arrayToNumber(x,y));
				}
				else if(side == Board.SIDE_WHITE)
				{
					if(board[x][y] > 0 && board[x][y] != Board.OOB)
						pos.add(arrayToNumber(x,y));
				}
				
			}
		}
		
		return toArray(pos);
	}
	
	
	/* These methods generate possible positions for each piece, given the starting location */
	/* All methods return int[], where destination squares are given in square notation */
	
	/** Generate pawn moves **/
	public int[] getPawnMoves(int square)
	{
		start();
		byte piece = getPiece(square);
		boolean isBlocked = false;
		
		if(piece != Board.WHITE_PAWN && piece != Board.BLACK_PAWN) //Invalid piece code
			return null;
		
		ArrayList<Integer> moves = new ArrayList<Integer>(); //Holds all valid moves
		
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];

		
		if(piece == Board.WHITE_PAWN)
		{
			isBlocked = !(getPiece(square+8) == Board.EMPTY);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square+8); //Up one
				
				if(y == 1 && getPiece(square + 16) == Board.EMPTY)
				{
					moves.add(square+16); //Up 2
				}
			}
			
			if(getPiece(x+1, y+1) != Board.EMPTY && getPiece(x+1, y+1) != Board.OOB)
			{
				moves.add(square + 9); //attack up right
			}
			
			if(getPiece(x-1, y+1) != Board.EMPTY && getPiece(x-1, y+1) != Board.OOB)
			{
				moves.add(square + 7); //attack up left
			}
		}
		else if(piece == Board.BLACK_PAWN) 
		{
			isBlocked = !(getPiece(square-8) == Board.EMPTY);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square - 8); //1 down
				
				if(y == 6 && getPiece(square - 16) == Board.EMPTY)
				{
					moves.add(square - 16); //2 down
				}
			}
			
			byte p = getPiece(x+1, y-1);
			if(p != Board.EMPTY && p != Board.OOB)
			{
				moves.add(square - 7); //Down left attack
			}
			
			p = getPiece(x-1, y-1);
			if(p != Board.EMPTY && p != Board.OOB)
			{
				moves.add(square - 9); //Down right attack
			}
		}
		
		/* Remove all moves that result in landing on a white piece */
		ArrayList<Integer> finalMoves = new ArrayList<Integer>(); //Holds all valid moves
		for(int i : moves)
		{
			if(side == Board.SIDE_BLACK)
			{
				if(getPiece(i) >= 0) //The piece is either white or empty
				{
					finalMoves.add(i);
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(i) <= 0) //The piece is either black or empty
				{
					finalMoves.add(i);
				}
			}
			
		}
		
		end("Pawn moves");
		return Board.toArray(finalMoves);
	}
	
	/** Generate knight moves **/
	public int[] getKnightMoves(int square)
	{
		start();
		if(getPiece(square) != Board.WHITE_KNIGHT && getPiece(square) != Board.BLACK_KNIGHT)
			return null;
		
		int[] pos = numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Add all the combinations of knight moves */
		allMoves.add(new int[] {x+1,y-2});
		allMoves.add(new int[] {x+1,y+2});
		allMoves.add(new int[] {x+2,y+1});
		allMoves.add(new int[] {x+2,y-1});
		allMoves.add(new int[] {x-1,y+2});
		allMoves.add(new int[] {x-1,y-2});
		allMoves.add(new int[] {x-2,y+1});
		allMoves.add(new int[] {x-2,y-1});
		
		/* Remove all moves that result in an OOB condition, or landing on a white piece */
		for(int[] i : allMoves)
		{
			byte p = getPiece(i);
			if(side == Board.SIDE_BLACK)
			{
				if(getPiece(i) >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB && p >= 0)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(i) <= 0) //The piece is either black or empty
				{
					if(p != Board.OOB && p <= 0)
						moves.add(arrayToNumber(i));
				}
			}
			
		}
		
		end("Knight moves");
		return toArray(moves);
	}
	
	/** Generate bishop moves **/
	public int[] getBishopMoves(int square)
	{
		if(getPiece(square) != Board.WHITE_BISHOP && getPiece(square) != Board.BLACK_BISHOP)
			return null;
		start();
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		int count = 1;
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Search in all four directions while adding moves, stop when you hit a piece/OOB */
		byte piece = 0;
		
		while(piece != Board.OOB) //To the right-up
		{
			int[] newPos = {x+count,y+count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //To the left-up
		{
			int[] newPos = {x-count,y+count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Right-Down
		{
			int[] newPos = {x+count,y-count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Left-down
		{
			int[] newPos = {x-count,y-count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		/* Remove all moves that result in an OOB condition */
		for(int[] i : allMoves)
		{
			byte p = getPiece(i);
			if(side == Board.SIDE_BLACK)
			{
				if(getPiece(i) >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB && p >= 0)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(i) <= 0) //The piece is either black or empty
				{
					if(p != Board.OOB && p <= 0)
						moves.add(arrayToNumber(i));
				}
			}
		}
		
		end("Bishop moves");
		return toArray(moves);
	}
	
	/** Generate rook moves **/
	public int[] getRookMoves(int square)
	{
		if(getPiece(square) != Board.WHITE_ROOK && getPiece(square) != Board.BLACK_ROOK)
			return null;
		
		start();
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		int count = 1;
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Search in all four directions while adding moves, stop when you hit a piece/OOB */
		byte piece = 0;
		
		while(piece != Board.OOB) //To the right
		{
			int[] newPos = {x+count,y};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //To the left
		{
			int[] newPos = {x-count,y};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Down
		{
			int[] newPos = {x,y-count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Up
		{
			int[] newPos = {x,y+count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		/* Remove all moves that result in an OOB condition */
		for(int[] i : allMoves)
		{
			byte p = getPiece(i);
			if(side == Board.SIDE_BLACK)
			{
				if(p >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB && p >= 0)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(i) <= 0) //The piece is either black or empty
				{
					if(p != Board.OOB && p <= 0)
						moves.add(arrayToNumber(i));
				}
			}
		}
		
		end("Rook moves");
		return toArray(moves);
	}
	
	/** Generate queen moves **/
	public int[] getQueenMoves(int square)
	{
		if(getPiece(square) != Board.WHITE_QUEEN && getPiece(square) != Board.BLACK_QUEEN)
			return null;
		start();
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		int count = 1;
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Search in all four horizontal directions */

		byte piece = 0;
		while(piece != Board.OOB) //To the right
		{
			int[] newPos = {x+count,y};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //To the left
		{
			int[] newPos = {x-count,y};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Down
		{
			int[] newPos = {x,y-count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Up
		{
			int[] newPos = {x,y+count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		/* Search diagonally */
		piece = 0;
		count = 1;
		while(piece != Board.OOB) //To the right-up
		{
			int[] newPos = {x+count,y+count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //To the left-up
		{
			int[] newPos = {x-count,y+count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Right-Down
		{
			int[] newPos = {x+count,y-count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		count = 1;
		piece = 0;
		while(piece != Board.OOB) //Left-down
		{
			int[] newPos = {x-count,y-count};
			piece = getPiece(newPos);
			
			if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
			{
				allMoves.add(newPos);
				break;
			}
			else if(piece == Board.EMPTY) //Empty square 
			{
				allMoves.add(newPos);
			}
			
			count++;
		}
		
		/* Remove all moves that result in an OOB condition */
		for(int[] i : allMoves)
		{
			byte p = getPiece(i);
			if(side == Board.SIDE_BLACK)
			{
				if(p >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(p <= 0) //The piece is either black or empty
				{
					if(p != Board.OOB)
						moves.add(arrayToNumber(i));
				}
			}
		}
		
		end("Queen moves");
		return toArray(moves);
	}
	
	/** Generate king moves **/
	public int[] getKingMoves(int square)
	{
		start();
		if(getPiece(square) != Board.WHITE_KING && getPiece(square) != Board.BLACK_KING)
			return null;
		
		int[] pos = numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Add all the combinations of king moves */
		allMoves.add(new int[] {x,y+1});
		allMoves.add(new int[] {x+1,y+1});
		allMoves.add(new int[] {x+1,y}); //CHANGES FOR SOME REASON
		allMoves.add(new int[] {x+1,y-1});
		allMoves.add(new int[] {x,y-1});
		allMoves.add(new int[] {x-1,y-1});
		allMoves.add(new int[] {x-1,y});
		allMoves.add(new int[] {x-1,y+1});
		
		/* Remove all moves that result in an OOB condition, or landing on a white piece */
		for(int[] i : allMoves)
		{
			byte p = getPiece(i);
			if(side == Board.SIDE_BLACK)
			{
				if(getPiece(i) >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB && p >= 0)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(i) <= 0) //The piece is either black or empty
				{
					if(p != Board.OOB && p <= 0)
						moves.add(arrayToNumber(i));
				}
			}
		}
		
		end("King moves");
		return toArray(moves);
	}
	
	
	/** Promote pawns if need be, return a boolean indicating if the pawn was actually promoted **/
	public boolean promotePawn(int[] start, int[] end, byte piece)
	{
		if(piece == Board.BLACK_PAWN)
		{
			if(end[1] != 0) //Check position
				return false;
			else 
			{
				board[start[0]][start[1]] = Board.EMPTY;
				board[end[0]][end[1]] = Board.BLACK_QUEEN;
				
				fullBoard = Board.padBoard(clone(board));
				return true;
			}
		}
		else if(piece == Board.WHITE_PAWN)
		{
			if(end[1] != 7) //Check position
				return false;
			else 
			{
				board[start[0]][start[1]] = Board.EMPTY;
				board[end[0]][end[1]] = Board.WHITE_QUEEN;
				
				fullBoard = Board.padBoard(clone(board));
				return true;
			}
		}
		
		return false;
	}
	/* End of move generation methods */
	
	
	/** Return true if the given move will cause check for the given side**/
	public boolean causesCheck(int[] move, int newSide)
	{
		byte[][] tempBoard = clone(board); //Store our current board for use later
		
		String moveString = numberToLetter(move[0],move[1]); //Play the move on our board
		playMove(moveString);
		
		int king = getKingLocation(newSide);
		int[] kingPos = numberToArray(king);
		int x = kingPos[0];
		int y = kingPos[1];
		
		if(newSide == Board.SIDE_BLACK) //Search for moves that cause check for black pieces
		{
			/* Search horizontally in all directions */
			byte piece = 0;
			int count = 1;
			while(piece != Board.OOB) //To the right
			{
				int[] newPos = {x+count,y};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //To the left
			{
				int[] newPos = {x-count,y};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Down
			{
				int[] newPos = {x,y-count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Up
			{
				int[] newPos = {x,y+count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			/* Search diagonally */
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Down right
			{
				int[] newPos = {x-count,y-count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Down right
			{
				int[] newPos = {x+count,y-count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Up-left
			{
				int[] newPos = {x-count,y+count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Up-right
			{
				int[] newPos = {x+count,y+count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			/* Search for enemy knights */
			if(getPiece(x+1,y+2) == Board.WHITE_KNIGHT || getPiece(x+1,y-2) == Board.WHITE_KNIGHT || getPiece(x+2,y+1) == Board.WHITE_KNIGHT || getPiece(x+2,y-1) == Board.WHITE_KNIGHT || getPiece(x-1,y-2) == Board.WHITE_KNIGHT || getPiece(x-1,y-2) == Board.WHITE_KNIGHT || getPiece(x-2,y+1) == Board.WHITE_KNIGHT || getPiece(x-2,y-1) == Board.WHITE_KNIGHT)
			{
				this.board = clone(tempBoard);
				this.fullBoard = clone(padBoard(tempBoard));
				return true;
			}
			
			/* Search for enemy pawns */
			if(getPiece(x-1,y-1) == Board.WHITE_PAWN || getPiece(x+1,y-1) == Board.WHITE_PAWN)
			{
				this.board = clone(tempBoard);
				this.fullBoard = clone(padBoard(tempBoard));
				return true;
			}
			
			/* Search for enemy king */
			if(getPiece(x-1,y-1) == Board.WHITE_KING || getPiece(x+1,y-1) == Board.WHITE_KING || getPiece(x,y-1) == Board.WHITE_KING  || getPiece(x+1,y) == Board.WHITE_KING || getPiece(x-1,y) == Board.WHITE_KING || getPiece(x+1,y+1) == Board.WHITE_KING || getPiece(x,y+1) == Board.WHITE_KING || getPiece(x-1,y+1) == Board.WHITE_KING)
			{
				this.board = clone(tempBoard);
				this.fullBoard = clone(padBoard(tempBoard));
				return true;
			}
		}
		
		else if(newSide == Board.SIDE_BLACK) //Search for moves that cause check for white pieces
		{
			/* Search horizontally in all directions */
			byte piece = 0;
			int count = 1;
			while(piece != Board.OOB) //To the right
			{
				int[] newPos = {x+count,y};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //To the left
			{
				int[] newPos = {x-count,y};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Down
			{
				int[] newPos = {x,y-count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Up
			{
				int[] newPos = {x,y+count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			/* Search diagonally */
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Down right
			{
				int[] newPos = {x-count,y-count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Down right
			{
				int[] newPos = {x+count,y-count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Up-left
			{
				int[] newPos = {x-count,y+count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			count = 1;
			piece = 0;
			while(piece != Board.OOB) //Up-right
			{
				int[] newPos = {x+count,y+count};
				piece = getPiece(newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
						this.board = clone(tempBoard);
						this.fullBoard = clone(padBoard(tempBoard));
						return true;
					}
					break;
				}
				
				count++;
			}
			
			/* Search for enemy knights */
			if(getPiece(x+1,y+2) == Board.BLACK_KNIGHT || getPiece(x+1,y-2) == Board.BLACK_KNIGHT || getPiece(x+2,y+1) == Board.BLACK_KNIGHT || getPiece(x+2,y-1) == Board.BLACK_KNIGHT || getPiece(x-1,y-2) == Board.BLACK_KNIGHT || getPiece(x-1,y-2) == Board.BLACK_KNIGHT || getPiece(x-2,y+1) == Board.BLACK_KNIGHT || getPiece(x-2,y-1) == Board.BLACK_KNIGHT)
			{
				this.board = clone(tempBoard);
				this.fullBoard = clone(padBoard(tempBoard));
				return true;
			}
			
			/* Search for enemy pawns */
			if(getPiece(x-1,y+1) == Board.BLACK_PAWN || getPiece(x+1,y+1) == Board.BLACK_PAWN)
			{
				this.board = clone(tempBoard);
				this.fullBoard = clone(padBoard(tempBoard));
				return true;
			}
			
			/* Search for enemy king */
			if(getPiece(x-1,y-1) == Board.BLACK_KING || getPiece(x+1,y-1) == Board.BLACK_KING || getPiece(x,y-1) == Board.BLACK_KING  || getPiece(x+1,y) == Board.BLACK_KING || getPiece(x-1,y) == Board.BLACK_KING || getPiece(x+1,y+1) == Board.BLACK_KING || getPiece(x,y+1) == Board.BLACK_KING || getPiece(x-1,y+1) == Board.BLACK_KING)
			{
				this.board = clone(tempBoard);
				this.fullBoard = clone(padBoard(tempBoard));
				return true;
			}
		}
		
		/* Restore our original board */
		this.board = clone(tempBoard);
		this.fullBoard = clone(padBoard(tempBoard));
		return false;
	}
	
	
	
	/** Return the location of the given side's king **/
	public int getKingLocation(int checkSide)
	{
		byte code = 0;
		
		if(checkSide == Board.SIDE_BLACK)
			code = Board.BLACK_KING;
		else if(checkSide == Board.SIDE_WHITE)
			code = Board.WHITE_KING;
		
		/* Loop the board and find the piece */
		for(int x = 0; x < board.length; x++)
		{
			for(int y= 0; y < board[0].length; y++)
			{
				if(board[x][y] == code)
					return arrayToNumber(x,y);
			}
		}
		
		return -1;
	}
	
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
    
    /** Convert sqaure notation to algebraic notation **/
    public static String numberToLetter(int s, int e)
    {
    	int[] start = numberToArray(s);
    	int[] end = numberToArray(e);
    	
        String result = LETTER_ARRAY[start[0]] + String.valueOf(start[1] + 1) + LETTER_ARRAY[end[0]] + String.valueOf(end[1]+1);
        return result;
    }
    
    
    /** Convert a square-to-square move to a String **/
    public static String moveToString(int s, int e)
    {
    	int[] start = numberToArray(s);
    	int[] end = numberToArray(e);
    	
    	return numberToLetter(start[0],start[1],end[0],end[1]);
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
    public static int arrayToNumber(int... coord)
    {
    	if(coord.length != 2)
    		return -1;
    	
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
    
    /** Convert ArrayList<Integer> to int[] **/
    public static int[][] to2DArray(ArrayList<int[]> data)
    {
    	int[][] newData = new int[data.size()][data.get(0).length];
    	
    	for(int i = 0; i < data.size(); i++)
    		newData[i] = data.get(i);
    	
    	return newData;
    }
    
    /** Record start time **/
    public void start()
    {
    	startTime = System.currentTimeMillis();
    }
    
    
    /** Clone a byte[][] **/
    public static byte[][] clone(byte[][] data)
    {
    	byte[][] newData = new byte[data.length][data[0].length];
    	for(int x = 0; x < data.length; x++)
    	{
    		for(int y = 0; y < data[0].length; y++)
    		{
    			newData[x][y] = data[x][y];
    		}
    	}
    	return newData;
    }
    
    
    /** Return a copy of the board **/
    public byte[][] copyBoard()
    {
    	return clone(board);
    }
    
    /** Print end time **/
    public void end(String s)
    {
    	//System.out.println(s + " time: " + String.valueOf(System.currentTimeMillis() - startTime));
    }
}
