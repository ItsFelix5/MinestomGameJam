package pvp.feature.attributes;

import pvp.feature.CombatFeature;

/**
 * Combat feature which handles equipment changes (applies weapon and armor attributes).
 */
public interface EquipmentFeature extends CombatFeature {
    EquipmentFeature NO_OP = new EquipmentFeature() {
    };
}
