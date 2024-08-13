package pvp.potion.effect;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.potion.PotionEffect;
import pvp.utils.CombatVersion;

public class GlowingPotionEffect extends CombatPotionEffect {
    public GlowingPotionEffect() {
        super(PotionEffect.GLOWING);
    }

    @Override
    public void onApplied(LivingEntity entity, byte amplifier, CombatVersion version) {
        entity.setGlowing(true);
    }

    @Override
    public void onRemoved(LivingEntity entity, byte amplifier, CombatVersion version) {
        entity.setGlowing(false);
    }
}
