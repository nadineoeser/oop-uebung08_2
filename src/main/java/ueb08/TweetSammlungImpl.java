package ueb08;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TweetSammlungImpl implements TweetSammlung {

    List<String> tweets = new LinkedList<>();
    Map<String, Integer> counts = new TreeMap<>();
    Set<String> stop = new TreeSet<>();

    /**
     * Stopwoerter werden beim Zaehlen nicht beruecksichtigt.
     *
     * @param file Fileobjekt in der die Stopwoerter gespeichert sind.
     */
    @Override
    public void setStopwords(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(file)));

        while (sc.hasNext())
            stop.add(sc.next());
    }

    /**
     * Einen weiteren Tweet indexieren.
     *
     * @param tweet lower-case, whitespace delimited text
     */
    @Override
    public void ingest(String tweet) {
        tweets.add(tweet);

        for (String s : TweetSammlung.tokenize(tweet)){
            //counts.merge(s, 1, (a, b) -> a + b);

            if (stop.contains(s))
                continue;

            //increase count for word
            counts.merge(s, 1, (a, b) -> a + b);
        }
    }

    /**
     * @return Ein Iterator über das Vokabular, alphabetisch aufsteigend.
     */
    @Override
    public Iterator<String> vocabIterator() {
        //TreeSet.iterator sortiert aufsteigend!
        // https: //docs.oracle.com/javase/8/docs/api/java/util/TreeMap.html#keySet--
        return counts.keySet().iterator();
    }

    /**
     * @return Ein Iterator über die am häufigsten verwendeten #hashtags, absteigend.
     */
    @Override
    public Iterator<String> topHashTags() {
        //filtern...
        List<Pair> help = new LinkedList<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()){
            if (e.getKey().startsWith("#"))
                help.add(new Pair(e.getKey(), e.getValue()));
        }

        //sortieren, aufsteigend
        help.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        //übertragenin nur-key
        List<String> list = new LinkedList<>();
        for (Pair e : help)
            list.add(e.getKey());

        return list.iterator();
    }

    /**
     * @return Ein Iterator über die am häufigsten verwendeten @mentions, absteigend.
     */
    @Override
    public Iterator<String> topWords() {
        //like above, but using streams and lambda
        return counts.entrySet().stream()
                //remove hashtags
                .filter(e -> Character.isAlphabetic(e.getKey().charAt(0)))
                //sortiere nach raw-count aufsteigend
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                //jezt nur den key behalten
                .map(Map.Entry::getKey).iterator();
    }

    /**
     * @return Ein Iterator ueber die Tweets, welche insgesamt die meisten buzzwordigen woerter haben.
     */
    @Override
    public Iterator<Pair> topTweets() {
        List<Pair> list = new LinkedList<>();

        for (String s : tweets){
            int wert = 0;
            for (String t : TweetSammlung.tokenize(s)){
                if (stop.contains(t))
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
}
