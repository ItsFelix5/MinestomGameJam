package pvp.feature.attack;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.trait.EntityInstanceEvent;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import pvp.enchantment.EntityGroup;
import pvp.enums.Tool;
import pvp.events.FinalAttackEvent;
import pvp.feature.FeatureType;
import pvp.feature.RegistrableFeature;
import pvp.feature.config.DefinedFeature;
import pvp.feature.config.FeatureConfiguration;
import pvp.feature.cooldown.AttackCooldownFeature;
import pvp.feature.enchantment.EnchantmentFeature;
import pvp.feature.food.ExhaustionFeature;
import pvp.feature.item.ItemDamageFeature;
import pvp.feature.knockback.KnockbackFeature;
import pvp.player.CombatPlayer;
import pvp.utils.CombatVersion;
import pvp.utils.ViewUtil;

/**
 * Vanilla implementation of {@link AttackFeature}
 * <p>
 * Listens on {@link EntityAttackEvent}
 */
public class VanillaAttackFeature implements AttackFeature, RegistrableFeature {
    public static final DefinedFeature<VanillaAttackFeature> DEFINED = new DefinedFeature<>(
            FeatureType.ATTACK, VanillaAttackFeature::new,
            FeatureType.ATTACK_COOLDOWN, FeatureType.EXHAUSTION, FeatureType.ITEM_DAMAGE,
            FeatureType.ENCHANTMENT, FeatureType.CRITICAL, FeatureType.SWEEPING, FeatureType.KNOCKBACK, FeatureType.VERSION
    );

    private static final double MAX_DISTANCE_SQUARED = 36.0;

    private final FeatureConfiguration configuration;

    private AttackCooldownFeature cooldownFeature;
    private ExhaustionFeature exhaustionFeature;
    private ItemDamageFeature itemDamageFeature;
    private EnchantmentFeature enchantmentFeature;

    private CriticalFeature criticalFeature;
    private SweepingFeature sweepingFeature;
    private KnockbackFeature knockbackFeature;

    private CombatVersion version;

    public VanillaAttackFeature(FeatureConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initDependencies() {
        this.cooldownFeature = configuration.get(FeatureType.ATTACK_COOLDOWN);
        this.exhaustionFeature = configuration.get(FeatureType.EXHAUSTION);
        this.itemDamageFeature = configuration.get(FeatureType.ITEM_DAMAGE);
        this.enchantmentFeature = configuration.get(FeatureType.ENCHANTMENT);
        this.criticalFeature = configuration.get(FeatureType.CRITICAL);
        this.sweepingFeature = configuration.get(FeatureType.SWEEPING);
        this.knockbackFeature = configuration.get(FeatureType.KNOCKBACK);
        this.version = configuration.get(FeatureType.VERSION);
    }

    @Override
    public void init(EventNode<EntityInstanceEvent> node) {
        node.addListener(EntityAttackEvent.class, event -> {
            if (event.getEntity() instanceof Player player
                    && player.getGameMode() != GameMode.SPECTATOR
                    && !player.isDead()
                    && player.getDistanceSquared(event.getTarget()) < MAX_DISTANCE_SQUARED) {
                performAttack(player, event.getTarget());
            }
        });
    }

    @Override
    public boolean performAttack(LivingEntity attacker, Entity target) {
        AttackValues.Final attack = prepareAttack(attacker, target);
        if (attack == null) return false; // Event cancelled

        float originalHealth = 0;
        boolean damageSucceeded = false;
        if (target instanceof LivingEntity livingTarget) {
            originalHealth = livingTarget.getHealth();
            damageSucceeded = livingTarget.damage(new Damage(
                    attacker instanceof Player ? DamageType.PLAYER_ATTACK : DamageType.MOB_ATTACK,
                    attacker, attacker,
                    null, attack.damage()
            ));
        }

        if (!damageSucceeded) {
            // No damage sound
            if (attack.sounds() && attack.playSoundsOnFail()) {
                ViewUtil.viewersAndSelf(attacker).playSound(Sound.sound(
                        SoundEvent.ENTITY_PLAYER_ATTACK_NODAMAGE, Sound.Source.PLAYER,
                        1.0f, 1.0f
                ), attacker);
            }
            return false;
        }

        // Target is always living now, because the damage would not have succeeded if it wasn't
        LivingEntity living = (LivingEntity) target;

        // Knockback and sweeping
        knockbackFeature.applyAttackKnockback(attacker, living, attack.knockback());
        if (attack.sweeping()) sweepingFeature.applySweeping(attacker, living, attack.damage());

        if (target instanceof CombatPlayer custom)
            custom.sendImmediateVelocityUpdate();

        // Play attack sounds
        if (attack.sounds()) {
            Audience audience = attacker.getViewersAsAudience();
            if (attacker instanceof Player player)
                audience = Audience.audience(audience, player);

            if (attack.sprint()) audience.playSound(Sound.sound(
                    SoundEvent.ENTITY_PLAYER_ATTACK_KNOCKBACK, Sound.Source.PLAYER,
                    1.0f, 1.0f
            ), attacker);

            if (attack.sweeping()) audience.playSound(Sound.sound(
                    SoundEvent.ENTITY_PLAYER_ATTACK_SWEEP, Sound.Source.PLAYER,
                    1.0f, 1.0f
            ), attacker);

            if (attack.critical()) audience.playSound(Sound.sound(
                    SoundEvent.ENTITY_PLAYER_ATTACK_CRIT, Sound.Source.PLAYER,
                    1.0f, 1.0f
            ), attacker);

            if (!attack.critical() && !attack.sweeping()) audience.playSound(Sound.sound(
                    attack.strong() ?
                            SoundEvent.ENTITY_PLAYER_ATTACK_STRONG :
                            SoundEvent.ENTITY_PLAYER_ATTACK_WEAK,
                    Sound.Source.PLAYER, 1.0f, 1.0f
            ), attacker);
        }

        // Play attack effects
        if (attack.critical()) attacker.sendPacketToViewersAndSelf(new EntityAnimationPacket(
                target.getEntityId(),
                EntityAnimationPacket.Animation.CRITICAL_EFFECT
        ));
        if (attack.magical()) attacker.sendPacketToViewersAndSelf(new EntityAnimationPacket(
                target.getEntityId(),
                EntityAnimationPacket.Animation.MAGICAL_CRITICAL_EFFECT
        ));

        // Thorns
        enchantmentFeature.onUserDamaged(living, attacker);
        enchantmentFeature.onTargetDamaged(attacker, target);

        // Damage item
        Tool tool = Tool.fromMaterial(attacker.getItemInMainHand().material());
        if (tool != null) itemDamageFeature.damageEquipment(attacker, EquipmentSlot.MAIN_HAND,
                (tool.isSword() || tool == Tool.TRIDENT) ? 1 : 2);

        if (attack.fireAspect() > 0)
            living.setFireTicks(attack.fireAspect() * 4 * ServerFlag.SERVER_TICKS_PER_SECOND);

        // Damage indicator particles
        float damageDone = originalHealth - living.getHealth();
        if (damageDone > 2) {
            int particleCount = (int) (damageDone * 0.5);
            Pos targetPosition = target.getPosition();
            target.sendPacketToViewersAndSelf(new ParticlePacket(
                    Particle.DAMAGE_INDICATOR, false,
                    targetPosition.x(), targetPosition.y() + target.getBoundingBox().height() * 0.5, targetPosition.z(),
                    0.1f, 0, 0.1f,
                    0.2F, particleCount
            ));
        }

        if (attacker instanceof Player player)
            exhaustionFeature.addAttackExhaustion(player);

        return true;
    }

    protected @Nullable AttackValues.Final prepareAttack(LivingEntity attacker, Entity target) {
        float damage = (float) attacker.getAttributeValue(Attribute.GENERIC_ATTACK_DAMAGE);
        float magicalDamage = enchantmentFeature.getAttackDamage(
                attacker.getItemInMainHand(),
                target instanceof LivingEntity living ? EntityGroup.ofEntity(living) : EntityGroup.DEFAULT
        );

        double cooldownProgress = 1;
        if (attacker instanceof Player player) {
            cooldownProgress = cooldownFeature.getAttackCooldownProgress(player);
            cooldownFeature.resetCooldownProgress(player);
        }

        // Apply cooldownProgress to damage
        damage *= (float) (0.2 + cooldownProgress * cooldownProgress * 0.8);
        magicalDamage *= (float) cooldownProgress;

        // Calculate attacks
        boolean strongAttack = cooldownProgress > 0.9;
        boolean sprintAttack = attacker.isSprinting() && strongAttack;
        int knockback = enchantmentFeature.getKnockback(attacker);
        int fireAspect = enchantmentFeature.getFireAspect(attacker);

        // Use features to determine critical and sweeping
        AttackValues.PreCritical preCritical = new AttackValues.PreCritical(
                damage, magicalDamage, cooldownProgress,
                strongAttack, sprintAttack, knockback, fireAspect
        );
        AttackValues.PreSweeping preSweeping = preCritical.withCritical(criticalFeature.shouldCrit(attacker, preCritical));
        AttackValues.PreSounds preSounds = preSweeping.withSweeping(sweepingFeature.shouldSweep(attacker, preSweeping));

        boolean critical = preSounds.critical();
        boolean sweeping = preSounds.sweeping();

        // Call event which can modify attack values
        FinalAttackEvent finalAttackEvent = new FinalAttackEvent(
                attacker, target, sprintAttack, critical, sweeping, damage,
                magicalDamage, true, true
        );
        EventDispatcher.call(finalAttackEvent);
        if (finalAttackEvent.isCancelled()) return null;

        sprintAttack = finalAttackEvent.isSprint();
        critical = finalAttackEvent.isCritical();
        sweeping = finalAttackEvent.isSweeping();
        damage = finalAttackEvent.getBaseDamage();
        magicalDamage = finalAttackEvent.getEnchantsExtraDamage();

        // Apply critical damage and knockback
        if (critical) damage = criticalFeature.applyToDamage(damage);
        damage += magicalDamage;

        if (sprintAttack) knockback++;

        return new AttackValues.Final(
                damage, strongAttack, sprintAttack, knockback, critical,
                magicalDamage > 0, fireAspect, sweeping,
                finalAttackEvent.hasAttackSounds(),
                finalAttackEvent.playSoundsOnFail()
        );
    }
}
