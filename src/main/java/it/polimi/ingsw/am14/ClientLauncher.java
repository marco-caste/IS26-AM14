package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.view.TUIView;

import java.io.IOException;

public class ClientLauncher {
    public static void main(String[] args) {

        String mode = args[0];
        try {
            if (mode.equalsIgnoreCase("cli")) {
                // TUI viewType = TUI
                try {
                    new TUIView().run();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (RuntimeException e) {
                    System.out.println("Exiting...");
                }
            } else if (mode.equalsIgnoreCase("gui")) {
                // GUI viewType = GUI
            } else if (mode.equalsIgnoreCase("gui-fxml")) {
                // GUI FXML viewType = GUI_FXML
            } else {
                // ERROR
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
