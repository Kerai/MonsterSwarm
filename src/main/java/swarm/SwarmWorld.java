package swarm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldServer;
import swarm.target.HellSelector;
import swarm.target.TargetSelector;
import swarm.util.Vec3D;
import swarm.util.Vec3I;

public class SwarmWorld {
	public final WorldServer world;
	final SwarmDigging digg;
	TargetSelector selector = new TargetSelector(this);

	public SwarmWorld(WorldServer world) {
		this.world = world;
		this.digg = new SwarmDiggingOld(this);
		if(world.provider.getHasNoSky()) {
			selector = new HellSelector(this);
		}


		//world.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
	}


	//public final Map<UUID, OfflineVillager> offliners = new HashMap<UUID, OfflineVillager>();

	public final List<EntityGhast> ghasts = new ArrayList<EntityGhast>();
	public final List<EntityCreature> attackers = new ArrayList<EntityCreature>();
	public final List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
	int idx = 0;

	private void collectTargetsAttackers() {
		attackers.clear();
		targets.clear();
		ghasts.clear();
		loop: for(Entity ent : (List<Entity>)world.loadedEntityList) {

			if(ent instanceof EntityGhast) {
				ghasts.add((EntityGhast) ent);
				continue loop;
			}
			if(ent instanceof EntityCreature) {
				for(Class<?> cls : MonsterSwarm.excludedAttackers) {
					if(cls.isInstance(ent)) {
						continue loop;
					}
				}
	
				for(Class<?> cls : MonsterSwarm.includedAttackers) {
					if(cls.isInstance(ent)) {
						attackers.add((EntityCreature) ent);
						break;
					}
				}
			}

			if(ent instanceof EntityPlayer) {
				if(((EntityPlayer) ent).capabilities.isCreativeMode)
					continue;
				if(((EntityPlayer) ent).isSpectator())
					continue;
			}

			for(Class<?> cls : MonsterSwarm.includedTargets) {
				if(cls.isInstance(ent)) {
					targets.add((EntityLivingBase) ent);
					break;
				}
			}
			//			if(o instanceof EntityMob) {
			//				if(o instanceof EntityEnderman)
			//					continue;
			//				if(o instanceof MoCEntityGolem)
			//					continue;
			//				if(o instanceof MoCEntityMiniGolem)
			//					continue;
			//
			//				if(isDay && moon != 0 && o instanceof MoCEntityOgre)
			//					continue;
			//
			//				attackers.add((EntityCreature) o);
			//			}
			//			else if (o instanceof MoCEntityBoar || o instanceof MoCEntityBear)
			//				attackers.add((EntityCreature) o);
			//			else if(o instanceof EntityPlayer || o instanceof EntityGolem || o instanceof EntityVillager)
			//				targets.add((EntityLivingBase) o);
		}
		idx = 0;
	}
	int ticks = 0;
	public int day;
	/** tells whever the monsters are aggro swarming right now */
	public boolean isDay = true;
	//public int moon = 0; // 0 == full moon



	boolean pm = true;
	int pmcounter = 100;


	//Map<UUID, EntityVillager> villagers = new HashMap<UUID, EntityVillager>();


	public void playerLoggedOut(EntityPlayerMP player) {
//		if(swarming || true) {
//			OfflineVillager off = new OfflineVillager(player.worldObj, player);
//			off.posX = player.posX;
//			off.posY = player.posY;
//			off.posZ = player.posZ;
//			off.rotationPitch = player.rotationPitch;
//			off.rotationYaw = player.rotationYaw;
//			off.rotationYawHead = player.rotationYawHead;
//			player.worldObj.spawnEntityInWorld(off);
//			offliners.put(player.getUniqueID(), off);
//			System.out.println("SPAWNING OFFLINE PLAYER");
//		}
	}

	public void playerLoggedIn(EntityPlayerMP p) {
//		OfflineVillager off = offliners.remove(p.getUniqueID());
//		if(off != null) {
//			if(off.isDead) {
//				p.attackEntityFrom(DamageSource.anvil, 99999);
//				System.out.println("KILLING PLAYER");
//			}
//			off.setDead();
//			System.out.println("REMOVING OFFLINE PLAYER");
//			//			off.setDead();
//			//			off.unticket();
//		}
	}

	int perfticks = 0;

	public void update() {
		ticks++;
		
		
		long startTime = System.nanoTime();

		
		int timeofday = (int) world.getWorldTime() % 24000;

//		if(!offliners.isEmpty() && !swarming) {
//			for (OfflineVillager a : offliners.values()) {
//				a.isDead = true;
//				a.unticket();
//			}
//
//			offliners.clear();
//		}
		
		
//		if(Config.LONGER_DAY) {
//			if(pmcounter > 0) {
//				pmcounter--;
//	
//				if(pmcounter == 0) {
//					//System.out.println("startujem upuyw czasu");
//					world.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
//				}
//			}
//	
//			boolean lastpm = pm;
//			pm = timeofday > 6000 && timeofday < 7000;
//	
//	
//	
//			if(lastpm == false && pm == true) {
//				//System.out.println("stopujem upuyw czasu");
//				world.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
//				pmcounter = 5000;
//			}
//		
//		}


		if(world.loadedEntityList.isEmpty())
			return;

		perfticks++;

		if(idx >= attackers.size()) { // also triggers if size is 0
			if(perfticks > 15) {
				collectTargetsAttackers();
				perfticks = 0;
			}
		}
		
		if(idx==0 && ghasts !=null) {
			for(EntityGhast mob : ghasts) {
				EntityLivingBase target = selector.selectTarget(mob);
				
				if(target == null) {
					continue;
				}
				
				mob.setAttackTarget(target);
				
//				vec1.set(mob.posX, mob.posY, mob.posZ);
//				vec2.set(target.posX, target.posY + 0.5, target.posZ);
	
	//					Vec3D tow = towards(vec1, vec2, 15);
	
	//					double dx = mob.posX - target.posX;
	//					double dz = mob.posZ - target.posZ;
				//double dy = target.posY - mob.posY;
	//					double dst = Maths.fastSqrt(dx*dx + dz*dz);
	
//				final Vec3D out = new Vec3D();
//				boolean result = Kurwektoria.calculateTrajectory(new Vec3D(target.posX - mob.posX, target.posY + target.getEyeHeight() - mob.posY - mob.getEyeHeight(), target.posZ - mob.posZ), 1.5f, 0.01f, false , out);
//				if(result) {
//					EntitySwarmRocket shot = new EntitySwarmRocket(mob.worldObj, mob);
//					shot.explosion = 3.2f;
//					shot.setThrowableHeading(out.x, out.y, out.z, 1.5f, 20f);
//	
//					mob.worldObj.spawnEntityInWorld(shot);
//					//mob.worldObj.playSoundAtEntity(mob, "fireworks.launch", 1.0F, 0.5F);
//				}
			}
		}
		
		

		// advance x mobs per tick
		int top = Math.min(idx + 2, attackers.size());

		int fucked = 0;
		for(; idx < top; idx++) {
			EntityCreature mob = attackers.get(idx);
			fucked++;

			if(isDay && Config.KILL_MOBS_DAYTIME) {
				if(!mob.hasCustomName() && !(mob instanceof EntityPigZombie)) {
					
					int light = mob.worldObj.getLightFor(EnumSkyBlock.SKY, mob.getPosition());
//					Chunk czunk = mob.worldObj.getChunkFromBlockCoords((int)mob.posX, (int)mob.posZ);
//					int lajt = czunk.getSavedLightValue(EnumSkyBlock.SKY, (int)mob.posX & 0xF, (int) (mob.posY + 1.5) & 0xFF, (int)mob.posZ & 0xF);
					if(light > 11) {
						mob.setFire(8);
						mob.attackEntityFrom(DamageSource.inFire, 2);
					}
				}
			}

			Entity entity = mob.getAttackTarget();
			EntityLivingBase target = entity instanceof EntityLivingBase? (EntityLivingBase) entity: null;
			if(target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.isCreativeMode) {
				target = null;
				mob.setAttackTarget(null);
			}

			if(target == null || target instanceof IMob || !target.isEntityAlive()) {
				target = selector.selectTarget(mob);
			} else {
				double dx = mob.posX - target.posX;
				double dy = mob.posY - target.posY;
				double dz = mob.posZ - target.posZ;
				double dist2 = dx*dx + dy*dy + dz*dz;

				if(dist2 > 16*16) {
					EntityLivingBase nt = selector.selectTarget(mob);
					if(nt != null)
						target = nt;
				}
			}
			

			if(target != null) {

				mob.setAttackTarget(target);

				vec1.set(mob.posX, mob.posY, mob.posZ);
				vec2.set(target.posX, target.posY + 0.5, target.posZ);

				Vec3D tow = towards(vec1, vec2, 15);
				if(Config.ENABLE_BUILDING || Config.ENABLE_DIGGING) {
					boolean digger = false;
					for(Class<?> cls : MonsterSwarm.includedDiggers) {
						if(cls.isInstance(mob)) {
							digger = true;
							break;
						}
					}
					if(digger) {
						digg.process(mob, new Vec3I(target));
					}
				}

				mob.getNavigator().tryMoveToXYZ(tow.x, tow.y, tow.z, target.isSprinting() && Config.ENABLE_SPRINTING ? 2.3f : 1f);

				if(mob.worldObj.getBlockState(mob.getPosition()).getMaterial().isLiquid()) {
					Vec3D vec = new Vec3D(target).sub(mob).normalize().scale(0.4);
					mob.motionX = vec.x;
					mob.motionY = vec.y;
					mob.motionZ = vec.z;
				}
			}
		}


		if(ticks>15) {
			ticks = 0;
			if(world.provider.getHasNoSky()) {//moon = 0;
				isDay = false;
			} else {
				day = (int) (world.getWorldTime() / 24000) + 1;
				//moon = day % 8;
				int time = (int) world.getWorldTime() % 24000;
				isDay = time < 12200 || time > 23850;
			}

			digg.update();
		}	
		if(Config.DEBUG) {
			long endTime = System.nanoTime();
			float ms = ((endTime-startTime) / 100000) / 10f;
			Log.println("ticking took: " + ms + "ms with " + fucked + " mobs");
		}
	}

	static final Vec3D vec = new Vec3D();
	public static final Vec3D towards(Vec3D from, Vec3D to, float dist) {
		vec.x = (to.x - from.x);
		vec.y = (to.y - from.y);
		vec.z = (to.z - from.z);
		double len = vec.length();

		if(len<dist)
			vec.set(to.x, to.y, to.z);
		else {
			vec.set(vec.x/len, vec.y/len, vec.z/len);
			vec.scale(dist);

			vec.x += from.x;
			vec.y += from.y;
			vec.z += from.z;
		}
		return vec;
	}

	final Vec3D vec1 = new Vec3D();
	final Vec3D vec2 = new Vec3D();


}
