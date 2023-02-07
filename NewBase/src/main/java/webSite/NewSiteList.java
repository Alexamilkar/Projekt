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

public class NewSiteList {
    private static final String stringUrl = "http://www.playback.ru/";

    public List<String> siteConnect(String stringUrl) throws IOException {
        List<String> stringList = new ArrayList<>();

        Document document = Jsoup.connect(stringUrl).get();
        Elements links = document.select("a[href]");

        for (Element link : links)
        {
            String string = link.attr("abs:href");

            if (!string.isEmpty() && string.startsWith(stringUrl) && !string.contains("#")) {
                stringList.add(string);
                Set<String> stringSet = new LinkedHashSet<>(stringList);
                stringList.clear();
                stringList.addAll(stringSet);
            }
        }
        return stringList;
    }

    public void selector() throws IOException {
        List<String> list = siteConnect(stringUrl);
        for (int i = 0; i < 1; i ++) {
            String site = list.get(i);
            Document document = Jsoup.connect(site).get();

            System.out.println(document.select("title") + "\n" + document.text());
        }
    }
}
