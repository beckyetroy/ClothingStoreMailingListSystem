package application.controller;

import application.model.Member;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    static AnchorPane root;
    static List<AnchorPane> anchor = new ArrayList<AnchorPane>();
    private static int sceneIndex = 0;

    public static Member user;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/Anchor.fxml"));

        anchor.add((AnchorPane)FXMLLoader.load(getClass().getResource("../view/HomePage.fxml"))); //index 0

        root.getChildren().add(anchor.get(0)); // initially set to MainScreen

        primaryStage.setTitle("Becky's Retail Mailing List System");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    private void init_app(){
        for(int i=0; i<anchor.size();i++){
            //can be used to initiate some details about each pane
        }
    }

    public static AnchorPane get_pane(int index){
        return anchor.get(index);
    }

    public static void set_pane(int index){
        if (index >= 0 && index < anchor.size()) {
            root.getChildren().remove(anchor.get(sceneIndex));  //remove the existing pane from the root
            root.getChildren().add(anchor.get(index));          //add the selected pane to the root
            sceneIndex=index;                                   //update the stored sceneIndex
        }
        else {
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Member getUser() {
        return user;
    }

    public static void setUser(Member user) {
        Main.user = user;
    }
}
