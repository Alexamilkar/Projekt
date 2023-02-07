package webSite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class PathListForkJoin extends RecursiveTask<List<String>> {
    private int size;
    private String url;

    public PathListForkJoin(int size, String url) {
        this.size = size;
        this.url = url;
    }

    @Override
    protected List<String> compute() {
        List<String> pathList = new ArrayList<>();
        List<PathForkJoin> list = new ArrayList<>();

        try {
            for (int i = 0; i < size; i++) {
                Document doc = Jsoup.connect(url).get();
                PathForkJoin location = new PathForkJoin(doc.location());
                location.fork();
                list.add(location);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (PathForkJoin path : list) {
            path.join();
            pathList.add(path.url);
        }

        return pathList;
    }
}
