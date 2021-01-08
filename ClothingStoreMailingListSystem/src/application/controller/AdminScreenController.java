package application.controller;

import application.model.Email;
import application.model.Group;
import application.model.Member;
import application.model.SystemModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;

public class AdminScreenController implements Initializable {

    //Tab 1
    @FXML private TextField newListName;

    @FXML private TextField updateSearch;
    @FXML private TextField updateField;
    @FXML private Button updateListBtn;

    @FXML private TextField removeSearch;
    @FXML private TextField removeField;
    @FXML private Button removeListBtn;

    @FXML private Label addFeedback;
    @FXML private Label editFeedback;
    @FXML private Label removeFeedback;

    //Tab 2
    @FXML private ComboBox<String> fullListCombo;
    @FXML private Button readMessageBtn;

    @FXML private TableView table;
    @FXML private TableColumn<Email, String> dateColumn;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> titleColumn;
    @FXML private TableColumn<Email, Integer> priorityColumn;

    @FXML private TextField title;
    @FXML private TextArea body;
    @FXML private Label sendFeedback;
    @FXML private ComboBox<String> mailingListOption;

    @FXML private RadioButton highPriority;
    @FXML private RadioButton mediumPriority;
    @FXML private RadioButton lowPriority;

    //Tab 3
    @FXML private ComboBox<String> mailingListCombo;
    @FXML private TextArea usersByList;

    @FXML private ComboBox<String> mailingListCombo2;
    @FXML private TextField removeUserFromList;
    @FXML private TextField removeFromListField;
    @FXML private Button removeUserFromListBtn;
    @FXML private Label removeFromListFeedback;

    @FXML private TextField removeUserSearch;
    @FXML private TextArea userDetails;
    @FXML private Label removeUserFeedback;
    @FXML private Button removeUserBtn;

    protected SystemModel system = new SystemModel();

    public void initialize(URL location, ResourceBundle resources) {

        updateListBtn.setDisable(true);
        removeListBtn.setDisable(true);
        removeUserFromListBtn.setDisable(true);
        removeUserBtn.setDisable(true);

        try {
            system.loadMembers();
        } catch (Exception e) {
            System.out.println("Error loading Members");
        }

        try {
            system.loadGroups();
            system.loadGroupMaps();
        } catch (Exception e) {
            System.out.println("Error loading Groups");
        }

        try {
            system.loadEmails();
        } catch (Exception e) {
            System.out.println("Error loading Emails");
        }

        dateColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("dateTimeSent"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("username"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("title"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<Email, Integer>("priority"));

        ObservableList<String> allGroups =
                observableArrayList(system.groupMap.keySet());


        table.setItems(null);
        fullListCombo.setItems(allGroups);
        mailingListOption.setItems(allGroups);
        mailingListOption.setValue("Nike");
        mailingListCombo.setItems(allGroups);
        mailingListCombo2.setItems(allGroups);

        table.setPlaceholder(new Label("No messages yet."));
        table.getSortOrder().add(priorityColumn);
        table.sort();

        readMessageBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
    }

    public void handleCreateBtn(ActionEvent e) throws Exception {
        HashSet<Member> memberSet = null;

        if (!(newListName.equals(null))) {
            try {
                if (system.addGroup(newListName.getText(), memberSet)) {
                    addFeedback.setText("List added successfully.");
                    newListName.setText(null);
                }
                else if (!system.addGroup(newListName.getText(), memberSet)) {
                    addFeedback.setText("Group already exists.");
                }
            } catch (Exception exception) {
                System.out.println("Error adding Group.");
            }
        }
        else {
            addFeedback.setText("No list name entered.");
        }

        system.saveGroups();
        system.saveGroupMaps();

    }

    public void handleUpdateSearchBtn(ActionEvent e) throws Exception {
        Set<String> groups = system.groupMap.keySet();

        for (String group : groups) {
            if (updateSearch.getText().equals(group)) {
                updateField.setText(group);
                updateListBtn.setDisable(false);
                editFeedback.setText("Edit list name here.");
            }
        }
        if (updateListBtn.isDisabled()) {
            editFeedback.setText("List not found. Please try again.");

        }
    }

    public void handleUpdateListBtn(ActionEvent e) throws Exception {
        system.updateGroup(updateSearch.getText(), updateField.getText());
        editFeedback.setText("List updated successfully.");
        updateSearch.setText(null);
        updateField.setText(null);
        updateListBtn.setDisable(true);
    }

    public void handleRemoveSearchBtn(ActionEvent e) throws Exception {
        Set<String> groups = system.groupMap.keySet();

        for (String group : groups) {
            if (removeSearch.getText().equals(group)) {
                removeField.setText(group);
                removeListBtn.setDisable(false);
                removeFeedback.setText("Confirm delete.");
            }
        }

        if (removeListBtn.isDisabled()) {
            removeFeedback.setText("List not found. Please try again.");
        }
    }

    public void handleRemoveListBtn(ActionEvent e) throws Exception {
        system.removeGroup(removeSearch.getText());
        removeFeedback.setText("List deleted successfully.");
        removeSearch.setText(null);
        removeField.setText(null);
        removeListBtn.setDisable(true);
    }

    public void changedGroupCombo(ActionEvent e) throws Exception
    {
        system.loadGroups();
        system.loadGroupMaps();
        system.loadEmails();
        try {
            table.setItems(FXCollections.observableArrayList(system.groupEmailMap.get(fullListCombo.getValue())));
            table.getSortOrder().add(priorityColumn);
            table.sort();
        } catch (NullPointerException exception) {
            table.setItems(null);
        }
    }

    public void changeSceneToReadMessage(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/ReadMessage2.fxml"));
        Parent tableViewParent = loader.load();

        Scene tableViewScene = new Scene(tableViewParent);

        ReadMessageController2 controller = loader.getController();
        Email emailToDisplay =(Email) table.getSelectionModel().getSelectedItem();
        if(emailToDisplay == null)
            return;

        controller.initData(emailToDisplay);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
    }

    public void handleSendBtn(ActionEvent e) throws Exception {
        int priority = 0;

        if (highPriority.isSelected()) {
            priority = 1;
        }

        else if (mediumPriority.isSelected()) {
            priority = 2;
        }

        else if (lowPriority.isSelected()) {
            priority = 3;
        }

        if(title.getText() == null || body.getText() == null || mailingListOption.getValue() == null
        || (!highPriority.isSelected() && !mediumPriority.isSelected() && !lowPriority.isSelected() ))
                {
            sendFeedback.setText("Please fill in all fields");
        }
        else if((highPriority.isSelected() && mediumPriority.isSelected()) || (highPriority.isSelected()
            && lowPriority.isSelected()) || (mediumPriority.isSelected() && lowPriority.isSelected())
            || (highPriority.isSelected() && mediumPriority.isSelected() && lowPriority.isSelected())) {
            sendFeedback.setText("Please select just one priority.");
        }

        else if (system.sendEmail(HomePageController.loggedInUser, title.getText(), priority,
                body.getText(), mailingListOption.getValue().toString()))
        {
            sendFeedback.setText("Email sent successfully.");
            title.setText("");
            body.setText("");
            mailingListOption.setValue(null);
            highPriority.setSelected(false);
            mediumPriority.setSelected(false);
            lowPriority.setSelected(false);
        }

        else if (!system.sendEmail(HomePageController.loggedInUser, title.getText(), priority,
                body.getText(), mailingListOption.getValue())) {
            sendFeedback.setText("There was a problem. Please try again.");
        }

        else {
            System.out.println("Sending failed");
        }
    }

    public void changedMailingListCombo(ActionEvent e) throws Exception {
        system.loadGroups();
        system.loadGroupMaps();
        system.loadEmails();
        try {
            Set<String> members = system.memberMap.keySet();
            Set<Member> memberObjects = new HashSet<>();

            for (String member : members) {
                memberObjects.add(system.memberMap.get(member));
            }

            Set<String> filteredMembers = new HashSet<>();

            for (Member filteringMembers : memberObjects) {
                    if (filteringMembers.getGroups().contains(mailingListCombo.getValue())) {
                        filteredMembers.add("- Username: " + filteringMembers.getUsername() +
                                " | Email Address: " + filteringMembers.getEmail() + ".\n");
                    }
                    else {

                    }
                }
            if (!filteredMembers.isEmpty()) {
                usersByList.setText(filteredMembers.toString().replace("[", "")
                        .replace("]", "").replace(", ", ""));
            }
            else if (filteredMembers.isEmpty()){
                usersByList.setText("There are no members for this list.");
            }

        }
        catch (NullPointerException exception) {
            System.out.println("There are no members registered.");
            usersByList.setText("There are no members for this list.");
        }
    }

    public void handleSearchUserFromListBtn (ActionEvent e) throws Exception {
        Set<String> members = system.memberMap.keySet();

        for (String member : members) {
            if (removeUserFromList.getText().equals(member)) {
                removeFromListField.setText(member);
                removeUserFromListBtn.setDisable(false);
                removeFromListFeedback.setText("Confirm subscription removal.");
            }
        }

        if (removeListBtn.isDisabled()) {
            removeFeedback.setText("User not found. Please try again.");
        }
    }

    public void handleRemoveUserFromListBtn(ActionEvent e) throws Exception {
        system.loadGroups();
        system.loadGroupMaps();
        system.loadEmails();
        system.loadMembers();

        Member member = system.memberMap.get(removeFromListField.getText());

        if (member.getGroups().contains(mailingListCombo2.getValue())) {
            if (system.removeUserFromList(member, mailingListCombo2.getValue())) {
                removeFromListFeedback.setText("User unsubscribed successfully");
                removeUserFromList.setText(null);
                removeFromListField.setText(null);
                removeUserFromListBtn.setDisable(true);
            }
            else if (!member.getGroups().contains(mailingListCombo2.getValue())) {
                removeFromListFeedback.setText("User is already unsubscribed from this list.");
            }
        }

        system.saveGroups();
        system.saveMembers();
        system.saveGroupMaps();
        system.saveEmails();
    }

    public void handleRemoveUserSearchBtn(ActionEvent e) throws Exception {
        Set<String> members = system.memberMap.keySet();

        for (String member : members) {
            if (removeUserSearch.getText().equals(member)) {
                userDetails.setText(system.memberMap.get(member).toString().replace
                        ("null", "N/A"));
                removeUserBtn.setDisable(false);
                removeUserFeedback.setText("Confirm delete.");
            }
        }

        if (removeUserBtn.isDisabled()) {
            removeUserFeedback.setText("User not found. Please try again.");
        }
    }

    public void handleRemoveUserBtn(ActionEvent e) throws Exception {
        system.removeUser(removeUserSearch.getText());
        removeUserFeedback.setText("User deleted successfully.");
        removeUserSearch.setText(null);
        userDetails.setText(null);
        removeUserBtn.setDisable(true);
    }

    public void handleSave(ActionEvent e) throws Exception {
        system.saveEmails();
        system.saveMembers();
        system.saveGroups();
        system.saveGroupMaps();
        System.out.println("Save successful.");
    }

    public void handleLoad(ActionEvent e) throws Exception {
        system.loadEmails();
        system.loadMembers();
        system.loadGroups();
        system.loadGroupMaps();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/AdminScreen.fxml"));
        Parent tableViewParent = loader.load();

        Scene tableViewScene = new Scene(tableViewParent);

        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();

        System.out.println("Load successful.");
    }

    public void handleLogOutBtn(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/HomePage.fxml"));
        Parent tableViewParent = loader.load();

        Scene tableViewScene = new Scene(tableViewParent);

        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
    }
}
