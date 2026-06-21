package it.polimi.ingsw.am14.launcher;

import it.polimi.ingsw.am14.view.GUIView;
import it.polimi.ingsw.am14.view.TUIView;
import javafx.application.Application;

import java.io.IOException;

/**
 * The {@code ClientLauncher} class is responsible for launching the client application
 * in either Command Line Interface (CLI) or Graphical User Interface (GUI) mode based on
 * the provided command-line arguments.
 */
public class ClientLauncher {

    /**
     * Starts the client application based on the provided command-line arguments.
     * The first argument specifies the mode ("cli" for TUI or "gui" for GUI).
     *
     * @param args command-line arguments where the first argument specifies the mode.
     */
    public static void startClient(String[] args) {

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
