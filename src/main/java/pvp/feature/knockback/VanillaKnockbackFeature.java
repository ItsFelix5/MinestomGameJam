package pvp.feature.knockback;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.network.packet.server.play.HitAnimationPacket;
import org.jetbrains.annotations.Nullable;
import pvp.events.EntityKnockbackEvent;
import pvp.feature.CombatFeature;
import pvp.feature.FeatureType;
import pvp.feature.config.DefinedFeature;
import pvp.feature.config.FeatureConfiguration;
import pvp.player.CombatPlayer;
import pvp.utils.CombatVersion;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Vanilla implementation of {@link KnockbackFeature}
 */
public class VanillaKnockbackFeature implements KnockbackFeature, CombatFeature {
    public static final DefinedFeature<VanillaKnockbackFeature> DEFINED = new DefinedFeature<>(
            FeatureType.KNOCKBACK, VanillaKnockbackFeature::new,
            FeatureType.VERSION
    );

    private final FeatureConfiguration configuration;

    private CombatVersion version;

    public VanillaKnockbackFeature(FeatureConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initDependencies() {
        this.version = configuration.get(FeatureType.VERSION);
    }

    @Override
    public boolean applyDamageKnockback(Damage damage, LivingEntity target) {
        Entity attacker = damage.getAttacker();
        Entity source = damage.getSource();

        double dx = attacker.getPosition().x() - target.getPosition().x();
        double dz = attacker.getPosition().z() - target.getPosition().z();

        // Randomize direction
        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (dx * dx + dz * dz < 0.0001) {
            dx = random.nextDouble(-1, 1) * 0.01;
            dz = random.nextDouble(-1, 1) * 0.01;
        }

        // Set the velocity
        if (!applyModernKnockback(target, attacker, source,
                EntityKnockbackEvent.KnockbackType.DAMAGE, 0.4f, dx, dz)) return false;

        // Send player a packet with its hurt direction
        if (target instanceof Player player) {
            float hurtDir = (float) (Math.toDegrees(Math.atan2(dz, dx)) - player.getPosition().yaw());
            player.sendPacket(new HitAnimationPacket(player.getEntityId(), hurtDir));
        }

        return true;
    }

    protected boolean applyModernKnockback(LivingEntity target, Entity attacker, @Nullable Entity source,
                                           EntityKnockbackEvent.KnockbackType type, float strength,
                                           double dx, double dz) {
        EntityKnockbackEvent knockbackEvent = new EntityKnockbackEvent(
                target, source == null ? attacker : source,
                type, strength
        );
        EventDispatcher.call(knockbackEvent);
        if (knockbackEvent.isCancelled()) return false;

        target.takeKnockback(knockbackEvent.getStrength(), dx, dz);
        return true;
    }

    @Override
    public boolean applyAttackKnockback(LivingEntity attacker, LivingEntity target, int knockback) {
        if (knockback <= 0) return false;

        double dx = Math.sin(Math.toRadians(attacker.getPosition().yaw()));
        double dz = -Math.cos(Math.toRadians(attacker.getPosition().yaw()));

        if (!applyModernKnockback(
                target, attacker, attacker,
                EntityKnockbackEvent.KnockbackType.ATTACK, knockback * 0.5f,
                dx, dz
        )) return false;

        if (attacker instanceof CombatPlayer custom)
            custom.afterSprintAttack();

        attacker.setSprinting(false);
        return true;
    }

    @Override
    public boolean applySweepingKnockback(LivingEntity attacker, LivingEntity target) {
        double dx = Math.sin(Math.toRadians(attacker.getPosition().yaw()));
        double dz = -Math.cos(Math.toRadians(attacker.getPosition().yaw()));

        return applyModernKnockback(
                target, attacker, null,
                EntityKnockbackEvent.KnockbackType.SWEEPING,
                0.4f, dx, dz
        );
    }
}
