package net.barribob.directionaltnt.mixin;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.barribob.directionaltnt.DirectionalExplosion;
import net.barribob.directionaltnt.DirectionalTnt;
import net.barribob.directionaltnt.DirectionalTntBlock;
import net.barribob.directionaltnt.DirectionalTntEntity;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private World world;

    @Shadow @Final @Nullable private Entity entity;

    @Shadow @Final private float power;

    @Shadow @Final private double x;

    @Shadow @Final private double y;

    @Shadow @Final private double z;

    @Shadow @Final private ExplosionBehavior behavior;

    @Shadow @Final private List<BlockPos> affectedBlocks;

    @Shadow public abstract DamageSource getDamageSource();

    @Shadow @Final private Map<PlayerEntity, Vec3d> affectedPlayers;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), method = "affectWorld", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void beforeBlockDestroy(boolean particles, CallbackInfo ci, boolean b1, ObjectArrayList objectArrayList, Iterator var4, BlockPos blockPos, BlockState blockState) {
        Explosion explosion = (Explosion) (Object) this;
        if (!world.isClient && blockState.getBlock() == DirectionalTnt.DIRECTIONAL_TNT && blockState.contains(DirectionalTntBlock.FACING)) {
            DirectionalTntEntity tntEntity = new DirectionalTntEntity(world, (double) blockPos.getX() + 0.5D, blockPos.getY(), (double) blockPos.getZ() + 0.5D, explosion.getCausingEntity(), blockState.get(DirectionalTntBlock.FACING));
            int i = tntEntity.getFuse();
            tntEntity.setFuse((short) (world.random.nextInt(i / 4) + i / 8));
            world.spawnEntity(tntEntity);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "collectBlocksAndDamageEntities", cancellable = true)
    public void onCollectBlocksAndDamageEntities(CallbackInfo ci){
        Explosion explosion = (Explosion) (Object) this;

        if(explosion instanceof DirectionalExplosion directionalExplosion) {
            doDirectionalExplosion(explosion, directionalExplosion);
            ci.cancel();
        }
    }

    private void doDirectionalExplosion(Explosion explosion, DirectionalExplosion directionalExplosion) {
        this.world.emitGameEvent(entity, GameEvent.EXPLODE, new BlockPos(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();
        Direction direction = directionalExplosion.getDirection();
        int boxSize = 24;
        int boxMaxX = boxSize + Math.min(0, direction.getOffsetX() * 12);
        int boxMaxY = boxSize + Math.min(0, direction.getOffsetY() * 12);
        int boxMaxZ = boxSize + Math.min(0, direction.getOffsetZ() * 12);
        int boxMinX = Math.max(0, direction.getOffsetX() * 12);
        int boxMinY = Math.max(0, direction.getOffsetY() * 12);
        int boxMinZ = Math.max(0, direction.getOffsetZ() * 12);
        float boxMax = boxSize - 1;
        int k;
        int l;
        for(int j = boxMinX; j < boxMaxX; ++j) {
            for(k = boxMinY; k < boxMaxY; ++k) {
                for(l = boxMinZ; l < boxMaxZ; ++l) {
                    if (j == 0 || j == boxMax || k == 0 || k == boxMax || l == 0 || l == boxMax) {
                        double d = (float)j / boxMax * 2.0F - 1.0F;
                        double e = (float)k / boxMax * 2.0F - 1.0F;
                        double f = (float)l / boxMax * 2.0F - 1.0F;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;

                        // Added for direction
                        float h = DirectionalExplosion.calculatePower(direction, d, e, f, power, world);
                        // End added for direction

                        double m = x;
                        double n = y;
                        double o = z;

                        for(; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = new BlockPos(m, n, o);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            FluidState fluidState = this.world.getFluidState(blockPos);
                            if (!this.world.isInBuildLimit(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = behavior.getBlastResistance(explosion, this.world, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.225F; // Changed multiplier from 0.3 to 0.225
                            }

                            if (h > 0.0F && this.behavior.canDestroyBlock(explosion, this.world, blockPos, blockState, h)) {
                                set.add(blockPos);
                            }

                            m += d * 0.30000001192092896D;
                            n += e * 0.30000001192092896D;
                            o += f * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        affectedBlocks.addAll(set);
        float q = this.power * 2.0F;
        k = MathHelper.floor(this.x - (double)q - 1.0D);
        l = MathHelper.floor(this.x + (double)q + 1.0D);
        int t = MathHelper.floor(this.y - (double)q - 1.0D);
        int u = MathHelper.floor(this.y + (double)q + 1.0D);
        int v = MathHelper.floor(this.z - (double)q - 1.0D);
        int w = MathHelper.floor(this.z + (double)q + 1.0D);
        List<Entity> list = this.world.getOtherEntities(this.entity, new Box(k, t, v, l, u, w));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

        for (Entity entity : list) {
            if (!entity.isImmuneToExplosion()) {
                double distance = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double) q;
                if (distance <= 1.0D) {
                    double x1 = entity.getX() - this.x;
                    double aa = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                    double ab = entity.getZ() - this.z;
                    double ac = Math.sqrt(x1 * x1 + aa * aa + ab * ab);
                    if (ac != 0.0D) {
                        x1 /= ac;
                        aa /= ac;
                        ab /= ac;
                        double ad = DirectionalExplosion.getExposure(vec3d, entity, direction); // Changed to use direction
                        if(ad > 0) {
                            double ae = (1.0D - distance) * ad;
                            entity.damage(getDamageSource(), (float) ((int) ((ae * ae + ae) / 2.0D * 7.0D * (double) q + 1.0D)));
                            double af = ae;
                            if (entity instanceof LivingEntity) {
                                af = ProtectionEnchantment.transformExplosionKnockback((LivingEntity) entity, ae);
                            }

                            entity.setVelocity(entity.getVelocity().add(x1 * af, aa * af, ab * af));
                            if (entity instanceof PlayerEntity playerEntity) {
                                if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                                    affectedPlayers.put(playerEntity, new Vec3d(x1 * ae, aa * ae, ab * ae));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
