package gamejam.buffs;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import pvp.events.PlayerFallDamageEvent;

public class SlamBuff extends Buff {
    public SlamBuff() {
        addListener(PlayerStartSneakingEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.isOnGround()) return;
            player.setVelocity(player.getVelocity().withY(-50));
        });
        addListener(PlayerFallDamageEvent.class, event -> {
            Player player = event.getPlayer();
            if (!player.isSneaking()) return;
            event.setCancelled(true);
            final Point pos = player.getPosition();
            double fallDistance = Math.max(5, event.getDistance() / 2);
            player.sendPacketToViewersAndSelf(new ParticlePacket(Particle.BLOCK.withBlock(event.getBlock()), false,
                    pos.x(), pos.y(), pos.z(), (float) fallDistance / 5, 0, (float) fallDistance / 5, 0.5f, 500));
            player.getViewersAsAudience().playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_BIG_FALL, Sound.Source.PLAYER, 5.0f, 1.0f), player);
            event.getInstance().getEntityTracker().nearbyEntities(pos, fallDistance, EntityTracker.Target.ENTITIES, e -> {
                if (!(e instanceof LivingEntity entity) || player == entity) return;
                Vec vec = Vec.fromPoint(entity.getPosition()).sub(pos);
                entity.setVelocity(entity.getVelocity().add(vec).withY(3).add(fallDistance));
                entity.damage(Damage.fromPlayer(player, (float) (Math.min(10, event.getDamage()) - vec.length())));
            });
        });
    }

    @Override
    public String description() {
        return "Shift mid air to slam downwards and knock everyone out of the way!";
    }

    @Override
    public TextColor color() {
        return NamedTextColor.RED;
    }

    @Override
    protected String name() {
        return "Slam";
    }
}
