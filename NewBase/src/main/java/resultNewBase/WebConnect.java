package resultNewBase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class WebConnect {
    private final List<String> stringList = new ArrayList<>();
    private final List<String> resultListPath = new ArrayList<>();
    private final List<Integer> resultListCode = new ArrayList<>();
    private final List<String> resultListContent = new ArrayList<>();
    private Map<String, Integer> mapLemma = new HashMap<>();


    public void webConnect(String stringUrl) throws IOException {
        Document document = Jsoup.connect(stringUrl).get();
        Elements links = document.select("a[href]");

        for (Element link : links) {
            String string = link.attr("abs:href");

            if (!string.isEmpty() && string.startsWith(stringUrl) && !string.contains("#")) {
                stringList.add(string);
                Set<String> stringSet = new LinkedHashSet<>(stringList);
                stringList.clear();
                stringList.addAll(stringSet);
            }
        }
        for (String st : stringList) {
            Document doc = Jsoup.connect(st).get();
            String siteText = doc.outerHtml();
            resultListContent.add(siteText);
        }

        for (int i = 0; i < resultListContent.size(); i++) {
            String s = resultListContent.get(i);

            Set<String> strings = LemmaFinder.getInstance().getLemmaSet(s);
            for (String lemma : strings) {
                if (mapLemma.containsKey(lemma)) {
                    mapLemma.replace(lemma, mapLemma.get(lemma), mapLemma.get(lemma) + 1);
                } else mapLemma.put(lemma, 1);
            }
        }
    }

    public List<String> getStringList() {
        return stringList;
    }
    public List<String> getResultListPath() {
        return resultListPath;
    }
    public List<Integer> getResultListCode() {
        return resultListCode;
    }
    public List<String> getResultListContent() {
        return resultListContent;
    }
    public Map<String, Integer> getMapLemma() {
        return mapLemma;
    }
}
