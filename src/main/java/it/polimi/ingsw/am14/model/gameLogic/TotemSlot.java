package it.polimi.ingsw.am14.model.gameLogic;

public final class TotemSlot {
    private final Player player;
    private final char position;

    public TotemSlot(Player player, char position) {
        this.player = player;
        this.position = position;
    }

    public char getPosition(){
        return this.position;
    }

    public Player getPlayer() {
        return player;
    }
}