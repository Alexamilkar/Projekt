package webSite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.RecursiveTask;

public class ContentForkJoin extends RecursiveTask<String> {
    private String stringText;
    final String url;

    public ContentForkJoin(String url) {
        this.url = url;
    }

    @Override
    protected String compute() {
        try {
            Thread.sleep(1500);

            Document doc = Jsoup.connect(url).get();
            ContentForkJoin title = new ContentForkJoin(doc.text());
            title.fork();
            stringText = title.url;
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        Thread.currentThread().interrupt();

        return stringText;
    }
}
