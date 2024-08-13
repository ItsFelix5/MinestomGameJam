package gamejam.buffs;

import gamejam.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class GrappleBuff extends Buff {
    GrappleBuff() {
        addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.isOnGround()) player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(3);
        });
    }

    @Override
    public void add(Player player) {
        player.getInventory().addItemStack(ItemStack.of(Material.FISHING_ROD).withCustomName(Component.text("Grapple Hook", color())));
    }

    @Override
    public void remove(Player player) {
        Utils.findItemsOf(player.getInventory(), Material.FISHING_ROD, i->ItemStack.AIR);
        player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(3);
    }

    @Override
    public String description() {
        return "Launch yourself or other players with this grapple hook!";
    }

    @Override
    public TextColor color() {
        return TextColor.color(140, 76, 0);
    }

    @Override
    protected String name() {
        return "Grapple";
    }
}
