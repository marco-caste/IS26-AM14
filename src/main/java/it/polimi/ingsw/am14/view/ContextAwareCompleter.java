package it.polimi.ingsw.am14.view;

import org.jline.builtins.Completers;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.AggregateCompleter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class ContextAwareCompleter implements Completer {

    private final Map<String, Completer> completers = new HashMap<>();

    public ContextAwareCompleter() {
        completers.put("default", buildLobbyCompleter());
        completers.put("inGame", buildInGameCompleter());
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String context = lineReader.getVariable("context") != null ? lineReader.getVariable("context").toString() : "default";
        Completer completer = completers.getOrDefault(context, completers.get("default"));
        completer.complete(lineReader, parsedLine, list);
    }

    private static Completer buildLobbyCompleter() {
        List<Candidate> candidates = List.of(
                new Candidate("help", "help", null, "show a list of useful commands", null, null, true),
                new Candidate("exit", "exit", null, "exit the application", null, null, true),
                new Candidate("connect", "connect", null, "specify the networking technology to use", null, null, true),
                new Candidate("create", "create", null, "create a new game", null, null, true),
                new Candidate("join", "join", null, "join an existing game", null, null, true)
        );
        Completer candidateCompleter = (reader, line, completions) -> {
            completions.addAll(candidates);
        };
        Completer treeCompleter = new Completers.TreeCompleter(
                node("connect", node("socket"), node("rmi")),
                node("join", node("white, red, blue, yellow, purple")),
                node("create", node("white, red, blue, yellow, purple"))
        );

        return new AggregateCompleter(candidateCompleter, treeCompleter);
    }

    private static Completer buildInGameCompleter() {
        List<Candidate> candidates = List.of(
                new Candidate("help", "help", null, "show a list of useful commands", null, null, true),
                new Candidate("pick", "pick", null, "pick a card from the board", null, null, true),
                new Candidate("place", "place", null, "place the totem on the board", null, null, true),
                new Candidate("show", "show", null, "show the current game state", null, null, true)
        );
        Completer candidateCompleter = (reader, line, completions) -> {
            completions.addAll(candidates);
        };
        Completer treeCompleter = new Completers.TreeCompleter(
                node("show", node("nickname", "cardId", "turnOrderCard", "offersPath"))
        );

        return new AggregateCompleter(candidateCompleter, treeCompleter);
    }
}
