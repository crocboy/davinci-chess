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
	
	public static final byte SIDE_WHITE = -1;
	public static final byte SIDE_BLACK = 1;
	
	
	/** Empty square **/
	public static final byte EMPTY = 0;
	
	/** Out of bounds square **/
	public static final byte OOB = 99;
	
	/** Used to convert Algebraic to Coordinates (and vice-versa) **/
	public static final String[] LETTER_ARRAY = {"a","b","c","d","e","f","g","h"};
	
	
	/* These are instance variables */
	public int gameSide = Board.SIDE_BLACK;
	
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
	byte[][] gameBoard = new byte[8][8];
	
	
	/** Private variables */
	private static long startTime;
	
	/** Public constructor: Empty **/
	public Board()
	{
		this.gameBoard = clone(init);
	}
	
	public String getBestMove()
	{
		start();
		
		int[] best = Evaluation.findBestMove(gameBoard, this.gameSide);
		
		/*Random r = new Random();
		int num = r.nextInt(moves.length);*/
		
		String move = numberToLetter(best[0],best[1]);
		end("Best move time");
		return move;
	}
	
	
	/** Play multiple moves **/
	public static byte[][] playMoves(String s)
	{
		byte[][] board = clone(init);
		
		s = s.trim();
		
		if(s.length() > 4) //More than one move
		{
			String[] moves = s.split(" ");
			
			for(String move : moves)
			{
				board = playMove(move, board);
			}
			System.out.print("");
			
			return board;
		}
		
		else //Only one move
			return playMove(s, board);

		
	}
	
	/** Play a single move **/
	public static byte[][] playMove(String s, byte[][] board)
	{
		s = s.trim();
		board = clone(board);
		
		if(s.equals("e1g1") && Main.board.gameSide == Board.SIDE_BLACK) //White castling!
		{
			board[5][0] = Board.WHITE_ROOK;
			board[6][0] = Board.WHITE_KING;
			board[4][0] = Board.EMPTY;
			board[7][0] = Board.EMPTY;
			return board;
		}
		
		if(s.length() == 5) //Pawn promotion
		{
			int[] move = letterToNumber(s.substring(0, 4));
			int[] start = numberToArray(move[0]);
			int[] end = numberToArray(move[1]);
			
			byte piece = getPiece(board,move[0]);
			
			if(piece == Board.WHITE_PAWN || piece == Board.BLACK_PAWN) //Check for pawn promotion 
			{
				if(promotePawn(start, end, piece, board)) //If the pawn was promoted, return because we're done
					return board;
			}
		}
		else if(s.length() == 4) //Regular move
		{
			int[] move = letterToNumber(s);
			int[] start = numberToArray(move[0]);
			int[] end = numberToArray(move[1]);
			
			byte piece = getPiece(board,move[0]);
			
			board[start[0]][start[1]] = Board.EMPTY;
			board[end[0]][end[1]] = piece;
		}
		
		return board;
	}
	
	
	/** Play a single move (Over-ridden version) **/
	public static byte[][] playMove(int[] move, byte[][] board)
	{
		return playMove(Board.numberToLetter(move[0], move[1]), board);
	}
	
	
	/** Checks if the given side is in check **/
	public boolean isCheck(int side, byte[][] board)
	{
		int[][] moves = getAllPossibleMoves(side, board);
		int kingSquare = getKingLocation(side, board);
		
		for(int[] pos : moves) //Look through all moves, see if our king's square is one of the destinations
		{
			int dest = pos[1];
			if(dest == kingSquare)
				return true;
		}
		
		return false;
	}
	
	
	/** Return a list of all possible moves, in square-notation **/
	public static int[][] getAllPossibleMoves(int side, final byte[][] board)
	{
		//start();
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		
		/* Find the locations of all white pieces */
		int[] locs = getLocations(side, board);
		
		/* Add all moves for all pieces */
		for(int square : locs)
		{
			int[] moves = getMoves(square, board);
			for(int move : moves)
			{
				if(!causesCheck(new int[] {square, move}, side, board)) //Make sure it doesn't cause check 
				{
					//String s = moveToString(square,move);
					//System.out.println(s);
					allMoves.add(new int[] {square,move});
				}
			}
		}
		
		//end("All moves");

		return to2DArray(allMoves);
	}
	
	
	/** Return all moves for the given side **/
	/** Return all moves for the piece on the given square.
	 * Moves are given as one number, the destination square.
	 * Because the piece location is given, you already know the origin. **/
	public static int[] getMoves(int square, byte[][] board)
	{
		if(getPiece(board,square) == Board.OOB || getPiece(board,square) == Board.EMPTY)
			return null;
		
		ArrayList<Integer> allMoves = new ArrayList<Integer>();
		
		byte piece = getPiece(board,square); //Get the piece code
		
		/* Return the moves for the appropriate piece */
		switch(piece)
		{
			case Board.WHITE_PAWN:
				return getPawnMoves(square, board);
			case Board.WHITE_KNIGHT:
				return getKnightMoves(square, board);
			case Board.WHITE_BISHOP:
				return getBishopMoves(square, board);
			case Board.WHITE_ROOK:
				return getRookMoves(square, board);
			case Board.WHITE_QUEEN:
				return getQueenMoves(square, board);
			case Board.WHITE_KING:
				return getKingMoves(square, board);
				
			case Board.BLACK_PAWN:
				return getPawnMoves(square, board);
			case Board.BLACK_KNIGHT:
				return getKnightMoves(square, board);
			case Board.BLACK_BISHOP:
				return getBishopMoves(square, board);
			case Board.BLACK_ROOK:
				return getRookMoves(square, board);
			case Board.BLACK_QUEEN:
				return getQueenMoves(square, board);
			case Board.BLACK_KING:
				return getKingMoves(square, board);
		}
		
		return toArray(allMoves);
	}
	
	
	/** Start a new game **/
	public void newGame()
	{
		this.gameBoard = clone(init);
	}
	
	
	/** Return a board that is the result of playing the given move on the given board **/
	public static byte[][] getResultBoard(int[] move, byte[][] board)
	{
		return new byte[0][0];
	}
	
	
	/** Get locations of all pieces on our side **/
	public static int[] getLocations(int side, byte[][] board)
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
	public static int[] getPawnMoves(int square, byte[][] board)
	{
		start();
		byte piece = getPiece(board,square);
		boolean isBlocked = false;
		
		int side = 0;
		if(piece == Board.BLACK_PAWN)
			side = Board.SIDE_BLACK;
		else if(piece == Board.WHITE_PAWN)
			side = Board.SIDE_WHITE;
		else
			return null;
		
		ArrayList<Integer> moves = new ArrayList<Integer>(); //Holds all valid moves
		
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];

		
		if(piece == Board.WHITE_PAWN)
		{
			isBlocked = !(getPiece(board,square+8) == Board.EMPTY);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square+8); //Up one
				
				if(y == 1 && getPiece(board,square + 16) == Board.EMPTY)
				{
					moves.add(square+16); //Up 2
				}
			}
			
			if(getPiece(board,x+1, y+1) != Board.EMPTY && getPiece(board,x+1, y+1) != Board.OOB)
			{
				moves.add(square + 9); //attack up right
			}
			
			if(getPiece(board,x-1, y+1) != Board.EMPTY && getPiece(board,x-1, y+1) != Board.OOB)
			{
				moves.add(square + 7); //attack up left
			}
		}
		else if(piece == Board.BLACK_PAWN) 
		{
			isBlocked = !(getPiece(board,square-8) == Board.EMPTY);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square - 8); //1 down
				
				if(y == 6 && getPiece(board,square - 16) == Board.EMPTY)
				{
					moves.add(square - 16); //2 down
				}
			}
			
			byte p = getPiece(board,x+1, y-1);
			if(p != Board.EMPTY && p != Board.OOB)
			{
				moves.add(square - 7); //Down left attack
			}
			
			p = getPiece(board,x-1, y-1);
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
				if(getPiece(board,i) >= 0) //The piece is either white or empty
				{
					finalMoves.add(i);
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(board,i) <= 0) //The piece is either black or empty
				{
					finalMoves.add(i);
				}
			}
			
		}
		
		end("Pawn moves");
		return Board.toArray(finalMoves);
	}
	
	/** Generate knight moves **/
	public static int[] getKnightMoves(int square, byte[][] board)
	{
		start();
		
		byte piece = getPiece(board,square);
		
		int side = 0;
		if(piece == Board.BLACK_KNIGHT)
			side = Board.SIDE_BLACK;
		else if(piece == Board.WHITE_KNIGHT)
			side = Board.SIDE_WHITE;
		else
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
			byte p = getPiece(board,i);
			if(side == Board.SIDE_BLACK)
			{
				if(getPiece(board,i) >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB && p >= 0)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(board,i) <= 0) //The piece is either black or empty
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
	public static int[] getBishopMoves(int square, byte[][] board)
	{
		byte piece = getPiece(board,square);
		
		int side = 0;
		if(piece == Board.BLACK_BISHOP)
			side = Board.SIDE_BLACK;
		else if(piece == Board.WHITE_BISHOP)
			side = Board.SIDE_WHITE;
		else
			return null;
		
		start();
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		int count = 1;
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Search in all four directions while adding moves, stop when you hit a piece/OOB */
		piece = 0;
		
		while(piece != Board.OOB) //To the right-up
		{
			int[] newPos = {x+count,y+count};
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			byte p = getPiece(board,i);
			if(side == Board.SIDE_BLACK)
			{
				if(getPiece(board,i) >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB && p >= 0)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(board,i) <= 0) //The piece is either black or empty
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
	public static int[] getRookMoves(int square, byte[][] board)
	{
		byte piece = getPiece(board, square);
		
		int side = 0;
		if(piece == Board.BLACK_ROOK)
			side = Board.SIDE_BLACK;
		else if(piece == Board.WHITE_ROOK)
			side = Board.SIDE_WHITE;
		else
			return null;
		
		start();
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		int count = 1;
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Search in all four directions while adding moves, stop when you hit a piece/OOB */
		piece = 0;
		
		while(piece != Board.OOB) //To the right
		{
			int[] newPos = {x+count,y};
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			byte p = getPiece(board,i);
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
				if(getPiece(board,i) <= 0) //The piece is either black or empty
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
	public static int[] getQueenMoves(int square, byte[][] board)
	{
		byte piece = getPiece(board,square);
		
		int side = 0;
		if(piece == Board.BLACK_QUEEN)
			side = Board.SIDE_BLACK;
		else if(piece == Board.WHITE_QUEEN)
			side = Board.SIDE_WHITE;
		else
			return null;
		
		start();
		int[] pos = Board.numberToArray(square);
		int x = pos[0];
		int y = pos[1];
		
		int count = 1;
		
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		
		/* Search in all four horizontal directions */

		piece = 0;
		while(piece != Board.OOB) //To the right
		{
			int[] newPos = {x+count,y};
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			piece = getPiece(board,newPos);
			
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
			byte p = getPiece(board,i);
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
	public static int[] getKingMoves(int square, byte[][] board)
	{
		start();
		
		byte piece = getPiece(board, square);
		
		int side = 0;
		if(piece == Board.BLACK_KING)
			side = Board.SIDE_BLACK;
		else if(piece == Board.WHITE_KING)
			side = Board.SIDE_WHITE;
		else
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
			byte p = getPiece(board,i);
			if(side == Board.SIDE_BLACK)
			{
				if(getPiece(board,i) >= 0) //The piece is either white or empty
				{
					if(p != Board.OOB && p >= 0)
						moves.add(arrayToNumber(i));
				}
			}
			else if(side == Board.SIDE_WHITE)
			{
				if(getPiece(board,i) <= 0) //The piece is either black or empty
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
	public static boolean promotePawn(int[] start, int[] end, byte piece, byte[][] board)
	{
		if(piece == Board.BLACK_PAWN)
		{
			if(end[1] != 0) //Check position
				return false;
			else 
			{
				board[start[0]][start[1]] = Board.EMPTY;
				board[end[0]][end[1]] = Board.BLACK_QUEEN;
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
				return true;
			}
		}
		
		return false;
	}
	/* End of move generation methods */
	
	
	/** Return true if the given move will cause check for the given side**/
	public static boolean causesCheck(int[] move, int side, byte[][] board)
	{
		board = clone(board); //Store our current board for use later
		
		String moveString = numberToLetter(move[0],move[1]); //Play the move on our board
		board = playMove(moveString, board);
		
		return isInCheck(side, board); //Call this function to return if the side is in check
	}
	
	
	
	/** Return true if the given side is IN check on the given board **/
	public static boolean isInCheck(int side, byte[][] board)
	{
		int king = getKingLocation(side, board);
		
		//if(king == -1)
			//System.out.println(Thread.currentThread().getStackTrace().toString());
		
		int[] kingPos = numberToArray(king);
		int x = kingPos[0];
		int y = kingPos[1];
		
		if(side == Board.SIDE_BLACK) //Search for moves that cause check for black pieces
		{
			/* Search horizontally in all directions */
			byte piece = 0;
			int count = 1;
			while(piece != Board.OOB) //To the right
			{
				int[] newPos = {x+count,y};
				piece = getPiece(board, newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
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
				piece = getPiece(board, newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_ROOK)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.WHITE_QUEEN || piece == Board.WHITE_BISHOP)
					{
						return true;
					}
					break;
				}
				
				count++;
			}
			
			/* Search for enemy knights */
			if(getPiece(board,x+1,y+2) == Board.WHITE_KNIGHT || getPiece(board,x+1,y-2) == Board.WHITE_KNIGHT || getPiece(board,x+2,y+1) == Board.WHITE_KNIGHT || getPiece(board,x+2,y-1) == Board.WHITE_KNIGHT || getPiece(board,x-1,y-2) == Board.WHITE_KNIGHT || getPiece(board,x-1,y-2) == Board.WHITE_KNIGHT || getPiece(board,x-2,y+1) == Board.WHITE_KNIGHT || getPiece(board,x-2,y-1) == Board.WHITE_KNIGHT)
			{
				return true;
			}
			
			/* Search for enemy pawns */
			if(getPiece(board,x-1,y-1) == Board.WHITE_PAWN || getPiece(board,x+1,y-1) == Board.WHITE_PAWN)
			{
				return true;
			}
			
			/* Search for enemy king */
			if(getPiece(board,x-1,y-1) == Board.WHITE_KING || getPiece(board,x+1,y-1) == Board.WHITE_KING || getPiece(board,x,y-1) == Board.WHITE_KING  || getPiece(board,x+1,y) == Board.WHITE_KING || getPiece(board,x-1,y) == Board.WHITE_KING || getPiece(board,x+1,y+1) == Board.WHITE_KING || getPiece(board,x,y+1) == Board.WHITE_KING || getPiece(board,x-1,y+1) == Board.WHITE_KING)
			{
				return true;
			}
		}
		
		else if(side == Board.SIDE_WHITE) //Search for moves that cause check for white pieces
		{
			/* Search horizontally in all directions */
			byte piece = 0;
			int count = 1;
			while(piece != Board.OOB) //To the right
			{
				int[] newPos = {x+count,y};
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_ROOK)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
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
				piece = getPiece(board,newPos);
				
				if(piece != Board.EMPTY && piece != Board.OOB) //It's an actual piece
				{
					if(piece == Board.BLACK_QUEEN || piece == Board.BLACK_BISHOP)
					{
						return true;
					}
					break;
				}
				
				count++;
			}
			
			/* Search for enemy knights */
			if(getPiece(board,x+1,y+2) == Board.BLACK_KNIGHT || getPiece(board,x+1,y-2) == Board.BLACK_KNIGHT || getPiece(board,x+2,y+1) == Board.BLACK_KNIGHT || getPiece(board,x+2,y-1) == Board.BLACK_KNIGHT || getPiece(board,x-1,y-2) == Board.BLACK_KNIGHT || getPiece(board,x-1,y-2) == Board.BLACK_KNIGHT || getPiece(board,x-2,y+1) == Board.BLACK_KNIGHT || getPiece(board,x-2,y-1) == Board.BLACK_KNIGHT)
			{
				return true;
			}
			
			/* Search for enemy pawns */
			if(getPiece(board,x-1,y+1) == Board.BLACK_PAWN || getPiece(board,x+1,y+1) == Board.BLACK_PAWN)
			{
				return true;
			}
			
			/* Search for enemy king */
			if(getPiece(board,x-1,y-1) == Board.BLACK_KING || getPiece(board,x+1,y-1) == Board.BLACK_KING || getPiece(board,x,y-1) == Board.BLACK_KING  || getPiece(board,x+1,y) == Board.BLACK_KING || getPiece(board,x-1,y) == Board.BLACK_KING || getPiece(board,x+1,y+1) == Board.BLACK_KING || getPiece(board,x,y+1) == Board.BLACK_KING || getPiece(board,x-1,y+1) == Board.BLACK_KING)
			{
				return true;
			}
		}
		
		/* Restore our original board */
		return false;
	}
	
	
	
	/** Return the location of the given side's king **/
	public static int getKingLocation(int checkSide, byte[][] board)
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
	public static byte getPiece(byte[][] _board, int square)
	{
		if(square > 63 || square < 0)
			return Board.OOB;
		
		int[] loc = Board.numberToArray(square);
		return clone(padBoard(_board))[loc[0]+2][loc[1]+2];
	}
	
	
	/** Return the value at the given coordinates **/
	public static byte getPiece(byte[][] board, int... loc)
	{
		if(loc[0] < -2)
		{
			//System.out.print(true);
		}
		
		return clone(padBoard(board))[loc[0]+2][loc[1]+2];
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
    	if(data.size() == 0)
    		return new int[0][0];
    	
    	int[][] newData = new int[data.size()][data.get(0).length];
    	
    	for(int i = 0; i < data.size(); i++)
    		newData[i] = data.get(i);
    	
    	return newData;
    }
    
    /** Record start time **/
    public static void start()
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
    	return clone(gameBoard);
    }
    
    /** Print end time **/
    public static void end(String s)
    {
    	//System.out.println(s + " time: " + String.valueOf(System.currentTimeMillis() - startTime));
    }
    
    
    /** Return the side opposite the given side **/
    public static int getOpposingSide(int side)
    {
    	if(side == Board.SIDE_BLACK)
    		return Board.SIDE_WHITE;
    	else
    		return Board.SIDE_BLACK;
    }
}
