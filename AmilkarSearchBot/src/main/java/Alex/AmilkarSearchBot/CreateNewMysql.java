package Alex.AmilkarSearchBot;

import Alex.AmilkarSearchBot.model.*;
import Alex.AmilkarSearchBot.webConnect.ContentForkJoin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

@Component
public class CreateNewMysql implements CommandLineRunner {
    private static final String stringUrl1 = "http://www.playback.ru/";
    private static final String stringUrl2 = "https://volochek.life/";
    private static final String stringUrl3 = "https://nikoartgallery.com/";
    private static final String stringUrl4 = "https://et-cetera.ru/mobile/";
    private Status status1 = Status.INDEXING; //ИНДЕКСАЦИЯ
    private Status status2 = Status.INDEXED; //ИНДЕКСИРОВАНО
    private Status status3 = Status.FAILED; //НЕ УДАЛОСЬ

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    PageCrudRepository pageCrudRepository;
    LemmaCrudRepository lemmaCrudRepository;
    FieldCrudRepository fieldCrudRepository;
    SearchIndexCrudRepository searchIndexCrudRepository;
    SiteCrudRepository siteCrudRepository;

    @Autowired
    public CreateNewMysql(PageCrudRepository pageCrudRepository,
                          LemmaCrudRepository lemmaCrudRepository,
                          FieldCrudRepository fieldCrudRepository,
                          SearchIndexCrudRepository searchIndexCrudRepository,
                          SiteCrudRepository siteCrudRepository) {
        this.pageCrudRepository = pageCrudRepository;
        this.lemmaCrudRepository = lemmaCrudRepository;
        this.fieldCrudRepository = fieldCrudRepository;
        this.searchIndexCrudRepository = searchIndexCrudRepository;
        this.siteCrudRepository = siteCrudRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> siteList = new ArrayList<>();

        siteList.add(stringUrl1);
        siteList.add(stringUrl2);
        siteList.add(stringUrl3);
        siteList.add(stringUrl4);

        createNewBase(siteList);
    }

    public void createNewBase(List<String> list) throws IOException {

        createNewSite(list);
        createNewField();

        ArrayList siteList = new ArrayList<>((Collection) siteCrudRepository.findAll());

        for (int s = 0; s < siteList.size(); s++)
        {
            String siteUrl = siteCrudRepository.findById(s + 1).get().getUrl();
            int siteId = siteCrudRepository.findById(s + 1).get().getId();

            ArrayList<Thread> threads = new ArrayList<>();

            threads.add(new Thread(() -> {
                synchronized (siteUrl) {
                    try {
                        createNewPage(siteUrl, siteId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }}));

            threads.add(new Thread(() -> {
                synchronized (siteUrl) {
                    try {
                        createNewLemma(siteUrl, siteId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }}));
            threads.forEach(Thread::start);
        }
        //createNewSearchIndex();
    }

    public void createNewSite(List<String> stringList) throws IOException
    {
        for (String stringUrl : stringList) {
            Document document = Jsoup.connect(stringUrl).get();

            String siteUrl = document.location();
            String siteName = document.title();

            Site site = new Site();

            site.setStatus(status1);
            site.setStatusTime(dateFormat.format(new Date()));
            site.setUrl(siteUrl);
            site.setName(siteName);

            siteCrudRepository.save(site);
        }
    }

    public void createNewField() {
        Field field1 = new Field();

        field1.setName("title");
        field1.setSelector("title");
        field1.setWeight(1);

        fieldCrudRepository.save(field1);

        Field field2 = new Field();

        field2.setName("body");
        field2.setSelector("body");
        field2.setWeight(0.8F);

        fieldCrudRepository.save(field2);
    }

    public void createNewPage(String siteUrl, int siteId) throws IOException {

//            String siteUrl = siteCrudRepository.findById(s+1).get().getUrl();
//            int siteId = siteCrudRepository.findById(s+1).get().getId();

        List<String> strings = new ArrayList<>();

        Document document = Jsoup.connect(siteUrl).get();
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

        for (int i = 0; i < strings.size(); i++)
        {
            String site = strings.get(i);

            String[] split = site.split("/");
            String siteName = '/' + split[split.length-1];

            URL url = new URL(site);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int responseCode = http.getResponseCode();

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

    public void createNewLemma(String siteUrl, int siteId) throws IOException {
        ArrayList siteList = new ArrayList<>((Collection) siteCrudRepository.findAll());


//            String siteUrl = siteCrudRepository.findById(s+1).get().getUrl();
//            int siteId = siteCrudRepository.findById(s+1).get().getId();

            List<String> strings = new ArrayList<>();

            Document document = Jsoup.connect(siteUrl).get();
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

            Map<String, Integer> mapLemma = new HashMap<>();

            for (int i = 0; i < strings.size(); i++) {

                String site = strings.get(i);
                String text = new ForkJoinPool().invoke(new ContentForkJoin(site));

                if (text != null) {
                    Set<String> stringSet = LemmaFinder.getInstance().getLemmaSet(text);
                    for (String lemma : stringSet) {
                        if (mapLemma.containsKey(lemma)) {
                            mapLemma.replace(lemma, mapLemma.get(lemma), mapLemma.get(lemma) + 1);
                        } else mapLemma.put(lemma, 1);
                    }
                }
            }

            List<String> lemmaList = new ArrayList<>(mapLemma.keySet());
            List<Integer> lemmaIntList = new ArrayList<>(mapLemma.values());

            for (int i = 0; i < lemmaList.size(); i++) {

                String lemmaSt = lemmaList.get(i);
                int limmaInt = lemmaIntList.get(i);

                Lemma lemma = new Lemma();

                lemma.setLemma(lemmaSt);
                lemma.setFrequency(limmaInt);
                lemma.setSiteId(siteId);

                lemmaCrudRepository.save(lemma);
            }

    }

    public void createNewSearchIndex() throws IOException {
        ArrayList pageList = new ArrayList<>((Collection) pageCrudRepository.findAll());

        ArrayList lemmaListIndex = new ArrayList<>((Collection) lemmaCrudRepository.findAll());

        float titleRank = fieldCrudRepository.findById(1).get().getWeight();

        float bodyRank = fieldCrudRepository.findById(2).get().getWeight();

        for (int j = 0; j < pageList.size(); j++) {
            int siteId = pageCrudRepository.findById(j + 1).get().getId();
            String siteContent = pageCrudRepository.findById(j + 1).get().getContent();

            Document doc = Jsoup.parse(siteContent);

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

            for (int i = 0; i < lemmaListIndex.size(); i++) {
                int lemmaId = lemmaCrudRepository.findById(i + 1).get().getId();
                lemma = lemmaCrudRepository.findById(i + 1).get().getLemma();

                if (stringsTitle.contains(lemma) && stringsBody.contains(lemma)) {
                    indexTitle = (int) titleRank;
                    int indexBody = stringsBody.indexOf(lemma);
                    wordCount = integersBody.get(indexBody);
                } else if (!stringsTitle.contains(lemma) && stringsBody.contains(lemma)) {
                    indexTitle = 0;
                    int indexBody = stringsBody.indexOf(lemma);
                    wordCount = integersBody.get(indexBody);
                }
                rank = indexTitle + (wordCount * bodyRank);

                SearchIndex searchIndex = new SearchIndex();

                searchIndex.setLemmaId(lemmaId);
                searchIndex.setPageId(siteId);
                searchIndex.setPageRank(rank);

                searchIndexCrudRepository.save(searchIndex);
            }
        }
    }
}
