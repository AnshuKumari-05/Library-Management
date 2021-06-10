package library.assistant.ui.main;

import com.jfoenix.effects.JFXDepthManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public HBox book_info;
    public HBox member_info;
    public TextField bookIDInput;
    public TextField bookName;
    public TextField bookAuthor;
    public TextField bookStatus;
    public TextField memberIDInput;
    public TextField memberName;
    public TextField memberMobile;
    public TextField bookID;
    public ListView issueDataList;
    public StackPane rootPane;

    DatabaseHandler databaseHandler;

    Boolean isReadyForSubmission = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JFXDepthManager.setDepth(book_info,1);
        JFXDepthManager.setDepth(member_info,1);
        databaseHandler = DatabaseHandler.getInstance();
    }

    public void loadAddMember(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/addmember/member_add.fxml","Add New Member");
    }

    public void loadAddBook(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/addbook/add_book.fxml","Add New Book");
    }

    public void loadMemberTable(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/listmember/member_list.fxml","Member List");
    }

    public void loadBookTable(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml","Book List");

    }
    public void loadSettings(ActionEvent actionEvent) {
        loadWindow("/library/assistant/settings/settings.fxml","Settings");
    }

    void loadWindow(String loc, String title)
    {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadBookInfo(ActionEvent actionEvent) {
        clearBookCache();
        String id = bookIDInput.getText();
        String qu = "SELECT * FROM BOOK WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                flag = true;

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus)?"Available":"Not Available";
                bookStatus.setText(status);
            }
            if(!flag)
            {
                bookName.setText("No such book available");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    void clearBookCache()
    {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");

    }

    void clearMemberCache()
    {
        memberName.setText("");
        memberMobile.setText("");
    }

    public void loadMemberInfo(ActionEvent actionEvent) {
        clearMemberCache();
        String id = memberIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");
                flag = true;

                memberName.setText(mName);
                memberMobile.setText(mMobile);;
            }
            if(!flag)
            {
                memberName.setText("No such member available");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void loadIssueOperation(ActionEvent actionEvent) {
        String memberID = memberIDInput.getText();
        String bookID = bookIDInput.getText();

        Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to issue the book " + bookName.getText() + "\n to " + memberName.getText() + "?");

        Optional<ButtonType> response = alert.showAndWait();
        if(response.get()==ButtonType.OK)
        {
            String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";

            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";

            System.out.println(str + " and " + str2);

            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("SUCCESS");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue Complete");
                alert1.showAndWait();
            }else{
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("FAILED");
                alert2.setHeaderText(null);
                alert2.setContentText("Issue Operation Failed");
                alert2.showAndWait();
            }
        }else{
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("CANCELLED");
            alert1.setHeaderText(null);
            alert1.setContentText("Issue Operation Cancelled");
            alert1.showAndWait();
        }
    }

    public void loadBookInfo2(ActionEvent actionEvent) {

        ObservableList<String> issueData = FXCollections.observableArrayList();

        isReadyForSubmission = false;
        String id = bookID.getText();
        String qu = "SELECT * FROM ISSUE WHERE bookID = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        try{
            while(rs.next()){
                String mBookID = id;
                String mMemberID = rs.getString("memberID");
                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                int mRenewCount = rs.getInt("renew_count");

                issueData.add("Issue Date and Time : "+mIssueTime.toGMTString());
                issueData.add("Renew Count : " + mRenewCount);

                issueData.add("Book Information:-");
                qu = "SELECT * FROM BOOK WHERE ID= '"+ mBookID +"'";
                ResultSet r1 = databaseHandler.execQuery(qu);
                try{
                    while(r1.next()){
                        issueData.add("Book Name: "+ r1.getString("title"));
                        issueData.add("Book ID: "+ r1.getString("id"));
                        issueData.add("Book Author: "+ r1.getString("author"));
                        issueData.add("Book Publisher: "+ r1.getString("publisher"));

                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }

                qu = "SELECT * FROM MEMBER WHERE ID= '"+ mMemberID +"'";
                r1 = databaseHandler.execQuery(qu);
                issueData.add("Member Information:-");

                try{
                    while(r1.next()){
                        issueData.add("Name: "+ r1.getString("name"));
                        issueData.add("Mobile: "+ r1.getString("mobile"));
                        issueData.add("Email: "+ r1.getString("email"));

                    }

                    isReadyForSubmission = true;
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        issueDataList.getItems().setAll(issueData);
    }

    public void loadSubmissionOp(ActionEvent actionEvent) {
        if(!isReadyForSubmission)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to submit");
            alert.showAndWait();
            return;
        }
        Alert alert1 = new Alert (Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Confirm Submit Operation");
        alert1.setHeaderText(null);
        alert1.setContentText("Are you sure want to submit the book ?");

        Optional<ButtonType> response = alert1.showAndWait();
        if(response.get()==ButtonType.OK)
        {
            String id = bookID.getText();
            String ac1 = "DELETE FROM ISSUE WHERE bookID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET isAvail = TRUE WHERE id = '" + id + "'";

            if (databaseHandler.execAction(ac1) ){
                if( databaseHandler.execAction(ac2)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Book has been submitted");
                    alert.showAndWait();
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setHeaderText(null);
                alert.setContentText("Submission has failed");
                alert.showAndWait();
            }
        }else{
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("CANCELLED");
            alert2.setHeaderText(null);
            alert2.setContentText("Submit Operation Cancelled");
            alert2.showAndWait();
        }

    }

    public void loadRenewOp(ActionEvent actionEvent) {
        if(!isReadyForSubmission)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to renew");
            alert.showAndWait();
            return;
        }
        Alert alert1 = new Alert (Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Confirm renew Operation");
        alert1.setHeaderText(null);
        alert1.setContentText("Are you sure want to renew the book ?");

        Optional<ButtonType> response = alert1.showAndWait();
        if(response.get()==ButtonType.OK) {
            String id = bookID.getText();

            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP , renew_count = renew_count+1 WHERE bookID= '" + id + "'" ;

            if(databaseHandler.execAction(ac))
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Book has been renewed");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setHeaderText(null);
                alert.setContentText("Renew has failed");
                alert.showAndWait();
            }
        }else
        {
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("CANCELLED");
            alert2.setHeaderText(null);
            alert2.setContentText("Renew Operation Cancelled");
            alert2.showAndWait();
        }
    }

    public void handleMenuClose(ActionEvent actionEvent) {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    public void handleMenuAddBook(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/addbook/add_book.fxml","Add New Book");
    }

    public void handleMenuAddMember(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/addmember/member_add.fxml","Add New Member");
    }

    public void handleMenuViewBook(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml","Book List");
    }

    public void handleMenuViewMember(ActionEvent actionEvent) {
        loadWindow("/library/assistant/ui/listmember/member_list.fxml","Member List");
    }

    public void handleMenuFullScreen(ActionEvent actionEvent) {
        Stage stage = ((Stage)rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }
}
