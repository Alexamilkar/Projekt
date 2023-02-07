package webSite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ContentListForkJoin extends RecursiveTask<List<String>> {
    private int size;
    private String url;

    public ContentListForkJoin(int size, String url) {
        this.size = size;
        this.url = url;
    }

    @Override
    protected List<String> compute() {
        List<String> contentList = new ArrayList<>();
        List<ContentForkJoin> list = new ArrayList<>();

        try {
            for (int i = 0; i < size; i++) {
                Document doc = Jsoup.connect(url).get();
                ContentForkJoin text = new ContentForkJoin(doc.text());
                text.fork();
                list.add(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (ContentForkJoin content : list) {
            content.join();
            contentList.add(content.url);
        }

        return contentList;
    }
}
