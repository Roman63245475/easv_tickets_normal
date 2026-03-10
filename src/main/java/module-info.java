module easv.easv_tickets_bar {
    requires javafx.controls;
    requires javafx.fxml;


    opens easv.easv_tickets_bar to javafx.fxml;
    exports easv.easv_tickets_bar;
}