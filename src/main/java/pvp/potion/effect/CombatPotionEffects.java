package pvp.potion.effect;

import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.NamespaceID;

import java.util.HashMap;
import java.util.Map;

public class CombatPotionEffects {
    private static final Map<PotionEffect, CombatPotionEffect> POTION_EFFECTS = new HashMap<>();

    public static CombatPotionEffect get(PotionEffect potionEffect) {
        return POTION_EFFECTS.get(potionEffect);
    }

    public static void register(CombatPotionEffect... potionEffects) {
        for (CombatPotionEffect potionEffect : potionEffects) {
            POTION_EFFECTS.put(potionEffect.getPotionEffect(), potionEffect);
        }
    }

    public static void registerAll() {
        register(
                new CombatPotionEffect(PotionEffect.SPEED).addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, NamespaceID.from("minecraft:effect.speed"), 0.2, AttributeOperation.MULTIPLY_TOTAL),
                new CombatPotionEffect(PotionEffect.SLOWNESS).addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, NamespaceID.from("minecraft:effect.slowness"), -0.15, AttributeOperation.MULTIPLY_TOTAL),
                new CombatPotionEffect(PotionEffect.HASTE).addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, NamespaceID.from("minecraft:effect.haste"), 0.1, AttributeOperation.MULTIPLY_TOTAL),
                new CombatPotionEffect(PotionEffect.MINING_FATIGUE).addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, NamespaceID.from("minecraft:effect.mining_fatigue"), -0.1, AttributeOperation.MULTIPLY_TOTAL),
                new CombatPotionEffect(PotionEffect.STRENGTH).addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, NamespaceID.from("minecraft:effect.strength"), 3.0, AttributeOperation.ADD_VALUE).addLegacyAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, NamespaceID.from("minecraft:effect.strength"), 1.3, AttributeOperation.MULTIPLY_TOTAL),
                new CombatPotionEffect(PotionEffect.INSTANT_HEALTH),
                new CombatPotionEffect(PotionEffect.INSTANT_DAMAGE),
                new CombatPotionEffect(PotionEffect.JUMP_BOOST).addAttributeModifier(Attribute.GENERIC_SAFE_FALL_DISTANCE, NamespaceID.from("minecraft:effect.jump_boost"), 1.0, AttributeOperation.ADD_VALUE),
                new CombatPotionEffect(PotionEffect.NAUSEA),
                new CombatPotionEffect(PotionEffect.REGENERATION),
                new CombatPotionEffect(PotionEffect.RESISTANCE),
                new CombatPotionEffect(PotionEffect.FIRE_RESISTANCE),
                new CombatPotionEffect(PotionEffect.WATER_BREATHING),
                new CombatPotionEffect(PotionEffect.INVISIBILITY),
                new CombatPotionEffect(PotionEffect.BLINDNESS),
                new CombatPotionEffect(PotionEffect.NIGHT_VISION),
                new CombatPotionEffect(PotionEffect.HUNGER),
                new CombatPotionEffect(PotionEffect.WEAKNESS).addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, NamespaceID.from("minecraft:effect.weakness"), -4.0, AttributeOperation.ADD_VALUE).addLegacyAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, NamespaceID.from("minecraft:effect.weakness"), -0.5, AttributeOperation.ADD_VALUE),
                new CombatPotionEffect(PotionEffect.POISON),
                new CombatPotionEffect(PotionEffect.WITHER),
                new HealthBoostPotionEffect().addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, NamespaceID.from("minecraft:effect.health_boost"), 4.0, AttributeOperation.ADD_VALUE),
                new AbsorptionPotionEffect(),
                new CombatPotionEffect(PotionEffect.SATURATION),
                new GlowingPotionEffect(),
                new CombatPotionEffect(PotionEffect.LEVITATION),
                new CombatPotionEffect(PotionEffect.LUCK).addAttributeModifier(Attribute.GENERIC_LUCK, NamespaceID.from("minecraft:effect.luck"), 1.0, AttributeOperation.ADD_VALUE),
                new CombatPotionEffect(PotionEffect.UNLUCK).addAttributeModifier(Attribute.GENERIC_LUCK, NamespaceID.from("minecraft:effect.unluck"), -1.0, AttributeOperation.ADD_VALUE),
                new CombatPotionEffect(PotionEffect.SLOW_FALLING),
                new CombatPotionEffect(PotionEffect.CONDUIT_POWER),
                new CombatPotionEffect(PotionEffect.DOLPHINS_GRACE),
                new CombatPotionEffect(PotionEffect.BAD_OMEN),
                new CombatPotionEffect(PotionEffect.HERO_OF_THE_VILLAGE),
                new CombatPotionEffect(PotionEffect.DARKNESS),
                new CombatPotionEffect(PotionEffect.TRIAL_OMEN, potion -> Particle.TRIAL_OMEN),
                new CombatPotionEffect(PotionEffect.RAID_OMEN, potion -> Particle.RAID_OMEN),
                new CombatPotionEffect(PotionEffect.WIND_CHARGED, potion -> Particle.SMALL_GUST),
                new CombatPotionEffect(PotionEffect.WEAVING),
                new CombatPotionEffect(PotionEffect.OOZING),
                new CombatPotionEffect(PotionEffect.INFESTED)
        );
    }
}
