package webSite;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class CodeListForkJoin extends RecursiveTask<List<Integer>> {
    private int size;
    private String url;

    public CodeListForkJoin(int size, String url) {
        this.size = size;
        this.url = url;
    }

    @Override
    protected List<Integer> compute() {
        List<Integer> codeList = new ArrayList<>();
        List<CodeForkJoin> list = new ArrayList<>();

        try {
            for (int i = 0; i < size; i++) {
                String site = url;
                URL url = new URL(site);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                int responseCode = http.getResponseCode();
                CodeForkJoin code = new CodeForkJoin(Integer.toString(responseCode));
                code.fork();
                list.add(code);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (CodeForkJoin code : list) {
            code.join();
            codeList.add(Integer.valueOf(code.url));
        }

        return codeList;
    }
}
