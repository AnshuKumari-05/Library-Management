package library.assistant.ui.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.settings.Preferences;
import library.assistant.util.LibraryAssistantUtil;
import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.logging.log4j.*;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public TextField username;
    public PasswordField password;
    public Label titleLabel;
    Preferences preference;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preference = Preferences.getPreferences();
    }

    public void handleLoginButtonAction(ActionEvent actionEvent) {
        String uname =username.getText();
        String pword = DigestUtils.shaHex(password.getText());

        if (uname.equals(preference.getUsername()) && pword.equals(preference.getPassword())) {
            closeStage();
            loadMain();
           // LOGGER.log(Level.INFO, "User successfully logged in {}", uname);
        }
        else {
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }

    }

    public void handleCancelButtonAction(ActionEvent actionEvent) {
        System.exit(0);
    }
    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }

    void loadMain() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        }
        catch (IOException ex) {
           //LOGGER.log(Level.ERROR, "{}", ex);
        }
    }


}
