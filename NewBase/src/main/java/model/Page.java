package model;

import javax.persistence.*;

@Entity
@Table(name = "search_engine")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;
    @Column(name = "Path", length = 50, nullable = false)
    private String path;
    @Column(name = "Code", nullable = false)
    private int code;
    @Column(name = "Content", columnDefinition = "mediumtext", nullable = false)
    private String content;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}


//    validate — проверить схему, не вносить изменения в базу данных;
//    update — обновить схему;
//    create — создаёт схему, уничтожая предыдущие данные;
//    create-drop — отказаться от схемы, когда SessionFactory закрывается явно — обычно, когда приложение остановлено.
