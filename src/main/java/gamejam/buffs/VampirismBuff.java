package gamejam.buffs;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.item.Material;
import pvp.events.DamageBlockEvent;
import pvp.events.FinalAttackEvent;

public class VampirismBuff extends Buff {
    VampirismBuff() {
        addListener(FinalAttackEvent.class, event -> {
            Player player = (Player) event.getEntity();
            PlayerMeta meta = (PlayerMeta) event.getTarget().getEntityMeta();
            if (player.getItemInMainHand().material().name().contains("_axe") || (meta.isHandActive() && ((Player) event.getTarget()).getItemInHand(meta.getActiveHand()).material() == Material.SHIELD))
                return;
            player.setHealth(player.getHealth() + event.getBaseDamage() / 4);
        });
        addListener(DamageBlockEvent.class, event -> {
            event.setKnockbackAttacker(true);
            Player player = (Player) event.getEntity();
            player.setHealth(player.getHealth() + Math.min(2, event.getDamage() - event.getResultingDamage()) / 6);
        });
    }

    @Override
    public String description() {
        return "Damage someone or block damage with your shield to heal!";
    }

    @Override
    public TextColor color() {
        return NamedTextColor.LIGHT_PURPLE;
    }

    @Override
    protected String name() {
        return "Vampirism";
    }
}
