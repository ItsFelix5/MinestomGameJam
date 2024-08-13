package pvp.entity.projectile;

import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.other.FishingHookMeta;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import pvp.feature.projectile.VanillaFishingRodFeature;

public class FishingBobber extends CustomEntityProjectile {
    private final double customGravity;
    private int stuckTime;
    private Entity hooked;
    private State state = State.IN_AIR;

    public FishingBobber(@Nullable Entity shooter) {
        super(shooter, EntityType.FISHING_BOBBER);
        setOwnerEntity(shooter);

        // Custom gravity logic: gravity is applied before movement
        customGravity = 0.03;
        setAerodynamics(getAerodynamics().withGravity(0));

        // Minestom seems to like having wrong values in its registries
        setAerodynamics(getAerodynamics().withHorizontalAirResistance(0.92).withVerticalAirResistance(0.92));
    }

    @Override
    public void tick(long time) {
        if(!hasNoGravity()) velocity = velocity.add(0, -customGravity * ServerFlag.SERVER_TICKS_PER_SECOND, 0);
        super.tick(time);
    }

    @Override
    public void update(long time) {
        if (!(getShooter() instanceof Player shooter)) {
            remove();
            return;
        }

        if (onGround) {
            stuckTime++;
            if (stuckTime >= 1200) {
                remove();
                return;
            }
        } else stuckTime = 0;

        if (state == State.IN_AIR) {
            if (hooked != null) {
                velocity = Vec.ZERO;
                setNoGravity(true);
                state = State.HOOKED_ENTITY;
            }
        } else {
            if (state == State.HOOKED_ENTITY) {
                if (hooked != null) {
                    if (hooked.isRemoved() || hooked.getInstance() != getInstance()) {
                        setHookedEntity(null);
                        setNoGravity(false);
                        state = State.IN_AIR;
                    } else {
                        Pos hookedPos = hooked.getPosition();
                        teleport(hookedPos.withY(hookedPos.y() + hooked.getBoundingBox().height() * 0.8));
                    }
                }
            }
        }
    }

    @Override
    public boolean onHit(Entity entity) {
        if (hooked != null) return false;
        setHookedEntity(entity);
        return false;
    }

    private void setHookedEntity(@Nullable Entity entity) {
        this.hooked = entity;
        ((FishingHookMeta) getEntityMeta()).setHookedEntity(entity);
    }

    public void setInBlock() {
        velocity = Vec.ZERO;
        state = State.HOOKED_BLOCK;
        setNoGravity(true);
    }

    private void setOwnerEntity(@Nullable Entity entity) {
        ((FishingHookMeta) getEntityMeta()).setOwnerEntity(entity);
    }

    public int retrieve() {
        if (!(getShooter() instanceof Player shooter)) return 0;

        int durability = 0;
        if (hooked != null) {
            pullEntity(hooked);
            triggerStatus((byte) 31);
            durability = hooked instanceof ItemEntity ? 3 : 5;
        } else if (state == State.HOOKED_BLOCK) {
            shooter.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(8);
            shooter.setVelocity(shooter.getVelocity().add(getPosition().sub(shooter.getPosition()).mul(5)));
        }

        remove();

        return durability;
    }

    private void pullEntity(Entity entity) {
        Entity shooter = getShooter();
        if (shooter == null) return;

        Pos shooterPos = shooter.getPosition();
        Pos pos = getPosition();
        Vec velocity = new Vec(shooterPos.x() - pos.x(), shooterPos.y() - pos.y(),
                shooterPos.z() - pos.z());
        velocity = velocity.mul(ServerFlag.SERVER_TICKS_PER_SECOND);
        entity.setVelocity(entity.getVelocity().add(velocity));
    }

    @Override
    public void remove() {
        Entity shooter = getShooter();
        if (shooter != null) {
            if (shooter.getTag(VanillaFishingRodFeature.FISHING_BOBBER) == this) {
                shooter.removeTag(VanillaFishingRodFeature.FISHING_BOBBER);
            }
        }

        super.remove();
    }

    private enum State {
        IN_AIR,
        HOOKED_ENTITY,
        HOOKED_BLOCK,
        BOBBING
    }
}
