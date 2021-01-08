package application.controller;

import application.model.Email;
import application.model.Group;
import application.model.Member;
import application.model.SystemModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javafx.collections.FXCollections.*;

public class UserScreenController implements Initializable {

    protected SystemModel system = new SystemModel();

    @FXML private DatePicker dateFilter;
    @FXML private ComboBox<String> comboGroup;
    @FXML private ComboBox<String> fullListCombo;
    @FXML private TextField searchItem;

    @FXML private TableView table;
    @FXML private TableColumn<Email, String> dateColumn;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> titleColumn;
    @FXML private TableColumn<Email, Integer> priorityColumn;

    @FXML private Button readMessageButton;

    public void initialize(URL location, ResourceBundle resources) {

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

        try {
            system.loadEmails();
        }
        catch (Exception e) {
            System.out.println("Error loading Emails");
        }

        dateColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("dateTimeSent"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("username"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("title"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<Email, Integer>("priority"));

        Set<String> groups = system.memberMap.get(HomePageController.loggedInUser).getGroups();

        Set<Email> memberGroupsSet = new HashSet<>();

        for (String group : groups) {
            Set<Email> e = system.groupEmailMap.get(group);
            try {
                memberGroupsSet.addAll(e);
            }
            catch (NullPointerException exception) {

            }
        }

        ObservableList<Email> data =
                null;

        try {
            data = observableArrayList(memberGroupsSet);
        }
        catch (NullPointerException e) {
        }

        ObservableList<String> memberGroups =
                observableArrayList(system.memberMap.get(HomePageController.loggedInUser).getGroups());

        ObservableList<String> allGroups =
                observableArrayList(system.groupMap.keySet());

        memberGroups.add(0, "All");

        table.setItems(data);
        comboGroup.setItems(memberGroups);
        fullListCombo.setItems(allGroups);

        table.setPlaceholder(new Label("No messages yet."));
        table.getSortOrder().add(priorityColumn);
        table.sort();

        comboGroup.setValue("All");
        readMessageButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
    }

    public void changedGroupCombo(ActionEvent e) throws Exception
    {
        try {
            if (!comboGroup.getValue().equals("All")) {
                table.setItems(FXCollections.observableArrayList(system.groupEmailMap.get(comboGroup.getValue())));
                table.getSortOrder().add(priorityColumn);
                table.sort();
                dateFilter.setValue(null);
            }
            else if (comboGroup.getValue().equals("All")){
                Set<String> groups = system.memberMap.get(HomePageController.loggedInUser).getGroups();

                Set<Email> memberGroupsSet = new HashSet<>();

                for (String group : groups) {
                    Set<Email> email = system.groupEmailMap.get(group);
                    try {
                        memberGroupsSet.addAll(email);
                    }
                    catch (NullPointerException exception) {
                    }
                }

                ObservableList<Email> data =
                        observableArrayList(memberGroupsSet);

                table.setItems(data);
                table.getSortOrder().add(priorityColumn);
                table.sort();
            }
        } catch (NullPointerException exception) {
            table.setItems(null);
        }
    }


    public void filteredDate(ActionEvent e) throws Exception
    {
        comboGroup.setValue("All");
        searchItem.setText(null);

        Set<String> groups = system.memberMap.get(HomePageController.loggedInUser).getGroups();

        Set<Email> filteredGroupsSet = new HashSet<>();

        for (String group : groups) {
            Set<Email> email = system.groupEmailMap.get(group);
            try {
                filteredGroupsSet.addAll(email);
            }
            catch (NullPointerException exception) {
            }
        }
        ObservableList<Email> data = observableArrayList(filteredGroupsSet);
        Set<Email> dateData = new HashSet<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");

        try {
            for (Email email : data) {
                if (email.getDateTimeSent().contains(dateFilter.getValue().format(formatter))) {
                    try {
                        dateData.add(email);
                    } catch (NullPointerException exception) {
                    }
                }
            }
            ObservableList<Email> dateDataList =
                    observableArrayList(dateData);

            table.setItems(dateDataList);
            table.getSortOrder().add(priorityColumn);
            table.sort();
        }
        catch (NullPointerException exception) {

        }
    }

    public void handleSearchBtn(ActionEvent e) {
        Set<String> groups = system.memberMap.get(HomePageController.loggedInUser).getGroups();

        Set<Email> filteredGroupsSet = new HashSet<>();

        for (String group : groups) {
            Set<Email> emails = system.groupEmailMap.get(group);
            try {
                filteredGroupsSet.addAll(emails);
            }
            catch (NullPointerException exception) {

            }

            Set <Email> toRemove = new HashSet<>();

            try {
                for (Email email : filteredGroupsSet) {
                    if (!(email.getMessageBody().toUpperCase().contains(searchItem.getText().toUpperCase()) ||
                            email.getTitle().toUpperCase().contains(searchItem.getText().toUpperCase()))) {
                        toRemove.add(email);
                    }
                }
                filteredGroupsSet.removeAll(toRemove);
            }
            catch (NullPointerException exception) {
                table.setItems(null);
            }
        }
        ObservableList<Email> data =
                observableArrayList(filteredGroupsSet);
        table.setItems(data);
        table.getSortOrder().add(priorityColumn);
        table.sort();
        comboGroup.setValue("All");
    }

    public void changeSceneToReadMessage(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/ReadMessage.fxml"));
        Parent tableViewParent = loader.load();

        Scene tableViewScene = new Scene(tableViewParent);

        ReadMessageController controller = loader.getController();
        Email emailToDisplay =(Email) table.getSelectionModel().getSelectedItem();
        if(emailToDisplay == null)
            return;

        controller.initData(emailToDisplay);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
    }

    public void pressHomeBtn(ActionEvent e) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/HomePage.fxml"));
        Parent tableViewParent = loader.load();

        Scene tableViewScene = new Scene(tableViewParent);

        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
    }

    public boolean handleSubscribeBtn(ActionEvent e) throws Exception {
        try {
            Set <String> groups = system.memberMap.get(HomePageController.loggedInUser).getGroups();
            if (!groups.contains(fullListCombo.getValue())) {
                groups.add(fullListCombo.getValue());
                System.out.println("List added successfully.");
            }
            else {
                System.out.println("List already subscribed to.");
                return false;
            }
        }
        catch (NullPointerException exception) {
            System.out.println("Error.");
        }
        system.saveMembers();
        system.loadMembers();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/UserScreen.fxml"));
        Parent tableViewParent = loader.load();

        Scene tableViewScene = new Scene(tableViewParent);

        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
        return true;
    }
}


