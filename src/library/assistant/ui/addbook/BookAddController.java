package library.assistant.ui.addbook;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.listbook.BookListController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookAddController {

    public TextField id;
    public TextField title;
    public TextField author;
    public TextField publisher;
    public Button saveButton;
    public Button cancelButton;
    public AnchorPane rootPane;

    DatabaseHandler databaseHandler;

    private Boolean isInEditMode = Boolean.FALSE;

    public BookAddController(){
        databaseHandler = DatabaseHandler.getInstance();
        checkData();
    }

    private void checkData() {
        String qu = "SELECT title FROM BOOK";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String titlex = rs.getString("title");
                System.out.println(titlex);
            }
        } catch(SQLException e)
        {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void addBook(ActionEvent actionEvent) {
        System.out.println("Add Successful");
       String bookID = id.getText();
       String bookName = title.getText();
       String bookAuthor = author.getText();
       String bookPublisher = publisher.getText();

       if(bookID.isEmpty() || bookName.isEmpty() || bookAuthor.isEmpty() || bookPublisher.isEmpty())
       {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setHeaderText(null);
           alert.setContentText("Please Enter in all fields");
           alert.showAndWait();
           return;
       }
       if(isInEditMode)
       {
           handleEditOperation();
           return;
       }
       String qu = "INSERT INTO BOOK VALUES ("+
               "'" + bookID + "'," +
               "'" + bookName+ "'," +
               "'" + bookAuthor+ "'," +
               "'" + bookPublisher + "'," +
               "'" + true + "'" + ")";

        System.out.println(qu);

        if(databaseHandler.execAction(qu)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
    }

    private void handleEditOperation() {
        BookListController.Book book = new BookListController.Book(title.getText(), id.getText(), author.getText(), publisher.getText(), true);
        if (databaseHandler.updateBook(book)) {
           // AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Success", "Update complete");
            AlertMaker.showSimpleAlert("Success","Book updated");
        } else {
           // AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed", "Could not update data");
            AlertMaker.showErrorMessage("Failed","Cannot be updated");
        }
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    public void inflateUI(BookListController.Book book) {
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }

}
