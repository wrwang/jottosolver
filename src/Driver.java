import java.util.Scanner;

public class Driver {
	public static void main(String[] args) {
		Jotto solve = new Jotto();
		System.out.print("How many letters? ");
		Scanner input = new Scanner(System.in);
		int letterCount = input.nextInt();

		solve.keepOnlyCountLetterWords(letterCount);
		solve.removeRepeatedCharacterWords();

		String myGuess = (String) solve.getRandomWord();
		System.out.print("0");

		while (true) {
			System.out.println(" / " + solve.getDictionarySize());
			System.out.println("Try this word: " + myGuess);
			int correctLetters = -2;
			while (correctLetters == -2) {
				System.out.print("How many correct? ");
				try {
					correctLetters = input.nextInt();
				} catch (Exception e) {
					System.out.println("Enter a valid number: Use " + (letterCount + 1) + " to exit, -1 for not a word");
					input.nextLine();
				}
			}

			if (correctLetters < 0) {
				solve.removeWord(myGuess);
			} else if (correctLetters == letterCount) {
				solve.removeAllWordsWithoutCharactersFromWord(myGuess);
			} else if (correctLetters == 0) {
				solve.removeAllWordsWithCharactersFromWord(myGuess);
			}
			
			if (correctLetters > letterCount) {
				break;
			} else if (correctLetters >= 0) {
				solve.addGuess(myGuess, correctLetters);
			}

			myGuess = solve.thinkNext();
		}

		System.out.println("Guessed the word!");
		input.close();
	}
}
