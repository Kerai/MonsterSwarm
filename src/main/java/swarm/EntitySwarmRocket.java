package swarm;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import swarm.util.Vec3D;

public class EntitySwarmRocket extends Entity implements IProjectile {
	
	public static boolean DISABLED = true;
	
	
	
	private int ticksInAir;
	
	public EntityLivingBase shooter;
	public float explosion;

	public EntitySwarmRocket(World world) {
		super(world);
		//this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
	}
	
	public EntitySwarmRocket(World world, EntityLivingBase shooter) {
		super(world);
		this.shooter = shooter;
		//this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
		this.setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ);
	}

	public void setThrowableHeading(double x, double y, double z, float speed, float spread)
	{
		float f2 = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= (double)f2;
		y /= (double)f2;
		z /= (double)f2;
		x += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)spread;
		y += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)spread;
		z += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)spread;
		x *= (double)speed;
		y *= (double)speed;
		z *= (double)speed;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f3 = MathHelper.sqrt_double(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);
		//this.ticksInGround = 0;
	}
	

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int wtf)
	{
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}


	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(x * x + z * z);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, (double)f) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			//this.ticksInGround = 0;
		}
	}
	
	@Override
	protected void entityInit() {
		//this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		// TODO Auto-generated method stub
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onUpdate() {
		if(DISABLED) {
			this.isDead = true;
			return;
		}
		super.onUpdate();
		
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float block = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)block) * 180.0D / Math.PI);
		}
		

		++ticksInAir;
		
		
		Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
		Vec3d nextpos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		RayTraceResult raytrace = this.worldObj.rayTraceBlocks(pos, nextpos, false, true, false);
		pos = new Vec3d(this.posX, this.posY, this.posZ);

		if (raytrace != null) {
			nextpos = new Vec3d(raytrace.hitVec.xCoord, raytrace.hitVec.yCoord, raytrace.hitVec.zCoord);
		} else {
			nextpos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		}

		Entity entity = null;
		List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getCollisionBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
		double d0 = 0.0D;
		float f1;

		for (int i = 0; i < list.size(); ++i)
		{
			Entity f2 = list.get(i);

			if (f2.canBeCollidedWith() && (this.ticksInAir >= 5) && !(f2 instanceof EntityMob))
			{
				f1 = 0.3F;
				AxisAlignedBB f4 = f2.getCollisionBoundingBox().expand((double)f1, (double)f1, (double)f1);
				RayTraceResult f3 = f4.calculateIntercept(pos, nextpos);

				if (f3 != null)
				{
					double l = pos.distanceTo(f3.hitVec);

					if (l < d0 || d0 == 0.0D)
					{
						entity = f2;
						d0 = l;
					}
				}
			}
		}

		if (entity != null)
		{
			raytrace = new RayTraceResult(entity);
		}

		if (raytrace != null && raytrace.entityHit != null && raytrace.entityHit instanceof EntityPlayer)
		{
			EntityPlayer var19 = (EntityPlayer)raytrace.entityHit;

			if (var19.capabilities.disableDamage || this.shooter instanceof EntityPlayer && !((EntityPlayer)this.shooter).canAttackPlayer(var19))
			{
				raytrace = null;
			}
		}

		if (raytrace != null)
		{
			this.onImpact(raytrace);
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

		for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var20) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
		{
			;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
		{
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F)
		{
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
		{
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
		float var22 = 1F;
		f1 = 0.01F;

		if (this.isInWater())
		{
			for (int var23 = 0; var23 < 4; ++var23)
			{
				float var21 = 0.25F;
				if(ticksInAir%2 == 0)
					this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)var21, this.posY - this.motionY * (double)var21, this.posZ - this.motionZ * (double)var21, this.motionX, this.motionY, this.motionZ);
			}

			var22 = 0.8F;
		}

		if (this.isWet())
		{
			this.extinguish();
		}

		{
			this.motionX *= (double)var22;
			this.motionY *= (double)var22;
			this.motionZ *= (double)var22;
			this.motionY -= (double)f1;
		}

		this.setPosition(this.posX, this.posY, this.posZ);
		this.doBlockCollisions();
		
		//if(worldObj.isRemote)
		this.doFlightSFX();
	}

	private void doFlightSFX() {
		if(ticksInAir > 5)
			this.worldObj.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
		
//		for (int i = 0; i < 4; ++i)
//		{
//			this.worldObj.spawnParticle("fireworksSpark", this.posX + this.motionX * (double)i / 4.0D, this.posY + this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
//		}
	}

	private void onImpact(RayTraceResult raytrace) {
		if(this.worldObj.isRemote)
			return;
		this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ,this.posY > 110 ? 4 : explosion, true);
		this.setDead();
	}

	public void setMotion(float yaw, float pitch, float speed) {
		motionX = Math.cos(yaw / 180*Math.PI) * Math.cos(pitch / 180*Math.PI) * speed;
		motionZ = Math.sin(yaw / 180*Math.PI) * Math.cos(pitch / 180*Math.PI) * speed;
		motionY = Math.sin(pitch / 180*Math.PI) * speed;
//        this.motionX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI)) * speed;
//        this.motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI)) * speed;
//        this.motionY = (double)(MathHelper.sin(pitch / 180.0F * (float)Math.PI)) * speed;
    }


}
