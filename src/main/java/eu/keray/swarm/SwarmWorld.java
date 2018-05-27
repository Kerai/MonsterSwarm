package eu.keray.swarm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class SwarmWorld {

	protected WorldServer world;
	private SwarmWorldDigger digg;

	public final List<EntityCreature> attackers = new ArrayList<EntityCreature>();
	public final List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();

	public boolean isOverworld = false;

	public SwarmWorld(WorldServer world) {

		Log.log("Registering world dimension " + world.provider.getDimension());

		this.world = world;
		this.digg = new SwarmWorldDigger(this);
		MinecraftForge.EVENT_BUS.register(this);

		if (world.provider.getDimension() == 0) {
			isOverworld = true;
		}
	}

	int index = 0;

	int ticks = 0;

	public void run() {
		ticks++;
		if (index >= attackers.size()) {

			if (ticks < 20)
				return;
			ticks = 0;

			index = 0;
			collectEntities();
			digg.update();
			// Log.log("Collected " + attackers.size() + " and " + targets.size());
			return;
		}

		boolean isDay = false;

		if (isOverworld) {
			int time = (int) world.getWorldTime() % 24000;
			isDay = time < 12200 || time > 23850;
		}

		int top = Math.min(index + 2, attackers.size());

		for (; index < top; index++) {
			EntityCreature mob = attackers.get(index);

			if (isDay && Config.KILL_MOBS_DAYTIME) {
				if (!mob.hasCustomName() && !(mob instanceof EntityPigZombie)) {

					int light = mob.getEntityWorld().getLightFor(EnumSkyBlock.SKY, mob.getPosition());
					if (light > 11) {
						mob.setFire(8);
						mob.attackEntityFrom(DamageSource.IN_FIRE, 2);
					}
				}
			}

			EntityLivingBase target = findTargetFor(mob, 64);
			if (target == null) {
				target = mob.getAttackTarget();

				if (target instanceof IMob) {
					target = null;
					mob.setAttackTarget(null);
				}
			}

			if (target == null) {
				continue;
			}

			// Log.log("Attacker " + mob + " has target " + target);

			mob.setAttackTarget(target);

			Vec3i point = Maths.findPointTowards(mob, target, 15);
			
			
			boolean canDigg = true;
			
			if(isOverworld) {
				canDigg = mob.posY < 40 && target.posY < 40;
			}

			if (canDigg && (mob instanceof EntityZombie || mob instanceof EntitySkeleton))
				digg.process(mob, target);

			mob.getNavigator().tryMoveToXYZ(point.getX() + 0.5, point.getY() + 0.5, point.getZ() + 0.5,
					target.isSprinting() && Config.ENABLE_SPRINTING ? 2.3f : 1f);

			if (mob.getEntityWorld().getBlockState(mob.getPosition()).getMaterial().isLiquid()) {
				Vec3D vec = new Vec3D(target).sub(mob).normalize().scale(0.4);
				mob.motionX = vec.x;
				mob.motionY = vec.y;
				mob.motionZ = vec.z;
			}
		}
	}

	public EntityLivingBase findTargetFor(EntityCreature attacker, int radius) {

		AxisAlignedBB aabb = new AxisAlignedBB(attacker.posX - radius, attacker.posY - radius - radius,
				attacker.posZ - radius, attacker.posX + radius, attacker.posY + radius + radius,
				attacker.posZ + radius);

		// System.out.println("Ents found: " + ents.size());
		EntityLivingBase nearest = null;
		double ndistSq = Double.POSITIVE_INFINITY;

		for (EntityLivingBase ent : targets) {

			if (!ent.isEntityAlive())
				continue;

			if (!Maths.contains(aabb, ent)) {
				// System.out.println(Math.sqrt(dist2) + " is too far");
				continue;
			}

			double dx = attacker.posX - ent.posX;
			double dy = attacker.posY - ent.posY;
			double dz = attacker.posZ - ent.posZ;

			double distSq = dx * dx + dz * dz;
			double distHeight = Math.abs(dy);

			if (distSq > radius * radius)
				continue;

			if (ent instanceof EntityAnimal || ent instanceof EntityVillager) {
				if (attacker instanceof EntityCreeper)
					continue;
			}

			if ((ent instanceof EntityGolem || ent instanceof EntityAnimal) && distSq > 20 * 20) {
				continue;
			}

			if (!(ent instanceof EntityPlayer))
				distSq += distSq;

			if (distSq < ndistSq) {

				if(isOverworld) {
					if (ent.posY < 40) {
						if (attacker.posY > 40)
							continue;
					} else {
						if (attacker.posY < 40)
							continue;
					}
				}

				nearest = (EntityLivingBase) ent;
				ndistSq = distSq;
			}
		}

		return nearest;
	}

	private void collectEntities() {
		attackers.clear();
		targets.clear();
		for (Entity ent : (List<Entity>) world.loadedEntityList) {
			
			if(ent instanceof EntityEnderman)
				continue;

			if (ent instanceof EntityCreature) {

				if (ent instanceof EntityEnderman) {

				} else if (ent instanceof EntityMob || ent instanceof IMob) {
					attackers.add((EntityCreature) ent);
					continue;
				}
			}

			if (ent instanceof EntityPlayer) {
				if (((EntityPlayer) ent).capabilities.isCreativeMode)
					continue;
				if (((EntityPlayer) ent).isSpectator())
					continue;

				targets.add((EntityLivingBase) ent);

			}

			// targets.add((EntityLivingBase) ent);
		}

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onServerTick(ServerTickEvent event) {
		if (event.phase != Phase.START)
			return;
		this.run();
	}
}
