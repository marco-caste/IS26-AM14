package it.polimi.ingsw.am14.model.gameLogic;

class WaitingRoomState extends GameState {

    @Override
    protected void onPlayerConnection(Game game, String nickname, String totemColor) {

        Player p = new Player(nickname, TotemColor.valueOf(totemColor));
        game.addPlayer(p);
        game.getBoard().getTurnOrderCard().addPlayer(p);
        if (game.playersSize() == game.getNumPlayers()) {
            this.nextState(game);
        }

        // TODO: client update
    }

    @Override
    protected void nextState(Game context) {
        context.setCurrentState(new PositionState());
        context.getBoard().getTurnOrderCard().setupPhaseTen();

        // TODO: client update
    }
}
