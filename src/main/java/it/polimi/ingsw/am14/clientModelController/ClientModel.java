package it.polimi.ingsw.am14.clientModelController;

import it.polimi.ingsw.am14.view.ViewType;
import it.polimi.ingsw.am14.view.viewEvents.ViewEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.am14.utils.Constants.*;
/**
 * The core localized data container on the client side, implementing the Observer Pattern.
 * <p>
 * This class mirrors the server-side state to support fluid visual rendering without continuous
 * network polling. It stores current board configurations (grid rows containing card unique IDs),
 * information regarding the local user ({@code homePlayer}), structural registries of opponents,
 * and localized grid tracking structures (such as {@code offersPath} sub-mappings per player count).
 * It also hosts local business validation methods to check turn sequencing and row draft eligibility.
 * </p>
 */
public class ClientModel {

    private static final Map<Integer, List<Character>> offersPath = Map.of(
            2, List.of('B','C','E','F'),
            3, List.of('B','C','D','E','F'),
            4, List.of('B','C','D','E','F','G'),
            5, List.of('A','B','C','D','E','F','G')
            );

    private List<ViewType> observers;
    private ClientPlayer homePlayer;
    private ArrayList<ClientPlayer> otherPlayers;
    private ArrayList<String> playerOrderInTurnOrderCard;
    private ArrayList<ArrayList<Integer>> board;
    private Map<Character, String> positionToNickname;
    private ArrayList<String> finalRank;
    private List<String> globalRank;
    private int turnOrderCardIndex; // it's needed to build the right TurnorderCard in the view, it depends on this index.
    private int gameId;
    private int numPlayers; // it's needed to build the right offersPath in the View.
    private String message;
    private int age;
    private ClientState state;
    /**
     * Constructs an empty {@code ClientModel} instance initializing local structures,
     * mapping placeholders, and default boundary indicators.
     */
    public ClientModel() {
        this.observers = new ArrayList<>();
        this.homePlayer = null;
        this.otherPlayers = new ArrayList<>();
        this.playerOrderInTurnOrderCard = new ArrayList<>();
        this.board = new ArrayList<>();
        this.positionToNickname = new HashMap<>();
        this.turnOrderCardIndex = 0;
        this.gameId = -1;
        this.numPlayers = 0;
        this.message = "";
        this.state = null;
    }
    /**
     * Registers a view rendering framework observer into the internal notification dispatch registry.
     *
     * @param view the user interface rendering skin component subscribing to state updates
     */
    //Observer methods
    public void registerObserver(ViewType view) {
        observers.add(view);
    }

    /**
     * Dispatches custom event notifications down to all active UI/CLI monitors to force a UI repaint.
     *
     * @param viewEvent the specific event envelope encapsulating the nature of the update action
     */
    public void notifyObservers(ViewEvent viewEvent){
        for(ViewType observer : observers){
            observer.update(this, viewEvent);
        }
    }


    //GETTERS
    public int getTurnOrderCardIndex() {
        return turnOrderCardIndex;
    }
    /**
     * Searches active registries to locate a player profile matching the requested nickname.
     *
     * @param nickname the textual username signature targeted for lookup
     * @return a matched {@link ClientPlayer} profile entity, or {@code null} if no matches are found
     */
    public ClientPlayer getPlayerByNickname(String nickname) {
        if (homePlayer != null && homePlayer.getNickname().equals(nickname)) {
            return homePlayer;
        }
        for (ClientPlayer player : otherPlayers) {
            if (player.getNickname().equals(nickname)) {
                return player;
            }
        }
        return null;
    }

    public ArrayList<ArrayList<Integer>> getBoard() {
        return board;
    }

    public String getMessage() {
        return message;
    }

    public int getAge() {
        return this.age;
    }

    public int getNumPlayers(){
        return this.numPlayers;
    }

    public ArrayList<Character> getOffersPath() {
        return new ArrayList<>(offersPath.get(numPlayers));
    }

    public ClientPlayer getHomePlayer (){
        return this.homePlayer;
    }
    /**
     * Safe lookup helper that returns the exact text nickname string mapping to the home profile.
     *
     * @return player username text string, or empty string if unassigned
     */
    public String getHomePlayerNickname(){
        return (this.homePlayer == null) ?
                "" : this.homePlayer.getNickname();

    }

    public ArrayList<ClientPlayer> getOtherPlayers() {
        return this.otherPlayers;
    }
    /**
     * Combines home parameters with external opponent arrays to provide a master list tracking all profiles.
     *
     * @return unified list containing all participant profiles
     */
    public ArrayList<ClientPlayer> getAllPlayers() {
        ArrayList<ClientPlayer> allPlayers = new ArrayList<>();
        allPlayers.add(homePlayer);
        allPlayers.addAll(otherPlayers);
        return allPlayers;
    }

    public int getGameId(){
        return this.gameId;
    }

    public int getPlayerPositionInTurnOrderCard(String nickname){
        return playerOrderInTurnOrderCard.indexOf(nickname);
        // if not present returns -1;
    }

    public ArrayList<String> getFinalRank() {
        return finalRank;
    }

    public List<String> getGlobalRank() {
        return globalRank;
    }

    public ArrayList<String> getPlayerOrderInTurnOrderCard() {
        return playerOrderInTurnOrderCard;
    }

    public Map<Character, String> getPositionToNickname() {
        return new HashMap<>(positionToNickname);
    }
    /**
     * Linear search utility that scans position entries to extract the specific slot coordinate key
     * claimed by a player nickname.
     *
     * @param nickname target user signature text
     * @return slot character identifier code, or {@code null} if unmapped
     */
    //Helper methods to get player positions from the positionToNickname map
    public Character getPlayerPosition(String nickname) {
        for (Map.Entry<Character, String> entry : positionToNickname.entrySet()) {
            if (entry.getValue().equals(nickname)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Character getHomePlayerPosition() {
        return getPlayerPosition(homePlayer.getNickname());
    }

    //SETTERS
    public void setMessage(String message) {
        this.message = message;
    }

    public void setPlayerOrderInTurnOrderCard(ArrayList<String> playerOrderInTurnOrderCard) {
            this.playerOrderInTurnOrderCard = playerOrderInTurnOrderCard;
    }

    public void setTurnOrderCardIndex(int turnOrderCardIndex) {
        this.turnOrderCardIndex = turnOrderCardIndex;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void setHomePlayer(ClientPlayer homePlayer) {
        this.homePlayer = homePlayer;
    }

    public void setBoard(ArrayList<ArrayList<Integer>> board) {
        this.board = board;
    }

    public void setHomePlayerTotemColor(String totemColor){
        homePlayer.setTotemColor(totemColor);
    }

    public void setFinalRank(ArrayList<String> finalRank) {
        this.finalRank = finalRank;
    }
    public void setGlobalRank(List<String> globalRank) {
        this.globalRank = globalRank;
    }
    /**
     * Loops through sequence fields to check if all standard slots are processed,
     * shifting phase flags automatically to card drafting mode ({@link ClientState#PICK}) when empty.
     */
    public void checkAndEventuallySetPickState() {
        for (String nickname : playerOrderInTurnOrderCard) {
            System.out.println(playerOrderInTurnOrderCard);
            if (nickname != null) return;
        }
        this.state = ClientState.PICK;
    }

    public void setPlaceState() {
        this.state = ClientState.PLACE;
    }

    public void setState(String state) {
        this.state = ClientState.valueOf(state);
    }


    //UPDATES
    public void updateAge(int age){
        this.age = age;
    }
    /**
     * Atomically refreshes core statistical values, production multipliers, resource balances, and card counts
     * for a player after a successful card pick action.
     *
     * @param nickname         the username of the player being updated
     * @param numFod           the updated food count balance
     * @param numPrestigePoints the updated prestige score balance
     * @param numStars          the updated developmental stars count score
     * @param buildingDiscount  the total cost reduction value applied to structural purchases
     * @param gatheringDiscount the total reduction multiplier applied during feeding calculations
     */
    public void updatePickPlayerStats(String nickname , int numFod, int numPrestigePoints, int numStars, int buildingDiscount, int gatheringDiscount){
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            updatePlayerFood(nickname, numFod);
            updatePlayerPrestigePoints(nickname, numPrestigePoints);
            player.setNumStars(numStars);
            player.setTotalBuildingDiscount(buildingDiscount);
            player.setTotalGatheringDiscount(gatheringDiscount);
        }
    }
    /**
     * Updates or shifts a player's totem token assignment position key across track slots.
     * Clears historical references to avoid duplication on the track.
     *
     * @param nickname       the username text signature of the target entity
     * @param playerPosition the character code addressing the new slot destination coordinate
     */
    public void updatePlayerPosition(String nickname, Character playerPosition) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            Character oldPosition = getPlayerPosition(nickname);
            if (oldPosition != null) {
                positionToNickname.remove(oldPosition);
            }
            if (playerPosition != null) {
                positionToNickname.put(playerPosition, nickname);
            }
        }
        else
            positionToNickname.put(playerPosition, nickname);
    }
    /**
     * Directly overwrites the localized food supply resource balance assigned to a participant profile.
     *
     * @param nickname target profile username text string
     * @param numFood  the new asset balance total
     */
    public void updatePlayerFood(String nickname, int numFood) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.setNumFood(numFood);
        }
    }
    /**
     * Directly overwrites the localized prestige point scoring metric assigned to a participant profile.
     *
     * @param nickname          target profile username text string
     * @param numPrestigePoints the new score balance total
     */
    public void updatePlayerPrestigePoints(String nickname, int numPrestigePoints) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.setNumPrestigePoints(numPrestigePoints);
        }
    }
    /**
     * Adds a newly claimed worker or building asset card ID code into a player's local tribe dataset.
     *
     * @param nickname username token being updated
     * @param CardID   unique card catalog numerical value
     */

    public void updatePlayerTribe(String nickname,int CardID ) {
        ClientPlayer player = getPlayerByNickname(nickname);
        if (player != null) {
            player.addToPlayerTribe(CardID);
        }
    }
    /**
     * Purges a card code entry from board row matrix grids immediately after a player successfully picks it.
     *
     * @param row    the targeted layout grid tier row index
     * @param cardID the unique identity code of the removed card
     */
    public void removePickedCardFromBoard(int row, int cardID){
        board.get(row).remove(Integer.valueOf(cardID));
    }
    /**
     * appends a newborn client opponent profile record wrapper into the other player collection cache.
     *
     * @param player external opponent {@link ClientPlayer} parameter data wrapper
     */
    public void addPlayer(ClientPlayer player) {
        otherPlayers.add(player);
    }
    /**
     * Mass update subroutine that executes block synchronization updates on all participant resources
     * at the close of an era sequence round.
     *
     * @param playersAttributes dictionary mapping usernames to an array tracking resource metrics (index 0: food, index 1: prestige)
     */
    public void updatePlayerRoundAttributes(Map<String, ArrayList<Integer>> playersAttributes) {
        for (String playerNickname : playersAttributes.keySet()) {
            ArrayList<Integer> attributes = playersAttributes.get(playerNickname);
            updatePlayerFood(playerNickname, attributes.get(0));
            updatePlayerPrestigePoints(playerNickname, attributes.get(1));
        }
    }
    /**
     * Blanks out a player nickname entry inside turn sequencing rows by overwriting it with {@code null}.
     * <p>
     * Overwriting with a null placeholder keeps index alignments stable, preventing downstream indices
     * from shifting up unexpectedly.
     * </p>
     *
     * @param nickname user string token undergoing placeholder purging
     */
    //We put null if the player choose the position because if we remove we move all the other totem upwards
    public void removePlayerFromTurnOrderCard(String nickname) {
        int index = playerOrderInTurnOrderCard.indexOf(nickname);
        if (index != -1) {
            playerOrderInTurnOrderCard.set(index, null);
        }
    }
    /**
     * Resets all structural vectors and state pointers to clear match-specific data
     * and safely return the client to the global room match lobby.
     */
    public void returnToLobby() {
        this.otherPlayers = new ArrayList<>();
        this.playerOrderInTurnOrderCard = new ArrayList<>();
        this.board = new ArrayList<>();
        this.positionToNickname = new HashMap<>();
        this.finalRank = new ArrayList<>();
        this.gameId = -1;
        this.state = null;
    }

    //Methods used by controller
    /**
     * Local logical checker determining if current board status allows the home player to pick a card.
     * <p>
     * Evaluation logic:
     * <ul>
     * <li>Returns true instantly if the turn order card list size completely matches active player counts.</li>
     * <li>Bails out (returns false) if the player hasn't deployed their totem or occupies starter fallback slots like 'A'.</li>
     * <li>Scans opponent grid addresses to ensure no opponent totem occupies an earlier, higher-priority slot layout position.</li>
     * </ul>
     * </p>
     *
     * @return {@code true} if the active player turn matches home specifications; {@code false} otherwise
     */
    public boolean checkPickTurn() {
        if (playerOrderInTurnOrderCard.size() == numPlayers) return true;
        Character homePlayerPosition = getHomePlayerPosition();

        if (homePlayerPosition == null || homePlayerPosition == 'A') {
            return false;
        }
        for (ClientPlayer player : otherPlayers) {
            Character playerPos = getPlayerPosition(player.getNickname());
            if (playerPos != null && homePlayerPosition > playerPos) {
                return false;
            }
        }
        return true;
    }
    /**
     * Scans rows of the grid structure to determine which tier holds the requested unique card number.
     *
     * @param cardId search target integer index value
     * @return row array index location, or {@code -1} if the card is absent
     */
    public int findRowFromCardId(int cardId) {
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).contains(cardId)) {
                return i;
            }
        }
        return -1; // Card not found
    }
    /**
     * Verifies if a specific grid row is legally accessible for drafting based on the
     * constraints of the home player's current track token position.
     * <p>
     * Layout business logic parameters:
     * <ul>
     * <li><b>AddTopPick Bonus Flow:</b> If the position pointer equals null, drafting is restricted exclusively to upper rows (Top Buildings/Deckable rows).</li>
     * <li><b>Top-Only Slots:</b> Positions 'C' or 'F' lock choices, rejecting bottom row selections.</li>
     * <li><b>Bottom-Only Slots:</b> Positions 'B' or 'D' reject upper tier selections, allowing drafts only from bottom rows.</li>
     * </ul>
     * </p>
     *
     * @param row target row index constant under validation
     * @return {@code true} if the combination aligns with track slot rules; {@code false} otherwise
     */
    public boolean checkRow(int row) {
        Character position = getHomePlayerPosition();

        //AddTopPick Case
        if (position == null) {
            return row == TOP_BUILDING_ROW || row == TOP_DECKABLE_ROW;
        }

        //In these track card you can pick only from top
        if ((position == 'C' || position == 'F') && (row == BOTTOM_DECKABLE_ROW || row == BOTTOM_BUILDING_ROW)) {
            return false;
        }
        //In these track card you can pick only from the bottom
        return (position != 'B' && position != 'D') || (row != TOP_BUILDING_ROW && row != TOP_DECKABLE_ROW);
    }
    /**
     * Evaluates sequence slots inside the turn order card to verify if the home player
     * is the active entity authorized to position their totem.
     *
     * @return {@code true} if the leading non-null nickname entry matches the home profile string; {@code false} otherwise
     */
    public boolean checkPlaceTurn() {
        for (String nickname : playerOrderInTurnOrderCard) {
            if (nickname == null) continue;
            return nickname.equals(homePlayer.getNickname());
        }
        return false;
    }

    /**
     * Checks whether a targeted track slot letter character represents a legal and currently unoccupied
     * deployment destination for this match scale configuration.
     *
     * @param position track address code character under validation
     * @return {@code true} if the letter is part of the valid path scale and contains no conflicting totem claims; {@code false} otherwise
     */
    public boolean checkTotemPosition(char position) {
        List<Character> validPositions = offersPath.get(numPlayers);
        if (!validPositions.contains(position)) {
            return false;
        }
        return !positionToNickname.containsKey(position);
    }

    /**
     * Verifies if the local system context mode is set to card drafting ({@link ClientState#PICK}).
     *
     * @return {@code true} if the state matches; {@code false} otherwise
     */
    public boolean checkPickState() {
        return state == ClientState.PICK;
    }
    /**
     * Verifies if the local system context mode is set to totem deployment ({@link ClientState#PLACE}).
     *
     * @return {@code true} if the state matches; {@code false} otherwise
     */
    public boolean checkPlaceState() {
        return state == ClientState.PLACE;
    }
}