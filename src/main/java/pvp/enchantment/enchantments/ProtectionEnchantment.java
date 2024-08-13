package pvp.enchantment.enchantments;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;
import pvp.damage.DamageTypeInfo;
import pvp.enchantment.CombatEnchantment;
import pvp.feature.config.FeatureConfiguration;
import pvp.feature.enchantment.EnchantmentFeature;

public class ProtectionEnchantment extends CombatEnchantment {
    private final Type type;

    public ProtectionEnchantment(DynamicRegistry.Key<Enchantment> enchantment, Type type, EquipmentSlot... slotTypes) {
        super(enchantment, slotTypes);
        this.type = type;
    }

    @Override
    public int getProtectionAmount(int level, DamageType damageType,
                                   EnchantmentFeature feature, FeatureConfiguration configuration) {
        DamageTypeInfo damageTypeInfo = DamageTypeInfo.of(MinecraftServer.getDamageTypeRegistry().getKey(damageType));
        if (damageTypeInfo.outOfWorld()) {
            return 0;
        } else if (type == Type.ALL) {
            return level;
        } else if (type == Type.FIRE && damageTypeInfo.fire()) {
            return level * 2;
        } else if (type == Type.FALL && damageTypeInfo.fall()) {
            return level * 3;
        } else if (type == Type.EXPLOSION && damageTypeInfo.explosive()) {
            return level * 2;
        } else {
            return type == Type.PROJECTILE && damageTypeInfo.projectile() ? level * 2 : 0;
        }
    }

    public enum Type {
        ALL, FIRE, FALL, EXPLOSION, PROJECTILE
    }
}
