package Alex.AmilkarSearchBot.createBaseMysql;

import Alex.AmilkarSearchBot.LemmaFinder;
import Alex.AmilkarSearchBot.model.Lemma;
import Alex.AmilkarSearchBot.model.LemmaCrudRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class CreateNewLemma implements Runnable{
    private final String siteUrl;
    private final int siteId;
    private final LemmaCrudRepository lemmaCrudRepository;

    public CreateNewLemma(String siteUrl, int siteId, LemmaCrudRepository lemmaCrudRepository) {
        this.siteUrl = siteUrl;
        this.siteId = siteId;
        this.lemmaCrudRepository = lemmaCrudRepository;
    }

    @Override
    public void run() {
        List<String> strings = new ArrayList<>();

        Document document = null;
        try {
            document = Jsoup.connect(siteUrl).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements links = document.select("a[href]");

        for (Element link : links) {
            String string = link.attr("abs:href");
            if (!string.isEmpty() && string.startsWith(siteUrl) && !string.contains("#")) {
                strings.add(string);
                Set<String> stringSet = new LinkedHashSet<>(strings);
                strings.clear();
                strings.addAll(stringSet);
            }
        }

        Map<String, Integer> mapLemma = new HashMap<>();

        for (String site : strings) {

            String siteContent = new ForkJoinPool().invoke(new ContentForkJoin(site));

            if (siteContent != null) {
                Set<String> stringSet = null;
                try {
                    stringSet = LemmaFinder.getInstance().getLemmaSet(siteContent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (String lemma : stringSet) {
                    if (mapLemma.containsKey(lemma)) {
                        mapLemma.replace(lemma, mapLemma.get(lemma), mapLemma.get(lemma) + 1);
                    } else mapLemma.put(lemma, 1);
                }
            }
        }

        List<String> lemmaList = new ArrayList<>(mapLemma.keySet());
        List<Integer> lemmaIntList = new ArrayList<>(mapLemma.values());

        for (int i = 0; i < lemmaList.size(); i++) {

            String lemmaSt = lemmaList.get(i);
            int limmaInt = lemmaIntList.get(i);

            Lemma lemma = new Lemma();

            lemma.setLemma(lemmaSt);
            lemma.setFrequency(limmaInt);
            lemma.setSiteId(siteId);

            lemmaCrudRepository.save(lemma);
        }
    }
}
