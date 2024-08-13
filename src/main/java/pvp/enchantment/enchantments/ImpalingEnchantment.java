package pvp.enchantment.enchantments;

import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.item.enchant.Enchantment;
import pvp.enchantment.CombatEnchantment;
import pvp.enchantment.EntityGroup;
import pvp.feature.config.FeatureConfiguration;
import pvp.feature.enchantment.EnchantmentFeature;

public class ImpalingEnchantment extends CombatEnchantment {
    public ImpalingEnchantment(EquipmentSlot... slotTypes) {
        super(Enchantment.IMPALING, slotTypes);
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group,
                                 EnchantmentFeature feature, FeatureConfiguration configuration) {
        return group == EntityGroup.AQUATIC ? (float) level * 2.5F : 0.0F;
    }
}
