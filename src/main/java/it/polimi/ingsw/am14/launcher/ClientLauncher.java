package it.polimi.ingsw.am14.launcher;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import javafx.application.Application;

import java.io.IOException;

/**
 * ClientLauncher class is the entry point for the client application.
 * It determines whether to launch the GUI or TUI based on the provided arguments.
 */
public class ClientLauncher {
    /**
     * The main method serves as the entry point for the client application.
     * It checks the provided arguments to determine whether to launch the GUI or TUI.
     *
     * @param args Command-line arguments. The first argument should be either "cli" or "gui".
     */
    public static void main(String[] args) {

        String mode = args[0];
        try {
            switch (mode.toLowerCase()) {
                case "cli" -> {
                    try {
                        new TUIView().run();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    } catch (RuntimeException e) {
                        System.out.println("Exiting...");
                        System.exit(0);
                    }
                }
                case "gui" -> Application.launch(GUIView.class, args);
                default -> {
                    System.out.println("Invalid argument. Please use 'cli' for TUI or 'gui' for GUI.");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
