package pvp.feature.item;

import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import pvp.feature.CombatFeature;

/**
 * Combat feature which handles damaging items (durability).
 */
public interface ItemDamageFeature extends CombatFeature {
    ItemDamageFeature NO_OP = new ItemDamageFeature() {
        @Override
        public void damageEquipment(LivingEntity entity, EquipmentSlot slot, int amount) {
        }

        @Override
        public void damageArmor(LivingEntity entity, DamageType damageType, float damage, EquipmentSlot... slots) {
        }
    };

    void damageEquipment(LivingEntity entity, EquipmentSlot slot, int amount);

    void damageArmor(LivingEntity entity, DamageType damageType, float damage, EquipmentSlot... slots);
}
