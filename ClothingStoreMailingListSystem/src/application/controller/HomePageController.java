package application.controller;

import application.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import java.nio.channels.FileChannel;
import java.util.ResourceBundle;


import application.controller.Main;
import application.model.SystemModel;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

import javafx.scene.control.TextField;

import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;
import static javafx.collections.FXCollections.observableArrayList;

public class HomePageController implements Initializable {

    protected SystemModel system;

    public static String loggedInUser;

    @FXML private CheckBox admin;
    @FXML private CheckBox customer;
    @FXML private TextField email;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private PasswordField repeat_password;
    @FXML private TextArea txtAreaFeedback;
    @FXML private TextArea txtAreaFeedback2;
    @FXML private ComboBox group;

    @FXML private TextField logInUsername;
    @FXML private PasswordField logInPassword;

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {

        system = new SystemModel();

        try {
            system.loadMembers();
        }
        catch (Exception e) {
            System.out.println("Error loading Members");
        }

        try {
            system.loadGroups();
            system.loadGroupMaps();
        }
        catch (Exception e) {
            System.out.println("Error loading Groups");
        }

        ObservableList<String> allGroups =
                observableArrayList(system.groupMap.keySet());

        group.setItems(allGroups);

        email.setText(null);
        username.setText(null);
        password.setText(null);
        repeat_password.setText(null);
        group.setValue(null);

    }

    public void handleRegisterBtn(ActionEvent e) throws Exception {
        if(email.getText() == null || username.getText() == null || password.getText() == null ||
                repeat_password.getText() == null || group.getValue() == null){
            txtAreaFeedback.setText("Please fill in all fields");
        }
        else if((password != null) && (password.getText().length()<8) ){
            txtAreaFeedback.setText("Password must be at least 8 characters");
        }
        else if(!password.getText().equals(repeat_password.getText())){
            txtAreaFeedback.setText("Password and Verified Password must match");
        }
        else if(!email.getText().contains("@") || !email.getText().contains(".")){
            txtAreaFeedback.setText("Invalid email format");
        }
        else if(admin.isSelected() && customer.isSelected()){
            txtAreaFeedback.setText("Please select just one user type.");
        }
        else if(!admin.isSelected() && !customer.isSelected()){
            txtAreaFeedback.setText("Please select a user type.");
        }

        else if (system.registerMember(email.getText(), username.getText(), password.getText(), admin.isSelected(),
                group.getValue().toString()))
        {
            txtAreaFeedback.setText("Registration Successful.");
            email.setText("");
            username.setText("");
            password.setText("");
            repeat_password.setText("");
            group.setValue(null);
            admin.setSelected(false);
            customer.setSelected(false);
        }

        else if (!system.registerMember(email.getText(), username.getText(), password.getText(), admin.isSelected(),
                group.getValue().toString())) {
            txtAreaFeedback.setText("Username and/or email address already in use. Please try again.");
        }

        else {
            System.out.println("Registration failed");
        }
    }

    public void handleLoginBtn(ActionEvent e) throws Exception {
        if (system.logInUserMember(logInUsername.getText(), logInPassword.getText())) {
            System.out.println("Successful Login");
            loggedInUser = logInUsername.getText();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/UserScreen.fxml"));
            Parent tableViewParent = loader.load();

            Scene tableViewScene = new Scene(tableViewParent);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();

            window.setScene(tableViewScene);
            window.show();
        }

        else if (system.logInAdminMember(logInUsername.getText(), logInPassword.getText())) {
            System.out.println("Successful Login");
            loggedInUser = logInUsername.getText();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/AdminScreen.fxml"));
            Parent tableViewParent = loader.load();

            Scene tableViewScene = new Scene(tableViewParent);

            Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();

            window.setScene(tableViewScene);
            window.show();
        }
        else {
            txtAreaFeedback2.setText("Unsuccessful Login. Please try again.");
            password.clear();
        }
    }

    public void handleClearBtn(ActionEvent e) throws Exception {
        try {
            email.setText("");
            username.setText("");
            password.setText("");
            repeat_password.setText("");
            group.setValue(null);
            admin.setSelected(false);
            customer.setSelected(false);
            logInUsername.setText("");
            logInPassword.setText("");
            txtAreaFeedback.setText("");
            txtAreaFeedback2.setText("");
        }
        catch (NullPointerException exception) {

        }
    }


}
