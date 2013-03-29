/** Evaluation contains all of the necessary pieces to numerically evaluate the position of a chess board **/
public class Evaluation {
	
	
	/** Find the best move among a list of moves and a given board **/
	public static int[] findBestMove(int[][] moves, byte[][] board, int side)
	{
		int maxScore = 0;
		int index = 0;
		
		for(int i = 0; i < moves.length; i++)
		{
			int[] move = moves[i];
			int eval = evaluate(move,board, side);
			
			if(Board.causesCheck(move, Board.SIDE_WHITE, board))
			{
				eval += 10;
				System.out.println("Can cause check: " + move[0] + " to " + move[1]);
			}
			
			if(eval > maxScore)
			{
				maxScore = eval;
				index = i;
			}
		}
		
		return moves[index];
	}
	
	
	/** Evaluate the result of the move on the given board **/
	public static int evaluate(int[] move, byte[][] board, int side)
	{
		byte[][] result = Board.clone(Board.playMove(Board.numberToLetter(move[0], move[1]),board));
		return evaluate(result, side);
	}
	
	
	/** Numerically evaluate the given board.  Assuming you are the given side, it will always return a positive number **/
	public static int evaluate(byte[][] board, int side)
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
						eval += 1;
					if(piece == Board.WHITE_KNIGHT)
						eval += 3;
					if(piece == Board.WHITE_BISHOP)
						eval += 3;
					if(piece == Board.WHITE_ROOK)
						eval += 5;
					if(piece == Board.WHITE_QUEEN)
						eval += 9;
					if(piece == Board.WHITE_KING)
						eval += 20;
				}
				
				if(side == Board.SIDE_BLACK)
				{
					if(piece == Board.BLACK_PAWN)
						eval += 1;
					if(piece == Board.BLACK_KNIGHT)
						eval += 3;
					if(piece == Board.BLACK_BISHOP)
						eval += 3;
					if(piece == Board.BLACK_ROOK)
						eval += 5;
					if(piece == Board.BLACK_QUEEN)
						eval += 9;
					if(piece == Board.BLACK_KING)
						eval += 20;
				}
			}
		}
		return eval;
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
