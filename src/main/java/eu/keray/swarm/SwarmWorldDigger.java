package eu.keray.swarm;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SwarmWorldDigger extends SwarmWorldDiggerBase {


	public SwarmWorldDigger(SwarmWorld sw) {
		super(sw);
	}

	public void process(EntityCreature mob, EntityLivingBase target) {
		int sx = MathHelper.floor(mob.posX);
		int sy = MathHelper.floor(mob.posY);
		int sz = MathHelper.floor(mob.posZ);

		int tx = MathHelper.floor(target.posX);
		int ty = MathHelper.floor(target.posY);
		int tz = MathHelper.floor(target.posZ);
		
		int dx = tx - sx;
		int dy = ty - sy;
		int dz = tz - sz;
		
		
		int xd = 0;
		int zd = 0;
		
		boolean higher = false;
		
		if(Math.abs(dx) > Math.abs(dz)) {
			xd = dx > 0 ? 1 : -1;
			higher = dy > Math.abs(dx);
		} else {
			zd = dz > 0 ? 1 : -1;
			higher = dy > Math.abs(dz);
		}
		
		if(higher) {
			// must go up and build up
			

			if(isFreePass(sx, sy-1, sz)) {
				bridge(sx, sy-1, sz);
				return;
			}

			if(tryAttack(sx, sy+1, sz))
				return;
			if(tryAttack(sx, sy+2, sz))
				return;
			


			mob.setPositionAndUpdate(sx + 0.5, sy + 1, sz + 0.5);
			bridge(sx, sy, sz);
			return;
		}
		
		if(dy > 0) {
			// digging up diagonal

			if(isFreePass(sx, sy-1, sz)) {
				bridge(sx, sy-1, sz);
				return;
			}

			if(tryAttack(sx+xd, sy+1, sz+zd))
				return;
			if(tryAttack(sx, sy+2, sz))
				return;
			if(tryAttack(sx+xd, sy+2, sz+zd))
				return;
			if(tryAttack(sx+xd, sy, sz+zd))
				return;
			
			if(isFreePass(sx+xd, sy, sz+zd) && isFreePass(sx+xd, sy-1, sz+zd) && isFreePass(sx+xd, sy-2, sz+zd)) {
				bridge(sx+xd, sy, sz+zd);
			}
			
			return;
		}
		
		if(dy < 0) {
			// digging down diagonal

			if(tryAttack(sx+xd, sy, sz+zd))
				return;
			if(tryAttack(sx, sy-1, sz))
				return;
			if(tryAttack(sx+xd, sy-1, sz+zd))
				return;
			
			if(isFreePass(sx+xd, sy-1, sz+zd) && isFreePass(sx+xd, sy-2, sz+zd)) {
				bridge(sx+xd, sy, sz+zd);
			}
			
			return;
		}
		
		// roughly on the same level

		if(tryAttack(sx+xd, sy+1, sz+zd))
			return;
		if(tryAttack(sx+xd, sy, sz+zd))
			return;
		
		if(isFreePass(sx+xd, sy-1, sz+zd) && isFreePass(sx+xd, sy-2, sz+zd)) {
			bridge(sx+xd, sy-1, sz+zd);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	


}
