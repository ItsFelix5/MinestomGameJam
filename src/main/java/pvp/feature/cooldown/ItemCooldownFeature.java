package pvp.feature.cooldown;

import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import pvp.feature.CombatFeature;

/**
 * Combat feature to manage a players item cooldown animation.
 */
public interface ItemCooldownFeature extends CombatFeature {
    ItemCooldownFeature NO_OP = new ItemCooldownFeature() {
        @Override
        public boolean hasCooldown(Player player, Material material) {
            return false;
        }

        @Override
        public void setCooldown(Player player, Material material, int ticks) {
        }
    };

    boolean hasCooldown(Player player, Material material);

    void setCooldown(Player player, Material material, int ticks);
}
