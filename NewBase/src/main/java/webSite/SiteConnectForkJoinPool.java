package webSite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class SiteConnectForkJoinPool {
    private List<String> stringList = new ArrayList<>();
    private List<String> resultListPath = new ArrayList<>();
    private List<Integer> resultListCode = new ArrayList<>();
    private List<String> resultListContent = new ArrayList<>();
    private String url;
    private int size;

    public void listSite(String stringUrl) throws IOException {
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

        size = stringList.size();

        for (String str : stringList) {
            url = str;
        }

        resultListPath = new ForkJoinPool().invoke(new PathListForkJoin(size, url));
        resultListCode = new ForkJoinPool().invoke(new CodeListForkJoin(size, url));
        resultListContent = new ForkJoinPool().invoke(new ContentListForkJoin(size, url));
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
}
