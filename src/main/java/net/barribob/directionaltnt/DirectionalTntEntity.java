package net.barribob.directionaltnt;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DirectionalTntEntity extends TntEntity {
    private Direction direction;

    public DirectionalTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, Direction direction) {
        super(world, x, y, z, igniter);
        this.direction = direction;
    }

    public DirectionalTntEntity(EntityType<DirectionalTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public Direction getDirection() {
        return direction;
    }
}
