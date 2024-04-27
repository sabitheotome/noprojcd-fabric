package me.sabitheotome.noprojcd.mixin;

import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.sabitheotome.noprojcd.NoProjectileCooldown;

@Mixin(MobEntity.class)
public class MobEntityMixin {

	@Inject(at = @At(value = "HEAD"), method = "mobTick")
	private void mobTick(CallbackInfo info) {
		if (!NoProjectileCooldown.isEnabled.get())
			return;

		var self = (MobEntity) (Object) this;
		var explosionIntensity = 1;
		SoundEvent sound = null;

		if (self instanceof GhastEntity) {
			sound = SoundEvents.ENTITY_GHAST_SHOOT;
		} else if (self instanceof BlazeEntity) {
			sound = SoundEvents.ENTITY_BLAZE_SHOOT;
		} else {
			return;
		}

		if (self.isDead() || self.getTarget() == null) {
			return;
		}
		var world = self.getWorld();
		double gx = self.getX();
		double gy = self.getY();
		double gz = self.getZ();
		double tx = self.getTarget().getX();
		double ty = self.getTarget().getY();
		double tz = self.getTarget().getZ();
		double dist = Math.sqrt(Math.pow(gx - tx, 2.0) + Math.pow(gy - ty, 2.0) + Math.pow(gz - tz, 2.0));

		var projectileEntity = new FireballEntity(world, self, 0, 0, 0, explosionIntensity);
		projectileEntity.setVelocity((tx - gx) / dist, (ty - gy) / dist, (tz - gz) / dist);
		projectileEntity.setPos(gx + (tx - gx) / dist, gy + (ty - gy) / dist, gz + (tz - gz) / dist);
		world.playSound((PlayerEntity) null, self.getX(), self.getY(), self.getZ(),
				sound, SoundCategory.NEUTRAL, 0.5F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!world.isClient) {
			world.spawnEntity(projectileEntity);
		}
	}
}