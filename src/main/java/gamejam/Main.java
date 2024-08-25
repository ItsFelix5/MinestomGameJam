package gamejam;

import blocks.MinestomBlocks;
import gamejam.blocks.Deck;
import gamejam.reimplementation.Doors;
import gamejam.reimplementation.Drops;
import net.hollowcube.polar.PolarLoader;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.HSVLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.InstanceRegisterEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import pvp.enchantment.CombatEnchantments;
import pvp.feature.CombatFeatures;
import pvp.feature.FeatureType;
import pvp.feature.config.CombatFeatureRegistry;
import pvp.player.CombatPlayer;
import pvp.player.CombatPlayerImpl;
import pvp.potion.effect.CombatPotionEffects;
import pvp.potion.item.CombatPotionTypes;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static DynamicRegistry.Key<DimensionType> fullbright;
    public static Instance lobby;
    public static Component colorwars = Component.empty();
    public static Map<Instance, Game> games = new HashMap<>();
    public static IChunkLoader loader;

    public static void main(String[] args) throws IOException {
        MinecraftServer server = MinecraftServer.init();

        init();
        setupMaps();
        handleJoin();
        handlePing();

        registerCommands();

        //BungeeCordProxy.enable();
        server.start("0.0.0.0", 25565);
    }

    private static void setupMaps() {
        DimensionType dimension = DimensionType.builder().ambientLight(2).build();
        fullbright = MinecraftServer.getDimensionTypeRegistry().register("world", dimension);
        lobby = MinecraftServer.getInstanceManager().createInstanceContainer(fullbright);
        lobby.setGenerator(unit -> unit.modifier().fillHeight(-10, 0, Block.GRASS_BLOCK));


        Path path = Path.of("world.polar");
        try {
            if (Files.exists(path)) loader = new PolarLoader(path);
            else loader = new PolarLoader(new URI("https://github.com/ItsFelix5/MinestomGameJam/raw/master/world.polar").toURL().openStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void init() {
        MinecraftServer.setBrandName("ColorWars");

        MinecraftServer.getBlockManager().registerBlockPlacementRule(new Deck());

        CombatEnchantments.registerAll();
        CombatPotionEffects.registerAll();
        CombatPotionTypes.registerAll();
        CombatFeatureRegistry.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(CombatPlayerImpl::new);
        MinecraftServer.getGlobalEventHandler().addChild(CombatFeatures.FEATURES.createNode());
        CombatPlayer.init(MinecraftServer.getGlobalEventHandler());
        MinecraftServer.getGlobalEventHandler().addListener(InstanceRegisterEvent.class,
                event -> event.getInstance().setExplosionSupplier(CombatFeatures.FEATURES.get(FeatureType.EXPLOSION).getExplosionSupplier()));

        MinestomBlocks.init();

        Drops.implement();
        Doors.implement();
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
            player.setReducedDebugScreenInformation(true);
            player.setEnableRespawnScreen(false);
            player.setGameMode(GameMode.ADVENTURE);
            if (player.getUsername().equals("ItsFelix___")) {
                event.getPlayer().showBossBar(MSPT);
                event.getPlayer().showBossBar(RAM);
                event.getPlayer().setPermissionLevel(4);
            }
        });
    }

    private static void registerCommands() {
        Command startCommand = new Command("start");
        startCommand.setDefaultExecutor(((sender, ctx) -> {
            Player player = (Player) sender;
            if (player.getPermissionLevel() < 3 && (player.getInstance() != lobby || lobby.getPlayers().stream().anyMatch(p -> p.getPermissionLevel() > 3)))
                return;
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

        Command gamemode = new Command("gamemode");
        Argument<GameMode> gamemodeArgument = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        gamemode.setCondition(((sender, s) -> ((Player) sender).getPermissionLevel() > 1));
        gamemode.addSyntax((sender, ctx) -> ((Player) sender).setGameMode(ctx.get("gamemode")), gamemodeArgument);
        MinecraftServer.getCommandManager().register(gamemode);
    }
}
