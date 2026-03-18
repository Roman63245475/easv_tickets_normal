package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AdminController implements IUserPanel{
    @FXML private VBox sideBar;
    @FXML private StackPane contentBox;
    private boolean isMenuOpen = false;
    private User user;

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
}
