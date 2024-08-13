package gamejam.buffs;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.Material;
import pvp.feature.CombatFeatures;
import pvp.feature.FeatureType;
import pvp.feature.cooldown.ItemCooldownFeature;

public class DashBuff extends Buff {
    private static final ItemCooldownFeature cooldown = CombatFeatures.FEATURES.get(FeatureType.ITEM_COOLDOWN);

    public DashBuff() {
        addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            Material item = player.getItemInMainHand().material();
            if (!item.name().contains("sword") || cooldown.hasCooldown(player, item)) return;
            CombatFeatures.FEATURES.get(FeatureType.TRIDENT).applyRiptide(player, 1);
            cooldown.setCooldown(player, item, 100);
            event.setCancelled(true);
        });
    }

    @Override
    public String description() {
        return "Right click your sword to dash!";
    }

    @Override
    public TextColor color() {
        return NamedTextColor.BLUE;
    }

    @Override
    protected String name() {
        return "Dash";
    }
}
