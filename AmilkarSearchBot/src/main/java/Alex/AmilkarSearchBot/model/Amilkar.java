package Alex.AmilkarSearchBot.model;

import javax.persistence.*;

@Entity
@Table(name = "alex")
public class Amilkar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "lemma")
    private String lemma;
    @Column(name = "lemma_id")
    private int idLemma;
    @Column(name = "rank")
    private float rank;
    @Column(name = "page")
    private String page;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getIdLemma() {
        return idLemma;
    }

    public void setIdLemma(int idLemma) {
        this.idLemma = idLemma;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
