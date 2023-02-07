//package resultNewBase;
//
//import Alex.AmilkarSearchBot.model.*;
//import Alex.AmilkarSearchBot.webSite.SiteConnectThread;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//
//public class StartDemo {
//    private static final String stringUrl1 = "http://www.playback.ru/";
//    private static final String stringUrl2 = "https://ipfran.ru/";
//    private static final String searchText = "Хочу купить золотой смартфон.";
//    PageCrudRepository pageCrudRepository;
//    LemmaCrudRepository lemmaCrudRepository;
//    FieldCrudRepository fieldCrudRepository;
//    SearchIndexCrudRepository searchIndexCrudRepository;
//    SiteCrudRepository siteCrudRepository;
//
//    @Autowired
//    public StartDemo(PageCrudRepository pageCrudRepository,
//                     LemmaCrudRepository lemmaCrudRepository,
//                     FieldCrudRepository fieldCrudRepository,
//                     SearchIndexCrudRepository searchIndexCrudRepository,
//                     SiteCrudRepository siteCrudRepository)
//    {
//        this.pageCrudRepository = pageCrudRepository;
//        this.lemmaCrudRepository = lemmaCrudRepository;
//        this.fieldCrudRepository = fieldCrudRepository;
//        this.searchIndexCrudRepository = searchIndexCrudRepository;
//        this.siteCrudRepository = siteCrudRepository;
//    }
//
////    @Override
////    public void run(String... args) throws Exception
////    {
////
////    }
//
//    public Map<String, Float> setSearchText(String searchText) throws IOException
//    {
//        List<String> stringText = new ArrayList<>(LemmaFinder.getInstance().getLemmaSet(searchText));
//
//        List<String> stringListLemma = new ArrayList<>((Collection) lemmaCrudRepository.findAll());
//        Map<String, Float> mapSiteRank = new HashMap<>();
//
//        for (String st : stringListLemma) {
//            if (stringText.contains(st)) {
//                int idLemma = st.indexOf(stringListLemma.toString());
//                int idSite = searchIndexCrudRepository.findById(idLemma).get().getPageId();
//                float rankSite = searchIndexCrudRepository.findById(idLemma).get().getPageRank();
//                String nameSite = pageCrudRepository.findById(idSite).get().getPath();
//                if (mapSiteRank.containsKey(nameSite)) {
//                    mapSiteRank.replace(nameSite, mapSiteRank.get(nameSite),
//                            (mapSiteRank.get(nameSite)+rankSite));
//                } else mapSiteRank.put(nameSite, rankSite);
//            }
//        }
//        return mapSiteRank;
//    }
//
//    public void createNewBase(String stringUrl) {
//
//    }
//
//    public void createNewSite(String stringUrl) throws IOException {
//        Document document = Jsoup.connect(stringUrl).get();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        Status status1 = Status.INDEXING; //ИНДЕКСАЦИЯ
//        Status status2 = Status.INDEXED; //ИНДЕКСИРОВАНО
//        Status status3 = Status.FAILED; //НЕ УДАЛОСЬ
//
//
//        String siteUrl = document.location();
//        String siteName = document.title();
//
//        Site site = new Site();
//
//        site.setStatus(status1);
//        site.setStatusTime(dateFormat.format(new Date()));
//        site.setUrl(siteUrl);
//        site.setName(siteName);
//
//        siteCrudRepository.save(site);
//    }
//
//    public void createNewPage() throws IOException {
//        ArrayList siteList = new ArrayList<>((Collection) siteCrudRepository.findAll());
//        for (int s = 0; s < siteList.size(); s++) {
//            String url = siteCrudRepository.findById(s+1).get().getUrl();
//            int siteId = siteCrudRepository.findById(s+1).get().getId();
//
//            SiteConnectThread siteConnect = new SiteConnectThread();
//            siteConnect.webConnect(url);
//
//            for (int i = 0; i < siteConnect.getStringList().size(); i++)
//            {
//                String site = siteConnect.getResultListPath().get(i);
//                int code = siteConnect.getResultListCode().get(i);
//                String text = siteConnect.getResultListContent().get(i);
//
//                Page page = new Page();
//
//                page.setPath(site);
//                page.setCode(code);
//                page.setContent(text);
//                page.setSiteId(siteId);
//
//                pageCrudRepository.save(page);
//            }
//        }
//    }
//
//    public void createNewLemma() throws IOException {
//        ArrayList siteList = new ArrayList<>((Collection) siteCrudRepository.findAll());
//
//        for (int s = 0; s < siteList.size(); s++) {
//
//            String url = siteCrudRepository.findById(s+1).get().getUrl();
//            int siteId = siteCrudRepository.findById(s+1).get().getId();
//
//            SiteConnectThread siteConnect = new SiteConnectThread();
//            siteConnect.webConnect(url);
//
//            Map<String, Integer> mapLemma = new HashMap<>();
//
//            for (int i = 0; i < siteConnect.getStringList().size(); i++) {
//
//                String text = siteConnect.getResultListContent().get(i);
//
//                Set<String> strings = LemmaFinder.getInstance().getLemmaSet(text);
//                for (String lemma : strings) {
//                    if (mapLemma.containsKey(lemma)) {
//                        mapLemma.replace(lemma, mapLemma.get(lemma), mapLemma.get(lemma) + 1);
//                    } else mapLemma.put(lemma, 1);
//                }
//            }
//
//            List<String> lemmaList = new ArrayList<>(mapLemma.keySet());
//            List<Integer> lemmaIntList = new ArrayList<>(mapLemma.values());
//
//            for (int i = 0; i < lemmaList.size(); i++) {
//
//                String lemmaSt = lemmaList.get(i);
//                int limmaInt = lemmaIntList.get(i);
//
//                Lemma lemma = new Lemma();
//
//                lemma.setLemma(lemmaSt);
//                lemma.setFrequency(limmaInt);
//                lemma.setSiteId(siteId);
//
//                lemmaCrudRepository.save(lemma);
//            }
//        }
//    }
//
//    public void createNewField() {
//        Field field1 = new Field();
//
//        field1.setName("title");
//        field1.setSelector("title");
//        field1.setWeight(1);
//
//        fieldCrudRepository.save(field1);
//
//        Field field2 = new Field();
//
//        field2.setName("body");
//        field2.setSelector("body");
//        field2.setWeight(0.8F);
//
//        fieldCrudRepository.save(field2);
//    }
//
//    public void createNewSearchIndex() throws IOException {
//        ArrayList pageList = new ArrayList<>((Collection) pageCrudRepository.findAll());
//
//        ArrayList lemmaListIndex = new ArrayList<>((Collection) lemmaCrudRepository.findAll());
//
//        float titleRank = fieldCrudRepository.findById(1).get().getWeight();
//
//        float bodyRank = fieldCrudRepository.findById(2).get().getWeight();
//
//        for (int j = 0; j < pageList.size(); j++) {
//            int siteId = pageCrudRepository.findById(j + 1).get().getId();
//            String siteContent = pageCrudRepository.findById(j + 1).get().getContent();
//
//            Document doc = Jsoup.parse(siteContent);
//
//            String title = doc.select("title").toString();
//            Set<String> lemmaTitle = LemmaFinder.getInstance().getLemmaSet(title);
//            List<String> stringsTitle = new ArrayList<>(lemmaTitle);
//
//            String body = doc.select("body").toString();
//            Map<String, Integer> lemmaMapBody = LemmaFinder.getInstance().collectLemmas(body);
//            List<String> stringsBody = new ArrayList<>(lemmaMapBody.keySet());
//            List<Integer> integersBody = new ArrayList<>(lemmaMapBody.values());
//
//            int wordCount = 0;
//            int indexTitle = 0;
//            float rank = 0;
//            String lemma = "";
//
//            for (int i = 0; i < lemmaListIndex.size(); i++) {
//                int lemmaId = lemmaCrudRepository.findById(i + 1).get().getId();
//                lemma = lemmaCrudRepository.findById(i + 1).get().getLemma();
//
//                if (stringsTitle.contains(lemma) && stringsBody.contains(lemma)) {
//                    indexTitle = (int) titleRank;
//                    int indexBody = stringsBody.indexOf(lemma);
//                    wordCount = integersBody.get(indexBody);
//                } else if (!stringsTitle.contains(lemma) && stringsBody.contains(lemma)) {
//                    indexTitle = 0;
//                    int indexBody = stringsBody.indexOf(lemma);
//                    wordCount = integersBody.get(indexBody);
//                }
//                rank = indexTitle + (wordCount * bodyRank);
//
//                SearchIndex searchIndex = new SearchIndex();
//
//                searchIndex.setLemmaId(lemmaId);
//                searchIndex.setPageId(siteId);
//                searchIndex.setPageRank(rank);
//
//                searchIndexCrudRepository.save(searchIndex);
//            }
//        }
//    }
//}
