package it.polimi.ingsw.am14.model.gameLogic;

import it.polimi.ingsw.am14.exceptions.*;
import it.polimi.ingsw.am14.model.boardComponents.Board;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.effects.ExtraEndTurnFood;
import it.polimi.ingsw.am14.model.cards.buildings.effects.ExtraShamanicStars;
import it.polimi.ingsw.am14.model.cards.buildings.effects.SetCompletionFood;
import it.polimi.ingsw.am14.model.cards.buildings.types.ImmediateBuilding;
import it.polimi.ingsw.am14.model.cards.buildings.types.SetBuilding;
import it.polimi.ingsw.am14.model.cards.deckCards.Artist;
import it.polimi.ingsw.am14.model.cards.deckCards.Builder;
import it.polimi.ingsw.am14.model.cards.deckCards.Gatherer;
import it.polimi.ingsw.am14.model.cards.deckCards.Hunter;
import it.polimi.ingsw.am14.model.cards.deckCards.Inventor;
import it.polimi.ingsw.am14.model.cards.deckCards.InventionType;
import it.polimi.ingsw.am14.model.cards.deckCards.Shaman;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PickStateTest {

    // Shared fixture object so each test can focus only on behavior under test.
    private static class TestFixture {
        private final PickState state;
        private final Game game;
        private final Player p1;
        private final Player p2;

        private TestFixture(PickState state, Game game, Player p1, Player p2) {
            this.state = state;
            this.game = game;
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    private TestFixture buildFixture() {
        PickState state = new PickState();
        Player p1 = new Player("P1", TotemColor.WHITE);
        Player p2 = new Player("P2", TotemColor.RED);

        // Use the simpler Board constructor that initializes everything properly
        Board board = new Board(2);

        Game game = new Game(
                state,
                new ArrayList<>(List.of(p1, p2)),
                board,
                0,
                2,
                1,
                1
        );

        return new TestFixture(state, game, p1, p2);
    }

    private void putTotem(TestFixture fixture, char position, Player player) {
        fixture.game.getBoard().getOffersPath().offersPathGet(position).setPlayerTotem(player);
    }

    private Player resolveCurrentPlayerFromOffersPath(Game game) {
        Set<Character> offersPathKeys = new HashSet<>(game.getBoard().getOffersPath().offersPathKeySet());
        for (Character key : offersPathKeys) {
            if (game.getBoard().getOffersPath().offersPathGet(key) != null
                    && game.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem() != null) {
                return game.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem();
            }
        }
        return null;
    }

    private Character findTotemPosition(Game game, Player player) {
        for (Character key : game.getBoard().getOffersPath().offersPathKeySet()) {
            if (game.getBoard().getOffersPath().offersPathGet(key) != null
                    && player.equals(game.getBoard().getOffersPath().offersPathGet(key).getPlayerTotem())) {
                return key;
            }
        }
        return null;
    }

    @Test
    public void testPickCardThrowsWrongTurnWhenPlayerIsNotCurrent() {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);

        assertThrows(WrongTurnException.class,
                () -> fixture.state.pickCard(fixture.game, fixture.p2, Optional.empty(), 0));
    }

    @Test
    public void testPickCardThrowsForcedPickWhenSkippingWithAvailableDeckableCard() {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        fixture.p1.setNumTopPicksLeft(1);

        Hunter hunter = new Hunter(1, 11, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunter)));

        assertThrows(ForcedPickException.class,
                () -> fixture.state.pickCard(fixture.game, fixture.p1, Optional.empty(), 0));
    }

    @Test
    public void testPickCardThrowsIllegalPickWhenBuildingIsNotAffordable() {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        fixture.p1.setNumTopPicksLeft(1);

        Building expensiveBuilding = new ImmediateBuilding(3, 0, 1, 21, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(expensiveBuilding)));

        assertThrows(IllegalPickException.class,
                () -> fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(expensiveBuilding), 1));
    }

    @Test
    public void testPickCardOnTopDeckableRowRemovesCardAndDecrementsTopPicks() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        fixture.p1.setNumTopPicksLeft(2);

        Hunter firstHunter = new Hunter(1, 31, false);
        Hunter secondHunter = new Hunter(1, 32, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(firstHunter, secondHunter)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(firstHunter), 0);

        assertEquals(1, fixture.p1.getNumTopPicksLeft());
        assertEquals(1, fixture.game.getBoard().topDeckableRowSize());
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());
        assertEquals(0, fixture.game.getCurrentPlayerIndex());
    }

    @Test
    public void testPickCardOnTopBuildingRowBuysBuildingAndAppliesImmediateEffect() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        fixture.p1.setNumTopPicksLeft(2);
        fixture.p1.setNumFood(2);

        Hunter hunter = new Hunter(1, 41, false);
        Building building = new ImmediateBuilding(1, 0, 1, 42, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunter)));
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(building)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(building), 1);

        assertEquals(1, fixture.p1.getNumTopPicksLeft());
        assertEquals(1, fixture.p1.getNumFood());
        assertEquals(1, fixture.p1.getTribe().getBuildings().size());
        assertEquals(1, fixture.p1.getAddFoodStartOfRound());
        assertEquals(0, fixture.game.getBoard().getTopBuildingRow().size());
    }

    @Test
    public void testPickCardAllowsSkipWhenCurrentPlayerCannotPickAnyCard() {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        Player currentPlayer = resolveCurrentPlayerFromOffersPath(fixture.game);
        assertNotNull(currentPlayer);
        Player otherPlayer = currentPlayer.equals(fixture.p1) ? fixture.p2 : fixture.p1;

        currentPlayer.setNumTopPicksLeft(1);
        currentPlayer.setNumBottomPicksLeft(1);
        currentPlayer.setNumFood(0);

        otherPlayer.setNumTopPicksLeft(1);
        otherPlayer.setNumBottomPicksLeft(0);
        otherPlayer.setNumFood(2);

        Building affordableForOtherOnly = new ImmediateBuilding(1, 0, 1, 51, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>());
        fixture.game.getBoard().setBottomDeckableRow(new ArrayList<>());
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(affordableForOtherOnly)));
        fixture.game.getBoard().setBottomBuildingRow(new ArrayList<>());

        Character currentTotemPosition = findTotemPosition(fixture.game, currentPlayer);

        assertDoesNotThrow(() -> fixture.state.pickCard(fixture.game, currentPlayer, Optional.empty(), 0));
        assertEquals(0, currentPlayer.getNumTopPicksLeft());
        assertEquals(0, currentPlayer.getNumBottomPicksLeft());
        assertNull(fixture.game.getBoard().getOffersPath().offersPathGet(currentTotemPosition).getPlayerTotem());
        assertEquals(currentPlayer, fixture.game.getBoard().getTurnOrderCard().getPlayer(0));
        assertEquals(1, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PickState.class, fixture.game.getCurrentState());
    }

    // test
    @Test
    public void testPickCardAllowsSkipWhenLastPlayerCannotPickAnyCard() {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        Player currentPlayer = resolveCurrentPlayerFromOffersPath(fixture.game);
        assertNotNull(currentPlayer);
        Player lastPlayer = currentPlayer.equals(fixture.p1) ? fixture.p2 : fixture.p1;

        // Current player cannot pick and chooses to skip.
        currentPlayer.setNumTopPicksLeft(1);
        currentPlayer.setNumBottomPicksLeft(1);
        currentPlayer.setNumFood(0);

        // Last player also cannot pick from any row.
        lastPlayer.setNumTopPicksLeft(1);
        lastPlayer.setNumBottomPicksLeft(1);
        lastPlayer.setNumFood(0);

        Building unaffordableBuilding = new ImmediateBuilding(2, 0, 1, 61, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>());
        fixture.game.getBoard().setBottomDeckableRow(new ArrayList<>());
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(unaffordableBuilding)));
        fixture.game.getBoard().setBottomBuildingRow(new ArrayList<>());

        Character currentTotemPosition = findTotemPosition(fixture.game, currentPlayer);
        Character lastTotemPosition = findTotemPosition(fixture.game, lastPlayer);

        assertDoesNotThrow(() -> fixture.state.pickCard(fixture.game, currentPlayer, Optional.empty(), 0));
        assertNull(fixture.game.getBoard().getOffersPath().offersPathGet(currentTotemPosition).getPlayerTotem());
        assertNull(fixture.game.getBoard().getOffersPath().offersPathGet(lastTotemPosition).getPlayerTotem());
        assertEquals(currentPlayer, fixture.game.getBoard().getTurnOrderCard().getPlayer(0));
        assertEquals(lastPlayer, fixture.game.getBoard().getTurnOrderCard().getPlayer(1));
        assertEquals(0, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardTwoTopPicksBuilderThenBuildingAppliesDiscount() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.p1.setNumTopPicksLeft(2);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setNumFood(2);
        fixture.p2.setNumTopPicksLeft(2);

        Builder builder = new Builder(1, 71, 1, 0);
        Hunter extraDeckable = new Hunter(1, 72, false);
        Building building = new ImmediateBuilding(3, 0, 1, 73, new ExtraShamanicStars());

        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(builder, extraDeckable)));
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(building)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(builder), 0);
        assertEquals(1, fixture.p1.getNumTopPicksLeft());
        assertEquals(1, fixture.p1.getTotalBuildingDiscount());
        assertEquals(1, fixture.p1.getTribe().getBuildersSize());

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(building), 1);
        assertEquals(0, fixture.p1.getNumTopPicksLeft());
        assertEquals(0, fixture.p1.getNumFood());
        assertEquals(1, fixture.p1.getTribe().getBuildings().size());
        assertEquals(0, fixture.game.getBoard().getTopBuildingRow().size());
    }

    @Test
    public void testPickCardBothPlayersOnePickEachCurrentBehavior() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);

        Hunter p1Hunter = new Hunter(1, 81, false);
        Hunter p2Hunter = new Hunter(1, 82, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(p1Hunter, p2Hunter)));

        // First player picks a hunter
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(p1Hunter), 0);
        assertEquals(0, fixture.p1.getNumTopPicksLeft());
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());

        // Second player picks a hunter
        fixture.state.pickCard(fixture.game, fixture.p2, Optional.of(p2Hunter), 0);

        // Verify second player picked successfully
        assertEquals(1, fixture.p2.getTribe().getHuntersSize());
        assertEquals(0, fixture.p2.getNumTopPicksLeft());
        assertEquals(6, fixture.game.getBoard().topDeckableRowSize());

        // Verify game state after both players picked
        assertEquals(0, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardFromBottomDeckableRow() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.p1.setNumBottomPicksLeft(1);
        fixture.p1.setNumTopPicksLeft(0);

        Hunter bottomHunter = new Hunter(1, 91, false);
        fixture.game.getBoard().setBottomDeckableRow(new ArrayList<>(List.of(bottomHunter)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(bottomHunter), 2);

        assertEquals(0, fixture.p1.getNumBottomPicksLeft());
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());
        assertEquals(0, fixture.game.getBoard().bottomDeckableRowSize());
    }

    @Test
    public void testPickCardFromBottomBuildingRow() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.p1.setNumBottomPicksLeft(1);
        fixture.p1.setNumFood(2);

        Building bottomBuilding = new ImmediateBuilding(1, 0, 1, 92, new ExtraShamanicStars());
        fixture.game.getBoard().setBottomBuildingRow(new ArrayList<>(List.of(bottomBuilding)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(bottomBuilding), 3);

        assertEquals(0, fixture.p1.getNumBottomPicksLeft());
        assertEquals(2, fixture.p1.getNumFood());
        assertEquals(1, fixture.p1.getTribe().getBuildings().size());
        assertEquals(0, fixture.game.getBoard().getBottomBuildingRow().size());
    }

    @Test
    public void testPickCardMixedTopAndBottomPicks() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(1);

        Hunter topHunter = new Hunter(1, 96, false);
        Hunter bottomHunter = new Hunter(1, 97, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(topHunter)));
        fixture.game.getBoard().setBottomDeckableRow(new ArrayList<>(List.of(bottomHunter)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(topHunter), 0);
        assertEquals(0, fixture.p1.getNumTopPicksLeft());
        assertEquals(1, fixture.p1.getNumBottomPicksLeft());
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(bottomHunter), 2);
        assertEquals(0, fixture.p1.getNumBottomPicksLeft());
        assertEquals(2, fixture.p1.getTribe().getHuntersSize());
    }

    @Test
    public void testPickCardExactFoodForBuilding() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumFood(5);

        Building exactCostBuilding = new ImmediateBuilding(5, 0, 1, 99, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(exactCostBuilding)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(exactCostBuilding), 1);

        assertEquals(2, fixture.p1.getNumFood());
        assertEquals(1, fixture.p1.getTribe().getBuildings().size());
    }

    @Test
    public void testPickCardPlayerSkipsAllPicksWhenNoPicksLeft() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        fixture.p1.setNumTopPicksLeft(0);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p2.setNumTopPicksLeft(0);
        fixture.p2.setNumBottomPicksLeft(0);

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.empty(), 0);

        assertEquals(0, fixture.game.getCurrentPlayerIndex());
        Character p1TotemPosition = findTotemPosition(fixture.game, fixture.p1);
        assertNull(p1TotemPosition);
    }

    @Test
    public void testPickCardBuildingAppliesEffectCorrectly() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumFood(2);
        fixture.p1.setAddFoodStartOfRound(0);

        Building foodBuilding = new ImmediateBuilding(1, 0, 1, 106, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(foodBuilding)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(foodBuilding), 1);

        assertEquals(1, fixture.p1.getAddFoodStartOfRound());
    }

    @Test
    public void testPickCardMultipleRoundsWithSamePlayers() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        fixture.p1.setNumTopPicksLeft(1);
        fixture.p2.setNumTopPicksLeft(1);

        Hunter h1 = new Hunter(1, 107, false);
        Hunter h2 = new Hunter(1, 108, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(h1, h2)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(h1), 0);
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());

        fixture.state.pickCard(fixture.game, fixture.p2, Optional.of(h2), 0);
        assertEquals(1, fixture.p2.getTribe().getHuntersSize());

        assertEquals(0, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardWithAddTopPickExceptionalPick() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.game.setCurrentPlayerIndex(0);

        // Setup both players to have no picks left
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);

        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);

        // Setup cards
        Hunter hunter = new Hunter(1, 200, false);
        Hunter hunterForExceptional = new Hunter(1, 200, false);
        Hunter hunterForExceptional2 = new Hunter(1, 210, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunter, hunterForExceptional, hunterForExceptional2)));
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(hunter), 0);

        fixture.state.pickCard(fixture.game, fixture.p2, Optional.of(hunterForExceptional2), 0);

        assertEquals(2, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PickState.class, fixture.game.getCurrentState());

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(hunterForExceptional), 0);

        assertEquals(2, fixture.p1.getTribe().getHuntersSize());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardWithAddTopPickBuildingExceptionalPick() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.game.setCurrentPlayerIndex(0);

        // Setup both players to have no picks left
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);
        fixture.p1.setNumFood(3);

        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);
        Hunter hunterForExceptional = new Hunter(1, 200, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunterForExceptional, hunterForExceptional, hunterForExceptional)));
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(hunterForExceptional), 0);

        // Setup building for exceptional pick
        Building buildingForExceptional = new ImmediateBuilding(2, 0, 1, 201, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(buildingForExceptional)));

        // pick di due
        fixture.state.pickCard(fixture.game, fixture.p2, Optional.of(hunterForExceptional), 0);

        // Exceptional building pick
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(buildingForExceptional), 1);

        assertEquals(3, fixture.p1.getNumFood());
        assertEquals(1, fixture.p1.getTribe().getBuildings().size());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardWithAddTopPickSkip() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);
        fixture.game.setCurrentPlayerIndex(0);

        // Setup both players to have no picks left
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);
        fixture.p1.setNumFood(3);

        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);
        Hunter hunterForExceptional = new Hunter(1, 200, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunterForExceptional, hunterForExceptional, hunterForExceptional)));
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(hunterForExceptional), 0);

        // Setup building for exceptional pick
        Building buildingForExceptional = new ImmediateBuilding(2, 0, 1, 201, new ExtraEndTurnFood());
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(buildingForExceptional)));

        // pick di due
        fixture.state.pickCard(fixture.game, fixture.p2, Optional.of(hunterForExceptional), 0);

        fixture.game.pickCard(fixture.p1.getNickname(), -1, -1);

        assertEquals(0, fixture.p1.getTribe().getBuildings().size());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardBothPlayersWithP1AddTopPickScenario() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 picks first, then P2, then back to P1 with addTopPick
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);

        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);

        Hunter p1Hunter = new Hunter(1, 202, false);
        Hunter p2Hunter = new Hunter(1, 203, false);
        Hunter extraHunter = new Hunter(1, 204, false);

        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(p1Hunter, p2Hunter, extraHunter)));

        // P1 first pick
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(p1Hunter), 0);
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());
        assertEquals(1, fixture.game.getCurrentPlayerIndex());

        // P2 first pick
        fixture.state.pickCard(fixture.game, fixture.p2, Optional.of(p2Hunter), 0);
        assertEquals(1, fixture.p2.getTribe().getHuntersSize());
        assertEquals(2, fixture.game.getCurrentPlayerIndex());
    }

    @Test
    public void testPickCardP1WithAddTopPickNoExceptionalCondition() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 still has picks left (not the exceptional case)
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);

        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);

        Hunter hunter1 = new Hunter(1, 205, false);
        Hunter hunter2 = new Hunter(1, 206, false);

        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunter1, hunter2)));

        // Pick with remaining picks - addTopPick should not trigger exceptional logic
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(hunter1), 0);

        assertEquals(0, fixture.p1.getNumTopPicksLeft());
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());
        assertEquals(1, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PickState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardP1AddTopPickWithP2SkippingTurns() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1: 1 pick with addTopPick
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);

        // P2: no picks available
        fixture.p2.setNumTopPicksLeft(0);
        fixture.p2.setNumBottomPicksLeft(0);

        Hunter p1Hunter = new Hunter(1, 207, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(p1Hunter)));

        // P1 picks
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(p1Hunter), 0);
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());

        // P2 should skip their turn automatically since they have no picks
        assertEquals(0, fixture.game.getCurrentPlayerIndex());
    }

    @Test
    public void testPickCardP1AddTopPickWithMultipleCardsAvailable() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // Setup for exceptional pick scenario
        fixture.p1.setNumTopPicksLeft(0);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);
        fixture.p1.setNumFood(5);

        fixture.p2.setNumTopPicksLeft(0);
        fixture.p2.setNumBottomPicksLeft(0);

        // Multiple cards on the board
        Hunter hunter = new Hunter(1, 208, false);
        Building building = new ImmediateBuilding(3, 0, 1, 209, new ExtraEndTurnFood());

        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunter)));
        fixture.game.getBoard().setTopBuildingRow(new ArrayList<>(List.of(building)));

        // Setup to reach exceptional pick scenario
        fixture.game.getBoard().getTurnOrderCard().addPlayer(fixture.p2);
        fixture.game.getBoard().getTurnOrderCard().addPlayer(fixture.p1);

        // P1 picks building with exceptional pick
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(building), 1);

        assertEquals(1, fixture.p1.getTribe().getBuildings().size());
        assertEquals(1, fixture.p1.getNumFood());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());

    }

    @Test
    public void canNextPlayerPickTest() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 still has picks left (not the exceptional case)
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(0);

        fixture.p2.setNumTopPicksLeft(0);
        fixture.p2.setNumBottomPicksLeft(1);

        Hunter hunter1 = new Hunter(1, 205, false);
        Hunter hunter2 = new Hunter(1, 206, false);

        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(hunter1, hunter2)));

        // Pick with remaining picks - addTopPick should not trigger exceptional logic
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(hunter1), 0);


        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testSetBuildingWithSetCompletionFoodEffectActivation() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setNumFood(0);

        // Create SetBuilding with SetCompletionFood effect
        SetCompletionFood foodEffect = new SetCompletionFood();
        SetBuilding setBuilding = new SetBuilding(3, 5, 2, 400, foodEffect);
        fixture.p1.getTribe().addBuilding(setBuilding);

        // Complete the set with all 6 character types
        fixture.p1.getTribe().addInventor(new Inventor(1, 402, InventionType.FLUTE));
        fixture.p1.getTribe().addShaman(new Shaman(1, 403, 2));
        fixture.p1.getTribe().addGatherer(new Gatherer(1, 404, 1));
        fixture.p1.getTribe().addArtist(new Artist(1, 405));
        fixture.p1.getTribe().addBuilder(new Builder(1, 406, 1, 2));

        // Pick a character to trigger set completion and SetCompletionFood effect
        Hunter triggerCard = new Hunter(1, 407, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(triggerCard)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(triggerCard), 0);

        // Verify SetBuilding was activated and SetCompletionFood added 5 food
        assertEquals(1, fixture.p1.getNumSetCompleted());
        assertEquals(6, fixture.p1.getNumFood());
    }

    @Test
    public void testPickCardP1CanOnlyPickFromBottomTriesTopThenPicksBottom() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 can only pick from bottom (no top picks left)
        fixture.p1.setNumTopPicksLeft(0);
        fixture.p1.setNumBottomPicksLeft(1);
        fixture.p1.setNumFood(2);

        // P2 has picks available but won't be used in this test
        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);

        // Setup cards on board
        Hunter topHunter = new Hunter(1, 500, false);
        Hunter bottomHunter = new Hunter(1, 501, false);

        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(topHunter)));
        fixture.game.getBoard().setBottomDeckableRow(new ArrayList<>(List.of(bottomHunter)));

        // P1 tries to pick from top (should throw IllegalPickException)
        assertThrows(IllegalPickException.class,
                () -> fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(topHunter), 0));

        // Verify state hasn't changed
        assertEquals(1, fixture.p1.getNumBottomPicksLeft());
        assertEquals(0, fixture.p1.getTribe().getHuntersSize());
        assertEquals(1, fixture.game.getBoard().bottomDeckableRowSize());

        // P1 successfully picks from bottom (row index 2)
        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(bottomHunter), 2);

        // Verify pick from bottom was successful
        assertEquals(0, fixture.p1.getNumBottomPicksLeft());
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());
        assertEquals(1, fixture.game.getCurrentPlayerIndex());

        // Verify bottom row is now empty
        assertEquals(0, fixture.game.getBoard().bottomDeckableRowSize());
    }

    @Test
    public void testPickCardP1WithAddTopPickTriesBottomPickThrowsException() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 has addTopPick=1 and zero picks left (exceptional pick scenario)
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);

        fixture.p2.setNumTopPicksLeft(0);
        fixture.p2.setNumBottomPicksLeft(0);

        // Setup cards on board
        Hunter bottomHunter = new Hunter(1, 600, false);
        fixture.game.getBoard().setBottomDeckableRow(new ArrayList<>(List.of(bottomHunter)));
        Hunter top1Hunter = new Hunter(1, 601, false);
        Hunter top2Hunter = new Hunter(1, 602, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(top1Hunter, top2Hunter)));

        // Setup to reach exceptional pick scenario (currentPlayerIndex == numPlayers)
        fixture.game.pickCard(fixture.p1.getNickname(), top1Hunter.getId(), 0); // P1 picks to trigger addTopPick

        // P1 tries to pick from bottom with exceptional pick active (should throw exception)
        // In exceptional pick mode, only row 0 (top deckable) and row 1 (top building) are allowed
        assertThrows(IllegalPickException.class,
                () -> fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(bottomHunter), 2));

        // Verify state hasn't changed
        assertEquals(0, fixture.p1.getNumBottomPicksLeft());
        assertEquals(1, fixture.p1.getTribe().getHuntersSize());
        assertEquals(1, fixture.game.getBoard().bottomDeckableRowSize());
    }

    @Test
    public void testPickCardP1TriesToSkipButHasPickableCardsForcedPickException() throws Exception {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 has picks available but tries to skip
        fixture.p1.setNumTopPicksLeft(2);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setNumFood(0);

        // P2 setup (not relevant for this test)
        fixture.p2.setNumTopPicksLeft(1);
        fixture.p2.setNumBottomPicksLeft(0);

        // Setup pickable cards on top row (no food required for character cards)
        Hunter availableHunter = new Hunter(1, 700, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(availableHunter)));

        // P1 tries to skip (Optional.empty()) but has pickable cards available
        ForcedPickException exception = assertThrows(ForcedPickException.class,
                () -> fixture.state.pickCard(fixture.game, fixture.p1, Optional.empty(), 0));

        // Verify exception message
        assertTrue(exception.getMessage().contains("can't skip"));

        // Verify state hasn't changed (P1 still has picks and hunter is still available)
        assertEquals(2, fixture.p1.getNumTopPicksLeft());
        assertEquals(0, fixture.p1.getTribe().getHuntersSize());
        assertEquals(1, fixture.game.getBoard().topDeckableRowSize());
        assertEquals(0, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PickState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testTransitionFalse() throws WrongTurnException, IllegalPickException, ForcedPickException {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 has picks available but tries to skip
        fixture.p1.setNumTopPicksLeft(2);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setNumFood(0);

        // P2 setup (not relevant for this test)
        fixture.p2.setNumTopPicksLeft(0);
        fixture.p2.setNumBottomPicksLeft(1);

        // Setup pickable cards on top row (no food required for character cards)
        Hunter topHunter = new Hunter(1, 700, false);
        Hunter bottomHunter = new Hunter(1, 700, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(topHunter)));
        fixture.game.getBoard().setBottomDeckableRow(new ArrayList<>(List.of(bottomHunter)));

        fixture.state.pickCard(fixture.game, fixture.p1, Optional.of(topHunter), 0);

        assertEquals(1, fixture.game.getCurrentPlayerIndex());
    }

    @Test
    public void testAddTopPickSkipMandatory() throws IllegalPickException, ForcedPickException, WrongStateException, WrongTurnException, UnknownNicknameException {
        TestFixture fixture = buildFixture();
        putTotem(fixture, 'B', fixture.p1);
        putTotem(fixture, 'C', fixture.p2);

        // P1 has addTopPick=1 and zero picks left (exceptional pick scenario)
        fixture.p1.setNumTopPicksLeft(1);
        fixture.p1.setNumBottomPicksLeft(0);
        fixture.p1.setAddTopPick(1);

        fixture.p2.setNumTopPicksLeft(0);
        fixture.p2.setNumBottomPicksLeft(0);

        // Setup cards on board
        Hunter top1Hunter = new Hunter(1, 601, false);
        fixture.game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(top1Hunter)));

        // Setup to reach exceptional pick scenario (currentPlayerIndex == numPlayers)
        fixture.game.pickCard(fixture.p1.getNickname(), top1Hunter.getId(), 0); // P1 picks to trigger addTopPick

        assertEquals(0, fixture.game.getCurrentPlayerIndex());
        assertInstanceOf(PositionState.class, fixture.game.getCurrentState());
    }

    @Test
    public void testPickCardWith3PlayersP1AddTopPickP2P3CannotPick() throws Exception {
        // Setup 3 players
        PickState state = new PickState();
        Player p1 = new Player("P1", TotemColor.WHITE);
        Player p2 = new Player("P2", TotemColor.RED);
        Player p3 = new Player("P3", TotemColor.BLUE);

        // Use the simpler Board constructor that initializes everything properly
        Board board = new Board(3);

        Game game = new Game(
                state,
                new ArrayList<>(List.of(p1, p2, p3)),
                board,
                0,
                3,
                1,
                1
        );

        // Place totems for all 3 players
        game.getBoard().getOffersPath().offersPathGet('B').setPlayerTotem(p1);
        game.getBoard().getOffersPath().offersPathGet('C').setPlayerTotem(p2);
        game.getBoard().getOffersPath().offersPathGet('D').setPlayerTotem(p3);

        // Setup P1 with addTopPick scenario
        p1.setNumTopPicksLeft(1);
        p1.setNumBottomPicksLeft(0);
        p1.setAddTopPick(1);
        p1.setNumFood(2);

        // P2 and P3 cannot pick (no picks available)
        p2.setNumTopPicksLeft(0);
        p2.setNumBottomPicksLeft(0);
        p3.setNumTopPicksLeft(0);
        p3.setNumBottomPicksLeft(0);

        // Setup card on top row for P1 to pick
        Hunter pickableHunter1 = new Hunter(1, 800, false);
        Hunter pickableHunter2 = new Hunter(2, 800, false);
        game.getBoard().setTopDeckableRow(new ArrayList<>(List.of(pickableHunter1, pickableHunter2)));

        state.pickCard(game, p1, Optional.of(pickableHunter1), 0);

        // Verify initial state
        assertEquals(3, game.getCurrentPlayerIndex());

        state.pickCard(game, p1, Optional.of(pickableHunter2), 0);

        // Verify all 3 players are in turn order (moved from offers path)
        assertEquals(p1, game.getBoard().getTurnOrderCard().getPlayer(0));
        assertEquals(p2, game.getBoard().getTurnOrderCard().getPlayer(1));
        assertEquals(p3, game.getBoard().getTurnOrderCard().getPlayer(2));
    }
}
