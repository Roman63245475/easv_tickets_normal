package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AdminController implements IUserPanel, IRefreshable{
    @FXML private VBox sideBar;
    @FXML private StackPane contentBox;

    private boolean isMenuOpen = false;
    private User user;
    private OpenWindow openWindow;



    public AdminController(){
        this.openWindow = new OpenWindow();
    }

    public void menuSlide(){
        UIHelper.sideBarAnimation(isMenuOpen, sideBar, () -> isMenuOpen = !isMenuOpen);
    }

    @FXML
    public void switchTab(String name){
        for(Node n : contentBox.getChildren()){
            if (n instanceof VBox && n.getId().equals(name)) n.setVisible(true);
            else n.setVisible(false);
        }
    }

    public void onUserManClick(){
        switchTab("userManagementBox");
    }

    public void onEventManClick(){
        switchTab("eventManagementBox");
    }


    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void addUser(){
        String fileName = "create_user.fxml";
        String title = "Create new user";
        try {
            openWindow.openNewWindow(fileName, title, this.user, true, this);
        } catch (IOException e) {
            System.out.println("here needs to be an alert");
        }


    }

    @Override
    public void refreshTable() {

    }
}
