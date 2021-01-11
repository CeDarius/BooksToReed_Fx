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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    Button submit;
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
            Iterator iterator = booksList.iterator();
            while(iterator.hasNext()) {
                book = (BookToRead) iterator.next();
                System.out.println(book);
                observableList.add(book);
            }
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        session.close();
    }

    @FXML
    public void enterToList(ActionEvent e) {
        System.out.println("enter to list");
        enterData();
    }

    private void enterData() {
        startTransaction();
        if(textFieldAuthor.getText().trim().equals("") && textFieldTitle.getText().trim().equals("")){
            session.close();
            return;
        }else {
            book = new BookToRead(textFieldAuthor, textFieldTitle);
        }
        System.out.println(book.toString());

        try {
            session.save(book);
            session.getTransaction().commit();
            clareTextFields();
        } catch(HibernateException e) {
            System.out.println(e.getMessage());
        } finally {
            session.close();
        }
        observableList.add(book);
        System.out.println("book saved");
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
        startTransaction();
        String a = textFieldAuthor.getText();
        String t = textFieldTitle.getText();
        System.out.println( a + t );
        if(textFieldAuthor.getText().trim().equals("") && textFieldTitle.getText().trim().equals("")) {
            session.close();
            return;
        } else {
            book.setAuthor(a);
            book.setTitle(t);
            session.update(book);
            session.getTransaction().commit();
            clareTextFields();
        }
        System.out.println(book);
        session.close();
        listView.refresh();
    }

    @FXML
    public void deleteEntry(ActionEvent e) {
        System.out.println(book);
        alert(e);
    }

    @FXML
    public void alert(ActionEvent event){
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
        startTransaction();
        try {
            BookToRead book1 = session.get(BookToRead.class, book.getId());
            System.out.println("delete book id " + book.getId());
            session.delete(book1);
            session.getTransaction().commit();
            System.out.println("refreshed");
            clareTextFields();
        } catch (HibernateException exception) {
            System.out.println(exception.getMessage());
        } finally {
            session.close();
        }
    }

    private void clareTextFields() {
        textFieldAuthor.setText("");
        textFieldTitle.setText("");
    }

}
