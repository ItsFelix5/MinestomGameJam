package gamejam.buffs;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerStartFlyingEvent;

public class DoubleJumpBuff extends Buff {
    public DoubleJumpBuff() {
        addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.isOnGround()) {
                player.setAllowFlying(true);
                player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(3);
            }
        });
        addListener(PlayerStartFlyingEvent.class, event -> {
            Player player = event.getPlayer();
            player.setAllowFlying(false);
            player.setFlying(false);
            player.setVelocity(player.getVelocity().mul(3).withY(12.5));
            player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(9);
        });
    }

    @Override
    public void remove(Player player) {
        super.remove(player);
        player.setAllowFlying(false);
        player.setFlying(false);
        player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(3);
    }

    @Override
    public String description() {
        return "Double tap the spacebar to jump a second time!";
    }

    @Override
    public TextColor color() {
        return NamedTextColor.DARK_GREEN;
    }

    @Override
    protected String name() {
        return "Double jump";
    }
}
