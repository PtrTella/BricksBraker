package brickbreaker.view;

import java.util.List;

import brickbreaker.model.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SetUpView extends ViewImpl {

    @FXML
    private AnchorPane root;

    @FXML private VBox vBoxMainContainer;
    
    @FXML
    private HBox hBoxNicknameInsertion;

    @FXML 
    private ImageView imgChoose;

    @FXML 
    private ImageView imgNickname;

    @FXML 
    private ComboBox<String> cbUsersList;
    
    @FXML 
    private Button btnAdd;

    private List<String> users;

    @Override
    public void init() {

        this.users = this.getController().getUserController().getUsersName();

        //Setting up the combo box.
        cbUsersList.getItems().addAll(this.users);
        cbUsersList.setPromptText("Type your nick");
        cbUsersList.setEditable(true);
    }

    @FXML
    public void switchToHome() {
        String nick = cbUsersList.getEditor().getText();

        if (nick.isEmpty()) {
            Dialog<String> d = new Dialog<>();
            d.setTitle("Empty user name.");
            d.setContentText("You cannot play without an user name!");
            d.getDialogPane().getButtonTypes().add(new ButtonType("Retry", ButtonData.BACK_PREVIOUS));
            d.showAndWait();
        } else {
            if (!this.users.contains(nick)) {
                User u = new User(nick);
                this.getController().getUserController().addUser(u);
                this.getController().setUser(nick);
            }
            ViewSwitcher.getInstance().switchView(getStage(), ViewType.HOME);
        }
    }

    @Override
    public void render() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }
}
