package swarm.target;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.AxisAlignedBB;
import swarm.Config;
import swarm.SwarmWorld;
import swarm.util.Maths;

public class TargetSelector {
	public int radius = 86;
	
	SwarmWorld sw;
	
	public TargetSelector(SwarmWorld sw) {
		this.sw = sw;
    }
	
	public static final boolean contains(AxisAlignedBB aabb, Entity ent) {
		return ent.posX >= aabb.minX && ent.posX <= aabb.maxX && ent.posY >= aabb.minY && ent.posY <= aabb.maxY && ent.posZ >= aabb.minZ && ent.posZ <= aabb.maxZ;
	}
	
	public EntityLivingBase selectTarget(EntityLiving attacker) {
		
		
		
		AxisAlignedBB aabb = new AxisAlignedBB(attacker.posX -radius, attacker.posY -radius-radius, attacker.posZ -radius, attacker.posX +radius, attacker.posY +radius+radius, attacker.posZ +radius);
		
		List<EntityLivingBase> ents = sw.targets;
		//System.out.println("Ents found: " + ents.size());
		EntityLivingBase nearest = null;
		double ndist2 = Double.POSITIVE_INFINITY;
		
		for(EntityLivingBase ent : ents) {
			
			if(!ent.isEntityAlive())
				continue;
			
			if(!contains(aabb, ent)) {
				//System.out.println(Math.sqrt(dist2) + " is too far");
				continue;
			}
			
			double dx = attacker.posX - ent.posX;
			double dy = attacker.posY - ent.posY;
			double dz = attacker.posZ - ent.posZ;

			double hdist2 = dx *dx + dz*dz;
			double dist2 = hdist2 + dy*dy;
			
			
			//double diszt = (sw.swarming || Config.ALWAYS_SWARM || (Config.UNDERGROUND && ent.posY<36)) ? 190*190 : 70*70;
			
			//if(dist2 > diszt) {
			//	continue;
			//}

			if(ent instanceof EntityAnimal || ent instanceof EntityVillager) {
				if(attacker instanceof EntityCreeper)
					continue;
			}
			
			if((ent instanceof EntityGolem || ent instanceof EntityAnimal)&& dist2>20*20) {
				continue;
			}
			
			//if(!canAttack(attacker, ent ,Maths.fastSqrt(hdist2), Math.abs(dy)))
			//	continue;
			
			if(!(ent instanceof EntityPlayer))
				dist2 += dist2;
			
			if(dist2 < ndist2) {
				if(!canAttack(attacker, ent ,Maths.fastSqrt(hdist2), Math.abs(dy)))
					continue;
				nearest = (EntityLivingBase) ent;
				ndist2 = dist2;
			}
		}
		
		return nearest;
	}
	
	/** horizontal distance and vertical distance */
	public boolean canAttack(EntityLiving mob, EntityLivingBase ent, double hdist, double ydist) {
		if(ent.isPotionActive(Potion.getPotionById(16))) // night vision
			return false;
		if (Config.UNDERGROUND && ent.posY<41 && mob.posY<41) {
			return true;
		}
		if(hdist < 16 && ydist < 16)
			return true;
		if(hdist > Config.AGGRO_RANGE)
			return false;
		if(ent.posY > 50) 
			return mob.posY > 50;
		if(ent.posY > 40) 
			return mob.posY > 40;
		return ydist < Math.min(Config.AGGRO_RANGE, 32);
	}

}
