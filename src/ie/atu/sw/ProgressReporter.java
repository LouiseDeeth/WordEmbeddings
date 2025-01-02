package ie.atu.sw;

public class ProgressReporter {

	/*
	 * Terminal Progress Meter ----------------------- You might find the progress
	 * meter below useful. The progress effect works best if you call this method
	 * from inside a loop and do not call System.out.println(....) until the
	 * progress meter is finished.
	 * 
	 * Please note the following carefully:
	 * 
	 * 1) The progress meter will NOT work in the Eclipse console, but will work on
	 * Windows (DOS), Mac and Linux terminals.
	 * 
	 * 2) The meter works by using the line feed character "\r" to return to the
	 * start of the current line and writes out the updated progress over the
	 * existing information. If you output any text between calling this method,
	 * i.e. System.out.println(....), then the next call to the progress meter will
	 * output the status to the next line.
	 * 
	 * 3) If the variable size is greater than the terminal width, a new line escape
	 * character "\n" will be automatically added and the meter won't work properly.
	 * 
	 * 
		 */
	/**
	 * The print progress method to show progress as words are being parsed in
	 * @param progressPercentage
	 */
	public static void printProgress(int progressPercentage) {
		System.out.print(ConsoleColour.CYAN);
	    final int width = 50; 		//Must be less than console width
	    int progress = (int) (width * progressPercentage / 100.0);
	    StringBuilder sb = new StringBuilder("["); 
	    for(int i = 0; i < width; i++) {
		    if(i < progress) {
		    	sb.append('█');
		    }else {
		    	sb.append('░');  	
		    }
	  }
	  sb.append("] ").append(progressPercentage).append("%");
	  System.out.print("\r" + sb.toString());      
	
	  if(progressPercentage >= 100) {
		  System.out.println();//move to new line
	    
	    /*
	     * A StringBuilder should be used for string concatenation inside a 
	     * loop. However, as the number of loop iterations is small, using
	     * the "+" operator may be more efficient as the instructions can
	     * be optimized by the compiler. Either way, the performance overhead
	     * will be marginal.  


	     * The line feed escape character "\r" returns the cursor to the 
	     * start of the current line. Calling print(...) overwrites the
	     * existing line and creates the illusion of an animation.
	     */
		}
	}
}
