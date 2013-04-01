/** Evaluation contains all of the necessary pieces to numerically evaluate the position of a chess board **/
public class Evaluation {
	
	/* These ints define the piece values */
	public static final int PAWN_VALUE = 100;
	public static final int KNIGHT_VALUE = 300;
	public static final int BISHOP_VALUE = 300;
	public static final int ROOK_VALUE = 500;
	public static final int QUEEN_VALUE = 900;
	public static final int KING_VALUE = 10000;
	
	/* Other constants used for scoring */
	public static final int CHECK_BONUS = 10;
	
	/* Search depth constants */
	public static final int SEARCH_DEPTH = 3;
	
	/* Value for mate */
	public static final int MATE = 10000;
	
	
	/** Find the best move among a list of moves and a given board **/
	public static int[] findBestMove(byte[][] board, int side)
	{
		long s = System.currentTimeMillis();
		
		int bestScore = -10000;
		int[] bestMove = null;
		
		int[][] moves = Board.getAllPossibleMoves(side, Board.clone(board));
		
		for(int i = 0; i < moves.length; i++)
		{
			int[] move = moves[i];
			byte[][] result = Board.clone(Board.playMove(move, board));
			
			int eval = AB(SEARCH_DEPTH, result, Board.getOpposingSide(side), -MATE, MATE);
			//int eval = negaMax(SEARCH_DEPTH, result, side);
			
			if(eval > bestScore)
			{
				bestScore = eval;
				bestMove = move;
			}
		}
		
		System.out.println("Total: " + (System.currentTimeMillis() - s) + "\n");
		
		return bestMove;
	}
	
	
	/** Evaluate the result of the move on the given board **/
	public static int evaluate(byte[][] board, int side)
	{
		int eval = 0;
		
		int myMaterial = getMaterialValue(side, board); //Get net material score
		int theirMaterial = getMaterialValue(Board.getOpposingSide(side), board);
		eval +=  myMaterial - theirMaterial;
		
		//Add a check bonus
		if(Board.isInCheck(Board.getOpposingSide(side), board))
		{
			eval += CHECK_BONUS;
			//System.out.println("Can cause check: " + move[0] + " to " + move[1]);
		}
		
		return eval;
	}
	
	
	
	/** Return the raw material value on the given board of the given side **/
	public static int getMaterialValue(int side, byte[][] board)
	{
		int eval = 0;
		
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board[0].length; y++)
			{
				byte piece = board[x][y];
				
				if(side == Board.SIDE_WHITE)
				{
					if(piece == Board.WHITE_PAWN)
						eval += PAWN_VALUE;
					if(piece == Board.WHITE_KNIGHT)
						eval += KNIGHT_VALUE;
					if(piece == Board.WHITE_BISHOP)
						eval += BISHOP_VALUE;
					if(piece == Board.WHITE_ROOK)
						eval += ROOK_VALUE;
					if(piece == Board.WHITE_QUEEN)
						eval += QUEEN_VALUE;
					if(piece == Board.WHITE_KING)
						eval += KING_VALUE;
				}
				
				if(side == Board.SIDE_BLACK)
				{
					if(piece == Board.BLACK_PAWN)
						eval += PAWN_VALUE;
					if(piece == Board.BLACK_KNIGHT)
						eval += KNIGHT_VALUE;
					if(piece == Board.BLACK_BISHOP)
						eval += BISHOP_VALUE;
					if(piece == Board.BLACK_ROOK)
						eval += ROOK_VALUE;
					if(piece == Board.BLACK_QUEEN)
						eval += QUEEN_VALUE;
					if(piece == Board.BLACK_KING)
						eval += KING_VALUE;
				}
			}
		}
		return eval;
	}
	
	
	
	/*public static int NegaMax(byte[][] board, int depth, int side) 
	{
		board = Board.clone(board);
		 if (depth == 0) 
			 return Evaluation.getMaterialValue(side, board);
		 
		int bestScore = -10000; 
		
		for(int[] move : Board.getAllPossibleMoves(side, board))
		{
			byte[][] newBoard = Board.clone(Board.playMove(move, board));
			int score = NegaMax(newBoard, depth-1, Board.getOpposingSide(side));
			 score = -score;
			 if ( score > bestScore )
			 {
				bestScore = score; 
			 }
			
		}
		 return bestScore;
	}*/
	
	
	public static int AB(int depth, byte[][] board, int side, int a, int b) 
	{	
		if(Board.getKingLocation(side, board) == -1)
		{
			System.out.print("");
		}
		
	    if (depth == 0) //Limiting condition
	    	return evaluate(board, side);
	    
	    int max = -10000;
	    int[][] moves = Board.getAllPossibleMoves(side, board);
	    for (int[] move : moves)  
	    {
	    	byte[][] result = Board.clone(Board.playMove(move, board));
	        int score = -AB(depth - 1, result, Board.getOpposingSide(side), -a, -b);
	        if( score > max )
	            max = score;
	        if(max > a)
	        	a = max;
	        if(max >= b)
	        	break;
	        
	    }
	    
	    return max;
	}
	
	
	public static int negaMax(int depth, byte[][] board, int side) 
	{	
		if(Board.getKingLocation(side, board) == -1)
		{
			//System.out.print(true);
		}
		
	    if (depth == 0) //Limiting condition
	    	return evaluate(board, side);
	    
	    int max = -10000;
	    int[][] moves = Board.getAllPossibleMoves(side, board);
	    for (int[] move : moves)  
	    {
	    	byte[][] result = Board.clone(Board.playMove(move, board));
	        int score = -negaMax(depth - 1, result, Board.getOpposingSide(side));
	        if( score > max )
	            max = score;
	        
	    }
	    
	    return max;
	}
	
	
	/* Minimax functions */
	/*public int maxi( int depth ) {
	    if ( depth == 0 ) return evaluate();
	    int max = -oo;
	    for ( all moves) {
	        score = mini( depth - 1 );
	        if( score > max )
	            max = score;
	    }
	    return max;
	}
	 
	public int mini( int depth ) {
	    if ( depth == 0 ) return -evaluate();
	    int min = +oo;
	    for ( all moves) {
	        score = maxi( depth - 1 );
	        if( score < min )
	            min = score;
	    }
	    return min;
	}*/
	

}
