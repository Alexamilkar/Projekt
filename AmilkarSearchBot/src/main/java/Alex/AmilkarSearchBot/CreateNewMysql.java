package Alex.AmilkarSearchBot;

import Alex.AmilkarSearchBot.createBaseMysql.CreateNewField;
import Alex.AmilkarSearchBot.createBaseMysql.CreateNewLemma;
import Alex.AmilkarSearchBot.createBaseMysql.CreateNewPage;
import Alex.AmilkarSearchBot.createBaseMysql.CreateNewSearchIndex;
import Alex.AmilkarSearchBot.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CreateNewMysql implements CommandLineRunner {
    private static final String stringUrl1 = "http://www.playback.ru/";
    private static final String stringUrl2 = "https://volochek.life/";
    private static final String stringUrl3 = "https://nikoartgallery.com/";
    private static final String stringUrl4 = "https://et-cetera.ru/mobile/";

    private String searchString = "телефон белый в метро";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private PageCrudRepository pageCrudRepository;
    private LemmaCrudRepository lemmaCrudRepository;
    private FieldCrudRepository fieldCrudRepository;
    private SearchIndexCrudRepository searchIndexCrudRepository;
    private SiteCrudRepository siteCrudRepository;
    private final AmilkarCrudRepository amilkarCrudRepository;

    @Autowired
    public CreateNewMysql(PageCrudRepository pageCrudRepository,
                          LemmaCrudRepository lemmaCrudRepository,
                          FieldCrudRepository fieldCrudRepository,
                          SearchIndexCrudRepository searchIndexCrudRepository,
                          SiteCrudRepository siteCrudRepository,
                          AmilkarCrudRepository amilkarCrudRepository) {
        this.pageCrudRepository = pageCrudRepository;
        this.lemmaCrudRepository = lemmaCrudRepository;
        this.fieldCrudRepository = fieldCrudRepository;
        this.searchIndexCrudRepository = searchIndexCrudRepository;
        this.siteCrudRepository = siteCrudRepository;
        this.amilkarCrudRepository = amilkarCrudRepository;
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {
//        siteIndexing(stringUrl1);
//        searchString(searchString);
    }



    public void searchString(String searchString) throws IOException {
        List<String> lemmaList = new ArrayList<>((Collection) lemmaCrudRepository.findAll());
        List<String> indexList = new ArrayList<>((Collection) searchIndexCrudRepository.findAll());
        List<String> pageList = new ArrayList<>((Collection) pageCrudRepository.findAll());

        Set<String> stringSet = LemmaFinder.getInstance().getLemmaSet(searchString);

        String lemma = "";
        int lemmaId = 0;
        int idPage = 0;
        float pageRank = 0;
        String page = "";

        for (String string : stringSet) {

            for (int i = 0; i < lemmaList.size(); i++) {
                lemma = lemmaCrudRepository.findById(i + 1).get().getLemma();
                if (stringSet.contains(lemma)) {
                    lemmaId = lemmaCrudRepository.findById(i + 1).get().getId();
                }
            }
            for (int j = 0; j < indexList.size(); j++) {
                int idLemma = searchIndexCrudRepository.findById(j + 1).get().getLemmaId();
                if (indexList.contains(idLemma)) {
                    idPage = searchIndexCrudRepository.findById(j + 1).get().getPageId();
                    pageRank = searchIndexCrudRepository.findById(j + 1).get().getPageRank();
                }
            }

            for (int p = 0; p < pageList.size(); p++) {
                int id = pageCrudRepository.findById(p + 1).get().getId();
                if (pageList.contains(id)) {
                    page = pageCrudRepository.findById(p + 1).get().getPath();
                }
            }

            Amilkar amilkar = new Amilkar();
            amilkar.setLemma(lemma);
            amilkar.setIdLemma(lemmaId);
            amilkar.setRank(pageRank);
            amilkar.setPage(page);

            amilkarCrudRepository.save(amilkar);
        }
    }

    public void siteIndexing(String stringUrl) throws IOException, InterruptedException {
        Document document = Jsoup.connect(stringUrl).userAgent("AmilkarSearchBot").get();

        String siteUrl = document.location();
        String siteName = document.title();

        Site site = new Site();

        site.setStatus(Status.INDEXING);
        site.setStatusTime(dateFormat.format(new Date()));
        site.setUrl(siteUrl);
        site.setName(siteName);
        siteCrudRepository.save(site);

        Thread threadField = new Thread(new CreateNewField(fieldCrudRepository));
        threadField.start();

        Thread threadPage = new Thread(new CreateNewPage(site.getUrl(), site.getId(), pageCrudRepository));
        threadPage.start();

        Thread threadLemma = new Thread(new CreateNewLemma(site.getUrl(), site.getId(), lemmaCrudRepository));
        threadLemma.start();

        threadPage.join();
        threadLemma.join();

        Thread threadSearchIndex = new Thread(new CreateNewSearchIndex(fieldCrudRepository,
                pageCrudRepository, lemmaCrudRepository, searchIndexCrudRepository));
        threadSearchIndex.start();
        threadSearchIndex.join();

        if (threadSearchIndex.getState().toString().equals("TERMINATED")) {
            site.setStatus(Status.INDEXED);
            site.setStatusTime(dateFormat.format(new Date()));
            site.setUrl(siteUrl);
            site.setName(siteName);
            siteCrudRepository.save(site);
        }

        if (threadSearchIndex.getState().toString().equals("BLOCKED")) {
            site.setStatus(Status.FAILED);
            site.setStatusTime(dateFormat.format(new Date()));
            site.setUrl(siteUrl);
            site.setName(siteName);
            siteCrudRepository.save(site);
        }
    }
}
