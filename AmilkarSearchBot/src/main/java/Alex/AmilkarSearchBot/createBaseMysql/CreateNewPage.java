package Alex.AmilkarSearchBot.createBaseMysql;

import Alex.AmilkarSearchBot.model.Page;
import Alex.AmilkarSearchBot.model.PageCrudRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class CreateNewPage implements Runnable{
    private final String siteUrl;
    private final int siteId;
    private final PageCrudRepository pageCrudRepository;

    public CreateNewPage(String siteUrl, int siteId, PageCrudRepository pageCrudRepository) {
        this.siteUrl = siteUrl;
        this.siteId = siteId;
        this.pageCrudRepository = pageCrudRepository;
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

        for (String site : strings) {
            String[] split = site.split("/");
            String siteName = '/' + split[split.length - 1];

            URL url = null;
            try {
                url = new URL(site);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int responseCode = 0;
            try {
                responseCode = http.getResponseCode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String siteContent = new ForkJoinPool().invoke(new ContentForkJoin(site));

            if (siteContent != null) {
                Page page = new Page();

                page.setPath(siteName);
                page.setCode(responseCode);
                page.setContent(siteContent);
                page.setSiteId(siteId);

                pageCrudRepository.save(page);
            }
        }
    }
}
