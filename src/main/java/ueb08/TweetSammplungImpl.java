package ueb08;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TweetSammplungImpl implements TweetSammlung {
	List<String> tweets = new LinkedList<>();
	Map<String, Integer> counts = new HashMap<>();
	Set<String> stopwords = new HashSet<>();

	@Override
	public void ingest(String tweet) {
		tweets.add(tweet);

		for (String s : TweetSammlung.tokenize(tweet)) {
			// counts.merge(s, 1, (a, b) -> a + b);

			if (stopwords.contains(s))
				continue;

			if (counts.containsKey(s))
				counts.put(s, counts.get(s) + 1);
			else
				counts.put(s, 1);
		}
	}

	@Override
	public Iterator<String> vocabIterator() {

		List<String> woerter = new LinkedList<>();
		woerter.addAll(counts.keySet());
		woerter.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		return woerter.iterator();
	}

	@Override
	public Iterator<String> topHashTags() {
		List<Map.Entry<String, Integer>> ht = new LinkedList<>();
		for (Map.Entry<String, Integer> e : counts.entrySet()) {
			if (e.getKey().startsWith("#"))
				ht.add(e);
		}

		ht.sort(new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});

		List<String> htt = new LinkedList<>();
		for (Map.Entry<String, Integer> e : ht) {
			htt.add(e.getKey());
		}

		return htt.iterator();

	}

	@Override
	public Iterator<String> topWords() {
		List<Map.Entry<String, Integer>> ht = new LinkedList<>();
		for (Map.Entry<String, Integer> e : counts.entrySet()) {
			if (Character.isAlphabetic(e.getKey().charAt(0)))
				ht.add(e);
		}

		ht.sort(new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});

		List<String> htt = new LinkedList<>();
		for (Map.Entry<String, Integer> e : ht) {
			htt.add(e.getKey());
		}

		return htt.iterator();
	}

	@Override
	public Iterator<Pair> topTweets() {
		List<Pair> list = new LinkedList<>();

		for (String s : tweets) {
			int wert = 0;
			for (String t : TweetSammlung.tokenize(s)) {
				if (stopwords.contains(t))
					continue;
				wert += counts.get(t);
			}

			list.add(new Pair(s, wert));
		}

		list.sort(new Comparator<Pair>() {
			@Override
			public int compare(Pair o1, Pair o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});

		return list.iterator();


	}

	@Override
	public void setStopwords(File file) throws FileNotFoundException {
		FileReader fr = new FileReader(file);
		Scanner sc = new Scanner(fr);

		while (sc.hasNext())
			stopwords.add(sc.next());
	}
}
