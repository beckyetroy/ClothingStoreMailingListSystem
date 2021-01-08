package application.controller;

import application.model.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReadMessageController2 {

    @FXML
    private Label titleLabel;
    @FXML
    private Label senderLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private TextArea message;


    public void initData(Email email) {
        titleLabel.setText(email.getTitle());
        senderLabel.setText("Sent by: " + email.getUsername());

        if (email.getPriority() == 1) {
            priorityLabel.setText("Priority: High");
        } else if (email.getPriority() == 2) {
            priorityLabel.setText("Priority: Medium");
        } else if (email.getPriority() == 3) {
            priorityLabel.setText("Priority: Low");
        }

        dateLabel.setText(email.getDateTimeSent());
        message.setText(email.getMessageBody());
    }

    public void backToMainAdmin(ActionEvent e) throws Exception {
        Parent tableViewParent = FXMLLoader.load(getClass().getResource("../view/AdminScreen.fxml"));
        Scene tableViewScene = new Scene(tableViewParent);

        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
    }
}