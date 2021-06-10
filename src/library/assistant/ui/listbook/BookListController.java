package library.assistant.ui.listbook;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addbook.BookAddController;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookListController implements Initializable {
    ObservableList<Book> list = FXCollections.observableArrayList();
    public AnchorPane rootPane;
    public TableView tableView;
    public TableColumn titleColm;
    public TableColumn idColm;
    public TableColumn authorColm;
    public TableColumn publisherColm;
    public TableColumn availabilityColm;

    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {

        titleColm.setCellValueFactory(new PropertyValueFactory<>("title"));
        idColm.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorColm.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColm.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityColm.setCellValueFactory(new PropertyValueFactory<>("availability"));

    }

    private void loadData() {
        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String titlex = rs.getString("title");
                String author = rs.getString("author");
                String id = rs.getString("id");
                String publisher = rs.getString("publisher");
                Boolean avail = rs.getBoolean("isAvail");

                list.add(new Book(titlex, id, author, publisher, avail));
                System.out.println("Book added in list");
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(list);
       // tableView.getItems().addAll(list);
        tableView.setItems(list);

    }

    public void handleBookDeleteOption(ActionEvent actionEvent) {
            Book selectedBookForDeletion = (Book) tableView.getSelectionModel().getSelectedItem();
            if(selectedBookForDeletion == null)
            {
                AlertMaker.showErrorMessage("No Book Selected","Please select a book for deletion first");
                return;
            }
            if(DatabaseHandler.getInstance().isBookAlreadyIssued(selectedBookForDeletion))
            {
                AlertMaker.showErrorMessage("Cannot be deleted", "This book is already issued hence can't deleted");
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deleting Book");
            alert.setContentText("Are you sure you want to delete the book" + selectedBookForDeletion.getTitle() + "?");
            Optional<ButtonType> answer = alert.showAndWait();
            if(answer.get() == ButtonType.OK)
            {
               boolean result = DatabaseHandler.getInstance().deleteBook(selectedBookForDeletion);
               if(result)
               {
                   AlertMaker.showSimpleAlert("Deleted sucessfully", selectedBookForDeletion.getTitle() + " was deleted successfully");
                    list.remove(selectedBookForDeletion);
               }
               else
               {
                   AlertMaker.showSimpleAlert("Failed", selectedBookForDeletion.getTitle() + " could not be deleted");
               }
            }
            else
            {
                AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
            }

    }

    public void handleRefresh(ActionEvent actionEvent) {
        loadData(); 
    }

    public void handleBookEditOption(ActionEvent actionEvent) {
        Book selectedBookForEdit = (Book) tableView.getSelectionModel().getSelectedItem();
        if(selectedBookForEdit == null)
        {
            AlertMaker.showErrorMessage("No Book Selected","Please select a book for edit first");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addbook/add_book.fxml"));
            Parent parent = loader.load();

            BookAddController controller = (BookAddController) loader.getController();
            controller.inflateUI(selectedBookForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static class Book{
        private final SimpleStringProperty id;
        private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleBooleanProperty availability;

        public String getId() {
            return id.get();
        }

        public String getTitle() {
            return title.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public boolean isAvailability() {
            return availability.get();
        }

        public Book(String title, String id, String author, String pub, Boolean avail) {

            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(pub);
            this.availability = new SimpleBooleanProperty(avail);
        }

    }
}
