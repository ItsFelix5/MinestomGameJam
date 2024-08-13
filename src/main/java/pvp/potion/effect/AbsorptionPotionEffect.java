package pvp.potion.effect;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.PotionEffect;
import pvp.utils.CombatVersion;

public class AbsorptionPotionEffect extends CombatPotionEffect {
    public AbsorptionPotionEffect() {
        super(PotionEffect.ABSORPTION);
    }

    @Override
    public void onApplied(LivingEntity entity, byte amplifier, CombatVersion version) {
        if (entity instanceof Player player) {
            player.setAdditionalHearts(player.getAdditionalHearts() + (float) (4 * (amplifier + 1)));
        }

        super.onApplied(entity, amplifier, version);
    }

    @Override
    public void onRemoved(LivingEntity entity, byte amplifier, CombatVersion version) {
        if (entity instanceof Player player) {
            player.setAdditionalHearts(Math.max(player.getAdditionalHearts() - (float) (4 * (amplifier + 1)), 0));
        }

        super.onRemoved(entity, amplifier, version);
    }
}
