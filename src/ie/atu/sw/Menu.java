package ie.atu.sw;

import java.util.*;

public class Menu {
	private final Scanner s;
	private final Runner r;

	public Menu(Scanner s, Runner r) {
		this.s = s;
		this.r = r;
	}

	/**
	 * Displays the Menu for user input
	 */
	public void displayMenu() {
		while (r.isRunning()) {// keep showing the menu until user chooses to exit
			System.out.println("\n************************************************************");
			System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
			System.out.println("*                                                          *");
			System.out.println("*          Similarity Search with Word Embeddings          *");
			System.out.println("*                                                          *");
			System.out.println("************************************************************\n");
			System.out.println("(1) Specify Embedding File");
			System.out.println("(2) Specify an Output File (default: ./out.txt)");
			System.out.println("(3) Enter a Word or text");
			System.out.println("(4) Configure Options");
			System.out.println("(5) Quit\n");
			
			// Output a menu of options and solicit text from the user
			System.out.print("Select Option [1-5]> ");
			int choice = s.nextInt();
			s.nextLine();// consume newline

			switch (choice) {
			case 1: // Specify Embedding File
				r.specifyEmbeddingFile(s);
				break;

			case 2: // Specify an Output File (default: ./out.txt)
				r.specifyOutputFile(s);
				break;

			case 3: // Enter a Word 
				r.enterWordOrText(s);
				break;

			case 4: // Configure Options
				r.configureOptions(s);
				break;

			case 5: // Quit
				r.stopRunning();// to exit loop
				System.out.println("Exiting");
				break;

			default:// Default
				System.out.println("Invalid option selected");
				break;
			}
		}
	}
}
