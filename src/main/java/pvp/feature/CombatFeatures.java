package pvp.feature;

import pvp.feature.armor.VanillaArmorFeature;
import pvp.feature.attack.VanillaAttackFeature;
import pvp.feature.attack.VanillaCriticalFeature;
import pvp.feature.attack.VanillaSweepingFeature;
import pvp.feature.attributes.VanillaEquipmentFeature;
import pvp.feature.block.VanillaBlockFeature;
import pvp.feature.config.CombatConfiguration;
import pvp.feature.config.DefinedFeature;
import pvp.feature.config.FeatureConfiguration;
import pvp.feature.cooldown.VanillaAttackCooldownFeature;
import pvp.feature.cooldown.VanillaItemCooldownFeature;
import pvp.feature.damage.VanillaDamageFeature;
import pvp.feature.effect.VanillaEffectFeature;
import pvp.feature.enchantment.VanillaEnchantmentFeature;
import pvp.feature.explosion.VanillaExplosionFeature;
import pvp.feature.explosion.VanillaExplosiveFeature;
import pvp.feature.fall.VanillaFallFeature;
import pvp.feature.food.VanillaExhaustionFeature;
import pvp.feature.food.VanillaFoodFeature;
import pvp.feature.food.VanillaRegenerationFeature;
import pvp.feature.item.VanillaItemDamageFeature;
import pvp.feature.knockback.VanillaKnockbackFeature;
import pvp.feature.potion.VanillaPotionFeature;
import pvp.feature.projectile.*;
import pvp.feature.provider.DifficultyProvider;
import pvp.feature.spectate.VanillaSpectateFeature;
import pvp.feature.state.VanillaPlayerStateFeature;
import pvp.feature.totem.VanillaTotemFeature;
import pvp.feature.tracking.VanillaDeathMessageFeature;

import java.util.List;

/**
 * Contains all the vanilla features which have been defined by MinestomPvP.
 * <p>
 * It also contains {@link CombatFeatureSet} instances which can be used to get a full vanilla combat experience.
 * See {@link CombatFeatures#FEATURES}.
 * <p>
 * It is also possible to gain more control over which features are used, see {@link CombatFeatures#empty()}.
 */
public class CombatFeatures {
    /**
     * @see VanillaArmorFeature
     */
    public static final DefinedFeature<VanillaArmorFeature> VANILLA_ARMOR = VanillaArmorFeature.DEFINED;
    /**
     * @see VanillaAttackFeature
     */
    public static final DefinedFeature<VanillaAttackFeature> VANILLA_ATTACK = VanillaAttackFeature.DEFINED;
    /**
     * @see VanillaCriticalFeature
     */
    public static final DefinedFeature<VanillaCriticalFeature> VANILLA_CRITICAL = VanillaCriticalFeature.DEFINED;
    /**
     * @see VanillaSweepingFeature
     */
    public static final DefinedFeature<VanillaSweepingFeature> VANILLA_SWEEPING = VanillaSweepingFeature.DEFINED;
    /**
     * @see VanillaEquipmentFeature
     */
    public static final DefinedFeature<VanillaEquipmentFeature> VANILLA_EQUIPMENT = VanillaEquipmentFeature.DEFINED;
    /**
     * @see VanillaBlockFeature
     */
    public static final DefinedFeature<VanillaBlockFeature> VANILLA_BLOCK = VanillaBlockFeature.DEFINED;
    /**
     * @see VanillaAttackCooldownFeature
     */
    public static final DefinedFeature<VanillaAttackCooldownFeature> VANILLA_ATTACK_COOLDOWN = VanillaAttackCooldownFeature.DEFINED;
    /**
     * @see VanillaItemCooldownFeature
     */
    public static final DefinedFeature<VanillaItemCooldownFeature> VANILLA_ITEM_COOLDOWN = VanillaItemCooldownFeature.DEFINED;
    /**
     * @see VanillaDamageFeature
     */
    public static final DefinedFeature<VanillaDamageFeature> VANILLA_DAMAGE = VanillaDamageFeature.DEFINED;
    /**
     * @see VanillaEffectFeature
     */
    public static final DefinedFeature<VanillaEffectFeature> VANILLA_EFFECT = VanillaEffectFeature.DEFINED;
    /**
     * @see VanillaEnchantmentFeature
     */
    public static final DefinedFeature<VanillaEnchantmentFeature> VANILLA_ENCHANTMENT = VanillaEnchantmentFeature.DEFINED;
    /**
     * @see VanillaExplosionFeature
     */
    public static final DefinedFeature<VanillaExplosionFeature> VANILLA_EXPLOSION = VanillaExplosionFeature.DEFINED;
    /**
     * @see VanillaExplosiveFeature
     */
    public static final DefinedFeature<VanillaExplosiveFeature> VANILLA_EXPLOSIVE = VanillaExplosiveFeature.DEFINED;
    /**
     * @see VanillaFallFeature
     */
    public static final DefinedFeature<VanillaFallFeature> VANILLA_FALL = VanillaFallFeature.DEFINED;
    /**
     * @see VanillaExhaustionFeature
     */
    public static final DefinedFeature<VanillaExhaustionFeature> VANILLA_EXHAUSTION = VanillaExhaustionFeature.DEFINED;
    /**
     * @see VanillaFoodFeature
     */
    public static final DefinedFeature<VanillaFoodFeature> VANILLA_FOOD = VanillaFoodFeature.DEFINED;
    /**
     * @see VanillaRegenerationFeature
     */
    public static final DefinedFeature<VanillaRegenerationFeature> VANILLA_REGENERATION = VanillaRegenerationFeature.DEFINED;
    /**
     * @see VanillaItemDamageFeature
     */
    public static final DefinedFeature<VanillaItemDamageFeature> VANILLA_ITEM_DAMAGE = VanillaItemDamageFeature.DEFINED;
    /**
     * @see VanillaKnockbackFeature
     */
    public static final DefinedFeature<VanillaKnockbackFeature> VANILLA_KNOCKBACK = VanillaKnockbackFeature.DEFINED;
    /**
     * @see VanillaPotionFeature
     */
    public static final DefinedFeature<VanillaPotionFeature> VANILLA_POTION = VanillaPotionFeature.DEFINED;
    /**
     * @see VanillaBowFeature
     */
    public static final DefinedFeature<VanillaBowFeature> VANILLA_BOW = VanillaBowFeature.DEFINED;
    /**
     * @see VanillaCrossbowFeature
     */
    public static final DefinedFeature<VanillaCrossbowFeature> VANILLA_CROSSBOW = VanillaCrossbowFeature.DEFINED;
    /**
     * @see VanillaFishingRodFeature
     */
    public static final DefinedFeature<VanillaFishingRodFeature> VANILLA_FISHING_ROD = VanillaFishingRodFeature.DEFINED;
    /**
     * @see VanillaMiscProjectileFeature
     */
    public static final DefinedFeature<VanillaMiscProjectileFeature> VANILLA_MISC_PROJECTILE = VanillaMiscProjectileFeature.DEFINED;
    /**
     * @see VanillaProjectileItemFeature
     */
    public static final DefinedFeature<VanillaProjectileItemFeature> VANILLA_PROJECTILE_ITEM = VanillaProjectileItemFeature.DEFINED;
    /**
     * @see VanillaTridentFeature
     */
    public static final DefinedFeature<VanillaTridentFeature> VANILLA_TRIDENT = VanillaTridentFeature.DEFINED;
    /**
     * @see VanillaSpectateFeature
     */
    public static final DefinedFeature<VanillaSpectateFeature> VANILLA_SPECTATE = VanillaSpectateFeature.DEFINED;
    /**
     * @see VanillaPlayerStateFeature
     */
    public static final DefinedFeature<VanillaPlayerStateFeature> VANILLA_PLAYER_STATE = VanillaPlayerStateFeature.DEFINED;
    /**
     * @see VanillaTotemFeature
     */
    public static final DefinedFeature<VanillaTotemFeature> VANILLA_TOTEM = VanillaTotemFeature.DEFINED;
    /**
     * @see VanillaDeathMessageFeature
     */
    public static final DefinedFeature<VanillaDeathMessageFeature> VANILLA_DEATH_MESSAGE = VanillaDeathMessageFeature.DEFINED;

    private static final List<DefinedFeature<?>> VANILLA = List.of(
            VANILLA_ARMOR, VANILLA_ATTACK, VANILLA_CRITICAL, VANILLA_SWEEPING,
            VANILLA_EQUIPMENT, VANILLA_BLOCK, VANILLA_ATTACK_COOLDOWN, VANILLA_ITEM_COOLDOWN,
            VANILLA_DAMAGE, VANILLA_EFFECT, VANILLA_ENCHANTMENT, VANILLA_EXPLOSION,
            VANILLA_EXPLOSIVE, VANILLA_FALL, VANILLA_EXHAUSTION, VANILLA_FOOD,
            VANILLA_REGENERATION, VANILLA_KNOCKBACK, VANILLA_POTION,
            VANILLA_BOW, VANILLA_CROSSBOW, VANILLA_FISHING_ROD, VANILLA_MISC_PROJECTILE,
            VANILLA_PROJECTILE_ITEM, VANILLA_TRIDENT, VANILLA_PLAYER_STATE,
            VANILLA_TOTEM, VANILLA_DEATH_MESSAGE
    );

    public static final CombatFeatureSet FEATURES = getVanilla(DifficultyProvider.DEFAULT).build();

    /**
     * Returns a feature set with all features for the given combat version and difficulty provider.
     *
     * @param difficultyProvider the difficulty provider
     * @return the {@link CombatFeatureSet} with all features
     */
    public static CombatConfiguration getVanilla(DifficultyProvider difficultyProvider) {
        return new CombatConfiguration().difficulty(difficultyProvider)
                .addAll(VANILLA);
    }

    /**
     * Utility method to get an empty {@link CombatConfiguration} to which features can be added.
     *
     * @return the empty configuration
     */
    public static CombatConfiguration empty() {
        return new CombatConfiguration();
    }

    /**
     * Utility method to construct a single feature without dependencies in a clean way.
     *
     * @param feature the single feature implementation to construct
     * @return the constructed feature
     */
    public static CombatFeature single(DefinedFeature<?> feature) {
        return feature.construct(new FeatureConfiguration());
    }
}
