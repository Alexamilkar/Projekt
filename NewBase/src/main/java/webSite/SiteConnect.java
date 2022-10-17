package webSite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SiteConnect{
    private final List<String> stringList = new ArrayList<>();
    private List<String> resultListLoc = new ArrayList<>();
    private List<Integer> resultListCod = new ArrayList<>();
    private List<String> resultListText = new ArrayList<>();


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

        for (String site : stringList)
        {
            URL url = new URL(site);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            Document doc = Jsoup.connect(site).get();

            String location = doc.location();
            Integer responseCode = http.getResponseCode();
            String siteText = doc.outerHtml();

            resultListLoc.add(location);
            resultListCod.add(responseCode);
            resultListText.add(siteText);
        }
    }


    public List<String> getStringList() {
        return stringList;
    }
    public List<String> getResultListLoc() {
        return resultListLoc;
    }
    public List<Integer> getResultListCod() {
        return resultListCod;
    }
    public List<String> getResultListText() {
        return resultListText;
    }
}
