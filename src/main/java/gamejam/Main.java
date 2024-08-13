package gamejam;

import gamejam.blocks.Deck;
import gamejam.blocks.Gray;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.HSVLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.Direction;
import net.minestom.server.world.DimensionType;
import reimplementation.Reimplementation;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static DynamicRegistry.Key<DimensionType> fullbright;
    public static Instance lobby;
    public static Component colorwars = Component.empty();
    public static Map<Instance, Game> games = new HashMap<>();

    public static void main(String[] args) throws IOException {
        MinecraftServer server = MinecraftServer.init();

        init();
        setupLobby();
        handleJoin();
        handlePing();

        registerStartCommand();
        initDebugUtils();

        server.start("0.0.0.0", 25565);
    }

    private static void setupLobby() {
        DimensionType dimension = DimensionType.builder().ambientLight(2).build();
        fullbright = MinecraftServer.getDimensionTypeRegistry().register("world", dimension);
        lobby = MinecraftServer.getInstanceManager().createInstanceContainer(fullbright);
        lobby.setGenerator(unit -> unit.modifier().fillHeight(-10, 0, Block.GRASS_BLOCK));
    }

    private static void init() {
        MinecraftServer.setBrandName("ColorWars");

        MinecraftServer.getBlockManager().registerBlockPlacementRule(new Deck());
        Reimplementation.reimplementAll();
    }

    private static void handlePing() {
        for (int i = 0; i < 5; i++)
            colorwars = colorwars.append(Component.text("Color".charAt(i), TextColor.color(HSVLike.hsvLike(i / 9F,
                    1, 1)), TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED));
        colorwars = colorwars.appendSpace();
        for (int i = 0; i < 4; i++)
            colorwars = colorwars.append(Component.text("Wars".charAt(i), TextColor.color(HSVLike.hsvLike((5 + i) / 9F,
                    1, 1)), TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED));

        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            responseData.setDescription(colorwars);
            event.setResponseData(responseData);
        });
    }

    private static void handleJoin() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

        BossBar MSPT = BossBar.bossBar(Component.empty(), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        BossBar RAM = BossBar.bossBar(Component.empty(), 1f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        DecimalFormat dec = new DecimalFormat("0.00");
        eventHandler.addListener(ServerTickMonitorEvent.class, e -> {
            double tickTime = Math.floor(e.getTickMonitor().getTickTime() * 100.0) / 100.0;
            MSPT.name(Component.text("MSPT: " + dec.format(tickTime)));
            MSPT.progress(Math.min((float) tickTime / (float) MinecraftServer.TICK_MS, 1f));

            long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            RAM.name(Component.text("RAM: " + used / 1048576 + "mb"));
            RAM.progress(Math.min((float) used / Runtime.getRuntime().totalMemory(), 1f));
        });

        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> event.setSpawningInstance(lobby));

        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            if (!event.isFirstSpawn()) return;
            Player player = event.getPlayer();
            //player.setReducedDebugScreenInformation(true);
            player.setEnableRespawnScreen(false);
            player.setGameMode(GameMode.ADVENTURE);
            if (player.getUsername().equals("ItsFelix___")) {
                player.setPermissionLevel(4);
                MinecraftServer.getBossBarManager().addBossBar(player, MSPT);
                MinecraftServer.getBossBarManager().addBossBar(player, RAM);
            }
        });
    }

    private static void registerStartCommand() {
        Command startCommand = new Command("start");
        startCommand.setDefaultExecutor(((sender, ctx) -> {
            if (!(sender instanceof Player) || ((Player) sender).getInstance() != lobby) return;
            lobby.sendMessage(Component.text(((Player) sender).getUsername() + " started the game"));
            lobby.sendMessage(colorwars);
            lobby.sendMessage(Component.text("""
                    Every team has a deck of colors that give them buffs.
                    If your team has no more buffs you can not respawn.
                    Let the games begin!""", NamedTextColor.YELLOW));
            Game game = new Game(lobby.getPlayers().stream().toList());
            games.put(game.instance, game);
        }));
        MinecraftServer.getCommandManager().register(startCommand);
    }

    private static void initDebugUtils() {
        Command gamemodeCommand = new Command("gamemode");
        ArgumentEnum<GameMode> gamemode = ArgumentType.Enum("gamemode", GameMode.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);
        gamemodeCommand.setCondition((sender, ctx) -> sender instanceof Player && ((Player) sender).getPermissionLevel() > 1);
        gamemodeCommand.addSyntax((sender, ctx) -> ((Player) sender).setGameMode(ctx.get(gamemode)), gamemode);
        MinecraftServer.getCommandManager().register(gamemodeCommand);

        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, (event) -> {
            ItemStack item = event.getPlayer().getItemInHand(event.getHand());
            if (item.isAir()) return;
            Material mat = item.material();
            if (!mat.name().contains("spawn_egg")) return;

            Direction dir = event.getBlockFace().toDirection();
            new EntityCreature(EntityType.fromNamespaceId(mat.namespace().value().replace("_spawn_egg", "").toLowerCase()))
                    .setInstance(event.getPlayer().getInstance(), new Pos(event.getBlockPosition().add(dir.normalX(), dir.normalY(), dir.normalZ())));
        });
    }
}
