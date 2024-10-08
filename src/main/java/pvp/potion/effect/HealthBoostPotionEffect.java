package pvp.potion.effect;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.potion.PotionEffect;
import pvp.utils.CombatVersion;

public class HealthBoostPotionEffect extends CombatPotionEffect {
    public HealthBoostPotionEffect() {
        super(PotionEffect.HEALTH_BOOST);
    }

    @Override
    public void onRemoved(LivingEntity entity, byte amplifier, CombatVersion version) {
        super.onRemoved(entity, amplifier, version);

        if (entity.getHealth() > entity.getAttributeValue(Attribute.GENERIC_MAX_HEALTH)) {
            entity.setHealth((float) entity.getAttributeValue(Attribute.GENERIC_MAX_HEALTH));
        }
    }
}
