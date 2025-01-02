package ie.atu.sw;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

/**
 * @author Louise Deeth
 * @version 1.0
 */

public class Runner {
    private static EmbeddingsParser embeddingsParser = new EmbeddingsParser();
    private static PrintWriter outputFile = null; 
    private boolean running = true;//control the loop   
    
    // main - O(1) constant time
	public static void main(String[] args) throws Exception {
		Runner r = new Runner();		
		try (Scanner s = new Scanner(System.in)) {
			Menu menu = new Menu(s, r);
			while(r.isRunning()) {
				menu.displayMenu();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			r.cleanup();
		}
	}
	
	/**
	 * stopRunning method
	 * O(1) constant time
	 */
	public void stopRunning() {
       this.running = false;
    }
    
	/**
	 * isRunning method
	 * @return current state of running variable
	 * O(1) constant time
	 */
	public boolean isRunning() {
        return this.running;
    }

	/**
	 * The method to show menu is called from here
	 * @param s
	 * O(1) constant time
	 */
    public void showMenu(Scanner s) {
        Menu menu = new Menu(s, this);
        menu.displayMenu();
    }
	
    /**
     * specify embedding file method
     * @param s
     * O(n) reads in n number of lines 
     */
	public void specifyEmbeddingFile(Scanner s) {
	    
		String filePath = "";
	    File file = null;

	    // Loop until a valid file path is entered
	    while (true) {
	        System.out.print("\nPlease enter the embedding file path (eg ./word-embeddings.txt): ");
	        filePath = s.nextLine().trim();

	        if (filePath.isEmpty()) {
	            System.out.println("File path cannot be empty. Please enter a valid file path.");
	        } else {
	            file = new File(filePath);
	            if (!file.exists()) {
	                System.out.println("File does not exist: " + filePath + ". Please enter a valid file path.");
	            } else {
	                break; // Valid file path entered; exit loop
	            }
	        }
	    }		
		//Used to speed up debugging with a default path
		 /* System.out.print("\nPlease enter the embedding path (default: ./word-embeddings.txt): ");
		String filePath = s.nextLine().trim(); //.trim()is used to remove whitespaces from a string
		if(filePath.isEmpty()) {
			filePath = "./word-embeddings.txt";
		}
		//check if file exists
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("File does not exist: " + filePath);
			return;
		}*/
	    
		try {
			embeddingsParser.loadEmbeddings(filePath);
			System.out.println("Embeddings loaded successfully");		
		//embeddingsParser.printRandomSample(5); used to output 5 random words for debugging
		}catch(IOException e) {
			System.out.println("Failed to load: " + e.getMessage());
		}
	}
	
	/**
	 * specify output file method
	 * @param s
	 * O(1) constant time - no reading in of data
	 */
	public void specifyOutputFile(Scanner s) {	
		System.out.print("\nEnter the output file path (press Enter for default: ./out.txt): ");
		String outputPath = s.nextLine().trim();
		if(outputPath.isEmpty()) {
			outputPath = "./out.txt";//default path
		}
        initializeOutputFile(outputPath);
    }

	/**
	 * Initialize the Output File & check that it has been initialised
	 * @param path
	 * O(1) constant time
	 */
	private void initializeOutputFile(String path) {
        try {
            if (outputFile != null) {
                outputFile.close(); // Close existing file writer if open
            }
            outputFile = new PrintWriter(new FileWriter(path, true));
            System.out.println("Output file specified: " + path);
        } catch (IOException e) {
            System.err.println("Error opening the output file: " + e.getMessage());
        }
    }
	private void ensureOutputFileInitialized(Scanner s) {
	    if (outputFile == null) {
	        System.out.println("Output file not specified. Please specify an output file path (press Enter for default: ./out.txt):");
	        String outputPath = s.nextLine().trim();
	        initializeOutputFile(outputPath.isEmpty() ? "./out.txt" : outputPath);
	    }
	}

	/**
	 * cleanup method
	 * O(1) constant time
	 */
    private void cleanup() {
        if (outputFile != null) {
            outputFile.close();
        }
    }     

    /**
     * enter the word or text to compare method
     * @param s
     * O(n) reads in n number of lines
     */
	public void enterWordOrText(Scanner s) {
		ensureOutputFileInitialized(s); 
	    System.out.print("\nEnter a word or short sentence: ");
	    String text = s.nextLine().trim();
	    //set the maximum no of words allowed to be 7
	    String[] words = text.split("\\s+");
	    if (words.length > 7) {
	        System.out.println("Only the first 7 words will be processed.");
	        words = Arrays.copyOfRange(words, 0, 7);
	    }	    

	    System.out.println("\nSelect comparison method (1-3):");
	    System.out.println("1. Dot Product");
	    System.out.println("2. Euclidean Distance");
	    System.out.println("3. Cosine Distance");
	    System.out.print("Choice: ");
	    int methodChoice = s.nextInt();
	    s.nextLine(); // Consume newline left-over
	    System.out.print("Enter the number of results to display: ");
	    int numberOfResults = s.nextInt();
	    s.nextLine(); // Consume newline left-over

	    List<Double> allScores = new ArrayList<>();	    
	    for (String word : words) {
	        word = word.toLowerCase();
	        System.out.println("\nResults for word: " + word);

	        List<Double> methodScores = null;
	        switch (methodChoice) {
	            case 1:
	                methodScores = compareUsingDotProduct(word, numberOfResults);
	                break;
	            case 2:
	                methodScores = compareUsingEuclideanDistance(word, numberOfResults);
	                break;
	            case 3:
	                methodScores = compareUsingCosineDistance(word, numberOfResults);
	                break;
	            default:
	                System.out.println("Invalid choice.");
	                break;
	        }
	        if (methodScores != null) {
	            allScores.addAll(methodScores);
	        }
	    }
	    // Calculate and print the average
	    if (!allScores.isEmpty()) {
	        double sum = allScores.stream().mapToDouble(Double::doubleValue).sum();
	        double average = sum / allScores.size();
	        System.out.println("\nAverage result: " + average);
	        //check outputFile is not null 
	        if (outputFile != null) {
	            outputFile.println("\nAverage result: " + average);
	            outputFile.flush(); 
	        } else {
	            System.out.println("Output file not specified. Please specify an output file first.");
	            specifyOutputFile(s);
	        }
	      }
	    }
	
	/**
	 * Configure options method to allow user to choose background & font colours
	 * @param s
	 * O(1) constant time - no reading in of data
	 */
	public void configureOptions(Scanner s) {
	    System.out.println("\nConfigure Options:");
	    System.out.println("1. Background: Black.  Font: White");
	    System.out.println("2. Background: White.  Font: Black");
	    System.out.println("3. Background: Blue.   Font: Black");
	    System.out.println("4. Background: Green.  Font: Black");
	    System.out.println("5. Background: Cyan.   Font: Black");
	    System.out.println("6. Reset to Default");
	    System.out.print("Select an option (1-6): ");
	    int option = s.nextInt();
	    s.nextLine(); // Consume newline

	    switch (option) {
	        case 1:
	            System.out.print(ConsoleColour.BLACK_BACKGROUND);
	            System.out.print(ConsoleColour.WHITE);
	            break;
	        case 2:
	            System.out.print(ConsoleColour.WHITE_BACKGROUND);
	            System.out.print(ConsoleColour.BLACK_BOLD);
	            break;
	        case 3:
	            System.out.print(ConsoleColour.BLUE_BACKGROUND);
	            System.out.print(ConsoleColour.BLACK_BOLD);
	            break;
	        case 4:
	            System.out.print(ConsoleColour.GREEN_BACKGROUND);
	            System.out.print(ConsoleColour.BLACK_BOLD);
	            break;
	        case 5:
	            System.out.print(ConsoleColour.CYAN_BACKGROUND);
	            System.out.print(ConsoleColour.BLACK_BOLD);
	            break;
	        case 6:
	            System.out.print(ConsoleColour.RESET);
	            System.out.println("Colours reset to default.\n");
	            break;
	        default:
	            System.out.println("Invalid option selected.\n");
	            break;
	    }
	}
	
	/**
	 * Compare words inputed by user using dot product method
	 * and output the number of results chosen by user to the console & output file
	 * @param word
	 * @param numberOfResults
	 * @return Similarity Scores of words using Dot Product
	 * O(n) reads in n number of lines
	 */
	private static List<Double> compareUsingDotProduct(String word, int numberOfResults) {
		if(outputFile == null) {
			System.out.println("Output file not specified. Please specify an output file: ");
			return new ArrayList<>();
		}
		List<Double> scores = new ArrayList<>();
	    // Check if the word's embedding exists
	    double[] wordEmbedding = embeddingsParser.getEmbedding(word.toLowerCase());
	    if (wordEmbedding == null) {
	        System.out.println("Embedding not found for: " + word);
	        outputFile.println("Embedding not found for: " + word);
	        return scores;
	    }
	    // Map to store the similarity (dot product) of the word against all the others
	    Map<String, Double> similarityScores = new HashMap<>();

	 // Compare input word with all words in the embeddings
	    for (Map.Entry<String, double[]> entry : embeddingsParser.getEmbeddingsMap().entrySet()) {
	        String otherWord = entry.getKey();
	        double[] otherEmbedding = entry.getValue();

	        // Skip if the same word
	        if (!otherWord.equalsIgnoreCase(word)) {
	            double dotProduct = dotProduct(wordEmbedding, otherEmbedding);
	            similarityScores.put(otherWord, dotProduct);
	        }
	    }

	    // Sort by similarity score in descending order
	    List<Map.Entry<String, Double>> sortedScores = similarityScores.entrySet().stream()
	            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
	            .limit(numberOfResults)
	            .collect(Collectors.toList());
	    
	    // Print to screen and file
	    System.out.println("\nTop " + numberOfResults + " similar words to '" + word + "' by dot product:");
	    outputFile.println("\nTop " + numberOfResults + " similar words to '" + word + "' by dot product:");

	    // Instead of printing, add the scores to the list and write to the file
	    for (Map.Entry<String, Double> score : sortedScores) {
	        scores.add(score.getValue());
	        System.out.println(score.getKey() + ": " + score.getValue());
	        outputFile.println(score.getKey() + ": " + score.getValue());
	    }

	    outputFile.flush(); // Ensure data is written to the file

	    return scores;
	}
	
	/**
	 * Method to calculate dot product of two vectors
	 * @param a
	 * @param b
	 * @return the dot product of 2 vectors
	 * O(n) - 1 loop
	 */
	private static double dotProduct(double[] a, double[] b) {
	    double result = 0;
	    for (int i = 0; i < a.length; i++) {
	        result += a[i] * b[i];
	    }
	    return result;
	}
	
	/**
	 * Compare words inputed by user using Euclidean Distance method
	 * and output the number of results chosen by user to the console & output file
	 * @param word
	 * @param numberOfResults
	 * @return the Euclidean Distance between specific words and return a list of the most similar
	 * O(n) reads in n number of lines
	 */
	private List<Double> compareUsingEuclideanDistance(String word, int numberOfResults) {
	    List<Double> scores = new ArrayList<>();
	    double[] wordEmbedding = embeddingsParser.getEmbedding(word.toLowerCase());
	    if (wordEmbedding == null) {
	        System.out.println("Embedding not found for: " + word);
	        return scores;
	    }

	    // Map to store Euclidean distances
	    Map<String, Double> distanceScores = new HashMap<>();

	    // Calculate Euclidean distance with all other words
	    for (Map.Entry<String, double[]> entry : embeddingsParser.getEmbeddingsMap().entrySet()) {
	        String otherWord = entry.getKey();
	        double[] otherEmbedding = entry.getValue();

	        // Skip if the same word
	        if (!otherWord.equalsIgnoreCase(word)) {
	            double distance = euclideanDistance(wordEmbedding, otherEmbedding);
	            distanceScores.put(otherWord, distance);
	        }
	    }

	    // Sort by distance in ascending order (shortest distances first) and get top results
	    List<Map.Entry<String, Double>> sortedDistances = distanceScores.entrySet().stream()
	            .sorted(Map.Entry.comparingByValue())
	            .limit(numberOfResults)
	            .collect(Collectors.toList());

	    // Print to screen and file
	    System.out.println("\nTop " + numberOfResults + " similar words to '" + word + "' by euclidean distance:");
	    outputFile.println("\nTop " + numberOfResults + " similar words to '" + word + "' by euclidean distance:");

	    // Instead of printing, add the scores to the list and write to the file
	    for (Map.Entry<String, Double> distance : sortedDistances) {
	        scores.add(distance.getValue());
	        System.out.println(distance.getKey() + ": " + distance.getValue());
	        outputFile.println(distance.getKey() + ": " + distance.getValue());
	    }

	    outputFile.flush(); // Ensure data is written to the file

	    return scores;
	}
	
	/**
	 * Method for calculating Euclidean distance between two vectors
	 * @param vectorA
	 * @param vectorB
	 * @return the Euclidean distance between 2 vectors
	 * O(n) - 1 loop
	 */
	private static double euclideanDistance(double[] vectorA, double[] vectorB) {
	    double sum = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        sum += Math.pow(vectorA[i] - vectorB[i], 2);
	    }
	    return Math.sqrt(sum);
	}
	
	/**
	 * Compare words inputed by user using Cosine Distance method
	 * and output the number of results chosen by user to the console & output file
	 * @param word
	 * @param numberOfResults
	 * @return A list of similarity scores for the specified number of most similar words
	 * O(n) reads in n number of lines
	 */
	private List<Double> compareUsingCosineDistance(String word, int numberOfResults) {
	    List<Double> scores = new ArrayList<>();

		 double[] wordEmbedding = embeddingsParser.getEmbedding(word.toLowerCase());
		    if (wordEmbedding == null) {
		        System.out.println("Embedding not found for: " + word);
		        return scores;
		    }

		    // Map to store cosine similarities
		    Map<String, Double> similarityScores = new HashMap<>();

		    // Calculate cosine similarity with all other words
		    for (Map.Entry<String, double[]> entry : embeddingsParser.getEmbeddingsMap().entrySet()) {
		        String otherWord = entry.getKey();
		        double[] otherEmbedding = entry.getValue();

		        if (!otherWord.equalsIgnoreCase(word)) { // Skip if the same word
		            double similarity = cosineSimilarity(wordEmbedding, otherEmbedding);
		            similarityScores.put(otherWord, similarity);
		        }
		    }

		    // Sort by similarity in descending order
		    List<Map.Entry<String, Double>> sortedScores = similarityScores.entrySet().stream()
		            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
		            .limit(numberOfResults)
		            .collect(Collectors.toList());

		    // Print to screen and file
		    System.out.println("\nTop " + numberOfResults + " similar words to '" + word + "' by cosine similarity:");
		    outputFile.println("\nTop " + numberOfResults + " similar words to '" + word + "' by cosine similarity:");

		    // Instead of printing, add the scores to the list and write to the file
		    for (Map.Entry<String, Double> score : sortedScores) {
		        scores.add(score.getValue());
		        System.out.println(score.getKey() + ": " + score.getValue());
		        outputFile.println(score.getKey() + ": " + score.getValue());
		    }
		    outputFile.flush(); // Ensure data is written to the file

		    return scores;
		}
	
	/**
	 * Method for calculating cosine similarity between two vectors
	 * @param vectorA
	 * @param vectorB
	 * @return The measure of the cosine of the angle between 2 vectors
	 * O(n) - 1 loop
	 */ 
	private static double cosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}	
}
