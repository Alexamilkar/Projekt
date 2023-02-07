package webSite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.RecursiveTask;

public class PathForkJoin extends RecursiveTask<String> {
    private String siteName;
    final String url;
    public PathForkJoin(String url) {
        this.url = url;
    }

    @Override
    protected String compute() {

        try {
            Thread.sleep(150);

            Document doc = Jsoup.connect(url).get();
            PathForkJoin title = new PathForkJoin(doc.location());
            title.fork();
            siteName = title.url;

        } catch (Exception ex) {
            ex.getStackTrace();
        }
        Thread.currentThread().interrupt();

        return siteName;
    }
}
