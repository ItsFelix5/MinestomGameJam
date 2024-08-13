package pvp.feature.attack;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import pvp.feature.CombatFeature;

/**
 * Combat feature which handles an entity attacking another entity.
 */
public interface AttackFeature extends CombatFeature {
    AttackFeature NO_OP = (attacker, target) -> false;

    /**
     * Performs an attack on the target entity.
     *
     * @param attacker the attacking entity
     * @param target   the target entity
     * @return whether the attack was successful
     */
    boolean performAttack(LivingEntity attacker, Entity target);
}
