package gamejam;

import gamejam.blocks.Gray;
import gamejam.buffs.Buff;
import gamejam.buffs.Buffs;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashSet;
import java.util.Set;

public enum Teams {
    A(new Pos(22, 125, 263), new BlockVec(18, 127, 259)),
    B(new Pos(50, -52, 128), new BlockVec(25, -51, 126)),
    C(new Pos(-70, 123, 272), new BlockVec(-68, 125, 267)),
    D(new Pos(-72, 148, 137), new BlockVec(-74, 150, 136));

    private final Pos core;
    public final BlockVec deck;

    Teams(Pos core, BlockVec deck) {
        this.core = core;
        this.deck = deck;
    }

    public Team create(Instance instance, Set<Player> members) {
        return new Team(this, instance, members);
    }

    public static class Team {
        public final Teams constants;
        private final net.minestom.server.scoreboard.Team team;
        private final Instance instance;
        private final Set<Player> members;
        private final Set<Buff> buffs = new HashSet<>();
        private final EventNode<PlayerEvent> node;

        private Team(Teams constants, Instance instance, Set<Player> members) {
            this.constants = constants;
            this.instance = instance;
            this.team =
                    MinecraftServer.getTeamManager().createBuilder(constants.name()).prefix(Component.text("[" + constants.name() + "] ")).nameTagVisibility
                            (TeamsPacket.NameTagVisibility.HIDE_FOR_OTHER_TEAMS).build();
            this.members = members;
            members.forEach(player -> {
                player.setGameMode(GameMode.ADVENTURE);//Just in case
                player.setHealth(20);
                player.setFood(20);
                player.setFoodSaturation(20);
                team.addMember(player.getUsername());
                buffs.forEach(b -> b.add(player));
                player.setInstance(instance, constants.core.add(0, 1, 0));
                player.playSound(Sound.sound(SoundEvent.ITEM_GOAT_HORN_SOUND_0, Sound.Source.AMBIENT, 1, 1));
                Kit.equip(player.getInventory());
            });

            node = EventNode.value(constants.name(), EventFilter.PLAYER, members::contains);
            node.addListener(PlayerDeathEvent.class, event -> {
                Player player = event.getPlayer();
                player.setGameMode(GameMode.SPECTATOR);
                player.setRespawnPoint(player.getPosition());
                player.getInventory().setItemStack(9, ItemStack.of(Material.ARROW, 64));
                player.getInventory().setItemStack(8, ItemStack.of(Material.COOKED_BEEF, 64));
                if (buffs.isEmpty()) {
                    members.remove(player);
                    buffs.forEach(b -> b.remove(player));
                    team.removeMember(player.getUsername());
                    if (members.isEmpty()) remove();
                    else updateScoreboard();
                } else {
                    player.showTitle(Title.title(Component.text("You Died!", NamedTextColor.RED), Component.empty()));
                    instance.scheduler().scheduleTask(() -> {
                        player.stopSpectating();
                        player.teleport(constants.core.add(0, 1, 0));
                        player.setGameMode(GameMode.ADVENTURE);
                    }, TaskSchedule.seconds(10), TaskSchedule.stop());
                }
            });
            node.addListener(PlayerDisconnectEvent.class, event -> {
                Player player = event.getPlayer();
                event.getInstance().sendMessage(Component.text(player.getUsername() + " disconnected", NamedTextColor.YELLOW));
                buffs.forEach(b -> b.remove(player));
                player.setRespawnPoint(Pos.ZERO);
                members.remove(player);
                if (members.isEmpty()) remove();
                else if (buffs.isEmpty()) updateScoreboard();
            });
            MinecraftServer.getGlobalEventHandler().addChild(node);
            instance.setBlock(constants.core.sub(0, 1, 0), Block.DIAMOND_BLOCK);
        }

        public void addBuff(Buff buff) {
            members.forEach(buff::add);
            if (buffs.isEmpty()) instance.setBlock(constants.core.sub(0, 1, 0), Block.DIAMOND_BLOCK);
            buffs.add(buff);
            instance.sendMessage(Component.text("Team "+constants.name()+" now has ").append(buff.getName()));
            members.forEach(p -> {
                p.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.AMBIENT, 1, 1));
                p.sendActionBar(Component.text("A new buff has been added!", NamedTextColor.GREEN));
                p.sendMessage(Component.text(buff.description(), NamedTextColor.GREEN));
            });
            updateScoreboard();
        }

        public void removeBuff(Buff buff) {
            buffs.remove(buff);
            members.forEach(buff::remove);
            if (buffs.isEmpty()) {
                instance.setBlock(constants.core.sub(0, 1, 0), Block.CRYING_OBSIDIAN);
                instance.sendMessage(Component.text("Team " + constants.name() + " has lost all buffs!", NamedTextColor.YELLOW));
                instance.getPlayers().forEach(p -> p.playSound(Sound.sound(SoundEvent.BLOCK_BEACON_DEACTIVATE, Sound.Source.AMBIENT, 1, 1)));
                members.forEach(p -> p.showTitle(Title.title(Component.text("All your buffs are gone!", NamedTextColor.RED),
                        Component.text("You cannot respawn unless a buff gets returned", NamedTextColor.RED))));
            } else {
                members.forEach(p -> {
                    p.playSound(Sound.sound(SoundEvent.ENTITY_BEE_STING, Sound.Source.AMBIENT, 1, 1));
                    p.showTitle(Title.title(Component.empty(), Component.text("Your ").append(buff.getName()).append(Component.text(" has been " +
                            "stolen!"))));
                });
                instance.sendMessage(Component.text("Team " + constants.name() + " has lost a buff!",
                        NamedTextColor.YELLOW));
            }
            updateScoreboard();
        }

        private void remove() {
            instance.sendMessage(Component.text("Team " + constants.name() + " has been eliminated!", NamedTextColor.RED));
            instance.getPlayers().forEach(p -> p.playSound(Sound.sound(SoundEvent.EVENT_MOB_EFFECT_RAID_OMEN, Sound.Source.AMBIENT, 10, .9f)));
            instance.setBlock(constants.core, Gray.GRAY);
            MinecraftServer.getGlobalEventHandler().removeChild(node);
            MinecraftServer.getTeamManager().deleteTeam(team);
            Game game = Main.games.get(instance);
            game.teams.remove(constants);
            game.scoreboard.removeLine(constants.name());
            if (game.teams.size() < 2) {
                if (!game.teams.isEmpty())
                    instance.sendMessage(Component.text("Team " + game.teams.keySet().stream().findAny().get().name() +
                                    " won!",
                            NamedTextColor.GOLD));
                game.teams.clear();
                instance.getPlayers().forEach(p -> {
                    game.scoreboard.removeViewer(p);
                    Buffs.buffs.values().forEach(b -> b.remove(p));
                    p.getInventory().clear();
                    p.setHealth(20);
                    p.setVelocity(Vec.ZERO);
                    p.setRespawnPoint(Pos.ZERO);
                    p.setInstance(Main.lobby, Pos.ZERO);
                    p.setGameMode(GameMode.ADVENTURE);
                });
                MinecraftServer.getInstanceManager().unregisterInstance(instance);
            }
        }

        private void updateScoreboard() {
            Sidebar scoreboard = Main.games.get(instance).scoreboard;
            final Component text;
            if (buffs.isEmpty()) text = Component.text(members.size(), NamedTextColor.RED);
            else text = buffs.stream().map(b -> Component.text("\u2B1B", b.getName().color(), TextDecoration.BOLD)).reduce(Component.empty(), TextComponent::append);
            scoreboard.updateLineContent(constants.name(), Component.text("Team " + constants.name() + ": ", NamedTextColor.WHITE).append(text));
        }
    }
}
