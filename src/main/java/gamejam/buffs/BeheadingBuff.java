package gamejam.buffs;

import gamejam.Utils;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import pvp.events.FinalAttackEvent;

public class BeheadingBuff extends Buff {
    BeheadingBuff() {
        addListener(FinalAttackEvent.class, event -> {
            if (!((Player) event.getEntity()).getItemInMainHand().material().name().contains("_axe") || !event.isCritical() || Utils.random.nextInt(4) != 0)
                return;
            event.getTarget().addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 1, 400));
            event.getTarget().addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 200));
            event.getTarget().addEffect(new Potion(PotionEffect.WEAKNESS, (byte) 1, 200));
        });
    }

    @Override
    public void add(Player player) {
        super.add(player);
    }

    @Override
    public String description() {
        return "When axe critting someone there is a 1/3 chance for them to be weakened and slowed.";
    }

    @Override
    public TextColor color() {
        return TextColor.color(30, 30, 30);
    }

    @Override
    protected String name() {
        return "Beheading";
    }
}
