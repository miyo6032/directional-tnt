package net.barribob.directionaltnt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DirectionalExplosion extends Explosion {
    private Direction direction;

    public DirectionalExplosion(World world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, Direction direction) {
        super(world, entity, damageSource, behavior, x, y, z, power * 3, createFire, destructionType);
        this.direction = direction;
    }

    public DirectionalExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks, Direction direction) {
        super(world, entity, x, y, z, power * 3, affectedBlocks);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public static float calculatePower(Direction direction, double d, double e, double f, float power, World world) {
        float dot = Math.max((float) (d * direction.getOffsetX() + e * direction.getOffsetY() + f * direction.getOffsetZ()), 0);
        return power * (0.8F + world.random.nextFloat() * 0.4F) * dot * dot;
    }

    public static float getExposure(Vec3d source, Entity entity, Direction direction) {
        Box box = entity.getBoundingBox();
        double d = 1.0D / ((box.maxX - box.minX) * 2.0D + 1.0D);
        double e = 1.0D / ((box.maxY - box.minY) * 2.0D + 1.0D);
        double f = 1.0D / ((box.maxZ - box.minZ) * 2.0D + 1.0D);
        double g = (1.0D - Math.floor(1.0D / d) * d) / 2.0D;
        double h = (1.0D - Math.floor(1.0D / f) * f) / 2.0D;
        Vec3d dir = new Vec3d(direction.getUnitVector());
        if (!(d < 0.0D) && !(e < 0.0D) && !(f < 0.0D)) {
            float i = 0;
            int j = 0;

            for(float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
                for(float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
                    for(float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
                        double n = MathHelper.lerp(k, box.minX, box.maxX);
                        double o = MathHelper.lerp(l, box.minY, box.maxY);
                        double p = MathHelper.lerp(m, box.minZ, box.maxZ);
                        Vec3d vec3d = new Vec3d(n + g, o, p + h);
                        if (entity.world.raycast(new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS) {
                            // Changed to factor in direction
                            double dot = Math.max(vec3d.subtract(source).normalize().dotProduct(dir), 0);
                            i += dot * dot;
                            // End change
                        }

                        ++j;
                    }
                }
            }

            return Math.round(i) / (float)j;
        } else {
            return 0.0F;
        }
    }
}
