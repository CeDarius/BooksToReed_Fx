package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    Button add;
    @FXML
    TextField textFieldAuthor, textFieldTitle;
    @FXML
    ListView<BookToRead> listView;

    BookToRead book;
    SessionFactory factory;
    Session session;
    Query query;
    List<BookToRead> booksList;
    ObservableList<BookToRead> observableList = FXCollections.observableArrayList();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(BookToRead.class).buildSessionFactory();
        readAllBooks();
        listView.setItems(observableList);
        textFieldAuthor.setText("");
        textFieldTitle.setText("");
    }

    private void startTransaction() {
        try{
            session = factory.getCurrentSession();
            session.beginTransaction();
        }catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void readAllBooks() {
        startTransaction();
        try {
            query = session.createQuery("from BookToRead");
            booksList = query.list();
            for (BookToRead bookToRead : booksList) {
                System.out.println(bookToRead);
                observableList.add(bookToRead);
            }
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void addToList(ActionEvent e) {
        System.out.println("enter to list");
        addBook();
    }

    private void addBook() {
        if(textFieldAuthor.getText().trim().equals("") && textFieldTitle.getText().trim().equals("")){
            return;
        }else {
            book = new BookToRead(textFieldAuthor, textFieldTitle);
            observableList.add(book);
            clareTextFields();
        }
        System.out.println(book.toString());
        System.out.println("book saved");
    }

    @FXML
    public void saveChanges(ActionEvent e) {
        for(BookToRead it : observableList) {
            Integer id = it.getId();
            if(id != null) {
                session.update(it);
            } else {
                session.save(it);
            }
        }
        session.getTransaction().commit();
        listView.refresh();
    }

    @FXML
    public void listClick(MouseEvent e) {
        book = listView.getSelectionModel().getSelectedItem();
        textFieldAuthor.setText(book.getAuthor());
        textFieldTitle.setText(book.getTitle());
        System.out.println(book);
    }

    @FXML
    public void updateEntry(ActionEvent e) {
        Integer id = book.getId();
        String a = textFieldAuthor.getText();
        String t = textFieldTitle.getText();
        System.out.println( a + t );
        if(textFieldAuthor.getText().trim().equals("") && textFieldTitle.getText().trim().equals("")) {
            return;
        } else {
            book.setAuthor(a);
            book.setTitle(t);
            try {
                observableList.set(id, book);
            } catch (IndexOutOfBoundsException ex) {
                System.out.println(ex.getMessage());
            }
            clareTextFields();
        }
        listView.refresh();
        System.out.println(book);
    }

    @FXML
    public void deleteEntry(ActionEvent e) {
        System.out.println(book);
        alertDelete(e);
    }

    @FXML
    public void alertDelete(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm your choice");
        alert.setContentText("Delete - " + book.toString());

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == buttonTypeYes) {
            deleteSelectedBook();
            listView.getItems().removeAll(book);
        }else if(result.get() == buttonTypeNo) {

        }
    }

    private void deleteSelectedBook() {
        //startTransaction();
        Integer id = book.getId();
        try {
            observableList.remove(id);
            session.delete(book);
            System.out.println("refreshed");
            clareTextFields();
        } catch (HibernateException exception) {
            System.out.println(exception.getMessage());
        }
        listView.refresh();
    }

    private void clareTextFields() {
        textFieldAuthor.setText("");
        textFieldTitle.setText("");
    }

}
