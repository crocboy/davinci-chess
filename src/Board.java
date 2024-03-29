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
	
	public static final byte BLACK_PAWN = -1;
	public static final byte BLACK_BISHOP = -2;
	public static final byte BLACK_KNIGHT = -3;
	public static final byte BLACK_ROOK = -4;
	public static final byte BLACK_QUEEN = -5;
	public static final byte BLACK_KING = -6;
	
	public static final byte SIDE_WHITE = -1;
	public static final byte SIDE_BLACK = 1;
	
	/* Testing variables */
	public static int TOTAL_CHECKING_TIME = 0;
	public static int TOTAL_CLONE_TIME = 0;
	
	public static int TOTAL_MOVES = 0;
	
	
	
	
	/** Variable for timing **/
	public static long startTime = 0;
	
	/** Empty square **/
	public static final byte EMPTY = 0;
	
	/** Out of bounds square **/
	public static final byte OOB = 99;
	
	/** Used to convert Algebraic to Coordinates (and vice-versa) **/
	public static final String[] LETTER_ARRAY = {"","","a","b","c","d","e","f","g","h","",""};
	
	
	/* These are instance variables */
	public int gameSide = Board.SIDE_WHITE;
	
	/** Defines starting position.  (0,0) is a1 **/
	public static final byte[][] init = 
			
		{{99,99,99,99,99,99,99,99,99,99,99,99},	
		{99,99,99,99,99,99,99,99,99,99,99,99},	
	    {99,99,4,1,0,0,0,0,-1,-4,99,99},
		{99,99,3,1,0,0,0,0,-1,-3,99,99},
		{99,99,2,1,0,0,0,0,-1,-2,99,99},
		{99,99,5,1,0,0,0,0,-1,-5,99,99},
		{99,99,6,1,0,0,0,0,-1,-6,99,99},
		{99,99,2,1,0,0,0,0,-1,-2,99,99},
		{99,99,3,1,0,0,0,0,-1,-3,99,99},
		{99,99,4,1,0,0,0,0,-1,-4,99,99},
		{99,99,99,99,99,99,99,99,99,99,99,99},	
		{99,99,99,99,99,99,99,99,99,99,99,99}};

	
	
	/** Defines our padded board **/
	byte[][] gameBoard = new byte[12][12];
	
	
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
			board[7][2] = Board.WHITE_ROOK;
			board[8][2] = Board.WHITE_KING;
			board[9][2] = Board.EMPTY;
			board[10][2] = Board.EMPTY;
			return board;
		}
		
		if(s.length() == 5) //Pawn promotion
		{
			int[] move = letterToNumber(s.substring(0, 4));
			int[] start = numberToArray(move[0]);
			int[] end = numberToArray(move[1]);
			
			byte piece = board[start[0]][start[1]];
			
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
			
			byte piece = board[start[0]][start[1]];
			
			board[start[0]][start[1]] = Board.EMPTY;
			board[end[0]][end[1]] = piece;
		}
		
		return board;
	}
	
	
	/** Play a single move **/
	public static byte[][] playMove(int[] move, byte[][] board)
	{
		return playMove(Board.numberToLetter(move[0], move[1]), board);
	}
	
	
	/** Return a list of all possible moves, in square-notation **/
	public static ArrayList<int[]> getAllPossibleMoves(int side, final byte[][] board)
	{
		//start();
		long start = System.currentTimeMillis();
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		
		/* Find the locations of all white pieces */
		ArrayList<Integer> locs = getLocations(side, board);
		
		/* Add all moves for all pieces */
		for(int square : locs)
		{
			ArrayList<Integer> moves = getMoves(square, board);
			
			/* Add all moves to list (Pseudo-legal moves */
			for(int move : moves)
			{
				allMoves.add(new int[] {square,move});
			}
		}
		//end("All moves");
		TOTAL_MOVES += (System.currentTimeMillis() - start);
		return allMoves;
	}
	
	
	/** Take in a list of moves, and return only the ones that don't cause check **/
	public static ArrayList<int[]> limitMoves(byte[][] board, int side, ArrayList<int[]> moves)
	{
		ArrayList<int[]> allMoves = new ArrayList<int[]>();
		
		for(int[] move : moves)
		{
			if(!causesCheck(move, side, board)) //Make sure it doesn't cause check 
			{
				//String s = moveToString(square,move);
				//System.out.println(s);
				allMoves.add(move);
			}
		}
		
		return allMoves;
	}
	
	
	/** Return all moves for the given side **/
	/** Return all moves for the piece on the given square.
	 * Moves are given as one number, the destination square.
	 * Because the piece location is given, you already know the origin. **/
	public static ArrayList<Integer> getMoves(int square, byte[][] board)
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
		
		return allMoves;
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
	public static ArrayList<Integer> getLocations(int side, byte[][] board)
	{
		ArrayList<Integer> pos = new ArrayList<Integer>();
		
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board[0].length; y++)
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
		
		return pos;
	}
	
	
	/* These methods generate possible positions for each piece, given the starting location */
	/* All methods return int[], where destination squares are given in square notation */
	
	/** Generate pawn moves **/
	public static ArrayList<Integer> getPawnMoves(int square, byte[][] board)
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
			isBlocked = !(getPiece(board,square+12) == Board.EMPTY);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square+12); //Up one
				
				if(y == 1 && getPiece(board,square + 24) == Board.EMPTY)
				{
					moves.add(square+24); //Up 2
				}
			}
			
			if(board[x+1][y+1] != Board.EMPTY && board[x+1][y+1] != Board.OOB)
			{
				moves.add(square + 13); //attack up right
			}
			
			if(board[x-1][y+1] != Board.EMPTY && board[x-1][y+1] != Board.OOB)
			{
				moves.add(square + 11); //attack up left
			}
		}
		else if(piece == Board.BLACK_PAWN) 
		{
			isBlocked = !(getPiece(board,square-12) == Board.EMPTY);
			
			if(!isBlocked) //Only add forward moves if it's not blocked
			{
				moves.add(square - 12); //1 down
				
				if(y == 6 && getPiece(board,square - 24) == Board.EMPTY)
				{
					moves.add(square - 24); //2 down
				}
			}
			
			byte p = board[x+1][y-1];
			if(p != Board.EMPTY && p != Board.OOB)
			{
				moves.add(square - 11); //Down left attack
			}
			
			p = board[x-1][y-1];
			if(p != Board.EMPTY && p != Board.OOB)
			{
				moves.add(square - 13); //Down right attack
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
		return finalMoves;
	}
	
	/** Generate knight moves **/
	public static ArrayList<Integer> getKnightMoves(int square, byte[][] board)
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
		return moves;
	}
	
	/** Generate bishop moves **/
	public static ArrayList<Integer> getBishopMoves(int square, byte[][] board)
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
		return moves;
	}
	
	/** Generate rook moves **/
	public static ArrayList<Integer> getRookMoves(int square, byte[][] board)
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
		return moves;
	}
	
	/** Generate queen moves **/
	public static ArrayList<Integer> getQueenMoves(int square, byte[][] board)
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
		return moves;
	}
	
	/** Generate king moves **/
	public static ArrayList<Integer> getKingMoves(int square, byte[][] board)
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
		return moves;
	}
	
	
	/** Promote pawns if need be, return a boolean indicating if the pawn was actually promoted **/
	public static boolean promotePawn(int[] start, int[] end, byte piece, byte[][] board)
	{
		if(piece == Board.BLACK_PAWN)
		{
			if(end[1] != 2) //Check position
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
			if(end[1] != 9) //Check position
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
		String moveString = numberToLetter(move[0],move[1]); //Play the move on our board
		board = playMove(moveString, board);
		
		return isInCheck(side, board); //Call this function to return if the side is in check
	}
	
	
	
	/** Return true if the given side is IN check on the given board **/
	public static boolean isInCheck(int side, byte[][] board)
	{
		long start = System.currentTimeMillis();
		int king = getKingLocation(side, board);
		
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
			if(getPiece(board,x+1,y+2) == Board.BLACK_KNIGHT || getPiece(board,x+1,y-2) == Board.BLACK_KNIGHT || getPiece(board,x+2,y+1) == Board.BLACK_KNIGHT || getPiece(board,x+2,y-1) == Board.BLACK_KNIGHT || getPiece(board,x-1,y-2) == Board.BLACK_KNIGHT || getPiece(board,x-1,y+2) == Board.BLACK_KNIGHT || getPiece(board,x-2,y+1) == Board.BLACK_KNIGHT || getPiece(board,x-2,y-1) == Board.BLACK_KNIGHT)
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
		
		Board.TOTAL_CHECKING_TIME += System.currentTimeMillis() - start;
		
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
	
	
	/** Get the piece at the given square **/
	public static byte getPiece(byte[][] board, int square)
	{
		if(square < 0)
			System.out.print(true);
		int[] loc = numberToArray(square);
		return board[loc[0]][loc[1]];
	}
	
	/** Get the piece at the given square **/
	public static byte getPiece(byte[][] board, int... loc)
	{
		if(loc[0] < 0 || loc[1] < 0)
			System.out.print(true);
		return board[loc[0]][loc[1]];
	}

    
    /** Convert sqaure notation to algebraic notation **/
    public static String numberToLetter(int s, int e)
    {
    	int[] start = numberToArray(s);
    	int[] end = numberToArray(e);
    	
        String result = LETTER_ARRAY[start[0]] + String.valueOf(start[1] - 1) + LETTER_ARRAY[end[0]] + String.valueOf(end[1] - 1);
        return result;
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
            start[1] = Integer.parseInt(String.valueOf(s.charAt(1))) + 1;
            end[0] = Util.indexOf(LETTER_ARRAY, String.valueOf(s.charAt(2)));
            end[1] = Integer.parseInt(String.valueOf(s.charAt(3))) + 1;
            
            int[] square = {arrayToNumber(start), arrayToNumber(end)};
            return square;
    }
    
    
    /** Convert a single square number (26) to it's coordinate equivalent (2,2) on a 12x12 board **/
    public static int[] numberToArray(int square)
    {
    	int[] pos = new int[2];
    	pos[0] = square % 12; //Get x-coord
    	pos[1] = square / 12; //Get y-coord
    	return pos;
    }
    
    /** Convert an array coordinate (2,2) on a 12x12 board to it's single number equivalent (26)**/
    public static int arrayToNumber(int... coord)
    {
    	if(coord.length != 2)
    		return -1;
    	
    	int square = coord[1] * 12;
    	square += coord[0];
    	
    	return square;
    }
    
    /** Record start time **/
    public static void start()
    {
    	startTime = System.currentTimeMillis();
    }
    
    
    /** Clone a byte[][] **/
    public static byte[][] clone(byte[][] data)
    {
    	long start = System.currentTimeMillis();
    	byte[][] newData = new byte[data.length][data[0].length];
    	for(int x = 0; x < data.length; x++)
    	{
    		for(int y = 0; y < data[0].length; y++)
    		{
    			newData[x][y] = data[x][y];
    		}
    	}
    	Board.TOTAL_CLONE_TIME += System.currentTimeMillis() - start;
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
