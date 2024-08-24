package gamejam;

import gamejam.buffs.Buffs;
import net.hollowcube.polar.PolarLoader;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.block.Block;
import net.minestom.server.scoreboard.Sidebar;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Game {
    public final Map<Teams, Teams.Team> teams = new HashMap<>();
    public final Instance instance;
    public final Sidebar scoreboard;

    public Game(List<Player> players) {
        try {
            instance = MinecraftServer.getInstanceManager().createInstanceContainer(Main.fullbright, new PolarLoader(Path.of("world.polar")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        instance.setWorldBorder(new WorldBorder(150, 0, 200, 0, 0));

        this.scoreboard = new Sidebar(Main.colorwars);
        players.forEach(scoreboard::addViewer);

        //very professional team creation system
        int totalPlayers = Math.min(25, players.size());
        if (totalPlayers == 1) {
            teams.computeIfAbsent(Teams.A, k -> k.create(instance, new HashSet<>(players)));
        } else if (totalPlayers == 2) {
            teams.computeIfAbsent(Teams.A, k -> k.create(instance, new HashSet<>(players.subList(0, 1))));
            teams.computeIfAbsent(Teams.B, k -> k.create(instance, new HashSet<>(players.subList(1, 2))));
        } else if (totalPlayers == 3) {
            teams.computeIfAbsent(Teams.A, k -> k.create(instance, new HashSet<>(players.subList(0, 2))));
            teams.computeIfAbsent(Teams.B, k -> k.create(instance, new HashSet<>(players.subList(2, 3))));
        } else {
            int numTeams = Math.min(4, totalPlayers);
            int playersPerTeam = totalPlayers / numTeams;
            int remainder = totalPlayers % numTeams;

            for (int i = 0; i < numTeams * playersPerTeam + remainder; i += playersPerTeam) {
                if(remainder > 0) i++;
                int j = i;
                teams.computeIfAbsent(Teams.values()[i % 4], k -> k.create(instance,
                        new HashSet<>(players.subList(j, j + playersPerTeam))));
            }
        }
        teams.forEach((c, t) -> scoreboard.createLine(new Sidebar.ScoreboardLine(c.name(), Component.empty(), 1, Sidebar.NumberFormat.blank())));

        instance.scheduler().scheduleNextTick(()->{
            ArrayList<Block> buffs = new ArrayList<>(Buffs.buffs.keySet());
            teams.forEach((team, t)->{
                int i = Utils.random.nextInt(buffs.size());
                instance.setBlock(team.deck, buffs.get(i));
                buffs.remove(i);
                i = Utils.random.nextInt(buffs.size());
                instance.setBlock(team.deck.add(team.ordinal()%2==0?1:0, 0, team.ordinal()%2==1?1:0), buffs.get(i));
                buffs.remove(i);
            });
        });
    }
}
