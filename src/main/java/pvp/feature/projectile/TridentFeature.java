package pvp.feature.projectile;

import net.minestom.server.entity.Player;
import pvp.feature.CombatFeature;

/**
 * Combat feature which handles using a trident.
 */
public interface TridentFeature extends CombatFeature {
    TridentFeature NO_OP = (player, level) -> {
    };

    void applyRiptide(Player player, int level);
}
