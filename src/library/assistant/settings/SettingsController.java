package library.assistant.settings;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    public TextField nDaysWithoutFine;
    public TextField finePerDay;
    public TextField username;
    public PasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initDefaultValues();

    }

    private void initDefaultValues() {
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(String.valueOf(preferences.getUsername()));
        password.setText(String.valueOf(preferences.getPassword()));

    }

    public void handleSaveButtonAction(ActionEvent actionEvent) {
        Preferences preferences = new Preferences();
        preferences.setnDaysWithoutFine(Integer.parseInt(nDaysWithoutFine.getText()));
        preferences.setFinePerDay(Float.parseFloat(finePerDay.getText()));
        preferences.setUsername(username.getText());
        preferences.setPassword(password.getText());

        preferences.writePreferenceToFile(preferences);
    }

    public void handleCancelButtonAction(ActionEvent actionEvent) {
        ((Stage)nDaysWithoutFine.getScene().getWindow()).close();
    }


}
