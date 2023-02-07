package Alex.AmilkarSearchBot.webConnect;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class SiteConnectForkJoin extends RecursiveTask<List<String>> {

    private final String url;

    public SiteConnectForkJoin(String url) {
        this.url = url;
    }

    @Override
    protected List<String> compute()
    {
        List<SiteConnectForkJoin> siteConnectList = new ArrayList<>();
        List<String> siteList = new ArrayList<>();

        try {
            Thread.sleep(150);
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("a[href]");
            for (Element element : elements) {
                String str = element.attr("abs:href");
                if (!str.isEmpty() && str.startsWith(url) && !str.contains("#")) {
                    SiteConnectForkJoin site = new SiteConnectForkJoin(str);
                    site.fork();
                    siteConnectList.add(site);
                }
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        Thread.currentThread().interrupt();

        siteConnectList.sort(Comparator.comparing((SiteConnectForkJoin s) -> s.url));
        for (SiteConnectForkJoin siteConnect : siteConnectList) {
            siteList.add(siteConnect.url);
        }
        Set<String> stringSet = new LinkedHashSet<>(siteList);
        siteList.clear();
        siteList.addAll(stringSet);
        return siteList;
    }
}
