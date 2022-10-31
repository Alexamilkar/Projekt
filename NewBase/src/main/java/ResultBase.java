import model.Page;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import webSite.SiteConnect;

import java.io.IOException;

public class ResultBase {
    public void addResult(String url) throws IOException {
        addNewPage(url);
    }

    public void addNewPage(String stringUrl) throws IOException {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        SiteConnect siteConnect = new SiteConnect();
        siteConnect.webConnect(stringUrl);

        for (int i = 0; i < siteConnect.getStringList().size(); i++)
        {
            String site = siteConnect.getResultListLoc().get(i);
            Integer code = siteConnect.getResultListCod().get(i);
            String text = siteConnect.getResultListText().get(i);

            Page newPage = new Page();

            newPage.setPath(site);
            newPage.setCode(code);
            newPage.setContent(text);

            session.save(newPage);

        }
        transaction.commit();
        sessionFactory.close();
    }
}
