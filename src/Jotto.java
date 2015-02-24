import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;

public class Jotto {
	public static final String[] DICT_FILES = {"/usr/share/dict/words", "sowpods.txt", "wordsEN.txt"};
	
	private Set<String> dictionary = new HashSet<>();
	private Map<String,Integer> guesses = new HashMap<>();
	private List<Character> letters = new ArrayList<>();

	Jotto() {
		for (int i = 0; i < 26; i++) {
			letters.add((char) ('a' + i));
		}

		try {
			for (int i = 0; i < DICT_FILES.length; i++) {
				File fileDictionary = new File(DICT_FILES[i]);
				if (fileDictionary.exists()) {
					Scanner scan = new Scanner(fileDictionary);
					while (scan.hasNextLine()) {
						dictionary.add(scan.nextLine().toLowerCase());
					}
					scan.close();
				} else {
					System.err.println("Could not find dictionary file.");
				}
			}
		} catch (Exception e) {
			System.err.println("Unable to parse dictionary file.");
			return;
		}
	}

	public static int jots(String s, String t) {
		int sum = 0;
		for (int i = 0; i < t.length(); i++) 
			if (s.contains(t.substring(i, i+1)))
				sum++;
		return sum;
	}
	
	public int getDictionarySize() {
		return dictionary.size();
	}
	
	public String getRandomWord() {
		Random random = new Random();
		return (String) dictionary.toArray()[random.nextInt(dictionary.size())];
	}
	
	public void addGuess(String word, int matches) {
		guesses.put(word, matches);
	}

	public String thinkNext() {
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
			return getRandomWord();
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

	public void keepOnlyCountLetterWords(int count) {
		Predicate<String> isNotCountLetters = p -> p.length() != count;
		dictionary.removeIf(isNotCountLetters);
	}

	public void removeRepeatedCharacterWords() {
		dictionary.removeIf(hasRepeatedLetters);
	}
	private static class hasRepeatedLetters implements Predicate<String> {
		@Override
		public boolean test(String t) {
			Set<Byte> charSet = new HashSet<>();
			byte[] bytes = t.getBytes();
			for (int i = 0; i < bytes.length; i++)
				if (!charSet.add(bytes[i])) return true;
			return false;
		}
	}
	private static final Predicate<String> hasRepeatedLetters = new hasRepeatedLetters();

	public void removeAllWordsWithCharactersFromWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			String sletter = word.substring(i, i+1);
			Predicate<String> letter = p -> p.contains(sletter);
			dictionary.removeIf(letter);
		}
	}

	public void removeWord(String word) {
		dictionary.remove(word);
	}

	public void removeAllWordsWithoutCharactersFromWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			String sletter = word.substring(i, i+1);
			Predicate<String> letter = p -> !p.contains(sletter);
			dictionary.removeIf(letter);
		}
	}


}
