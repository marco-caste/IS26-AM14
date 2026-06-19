package it.polimi.ingsw.am14.model.gameLogic;

/**
 * <p>
 *     Enum representing the possible colors of the totems in the game. Each player chooses a totem color when joining a game, and this enum is used to represent those colors in a type-safe way.
 * </p>
 */
public enum TotemColor {
    WHITE,
    PURPLE,
    YELLOW,
    RED,
    BLUE;

    @Override
    public String toString() {
        return this.name();
    }
}
