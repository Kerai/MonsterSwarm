package swarm;

import static java.lang.Math.abs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import swarm.util.Vec3I;

public class SwarmDiggingOld extends SwarmDigging {
	
	static final Block quicksand = Block.getBlockFromName("BiomesOPlenty:mud");
	static final Block web = Blocks.WEB;

	public SwarmDiggingOld(SwarmWorld sw) {
	    super(sw);
    }
	

	
	@Override
	public void update() {
	    super.update();

	}
	
	private final int clamp(int val) {
		return val == 0 ? 0 : val > 0 ? 1 : -1;
	}
	
	public final boolean tryAttack(int x, int y, int z) {
		IBlockState state = sw.world.getBlockState(new BlockPos(x, y, z));
		Block type = state.getBlock();
		
		if(type == null || type == Blocks.AIR) {
			return false;
		}
		
		if(state.getMaterial().blocksMovement() || type == web || type == quicksand) {
			//MobUtil.playAnimation(cr, 0);
			return !damage(x, y, z, damage);
		}
		
		//damage(x, y, z, damage);
		return false;
	}
	

	void build(int x, int y, int z) {
		build(x, y, z, 7);
		
	}
	
	void build(int x, int y, int z, int time) {
		IBlockState state = sw.world.getBlockState(new BlockPos(x, y, z));
		Block type = state.getBlock();
		if(type != null && state.getMaterial().isSolid())
			return;
		
		if(type!=null && type!=Blocks.AIR) {
			damage(x, y, z, damage);
			//MobUtil.playAnimation(cr, 0);
			return;
		}
		
		if(!bridge(x, y, z, time)) {
			return;
		} else {
			mob.set(x,y,z);
		}
		
		if(cr.posY < y+1.1) {
			cr.setPositionAndUpdate(x + 0.5, y + 1, z + 0.5);
		}
		
		
		//MobUtil.playAnimation(cr, 0);
		
		//List<Entity> nearby = cr.getNearbyEntities(1, 2, 1);
		//Location tar = b.getLocation().add(0.5, 1.1, 0.5);
		//tar.setDirection(cr.getLocation().getDirection());
//		for(Entity ent : nearby) {
//			if(ent instanceof Monster) {
//				Location lc = ent.getLocation();
//				int dy = lc.getBlockY() - b.getY();
//				int dx = Math.abs(lc.getBlockX() - b.getX());
//				int dz = Math.abs(lc.getBlockZ() - b.getZ());
//				
//				if(dx<2 && (dy >= -2 && dy<1) && dz<2) {
//					ent.teleport(tar);
//				}
//			}
//		}
		//cr.teleport(b.getLocation().add(0, 1, 0));
	}

	
	/** @returns true if passable block */
	public final boolean isFree(int x, int y, int z) {
		IBlockState t = sw.world.getBlockState(new BlockPos(x, y, z));
		return !t.getMaterial().isSolid() && t.getMaterial() != Material.LAVA;
	}
	
	/** @returns true if passable block */
	public final boolean isFreeorLava(int x, int y, int z) {
		IBlockState t = sw.world.getBlockState(new BlockPos(x, y, z));
		return !t.getMaterial().isSolid();
	}

	/** @returns true if passable block and if lava, will remove it first */
	public final boolean isFreePass(int x, int y, int z) {
		IBlockState t = sw.world.getBlockState(new BlockPos(x, y, z));
		//if(isLava(b)) {
		//	world.getBlockAt(x, y, z).setType(Material.AIR);
		//	cr.addPotionEffect(potion);
		//	return true;
		//}
		return !t.getMaterial().blocksMovement() && t.getMaterial() != Material.WATER;
	}
	
	public final boolean isDouble(int x, int y, int z) {
		Block type = sw.world.getBlockState(new BlockPos(x, y, z)).getBlock();
		
		return type instanceof BlockFence;
	}
	
	private boolean tryWaterDown(int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = sw.world.getBlockState(pos);
		if(state.getMaterial().isLiquid()) {
			if(state.getValue(BlockLiquid.LEVEL) != 0)
				sw.world.setBlockToAir(pos);
			return true;
		}
		return false;
	}

	protected final boolean isFreeDouble(int x, int y, int z) {
		return isFreePass(x, y, z) && isFreePass(x, y+1, z) && !isDouble(x, y-1, z);
	}
	
	protected final boolean isBackway(int x, int y, int z) {
		return isFree(x, y, z) && isFree(x, y+1, z) && isFree(x, y-1, z) && !isFreeorLava(x, y-2, z);
	}
	
	protected final boolean isBackup(int x, int y, int z) {
		return !isFreeorLava(x, y, z) && isFree(x, y+1, z) && isFree(x, y+2, z);
	}
	
	protected final boolean tryBuildUp(int x, int y, int z, boolean checkway) {
		if(checkway && isBackway(x, y, z))
			return false;
		if(!isFreeDouble(x, y+1, z))
			return false;
		if(isStill)
			build(x, y, z, 90);
		return true;
	}
	
	protected final boolean tryDigUp(int x, int y, int z) {
		if(isBackway(x, y, z))
			return false;
		if(tryAttack(x, y+1, z))
			return true;
		if(tryAttack(x, y+2, z))
			return true;
		if(isDouble(x, y, z))
			tryAttack(x, y, z);
		return false;
	}

	
	protected final boolean tryBuildDown(int x, int y, int z) {
		if(isBackup(x, y, z))
			return false;
		if(!isFreePass(x, y, z) || !isFreePass(x, y+1, z) || !isFreePass(x, y-1, z))
			return false;
		if(isStill)
			build(x, y-2, z);
		return true;
	}
	
	protected final boolean tryDigDown(int x, int y, int z) {
		if(isBackup(x, y, z))
			return false;
		if(tryAttack(x, y, z))
			return true;
		if(tryAttack(x, y-1, z))
			return true;
		if(tryAttack(x, y+1, z))
			return true;
		return true;
	}
	
	private EntityCreature cr;
	private Mob mob;
	private int damage = 2;
	private boolean isStill = false;
	
	@Override
	public void process(EntityCreature entity, Vec3I to) {
		cr = entity;
		int height = 2;

		Vec3I from = new Vec3I(cr);
		mob = getMob(entity);
		if(mob.isNear(from)) {
			//System.out.println("Isnear " + mob.time);
			mob.time++;
			isStill = mob.time > 4;
			
		} else {
			//System.out.println(from + " is far from " + mob);
			mob.time = 0;
			mob.set(from.x, from.y, from.z);
			isStill = false;
		}
		
		int x = from.x;
		int y = from.y;
		int z = from.z;
		
		int dx = (to.x - x);
		int dy = (to.y - y);
		int dz = (to.z - z);
		
		boolean isx = abs(dx) > abs(dz);
		int horizontal = Math.max(abs(dx), abs(dz));
		
		boolean nearxz = Math.abs(dx) + Math.abs(dz)<8 && Math.abs(dx) < 5 && Math.abs(dz) <5;
		
		damage = nearxz && dy>2 ? 3 : 2;
		
		int tx;
		int tz;
		/* if(dx==dz) {
			tx = tz = 0;
		} else */ if(isx) {
			tx = clamp(dx);
			tz = 0;
		} else {
			tx = 0;
			tz = clamp(dz);
		}
		
		Block tp = sw.world.getBlockState(new BlockPos(x, (int) entity.posY, z)).getBlock();
		if(tp == quicksand || tp == web) {
			damage(x, (int)entity.posY, z, 1);
		}
		
		if(height>2 && tryAttack(x , y+2, z))
				return;
		if(height>1 && tryAttack(x , y+1, z))
				return;
		
		int tox = x + tx;
		int toz = z + tz;
		
		if(dy > 1 && (dy>horizontal+9 || nearxz)) {
			// we go up
			
			damage = 2;
			
			if(tryAttack(x , y+2, z))
				return;

			if(isDouble(x, y, z)) {
				tryAttack(x, y, z);
				return;
			}

			if(isStill)
				if(isFreePass(x, y-1, z))
					if(!isBackway(x, y, z)) {
						build(x, y-1, z);
						return;
					}
			
			
			if(tx == tz) {
				if(Math.random()<0.5) {
					tx = Math.random()<0.5 ? 1 : -1;
				} else {
					tz = Math.random()<0.5 ? 1 : -1;
				}
				tox = x + tx;
				toz = z + tz;
			}
			
//			if(!isBackway(tox, y, toz)) {
//				if(tryAttack(tox, +1, tz) || tryAttack(tox, y+2, toz))
//					return;
//				if(isDouble(tox, y, toz) && tryAttack(tox, y, toz))
//					return;
//				if(isFreeDouble(tox, y+1, toz)) {
//					
//				}
//			}
			
			
			//System.out.println("We go up with " + tx +", "+tz);
			
			// digg up
			if(tx!=0) {
				if(tryDigUp(x+tx, y, z))
					return;
				
				if(tryDigUp(x, y, z + (dz>0 ? 1 : -1)))
					return;
				if(tryDigUp(x, y, z + (dz>0 ? -1 : 1)))
					return;
				if(dy > 4)
					if(tryDigUp(x-tx, y, z))
						return;
			}
			else if(tz!=0) {
				if(tryDigUp(x, y, z+tz))
					return;
				
				if(tryDigUp(x + (dx>0 ? 1 : -1), y, z))
					return;
				if(tryDigUp(x + (dx>0 ? -1 : 1), y, z))
					return;
				if(dy > 4)
					if(tryDigUp(x, y, z-tz))
						return;
			}
			
			//System.out.println("step1");
			
			if(tryAttack(tox, y+1, toz))
				return;
			if(tryAttack(tox, y+2, toz))
				return;
			if(isDouble(tox, y, toz)) {
				tryAttack(tox, y, toz);
				return;
			}
			//System.out.println("step2 will build up? " + isStill);
			
			//build up
			if(tx!=0) {
				if(tryBuildUp(x+tx, y, z, true))
					return;
				
				if(tryBuildUp(x, y, z + (dz>0 ? 1 : -1), false))
					return;
				if(tryBuildUp(x, y, z + (dz>0 ? -1 : 1), false))
					return;
				if(tryBuildUp(x+tx, y, z, false))
					return;
				if(dy > 2)
					if(tryBuildUp(x-tx, y, z, false))
						return;
			}
			else if(tz!=0) {
				if(tryBuildUp(x, y, z+tz, true))
					return;
				
				if(tryBuildUp(x + (dx>0 ? 1 : -1), y, z, false))
					return;
				if(tryBuildUp(x + (dx>0 ? -1 : 1), y, z, false))
					return;
				if(tryBuildUp(x, y, z+tz, false))
					return;
				if(dy > 2)
					if(tryBuildUp(x, y, z-tz, false))
						return;
			} else {
				tryBuildUp(x, y, z, false);
			}
			//System.out.println("step2 didnt build");

			if(isStill)
				build(tox, y, toz);
			
		}  else if(dy < -1 && (-dy>horizontal+9 || nearxz)){
			if(tryAttack(x , y, z )) {
				return;
			}
			
			
			tryWaterDown(x, y+1, z);
			tryWaterDown(x, y+2, z);
			
			if(tryWaterDown(x, y, z)) {
				damage = 9;
				
				tryWaterDown(x+1, y, z);
				tryWaterDown(x, y, z+1);
				tryWaterDown(x-1, y, z);
				tryWaterDown(x, y, z-1);
				
				
				
				cr.motionY = -1;
				//return;
			}
			if(tryWaterDown(x, y-1, z)) {
				damage = 9;

				tryWaterDown(x+1, y-1, z);
				tryWaterDown(x, y-1, z+1);
				tryWaterDown(x-1, y-1, z);
				tryWaterDown(x, y-1, z-1);
				
				cr.motionY = -1;
				
				if(Math.random()<0.1)
					build(x, y+2, z);
				//return;
			}
			

			if(tx == tz) {
				if(Math.random()<0.5) {
					tx = Math.random()<0.5 ? 1 : -1;
				} else {
					tz = Math.random()<0.5 ? 1 : -1;
				}
				tox = x + tx;
				toz = z + tz;
			}
			
			// build down
//			if(tx!=0) {
//				if(tryBuildDown(x+tx, y, z))
//					return;
//				
//				if(tryBuildDown(x, y, z + (dz>0 ? 1 : -1)))
//					return;
//				if(tryBuildDown(x, y, z + (dz>0 ? -1 : 1)))
//					return;
//				if(dy < -2)
//					if(tryBuildDown(x-tx, y, z))
//						return;
//			}
//			else if(tz!=0) {
//				if(tryBuildDown(x, y, z+tz))
//					return;
//				
//				if(tryBuildDown(x + (dx>0 ? 1 : -1), y, z))
//					return;
//				if(tryBuildDown(x + (dx>0 ? -1 : 1), y, z))
//					return;
//				if(dy < -2)
//					if(tryBuildDown(x, y, z-tz))
//						return;
//			}
			
			// digg down
			if(tx!=0) {
				if(tryDigDown(x+tx, y, z))
					return;
				
				if(tryDigDown(x, y, z + (dz>0 ? 1 : -1)))
					return;
				if(tryDigDown(x, y, z + (dz>0 ? -1 : 1)))
					return;
				if(dy < -2)
					if(tryDigDown(x-tx, y, z))
						return;
			}
			else if(tz!=0) {
				if(tryDigDown(x, y, z+tz))
					return;
				
				if(tryDigDown(x + (dx>0 ? 1 : -1), y, z))
					return;
				if(tryDigDown(x + (dx>0 ? -1 : 1), y, z))
					return;
				if(dy < -2)
					if(tryDigDown(x, y, z-tz))
						return;
			}
			
			if(tryAttack(tox, y, toz))
				return;
			if(tryAttack(tox, y-1, toz))
				return;
			if(tryAttack(x, y-1, z))
				return;
		} else {
			// we go forward

			if(isStill)
				if(isFreePass(x, y-1, z))
					if(!isBackway(x, y, z))
						build(x, y-1, z);
			
			if(tryAttack(x, y, z))
				return;
			
			// 2 meters high wall? just build one block to jump over it
			if(!isFree(tox, y+1, toz) && isFree(tox, y+2, toz) && isFree(x, y+2, z)) {

				if(isStill)
					build(x, y, z);
				return;
			}
			{
				//System.out.println("attacking foward: " + tox +","+(y+1)+","+toz+" | " + sw.world.getBlock(x,y,z).getUnlocalizedName());
				if(tryAttack(tox, y+1, toz))
					return;
			}
			if(isFree(tox, y, toz)) {
				if(isDouble(tox, y-1, toz)) {
					tryAttack(tox, y-1, toz);
					return;
				}
			}
			if(dy<3 && tryAttack(tox, y, toz))
				return;
			if(tryAttack(x, y+2, z))
				return;
			if(tryAttack(tox, y+2, toz))
				return;
			
			if(!isFree(tox, y, toz))
				return;
			if(!isFreePass(tox, y-1, toz))
				return;
			if(!isFreeorLava(tox, y-2, toz))
				return;
			if(!isFreeorLava(tox, y-3, toz))
				return;
			if(dy<-5) {

				if(isStill) {
					//build(tox, y-2, toz);
					cr.motionX = tx * 0.6f;
					cr.motionZ = tz * 0.6f;
					
				}
			} else {

				if(isStill) {
					if(horizontal <16 && Math.random() < 0.8) {
						build(tox, y-1, toz);
					} else {
						cr.motionX = tx * 0.6f;
						cr.motionZ = tz * 0.6f;
					}
				}
			}
		}
		
	}

}
