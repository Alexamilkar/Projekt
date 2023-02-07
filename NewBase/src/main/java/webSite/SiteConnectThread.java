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

public class SiteConnectThread {
    private final List<String> stringList = new ArrayList<>();
    private final List<String> resultListPath = new ArrayList<>();
    private final List<Integer> resultListCode = new ArrayList<>();
    private final List<String> resultListContent = new ArrayList<>();


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

        for (String site : stringList)
        {
            URL url = new URL(site);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            Document doc = Jsoup.connect(site).get();

            ArrayList<Thread> threads = new ArrayList<>();

            threads.add(new Thread(() -> {
                synchronized (site) {
                String location = doc.location();
                resultListPath.add(location);
            }}));

            threads.add(new Thread(() -> {
                synchronized (site) {
                Integer responseCode = null;
                try {
                    responseCode = http.getResponseCode();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                resultListCode.add(responseCode);
            }}));

            threads.add(new Thread(() -> {
                synchronized (site) {
                String siteText = doc.outerHtml();
                resultListContent.add(siteText);
            }}));

            threads.forEach(Thread::start);
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
}
