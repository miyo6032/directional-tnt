package net.barribob.directionaltnt;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DirectionalTntEntity extends TntEntity {
    public DirectionalTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(world, x, y, z, igniter);
    }

    public DirectionalTntEntity(EntityType<DirectionalTntEntity> entityType, World world) {
        super(entityType, world);
    }
}
