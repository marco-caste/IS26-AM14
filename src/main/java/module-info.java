module it.polimi.ingsw.am14 {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.polimi.ingsw.am14 to javafx.fxml;
    exports it.polimi.ingsw.am14;
}