import java.util.ArrayList;

/** Evaluation contains all of the necessary pieces to numerically evaluate the position of a chess board **/
public class Evaluation {
	
	/* These ints define the piece values */
	public static final int PAWN_VALUE = 100;
	public static final int KNIGHT_VALUE = 300;
	public static final int BISHOP_VALUE = 300;
	public static final int ROOK_VALUE = 500;
	public static final int QUEEN_VALUE = 900;
	public static final int KING_VALUE = 20000;
	
	public static double NODES = 0;
	public static double NPS = 0;
	public static long START = 0;
	
	/* Other constants used for scoring */
	public static final int CHECK_BONUS = 10;
	
	/* The following are weight constants.  Normally they are 1.  +/- to change evaluations. */
	public static final double MOBILITY_WEIGHT = 1;
	public static final double MATERIAL_WEIGHT = 1;
	public static final double POSITION_WEIGHT = 2;
	
	public static int BLACK_MOB = 0;
	public static int WHITE_MOB = 0;
	
	
	/* Search depth constants */
	public static final int SEARCH_DEPTH = 5;
	
	/* Value for mate */
	public static final int MATE = Integer.MAX_VALUE;
	
	public static int TOTAL_EVAL_TIME = 0;
	
	
	/** Find the best move among a list of moves and a given board **/
	public static int[] findBestMove(byte[][] board, int side)
	{
		NODES = 0;
		START = System.currentTimeMillis();
		
		/* Reset check vars */
		Board.TOTAL_CHECKING_TIME = 0;
		Board.TOTAL_CLONE_TIME = 0;
		Board.TOTAL_MOVES = 0;
		Evaluation.TOTAL_EVAL_TIME = 0;
		Board.TOTAL_CLONE_TIME =0;
		
		int bestScore = -MATE;
		int[] bestMove = null;
		
		ArrayList<int[]> moves = Board.getAllPossibleMoves(side, board);
		ArrayList<int[]> legalMoves = Board.limitMoves(board, side, moves);
		
		for(int[] move : legalMoves)
		{
			NODES++;
			byte[][] result = Board.clone(Board.playMove(move, board));
			int eval = 0;
			
			if(SEARCH_DEPTH == 0)
				eval = evaluate(result, side);
			
			else 
			{
				eval = -AB(SEARCH_DEPTH, result, -side, -MATE, MATE);
				//eval = -negaMax(SEARCH_DEPTH, result, -side);
			}
			
			if(eval > bestScore)
			{
				bestScore = eval;
				bestMove = move;
			}
		}
		
		/* Nodes per second calculations */
		double totalTime = System.currentTimeMillis() - START;
		double nps = NODES / (totalTime/1000f);
		System.out.println("info nps " + (int)nps  + " nodes " + (int)NODES);
		
		System.out.println("Total: " + (System.currentTimeMillis() - START));
		System.out.println("Total Clone: " + Board.TOTAL_CLONE_TIME);
		System.out.println("Total Checking: " + Board.TOTAL_CHECKING_TIME);
		System.out.println("Total Moves: " + Board.TOTAL_MOVES);
		
		
		return bestMove;
	}
	
	
	/** Evaluate the result of the move on the given board **/
	public static int evaluate(byte[][] board, int side)
	{
		//side = Board.SIDE_BLACK;
		long start = System.currentTimeMillis();
		int material = getMaterialValue(side, board);
		
		//Add a check bonus
		/*if(Board.isInCheck(Board.getOpposingSide(side), board))
		{
			eval += CHECK_BONUS;
			//System.out.println("Can cause check: " + move[0] + " to " + move[1]);
		}*/
		
		int mobilityScore = 0; //MOBILITY_WEIGHT * (bMob - wMob);
		
		Evaluation.TOTAL_EVAL_TIME += System.currentTimeMillis() - start;
		return (material);
	}
	
	
	
	/** Return the raw material value on the given board of the given side **/
	public static int getMaterialValue(int side, byte[][] board)
	{
		int wEval = 0;
		int bEval = 0;
		
		int wPos = 0;
		int bPos = 0;
		
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board[0].length; y++)
			{
				byte piece = board[x][y];
				if(piece == Board.WHITE_PAWN)
				{
					wEval += PAWN_VALUE;
					wPos += WhitePawnTable[x-2][y-2];
				}
				if(piece == Board.WHITE_KNIGHT)
				{
					wEval += KNIGHT_VALUE;
					wPos += WhiteKnightTable[x-2][y-2];
				}
				if(piece == Board.WHITE_BISHOP)
				{
					wEval += BISHOP_VALUE;
					wPos += WhiteBishopTable[x-2][y-2];
				}
				if(piece == Board.WHITE_ROOK)
				{
					if(x == 0 || y == 0)
						System.out.print(true);
					
					wEval += ROOK_VALUE;
					wPos += WhiteRookTable[x-2][y-2];
				}
				if(piece == Board.WHITE_QUEEN)
				{
					wEval += QUEEN_VALUE;
					wPos += WhiteQueenTable[x-2][y-2];
				}
				if(piece == Board.WHITE_KING)
				{
					wEval += KING_VALUE;
					wPos += WhiteKingMiddleGame[x-2][y-2];
				}

				if(piece == Board.BLACK_PAWN)
				{
					bEval += PAWN_VALUE;
					bPos += BlackPawnTable[x-2][y-2];
				}
				if(piece == Board.BLACK_KNIGHT)
				{
					bEval += KNIGHT_VALUE;
					bPos += BlackKnightTable[x-2][y-2];
				}
				if(piece == Board.BLACK_BISHOP)
				{
					bEval += BISHOP_VALUE;
					bPos += BlackBishopTable[x-2][y-2];
				}
				if(piece == Board.BLACK_ROOK)
				{
					bEval += ROOK_VALUE;
					bPos += BlackRookTable[x-2][y-2];
				}
				if(piece == Board.BLACK_QUEEN)
				{
					bEval += QUEEN_VALUE;
					bPos += BlackQueenTable[x-2][y-2];
				}
				if(piece == Board.BLACK_KING)
				{
					bEval += KING_VALUE;
					bPos += BlackKingMiddleGame[x-2][y-2];
				}
			}
		}
		
		double material = (bEval - wEval) * MATERIAL_WEIGHT;
		double position = (bPos - wPos) * POSITION_WEIGHT;
		
		//return (bEval - wEval) * side;
		int total = (int)(material + position) * side;
		return total;
	}
	
	
	
	public static int AB(int depth, byte[][] board, int side, int a, int b) 
	{	
		NODES++;
		if(Board.getKingLocation(side, board) == -1)
			System.out.println("He be gone!");
		
	    if (depth == 0) //Limiting condition
	    	return evaluate(board, side);
	    
	    ArrayList<int[]> moves = Board.getAllPossibleMoves(side,board);
	    ArrayList<int[]> legalMoves = Board.limitMoves(board, side, moves);
	    
	    if(moves.size() == 0)
	    	return MATE;
	    
	    for (int[] move : legalMoves)  
	    {
	    	byte[][] result = Board.playMove(move, board);
	        int score = -AB(depth - 1, result, -side, -b, -a);
	        
	        if(score >= b)
	        	return b;
	        if(score > a)
	        	a = score;
	    }
	    
	    return a;
	}
	
	
	public static int negaMax(int depth, byte[][] board, int side) 
	{	
		NODES++;
	    if (depth == 0) //Limiting condition
	    	return evaluate(board, side);
	    
	    int max = -MATE;
	    ArrayList<int[]> moves = Board.getAllPossibleMoves(side, board);
	    
	    if(moves.size() == 0)
	    	return MATE;
	    	
	    for (int[] move : moves)  
	    {
	    	byte[][] result = Board.playMove(move, board);
	        int score = -negaMax(depth - 1, result, -side);
	        
	        if(score > max)
	            max = score;
	        
	    }
	    
	    return max;
	}
	
	
	/* Define piece square tables (PST's) */
	
	public static final byte[][] WhitePawnTable = 
			
		{{0,  0,  0,  0,  0,  0,  0,  0},
		{50, 50, 50, 50, 50, 50, 50, 50},
		{10, 10, 20, 30, 30, 20, 10, 10},
		{5,  5, 10, 25, 25, 10,  5,  5},
		{0,  0,  0, 20, 20,  0,  0,  0},
		{5, -5,-10,  0,  0,-10, -5,  5},
		{5, 10, 10,-20,-20, 10, 10,  5},
		{ 0,  0,  0,  0,  0,  0,  0,  0 }};
	
	public static final byte[][] BlackPawnTable = 
			
		{{ 0,  0,  0,  0,  0,  0,  0,  0},
		{5, 10, 10,-20,-20, 10, 10,  5},
		{5, -5,-10,  0,  0,-10, -5,  5},
		{0,  0,  0, 20, 20,  0,  0,  0},
		{5,  5, 10, 25, 25, 10,  5,  5},
		{10, 10, 20, 30, 30, 20, 10, 10},
		{50, 50, 50, 50, 50, 50, 50, 50},
		{0,  0,  0,  0,  0,  0,  0,  0}};
		
	
	public static final byte[][] WhiteKnightTable = 
			
		{{-50,-40,-30,-30,-30,-30,-40,-50},
		{-40,-20,  0,  0,  0,  0,-20,-40},
		{-30,  0, 10, 15, 15, 10,  0,-30},
		{-30,  5, 15, 20, 20, 15,  5,-30},
		{-30,  0, 15, 20, 20, 15,  0,-30},
		{-30,  5, 10, 15, 15, 10,  5,-30},
		{-40,-20,  0,  5,  5,  0,-20,-40},
		{-50,-40,-30,-30,-30,-30,-40,-50}};
	
	public static final byte[][] BlackKnightTable = 
			
		{{-50,-40,-30,-30,-30,-30,-40,-50},
		{-40,-20,  0,  5,  5,  0,-20,-40},
		{-30,  5, 10, 15, 15, 10,  5,-30},
		{-30,  0, 15, 20, 20, 15,  0,-30},
		{-30,  5, 15, 20, 20, 15,  5,-30},
		{-30,  0, 10, 15, 15, 10,  0,-30},
		{-40,-20,  0,  0,  0,  0,-20,-40},
		{-50,-40,-30,-30,-30,-30,-40,-50}};
		
	
	public static final byte[][] WhiteBishopTable = 
			
		{{-20,-10,-10,-10,-10,-10,-10,-20},
		{-10,  0,  0,  0,  0,  0,  0,-10},
		{-10,  0,  5, 10, 10,  5,  0,-10},
		{-10,  5,  5, 10, 10,  5,  5,-10},
		{-10,  0, 10, 10, 10, 10,  0,-10},
		{-10, 10, 10, 10, 10, 10, 10,-10},
		{-10,  5,  0,  0,  0,  0,  5,-10},
		{-20,-10,-10,-10,-10,-10,-10,-20}};
	
	public static final byte[][] BlackBishopTable = 
		
		{{-20,-10,-10,-10,-10,-10,-10,-20},
		{-10,  5,  0,  0,  0,  0,  5,-10},
		{-10, 10, 10, 10, 10, 10, 10,-10},
		{-10,  0, 10, 10, 10, 10,  0,-10},
		{-10,  5,  5, 10, 10,  5,  5,-10},
		{-10,  0,  5, 10, 10,  5,  0,-10},
		{-10,  0,  0,  0,  0,  0,  0,-10},
		{-20,-10,-10,-10,-10,-10,-10,-20}};
		
	
	public static final byte[][] WhiteRookTable = 
			
		{{ 0,  0,  0,  0,  0,  0,  0,  0},
		{5, 10, 10, 10, 10, 10, 10,  5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{ 0,  0,  0,  5,  5,  0,  0,  0}};
	
	
	public static final byte[][] BlackRookTable = 
		
		{{ 0,  0,  0,  5,  5,  0,  0,  0},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{-5,  0,  0,  0,  0,  0,  0, -5},
		{5, 10, 10, 10, 10, 10, 10,  5},
		{ 0,  0,  0,  0,  0,  0,  0,  0}};
		
		
	public static final byte[][] WhiteQueenTable = 
			
		{{-20,-10,-10, -5, -5,-10,-10,-20},
		{-10,  0,  0,  0,  0,  0,  0,-10},
		{-10,  0,  5,  5,  5,  5,  0,-10},
		{ -5,  0,  5,  5,  5,  5,  0, -5},
		{  0,  0,  5,  5,  5,  5,  0, -5},
		{-10,  5,  5,  5,  5,  5,  0,-10},
		{-10,  0,  5,  0,  0,  0,  0,-10},
		{-20,-10,-10, -5, -5,-10,-10,-20}};
	
	public static final byte[][] BlackQueenTable = 
		
		{{-20,-10,-10, -5, -5,-10,-10,-20},
		{-10,  0,  0,  0,  0,  5,  0,-10},
		{-10,  0,  5,  5,  5,  5,  5,-10},
		{  0,  0,  5,  5,  5,  5,  0, -5},
		{ -5,  0,  5,  5,  5,  5,  0, -5},
		{-10,  0,  5,  5,  5,  5,  0,-10},
		{-10,  0,  0,  0,  0,  0,  0,-10},
		{-20,-10,-10, -5, -5,-10,-10,-20}};
		
		
	public static final byte[][] WhiteKingMiddleGame = 
			
		{{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-20,-30,-30,-40,-40,-30,-30,-20},
		{-10,-20,-20,-20,-20,-20,-20,-10},
		{ 20, 20,  0,  0,  0,  0, 20, 20},
		{ 20, 30, 10,  0,  0, 10, 30, 20}};
	
	public static final byte[][] BlackKingMiddleGame = 
		
		{{ 20, 30, 10,  0,  0, 10, 30, 20},
		{ 20, 20,  0,  0,  0,  0, 20, 20},
		{-10,-20,-20,-20,-20,-20,-20,-10},
		{-20,-30,-30,-40,-40,-30,-30,-20},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30},
		{-30,-40,-40,-50,-50,-40,-40,-30}};
		
	
	public static final byte[][] WhiteKingEndGame = 
			
		{{-50,-40,-30,-20,-20,-30,-40,-50},
		{-30,-20,-10,  0,  0,-10,-20,-30},
		{-30,-10, 20, 30, 30, 20,-10,-30},
		{-30,-10, 30, 40, 40, 30,-10,-30},
		{-30,-10, 30, 40, 40, 30,-10,-30},
		{-30,-10, 20, 30, 30, 20,-10,-30},
		{-30,-30,  0,  0,  0,  0,-30,-30},
		{-50,-30,-30,-30,-30,-30,-30,-50}};
	
	public static final byte[][] BlackKingEndGame = 
		
		{{-50,-30,-30,-30,-30,-30,-30,-50},
		{-30,-30,  0,  0,  0,  0,-30,-30},
		{-30,-10, 20, 30, 30, 20,-10,-30},
		{-30,-10, 30, 40, 40, 30,-10,-30},
		{-30,-10, 30, 40, 40, 30,-10,-30},
		{-30,-10, 20, 30, 30, 20,-10,-30},
		{-30,-20,-10,  0,  0,-10,-20,-30},
		{-50,-40,-30,-20,-20,-30,-40,-50}};
		
		
	/* End definition of PST's */
			
			
			
}
