package pvp.feature.projectile;

import pvp.feature.CombatFeature;

/**
 * Combat feature which handles throwing and retrieving fishing rods.
 */
public interface FishingRodFeature extends CombatFeature {
    FishingRodFeature NO_OP = new FishingRodFeature() {
    };
}
