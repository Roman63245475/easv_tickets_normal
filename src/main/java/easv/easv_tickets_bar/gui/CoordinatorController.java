package easv.easv_tickets_bar.gui;

import easv.easv_tickets_bar.be.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CoordinatorController implements IUserPanel{
    @FXML
    private VBox sideBar;
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

    public void onEventManClick(){
        switchTab("eventManagementBox");
    }

    public void onTicketManClick(){
        switchTab("ticketManagementBox");
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
