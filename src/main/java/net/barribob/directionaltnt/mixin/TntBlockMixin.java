package net.barribob.directionaltnt.mixin;

import net.barribob.directionaltnt.DirectionalTntBlock;
import net.barribob.directionaltnt.DirectionalTntEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin {
    @Inject(at = @At("HEAD"), method = "primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)V", cancellable = true)
    private static void onPrimeTnt(World world, BlockPos pos, LivingEntity igniter, CallbackInfo ci) {
        BlockState state = world.getBlockState(pos);
        if (!world.isClient && state.getBlock() instanceof DirectionalTntBlock && state.contains(DirectionalTntBlock.FACING)) {
            DirectionalTntEntity tntEntity = new DirectionalTntEntity(world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, igniter, state.get(DirectionalTntBlock.FACING));
            world.spawnEntity(tntEntity);
            world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
            ci.cancel();
        }
    }
}
