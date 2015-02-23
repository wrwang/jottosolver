import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;


public class Driver {
	
	public static final String DICT_FILE = "/usr/share/dict/words";
	
	public static void main(String[] args) {
		Driver solve = new Driver();
		System.out.print("How many letters? ");
		Scanner input = new Scanner(System.in);
		int letterCount = input.nextInt();

		solve.keepOnlyCountLetterWords(letterCount);
		solve.removeRepeatedCharacterWords();

		Random random = new Random();
		int guessWord = random.nextInt(solve.dictionary.size());
		String myGuess = (String) solve.dictionary.toArray()[guessWord];
		System.out.print("0");
		
		while (true) {
			System.out.println(" / " + solve.dictionary.size());
			System.out.println("Try this word: " + myGuess);
			System.out.print("How many correct? ");
			int correctLetters = 0;
			try {
				correctLetters = input.nextInt();
			} catch (Exception e) {
				break;
			}

			if (correctLetters < 0) {
				solve.removeWord(myGuess);
			} else if (correctLetters == letterCount) {
				solve.removeAllWordsWithoutCharactersFromWord(myGuess);
			} else if (correctLetters == 0) {
				solve.removeAllWordsWithCharactersFromWord(myGuess);
			}
			if (correctLetters >= 0) {
				solve.guesses.put(myGuess, correctLetters);
			}
 
			myGuess = solve.thinkNext();
		}

		System.out.println(solve.dictionary.size());
	}

	Set<String> dictionary = new HashSet<>();
	Map<String,Integer> guesses = new HashMap<>();
	List<Character> letters = new ArrayList<>();

	Driver() {
		for (int i = 0; i < 26; i++) {
			letters.add((char) ('a' + i));
		}

		try {
			File fileDictionary = new File(DICT_FILE);
			Scanner scan = new Scanner(fileDictionary);
			while (scan.hasNextLine()) {
				dictionary.add(scan.nextLine().toLowerCase());
			}
		} catch (Exception e) {
			System.out.println("Unable to open dictionary file");
			return;
		}
	}

	int jots(String s, String t) {
		int sum = 0;
		for (int i = 0; i < t.length(); i++) 
			if (s.contains(t.substring(i, i+1)))
				sum++;
		return sum;
	}

	String thinkNext() {
		Map<String,Integer> nextGuess = new HashMap<>();

		Set<String> words = guesses.keySet();
		for (String guess : words) {
			for (String word : dictionary) {
				if (jots(guess, word) == guesses.get(guess)) {
					nextGuess.put(word, (nextGuess.get(word) == null ? 0 : nextGuess.get(word) + 1));
				}
			}
		}

		
		Entry<String, Integer> maxWord = null;
		for (Entry<String, Integer> word : nextGuess.entrySet()) {
			if ((maxWord == null || maxWord.getValue() < word.getValue()) && !guesses.containsKey(word.getKey())) {
				maxWord = word;
			}
		}

		if (maxWord == null) {
			Random random = new Random();
			return (String) dictionary.toArray()[random.nextInt(dictionary.size())];
		} else {
			String word = maxWord.getKey();
			Set<String> dictCopy = new HashSet<>(dictionary);
			for (int i = 0; i < word.length(); i++) {
				String sletter = word.substring(i, i+1);
				Predicate<String> letter = p -> !p.contains(sletter);
				dictCopy.removeIf(letter);
			}
			System.out.println(dictCopy);
		}
		System.out.print(nextGuess.size());
		
		return maxWord.getKey();
	}

	void keepOnlyCountLetterWords(int count) {
		Predicate<String> isNotCountLetters = p -> p.length() != count;
		dictionary.removeIf(isNotCountLetters);
	}

	void removeRepeatedCharacterWords() {
		dictionary.removeIf(hasRepeatedLetters);
	}
	static class hasRepeatedLetters implements Predicate<String> {
		@Override
		public boolean test(String t) {
			Set<Byte> charSet = new HashSet<>();
			byte[] bytes = t.getBytes();
			for (int i = 0; i < bytes.length; i++)
				if (!charSet.add(bytes[i])) return true;
			return false;
		}
	}
	static final Predicate<String> hasRepeatedLetters = new hasRepeatedLetters();

	void removeAllWordsWithCharactersFromWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			String sletter = word.substring(i, i+1);
			Predicate<String> letter = p -> p.contains(sletter);
			dictionary.removeIf(letter);
		}
	}

	void removeWord(String word) {
		dictionary.remove(word);
	}

	void removeAllWordsWithoutCharactersFromWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			String sletter = word.substring(i, i+1);
			Predicate<String> letter = p -> !p.contains(sletter);
			dictionary.removeIf(letter);
		}
	}
}
