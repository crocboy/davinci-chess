/** Util defines basic methods that either have nothing to do with chess, or need to be used globally. */
public class Util 
{
	/** Return the index (From LetterArray) of the given argument **/
    public static int indexOf(String[] original, String target)
    {
            for(int i = 0; i < original.length; i++)
            {
                    if(original[i].equals(target))
                            return i;
            }
            
            return -1;
    }
    
    /** Clone a byte array into another, completely seperate byte array **/
    public byte[][] clone(byte[][] original)
    {
    	byte[][] newArray = new byte[original.length][original[0].length];
        
        for(int x = 0; x < original.length; x++)
        {
                for(int y = 0; y < original[0].length; y++)
                {
                        newArray[x][y] = original[x][y];
                }
        }
        
        return newArray;
    }
    
    /** Return one long divided by the other **/
    public static double getPercent(long s, long e)
    {
    	double d = (double)s/(double)e;
    	return d;
    }
    
}
