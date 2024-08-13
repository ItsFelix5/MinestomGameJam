package pvp.events;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class PlayerFallDamageEvent implements PlayerInstanceEvent, CancellableEvent {
    private final Player player;
    private final double distance;
    private final Block block;
    private int damage;
    private boolean cancelled;

    public PlayerFallDamageEvent(Player player, int damage, double distance, Block block) {
        this.player = player;
        this.damage = damage;
        this.distance = distance;
        this.block = block;
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public double getDistance() {
        return distance;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
