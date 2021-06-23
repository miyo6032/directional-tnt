package net.barribob.directionaltnt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class DirectionalExplosion extends Explosion {
    public DirectionalExplosion(World world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, Direction direction) {
        super(world, entity, damageSource, behavior, x + direction.getOffsetX() * -4, y + direction.getOffsetY() * -4, z + direction.getOffsetZ() * -4, power, createFire, destructionType);
    }
}
