package Alex.AmilkarSearchBot.createBaseMysql;

import Alex.AmilkarSearchBot.LemmaFinder;
import Alex.AmilkarSearchBot.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

public class CreateNewSearchIndex implements Runnable{
    private final FieldCrudRepository fieldCrudRepository;
    private final PageCrudRepository pageCrudRepository;
    private final LemmaCrudRepository lemmaCrudRepository;
    private final SearchIndexCrudRepository searchIndexCrudRepository;

    public CreateNewSearchIndex(FieldCrudRepository fieldCrudRepository,
                                PageCrudRepository pageCrudRepository,
                                LemmaCrudRepository lemmaCrudRepository,
                                SearchIndexCrudRepository searchIndexCrudRepository)
    {
        this.fieldCrudRepository = fieldCrudRepository;
        this.pageCrudRepository = pageCrudRepository;
        this.lemmaCrudRepository = lemmaCrudRepository;
        this.searchIndexCrudRepository = searchIndexCrudRepository;
    }

    @Override
    public void run() {
        ArrayList pageList = new ArrayList<>((Collection) pageCrudRepository.findAll());

        ArrayList lemmaListIndex = new ArrayList<>((Collection) lemmaCrudRepository.findAll());

        float titleRank = fieldCrudRepository.findById(1).get().getWeight();

        float bodyRank = fieldCrudRepository.findById(2).get().getWeight();

        for (int j = 0; j < pageList.size(); j++) {
            int siteId = pageCrudRepository.findById(j + 1).get().getId();
            String siteContent = pageCrudRepository.findById(j + 1).get().getContent();

            Document doc = Jsoup.parse(siteContent);

            String title = doc.select("title").toString();
            Set<String> lemmaTitle = null;
            try {
                lemmaTitle = LemmaFinder.getInstance().getLemmaSet(title);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<String> stringsTitle = new ArrayList<>(lemmaTitle);

            String body = doc.select("body").toString();
            Map<String, Integer> lemmaMapBody = null;
            try {
                lemmaMapBody = LemmaFinder.getInstance().collectLemmas(body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
