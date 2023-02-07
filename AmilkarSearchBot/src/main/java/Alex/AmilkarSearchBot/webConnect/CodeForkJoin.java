package Alex.AmilkarSearchBot.webConnect;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.RecursiveTask;

public class CodeForkJoin extends RecursiveTask<Integer> {
    private int result;
    final String url;
    public CodeForkJoin(String url) {
        this.url = url;
    }

    @Override
    protected Integer compute() {

        try {
            Thread.sleep(150);

            String site = url;
            URL url = new URL(site);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            int responseCode = http.getResponseCode();
            CodeForkJoin code = new CodeForkJoin(Integer.toString(responseCode));
            code.fork();
            result = Integer.parseInt(code.url);

        } catch (Exception ex) {
            ex.getStackTrace();
        }
        Thread.currentThread().interrupt();

        return result;
    }
}
