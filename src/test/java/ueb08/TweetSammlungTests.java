package ueb08;

import org.junit.jupiter.api.Test;


import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TweetSammlungTests {
	@Test
	void testFactory() throws Exception {
		assertNotNull(TweetSammlung.create());
	}

	@Test
	void testIngest() throws Exception {
		TweetSammlung ts = TweetSammlung.create();

		ts.ingest("hans dampf will surfen #tale");
		ts.ingest("peter pan will fliegen #fairy");
		ts.ingest("little red riding hood #fairy");

		Iterator<String> vi = ts.vocabIterator();
		assertEquals("#fairy", vi.next());
		assertEquals("#tale", vi.next());
		assertEquals("dampf", vi.next());
		assertEquals("fliegen", vi.next());
		assertEquals("hans", vi.next());
		assertEquals("hood", vi.next());
		assertEquals("little", vi.next());
		assertEquals("pan", vi.next());
		assertEquals("peter", vi.next());
		assertEquals("red", vi.next());
		assertEquals("riding", vi.next());
		assertEquals("surfen", vi.next());
		assertEquals("will", vi.next());
		assertFalse(vi.hasNext());

		Iterator<String> ht = ts.topHashTags();
		assertEquals("#fairy", ht.next());
		assertEquals("#tale", ht.next());
		assertFalse(ht.hasNext());

		Iterator<String> tw = ts.topWords();
		assertEquals("will", tw.next());
		assertTrue(tw.hasNext());
	}

	@Test
	void testStopwords() throws Exception {
		TweetSammlung ts1 = TweetSammlung.create();
		ts1.ingest("i i i think this is great");
		assertEquals("i", ts1.topWords().next());

		TweetSammlung ts2 = TweetSammlung.create();
		ts2.setStopwords(new File(getClass().getClassLoader().getResource("stopwords.txt").getFile()));
		ts2.ingest("i i i think this is great great");
		assertEquals("great", ts2.topWords().next());
	}

	@Test
	void testTopHashTags(){
		TweetSammlung tweetSammlung = TweetSammlung.create();
		tweetSammlung.ingest("hans dampf will surfen #tale");
		tweetSammlung.ingest("peter pan will fliegen #fairy");
		tweetSammlung.ingest("little red riding hood #fairy");
		tweetSammlung.ingest("hans blub trinkt bier #beer");
		tweetSammlung.ingest("kein Bier vor vier #beer");
		tweetSammlung.ingest("programmer for life #develop");
		tweetSammlung.ingest("Peter pan geht baden #fairy");
		tweetSammlung.ingest("Hans Dampf geht gern in den Biergarten #beer");
		tweetSammlung.ingest("Peter pan geht schwimmen #fairy");
		Iterator<String> iterator = tweetSammlung.topHashTags();
		assertEquals("#fairy", iterator.next());
		assertEquals("#beer", iterator.next());
		assertEquals("#develop", iterator.next());
		assertEquals("#tale", iterator.next());
	}

	@Test
	void testTopWords(){
		TweetSammlung tweetSammlung = TweetSammlung.create();
		tweetSammlung.ingest("hans dampf will surfen #tale");
		tweetSammlung.ingest("peter pan will fliegen #fairy");
		tweetSammlung.ingest("dampf will in den Urlaub #fairy");
		Iterator<String> iterator = tweetSammlung.topWords();
		//Top Nr. One word -> 3x
		assertEquals("will", iterator.next());
		//Top Nr. Two word -> 2x
		assertEquals("dampf", iterator.next());
	}

	@Test
	void testTopTweets(){
		TweetSammlung tweetSammlung = TweetSammlung.create();
		tweetSammlung.ingest("hans dampf will surfen #tale");
		tweetSammlung.ingest("peter pan will fliegen #fairy");
		tweetSammlung.ingest("dampf will in den Urlaub #fairy");
		Iterator<Pair> iterator = tweetSammlung.topTweets();
		Pair pair = iterator.next();
		assertEquals("dampf will in den Urlaub #fairy", pair.getKey());
		assertEquals("10", Integer.toString(pair.getValue()));
		Pair pair2 = iterator.next();
		assertEquals("hans dampf will surfen #tale", pair2.getKey());
		assertEquals("8", Integer.toString(pair2.getValue()));
		Pair pair3 = iterator.next();
		assertEquals("peter pan will fliegen #fairy", pair3.getKey());
		assertEquals("8", Integer.toString(pair3.getValue()));
	}


	@Test
	void testTrumpTweets() throws IOException {
		TweetSammlung tsi = TweetSammlung.create();
		tsi.setStopwords(new File(getClass().getClassLoader().getResource("stopwords.txt").getFile()));

		BufferedReader br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("trump.txt").getFile()));
		String l;
		while ((l = br.readLine()) != null) {
			tsi.ingest(l);
		}

		int n = 10;
		System.out.println("** Vocab **");
		Iterator<String> it0 = tsi.vocabIterator();
		for (int i = 0; i < n && it0.hasNext(); i++)
			System.out.println(it0.next());

		System.out.println("\n" + "\n");
		System.out.println("---------------------------------------");
		System.out.println("** TopHash **");
		it0 = tsi.topHashTags();
		for (int i = 0; i < n && it0.hasNext(); i++)
			System.out.println(it0.next());

		System.out.println("\n" + "\n");
		System.out.println("---------------------------------------");
		System.out.println("** TopWord **");
		it0 = tsi.topWords();
		for (int i = 0; i < n && it0.hasNext(); i++)
			System.out.println(it0.next());

		System.out.println("\n" + "\n");
		System.out.println("---------------------------------------");
		System.out.println("** TopTweets **");
		Iterator<Pair> it1 = tsi.topTweets();
		for (int i = 0; i < n && it1.hasNext(); i++)
			System.out.println(it1.next());
	}
}