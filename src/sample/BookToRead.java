package sample;

import javafx.scene.control.TextField;

import javax.persistence.*;


@Entity
@Table(name = "books")
public class BookToRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "author")
    private String author;

    @Column(name = "title")
    private String title;

    public BookToRead() {}

    public BookToRead(TextField textFieldAuthor, TextField textFieldTitle){
        this.author = textFieldAuthor.getText();
        this.title = textFieldTitle.getText();
    }

    public Integer getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("%d  %s \"%s\"", id, author, title );

    }
}
