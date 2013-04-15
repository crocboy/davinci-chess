import java.util.ArrayList;

/** Evaluation contains all of the necessary pieces to numerically evaluate the position of a chess board **/
public class Evaluation {
	
	/* These ints define the piece values */
	public static final int PAWN_VALUE = 10;
	public static final int KNIGHT_VALUE = 30;
	public static final int BISHOP_VALUE = 30;
	public static final int ROOK_VALUE = 50;
	public static final int QUEEN_VALUE = 90;
	public static final int KING_VALUE = 1000;
	
	/* Other constants used for scoring */
	public static final int CHECK_BONUS = 10;
	
	/* Search depth constants */
	public static final int SEARCH_DEPTH = 1;
	
	/* Value for mate */
	public static final int MATE = Integer.MAX_VALUE;
	
	public static int TOTAL_EVAL_TIME = 0;
	
	
	/** Find the best move among a list of moves and a given board **/
	public static int[] findBestMove(byte[][] board, int side)
	{
		long s = System.currentTimeMillis();
		
		/* Reset check vars */
		Board.TOTAL_CHECKING_TIME = 0;
		Board.TOTAL_MOVES_TIME = 0;
		Evaluation.TOTAL_EVAL_TIME = 0;
		Board.TOTAL_CLONE_TIME =0;
		
		int bestScore = -MATE;
		int[] bestMove = null;
		
		ArrayList<int[]> moves = Board.getAllPossibleMoves(side, Board.clone(board));
		
		for(int[] move : moves)
		{
			byte[][] result = Board.clone(Board.playMove(move, board));
			
			//int eval = AB(SEARCH_DEPTH, result, side, -MATE, MATE);
			int eval = -negaMax(SEARCH_DEPTH - 1, result, -side);
			
			if(eval > bestScore)
			{
				bestScore = eval;
				bestMove = move;
			}
		}
		
		
		long total = System.currentTimeMillis() - s;
		System.out.println("Total: " + total);
		System.out.println("Total Checking: " + Util.getPercent(Board.TOTAL_CHECKING_TIME, total));
		System.out.println("Total Cloning: " + Util.getPercent(Board.TOTAL_CLONE_TIME, total));
		System.out.println("Total Moves: " + Util.getPercent(Board.TOTAL_MOVES_TIME, total));
		System.out.println("Total Eval: " + Evaluation.TOTAL_EVAL_TIME);
		
		return bestMove;
	}
	
	
	/** Evaluate the result of the move on the given board **/
	public static int evaluate(byte[][] board, int side)
	{
		long start = System.currentTimeMillis();
		int material = getMaterialValue(side, board);
		
		//Add a check bonus
		/*if(Board.isInCheck(Board.getOpposingSide(side), board))
		{
			eval += CHECK_BONUS;
			//System.out.println("Can cause check: " + move[0] + " to " + move[1]);
		}*/
		
		Evaluation.TOTAL_EVAL_TIME += System.currentTimeMillis() - start;
		return material;
	}
	
	
	
	/** Return the raw material value on the given board of the given side **/
	public static int getMaterialValue(int side, byte[][] board)
	{
		int wEval = 0;
		int bEval = 0;
		
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board[0].length; y++)
			{
				byte piece = board[x][y];
				if(piece == Board.WHITE_PAWN)
					wEval += PAWN_VALUE;
				if(piece == Board.WHITE_KNIGHT)
					wEval += KNIGHT_VALUE;
				if(piece == Board.WHITE_BISHOP)
					wEval += BISHOP_VALUE;
				if(piece == Board.WHITE_ROOK)
					wEval += ROOK_VALUE;
				if(piece == Board.WHITE_QUEEN)
					wEval += QUEEN_VALUE;
				if(piece == Board.WHITE_KING)
					wEval += KING_VALUE;

				if(piece == Board.BLACK_PAWN)
					bEval += PAWN_VALUE;
				if(piece == Board.BLACK_KNIGHT)
					bEval += KNIGHT_VALUE;
				if(piece == Board.BLACK_BISHOP)
					bEval += BISHOP_VALUE;
				if(piece == Board.BLACK_ROOK)
					bEval += ROOK_VALUE;
				if(piece == Board.BLACK_QUEEN)
					bEval += QUEEN_VALUE;
				if(piece == Board.BLACK_KING)
					bEval += KING_VALUE;
			}
		}
		
		return (bEval - wEval) * side;
	}
	
	
	public static int AB(int depth, byte[][] board, int side, int a, int b) 
	{	
		if(Board.getKingLocation(side, board) == -1)
		{
			System.out.println("HE'S GONEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
		}
		
	    if (depth == 0) //Limiting condition
	    	return evaluate(board, side);
	    
	    ArrayList<int[]> moves = Board.getAllPossibleMoves(side, board.clone());
	    for (int[] move : moves)  
	    {
	    	byte[][] result = Board.playMove(move, board);
	        int score = -AB(depth - 1, result, Board.getOpposingSide(side), -b, -a);
	        
	        if(score >= b)
	        	return b;
	        if(score > a)
	        	a = score;
	    }
	    
	    return a;
	}
	
	
	public static int negaMax(int depth, byte[][] board, int side) 
	{	
	    if (depth == 0) //Limiting condition
	    	return evaluate(board, side);
	    
	    int max = -MATE;
	    ArrayList<int[]> moves = Board.getAllPossibleMoves(side, board);
	    for (int[] move : moves)  
	    {
	    	byte[][] result = Board.playMove(move, board);
	        int score = -negaMax(depth - 1, result, -side);
	        
	        if(score > max)
	            max = score;
	        
	    }
	    
	    return max;
	}
}
