package it.polimi.ingsw.am14;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import javafx.application.Application;

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
//                    system exit;
//                    grateful shutdown...
                }
            } else if (mode.equalsIgnoreCase("gui")) {
                Application.launch(GUIView.class, args);
            } else {
                // TODO: ERROR
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
