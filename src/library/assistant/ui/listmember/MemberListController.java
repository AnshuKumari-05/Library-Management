package library.assistant.ui.listmember;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addmember.MemberAddController;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemberListController implements Initializable {
    ObservableList<MemberListController.Member> list = FXCollections.observableArrayList();
    public AnchorPane rootPane;
    public TableView tableView;
    public TableColumn nameCol;
    public TableColumn idCol;
    public TableColumn mobileCol;
    public TableColumn emailCol;

    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

    }

    private void loadData() {
        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.execQuery(qu);
       // System.out.println("12\n");

        try {
            while (rs.next()) {
                String Name = rs.getString("name");
                String id = rs.getString("id");
                String Mobile = rs.getString("mobile");
                String Email = rs.getString("email");

                list.add(new Member(Name, id, Mobile, Email));
               // System.out.println("Member added in list");
                //System.out.println(Mobile);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(list);
        tableView.setItems(list);
    }

    public void handleRefresh(ActionEvent actionEvent) {
        loadData();
    }

    public void handleMemberEdit(ActionEvent actionEvent) {
        Member selectedForEdit = (Member) tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null)
        {
            AlertMaker.showErrorMessage("No Book Selected","Please select a member for edit first");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"));
            Parent parent = loader.load();

            MemberAddController controller = (MemberAddController)loader.getController();
            controller.inflateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
            //System.out.println("1\n");
            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void handleMemberDelete(ActionEvent actionEvent) {
    }

    public static class Member{
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;

        public String getId() {
            return id.get();
        }

        public String getName() {
            return name.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

        public Member(String name,String id, String mobile, String email) {
            this.name = new SimpleStringProperty(name);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
            this.id = new SimpleStringProperty(id);
        }

    }
}
