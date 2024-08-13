package gamejam.buffs;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import pvp.events.PlayerRegenerateEvent;

public class AdrenalineBuff extends Buff {
    public AdrenalineBuff() {
        addListener(EntityDamageEvent.class, event -> update((Player) event.getEntity()));
        addListener(PlayerRegenerateEvent.class, event -> update(event.getPlayer()));
    }

    public void update(Player player) {
        float hp = player.getHealth();
        byte speed = (byte) Math.ceil((-1f / 5f) * hp + 2f);
        if (speed > 0) player.addEffect(new Potion(PotionEffect.SPEED, speed, -1));
        else player.removeEffect(PotionEffect.SPEED);

        byte strength = 0;
        if (hp < 4) strength++;
        if (hp < 2) strength++;
        if (strength > 0) player.addEffect(new Potion(PotionEffect.STRENGTH, strength, -1));
        else player.removeEffect(PotionEffect.STRENGTH);
    }

    @Override
    public void remove(Player player) {
        super.remove(player);
        player.removeEffect(PotionEffect.STRENGTH);
        player.removeEffect(PotionEffect.SPEED);
    }

    @Override
    public String description() {
        return "The lower your health gets the faster you can run and the stronger you get.";
    }

    @Override
    public TextColor color() {
        return NamedTextColor.YELLOW;
    }

    @Override
    protected String name() {
        return "Adrenaline";
    }
}
