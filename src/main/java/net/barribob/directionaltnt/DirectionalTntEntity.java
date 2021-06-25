package net.barribob.directionaltnt;

import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
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

    @Override
    public EntityType<?> getType() {
        return DirectionalTnt.DIRECTIONAL_TNT_ENTITY;
    }

    @Override
    protected Text getDefaultName() {
        return getType().getName();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return getType().getDimensions();
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, direction.getId());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        this.direction = Direction.byId(packet.getEntityData());
        super.onSpawnPacket(packet);
    }

    public Direction getDirection() {
        return direction;
    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putShort("Direction", (short)this.direction.getId());
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.direction = Direction.byId(nbt.getShort("Direction"));
    }
}
