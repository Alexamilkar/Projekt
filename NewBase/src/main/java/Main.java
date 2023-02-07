import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import resultNewBase.LemmaFinder;
import resultNewBase.WebConnect;
import webSite.ContentForkJoin;
import webSite.SiteConnectForkJoin;
import webSite.SiteConnectThread;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {

    //http://www.playback.ru/
    //https://volochek.life/
    //http://radiomv.ru/
    //https://ipfran.ru/
    //https://dimonvideo.ru/
    //https://nikoartgallery.com/
    //https://et-cetera.ru/mobile/
    //https://www.lutherancathedral.ru/
    //https://dombulgakova.ru/
    //https://www.svetlovka.ru/
    private static final String stringUrl1 = "http://www.playback.ru/";
    private static final String stringUrl2 = "https://volochek.life/";
    private static final String stringUrl3 = "https://nikoartgallery.com/";

    public static void main(String[] args) {

        long timeStart = System.currentTimeMillis();

        List<String> strings = new ForkJoinPool().invoke(new SiteConnectForkJoin(stringUrl3));
        for (String str : strings) {
            String content = new ForkJoinPool().invoke(new ContentForkJoin(str));
            System.out.println(content);
        }

        System.out.println("Timer - " + (System.currentTimeMillis() - timeStart) + " ms.");
    }

    public static void demoSorted(String nameSite) throws IOException, URISyntaxException {
        Map<String, Float> map = lemmaBaseTemp(nameSite);

        Map<String, Float> result = new LinkedHashMap<>();

        //sorted
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                .forEach(r -> result.put(r.getKey(), r.getValue()));

        System.out.println( " +++++ " + map.values() + " = \n" + result.values());

        WebConnect webConnect = new WebConnect();
        webConnect.webConnect(stringUrl1);
        Map<String, Integer> mapLemma = webConnect.getMapLemma();
        mapLemma.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(System.out::println);
    }

    public static Map<String, Float> lemmaBaseTemp(String stringUrl) throws IOException, URISyntaxException {
        Map<String, Float> stringFloatMap = new HashMap<>();
        Document doc = Jsoup.connect(stringUrl).get();

        String text = doc.text();
        Set<String> lemmaText = LemmaFinder.getInstance().getLemmaSet(text);
        List<String> stringsText = new ArrayList<>(lemmaText);

        String title = doc.select("title").toString();
        Set<String> lemmaTitle = LemmaFinder.getInstance().getLemmaSet(title);
        List<String> stringsTitle = new ArrayList<>(lemmaTitle);

        String body = doc.select("body").toString();
        Map<String, Integer> lemmaMapBody = LemmaFinder.getInstance().collectLemmas(body);
        List<String> stringsBody = new ArrayList<>(lemmaMapBody.keySet());
        List<Integer> integersBody = new ArrayList<>(lemmaMapBody.values());

        int wordCount = 0;
        int indexTitle = 0;
        float rank = 0;
        String lemma = "";

        for (String s : stringsText) {
            lemma = s;

            if (stringsTitle.contains(lemma) && stringsBody.contains(lemma)) {
                indexTitle = 1;
                int indexBody = stringsBody.indexOf(lemma);
                wordCount = integersBody.get(indexBody);
            } else if (!stringsTitle.contains(lemma) && stringsBody.contains(lemma)) {
                indexTitle = 0;
                int indexBody = stringsBody.indexOf(lemma);
                wordCount = integersBody.get(indexBody);
            }
            rank = indexTitle + (wordCount * 0.8F);

            stringFloatMap.put(lemma, rank);
        }
        return stringFloatMap;
    }


    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void siteConnectThread(String stringUrl) throws IOException {
        SiteConnectThread siteConnect = new SiteConnectThread();
        siteConnect.listSite(stringUrl);

        String path = siteConnect.getResultListPath().get(0);
        int code = siteConnect.getResultListCode().get(0);
        String content = siteConnect.getResultListContent().get(0);

        System.out.println("Name - " + path + "\n"
                + "Code - " + code + "\n"
                + "Content - " + content);
    }

}