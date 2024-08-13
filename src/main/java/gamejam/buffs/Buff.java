package gamejam.buffs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.EntityEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Buff {
    private final Set<Player> players = new HashSet<>();

    protected <E extends EntityEvent> void addListener(Class<E> eventType, Consumer<E> listener) {
        MinecraftServer.getGlobalEventHandler().addListener(eventType, event -> {
            if (event.getEntity() instanceof Player p && players.contains(p)) listener.accept(event);
        });
    }

    public void add(Player player) {
        players.add(player);
    }

    public void remove(Player player) {
        players.remove(player);
    }

    public abstract TextColor color();

    protected abstract String name();

    public abstract String description();

    public Component getName() {
        return Component.text(name(), color());
    }
}
