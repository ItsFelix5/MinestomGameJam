package pvp.feature.totem;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import pvp.damage.DamageTypeInfo;
import pvp.events.TotemUseEvent;
import pvp.feature.CombatFeature;
import pvp.feature.FeatureType;
import pvp.feature.config.DefinedFeature;
import pvp.utils.PotionFlags;

/**
 * Vanilla implementation of {@link TotemFeature}
 */
public class VanillaTotemFeature implements TotemFeature, CombatFeature {
    public static final DefinedFeature<VanillaTotemFeature> DEFINED = new DefinedFeature<>(
            FeatureType.TOTEM, configuration -> new VanillaTotemFeature()
    );

    @Override
    public boolean tryProtect(LivingEntity entity, DamageType type) {
        if (DamageTypeInfo.of(MinecraftServer.getDamageTypeRegistry().getKey(type)).outOfWorld()) return false;

        boolean hasTotem = false;
        for (Player.Hand hand : Player.Hand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (stack.material() == Material.TOTEM_OF_UNDYING) {
                TotemUseEvent totemUseEvent = new TotemUseEvent(entity, hand);
                EventDispatcher.call(totemUseEvent);

                if (totemUseEvent.isCancelled()) continue;

                hasTotem = true;
                entity.setItemInHand(hand, stack.withAmount(stack.amount() - 1));
                break;
            }
        }

        if (hasTotem) {
            entity.setHealth(1.0f);
            entity.clearEffects();
            entity.addEffect(new Potion(PotionEffect.REGENERATION, (byte) 1, 900, PotionFlags.defaultFlags()));
            entity.addEffect(new Potion(PotionEffect.ABSORPTION, (byte) 1, 100, PotionFlags.defaultFlags()));
            entity.addEffect(new Potion(PotionEffect.FIRE_RESISTANCE, (byte) 0, 800, PotionFlags.defaultFlags()));

            // Totem particles
            entity.triggerStatus((byte) 35);
        }

        return hasTotem;
    }
}
