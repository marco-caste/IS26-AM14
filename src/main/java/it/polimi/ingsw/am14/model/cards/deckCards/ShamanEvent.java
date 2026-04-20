package it.polimi.ingsw.am14.model.cards.deckCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am14.model.cards.buildings.Building;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.ShamanBonusEventVisitor;
import it.polimi.ingsw.am14.model.cards.buildings.visitors.ShamanMalusEventVisitor;
import it.polimi.ingsw.am14.model.gameLogic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShamanEvent extends EventCard {

    @JsonCreator
    public ShamanEvent(
            @JsonProperty("age") int age,
            @JsonProperty("id") int id
    ) {
        super(age, id);
    }

    private List<Player> getMAxStarPlayer(Game context){

       Player maxPlayer =  context.getPlayers().stream()
                .max((a, b) -> Integer.compare(a.getNumStars(), b.getNumStars())).orElse(context.getPlayers().getFirst());

       return context.getPlayers().stream()
               .filter(x-> {
                   return x.getNumStars() == maxPlayer.getNumStars();
               })
               .collect(Collectors.toList());

    }

    private List<Player> getMinStarPlayer(Game context){

        Player minPlayer =  context.getPlayers().stream()
                .min((a, b) -> Integer.compare(a.getNumStars(), b.getNumStars())).orElse(context.getPlayers().getFirst());

        return context.getPlayers().stream()
                .filter(x-> {
                    return x.getNumStars() == minPlayer.getNumStars();
                })
                .collect(Collectors.toList());
    }


    private void prestigePointsLoss (Player player, Game context){

        player.addToPrestigePoints(-(1 + 2* this.getAge())); // removing points player

        ShamanMalusEventVisitor visitor = new ShamanMalusEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }
    }



    private void prestigePointsWon (Player player, Game context){

        player.addToPrestigePoints(5* this.getAge()); // adding points player

        ShamanBonusEventVisitor visitor = new ShamanBonusEventVisitor(context, player);
        ArrayList<Building> buildings = player.getTribe().getBuildings();
        for (Building building : buildings) {
            building.accept(visitor);
        }

    }


    @Override
    public void applyEffect (Player player, Game context){

        List<Player> maxPlayers = getMAxStarPlayer(context);
        List<Player> minPlayers = getMinStarPlayer(context);

        for ( Player pMax : maxPlayers){
            prestigePointsWon(pMax, context);
        }

        for( Player pMin : minPlayers){
            prestigePointsLoss(pMin , context);
        }


    }

}
