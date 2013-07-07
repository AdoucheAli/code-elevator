package elevator.server;

import elevator.Clock;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;

import static java.util.Collections.unmodifiableSet;
import static java.util.concurrent.TimeUnit.SECONDS;

class StartedElevatorServer {

    private final Map<Player, ElevatorGame> elevatorGames = new TreeMap<>();
    private final Clock clock = new Clock();

    StartedElevatorServer() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(clock::tick, 0, 1, SECONDS);
    }

    public StartedElevatorServer addElevatorGame(Player player, URL server) throws MalformedURLException {
        if (elevatorGames.containsKey(player)) {
            throw new IllegalStateException("a game with player " + player + " has already have been added");
        }
        ElevatorGame elevatorGame = new ElevatorGame(player, server, clock);
        elevatorGame.start();
        elevatorGames.put(player, elevatorGame);
        return this;
    }

    public Set<Player> players() {
        return unmodifiableSet(elevatorGames.keySet());
    }

    public StartedElevatorServer removeElevatorGame(String pseudo) {
        Player player = new Player("", pseudo);
        if (elevatorGames.containsKey(player)) {
            ElevatorGame game = elevatorGames.get(player);
            game.stop();
            elevatorGames.remove(player);
        }
        return this;
    }

    public PlayerInfo getPlayerInfo(String pseudo) throws PlayerNotFoundException {
        Player player = new Player("", pseudo);
        if (!elevatorGames.containsKey(player)) {
            throw new PlayerNotFoundException("Player not found");
        }
        return elevatorGames.get(player).getPlayerInfo();
    }

    public Collection<ElevatorGame> getUnmodifiableElevatorGames() {
        return Collections.unmodifiableCollection(elevatorGames.values());
    }

}
