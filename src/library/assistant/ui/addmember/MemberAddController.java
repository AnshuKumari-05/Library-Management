package library.assistant.ui.addmember;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.listmember.MemberListController;

import java.net.URL;
import java.util.ResourceBundle;

public class MemberAddController implements Initializable {

    public TextField name;
    public TextField id;
    public TextField mobile;
    public TextField email;
    public Button saveButton;
    public Button cancelButton;
    private Boolean isInEditMode = Boolean.FALSE;

    DatabaseHandler databaseHandler;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        databaseHandler = DatabaseHandler.getInstance();
    }

    public void addMember(ActionEvent actionEvent) {
        String Name = name.getText();
        String ID = id.getText();
        String Mobile = mobile.getText();
        String Email = email.getText();

        if(ID.isEmpty() || Name.isEmpty() || Mobile.isEmpty() || Email.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all fields");
            alert.showAndWait();
            return;
        }

        if(isInEditMode){
            handleUpdateMember();
            return;
        }
        String qu = "INSERT INTO MEMBER VALUES ("+
                "'" + ID + "'," +
                "'" + Name+ "'," +
                "'" + Mobile + "'," +
                "'" + Email + "'" + ")";

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

    private void handleUpdateMember() {
        MemberListController.Member member = new MemberListController.Member(id.getText(), name.getText(), mobile.getText(), email.getText());
        if (databaseHandler.updateMember(member)) {
           // System.out.println("IN HANDLEUPDATE\n");
            // AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Success", "Update complete");
            AlertMaker.showSimpleAlert("Success","Member updated");
        } else {
            // AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed", "Could not update data");
            AlertMaker.showErrorMessage("Failed","Cannot be updated");
        }

    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage)name.getScene().getWindow();
        stage.close();
    }

    public void inflateUI(MemberListController.Member member) {
        name.setText(member.getName());
        id.setText(member.getId());
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());

        isInEditMode = Boolean.TRUE;
        id.setEditable(false);
    }
}
