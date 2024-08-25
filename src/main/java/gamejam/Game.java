package gamejam;

import gamejam.buffs.Buffs;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.block.Block;
import net.minestom.server.scoreboard.Sidebar;

import java.util.*;

public class Game {
    public final Map<Teams, Teams.Team> teams = new HashMap<>();
    public final Instance instance;
    public final Sidebar scoreboard;

    public Game(List<Player> players) {
        instance = MinecraftServer.getInstanceManager().createInstanceContainer(Main.fullbright, Main.loader);
        instance.setWorldBorder(new WorldBorder(150, 0, 200, 0, 0));

        this.scoreboard = new Sidebar(Main.colorwars);
        players.forEach(scoreboard::addViewer);

        List<Set<Player>> playerTeams = new ArrayList<>();
        for (int i = 0; i < Math.min(25, players.size()); i++) {
            if (playerTeams.size() <= i % 4) playerTeams.add(new HashSet<>());
            playerTeams.get(i % 4).add(players.get(i));
        }
        for (int i = 0; i < playerTeams.size(); i++) {
            Teams team = Teams.values()[i];
            teams.put(team, team.create(instance, new HashSet<>(playerTeams.get(i))));
        }
        teams.forEach((c, t) -> scoreboard.createLine(new Sidebar.ScoreboardLine(c.name(), Component.empty(), 1, Sidebar.NumberFormat.blank())));

        instance.scheduler().scheduleNextTick(() -> {
            ArrayList<Block> buffs = new ArrayList<>(Buffs.buffs.keySet());
            teams.forEach((team, t) -> {
                int i = Utils.random.nextInt(buffs.size());
                instance.setBlock(team.deck, buffs.get(i));
                buffs.remove(i);
                i = Utils.random.nextInt(buffs.size());
                instance.setBlock(team.deck.add(team.ordinal() % 2 == 0 ? 1 : 0, 0, team.ordinal() % 2 == 1 ? 1 : 0), buffs.get(i));
                buffs.remove(i);
            });
        });
    }
}
