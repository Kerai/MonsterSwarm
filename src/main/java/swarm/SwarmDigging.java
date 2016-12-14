package swarm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import swarm.api.SwarmControl;
import swarm.util.ObjPool;
import swarm.util.ValueMap;
import swarm.util.ValueMap.ReduceObserver;
import swarm.util.Vec3I;

public abstract class SwarmDigging {
	
	protected Block bridge = Blocks.MOSSY_COBBLESTONE;
	
	protected final ObjPool<Vec3I> vecpool = new ObjPool<Vec3I>(Vec3I.class);
	
	protected final SwarmWorld sw;
	
	private final ValueMap<Vec3I> damaged = new ValueMap<Vec3I>(2048);
	private final ValueMap<Vec3I> bridges = new ValueMap<Vec3I>(2048);
	
	public SwarmDigging(SwarmWorld sw) {
		this.sw = sw;
		if(sw.world.provider.doesWaterVaporize())
			bridge = Blocks.NETHERRACK;
//		Block flesh = Block.getBlockFromName("BiomesOPlenty:flesh");
//		if(flesh != null)
//			bridge = flesh;
	}
	

	/** returns true if block was destroyed or there was no block at all */
	public boolean damage(Vec3I loc, int damage) {
		return damage(loc.x, loc.y, loc.z, damage);
	}
	
	/** returns true if block was destroyed or there was no block at all */
	public boolean damage(int x, int y, int z, int damage) {
		if(!Config.ENABLE_DIGGING)
			return true;
		
		//try {
		
			//if(sw.world.provider.hasNoSky)
			//	damage += 2;
		
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = sw.world.getBlockState(pos);
			Block type = state.getBlock();
			if(type == Blocks.AIR || type == null)
				return true;
			
			int maxdamage = getMatDmg(type, x, y, z);
			if(maxdamage < 0)
				return false;
			
			if(!SwarmControl.instance.canSwarmBreakBlock(sw.world, x, y, z))
				return false;
			
			if(maxdamage == 0) {
				//world.playEffect(block.getLocation(locc), Effect.STEP_SOUND, block.getTypeId());
				//block.breakNaturally();
				//if(Config.ENABLE_SOUNDS)
				//	sw.world.playSoundEffect(x, y, z, "dig.stone", 1f, 1f);
				
				if(state.getMaterial() == Material.GLASS) {
					sw.world.playSound(x, y, z, new SoundEvent(new ResourceLocation("dig.glass")), SoundCategory.BLOCKS, 1f, 1f, true);
					//sw.world.playSound(x, y, z, "dig.glass", 1f, 1f);
				}
				
				type.dropBlockAsItemWithChance(sw.world, pos, state, 1f, 0);
				sw.world.setBlockToAir(pos);
				return true;
			}
			
			if(damaged.size() >= 2048)
				return false;
			
			Vec3I loc = new Vec3I(x, y, z);
			
			damage = damaged.increment(loc, damage) + damage;
			
			if(damage>=maxdamage) {
				damaged.remove(loc, 0);
				type.breakBlock(sw.world, pos, state);
				type.dropBlockAsItemWithChance(sw.world, pos, state, 1f, 0);
				sw.world.setBlockToAir(pos);
				
				if(state.getMaterial() == Material.GLASS) {
					sw.world.playSound(x, y, z, new SoundEvent(new ResourceLocation("dig.glass")), SoundCategory.BLOCKS, 1f, 1f, true);
				}
				return true;
			} else {
				if(Config.ENABLE_SOUNDS) {
				
					SoundEvent sound = null;
					
					if(type.getSoundType() != null) {
						SoundEvent s = type.getSoundType().getBreakSound();
						if(s != null)
							sound = s;
					}
					
					if(sound == null) {
						sound = new SoundEvent(new ResourceLocation("dig.stone"));
					}
					sw.world.playSound(x, y, z, sound, SoundCategory.BLOCKS, 1f, 1f, true);
				}
				
//				Sound snd = SOUNDS.get(block.getType());
//				if(snd == null)
//					snd = Sound.DIG_STONE;
//				block.getWorld().playSound(block.getLocation(), snd, 1, 1);
				if(type == Blocks.STONEBRICK && state.getValue(BlockStoneBrick.VARIANT) == BlockStoneBrick.EnumType.DEFAULT && damage > maxdamage/2) {
					sw.world.setBlockState(pos, state.withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED), 2);
				}
			}
		//} catch (Throwable t) {
		//	System.err.println("damaged.size: " + damaged.size() + ", bridges.size: " + bridges.size());
		//	t.printStackTrace();
		//	cleanup();
		//}
		return false;
	}
	
	private void cleanup() {
		if(bridges != null) {
			for(Vec3I key : bridges.keys()) {
				observer.removed(key);
			}
			bridges.clear();
		}
    }
	
	private static final float minimum = 1.3f / 5;

	protected int getMatDmg(Block type, int x, int y, int z) {
		//return (int) type.getBlockHardness(sw.world, x, y, z);
		
		//if(type == Blocks.netherrack || type == Blocks.soul_sand)
		//	return 3;
		
		float res = type.getExplosionResistance(null) * Config.RESISTANCE_MULTIPLIER;
		if(res < minimum)
			return 0;
		if(res > Config.MAX_RES)
			res = Config.MAX_RES;
		
		return Math.max(2, (int)(res * 2.5f));
    }


	public boolean bridge(int x, int y, int z, int time) {
		if(!Config.ENABLE_BUILDING)
			return false;
		
		
		
		BlockPos pos = new BlockPos(x, y, z);
		
		Chunk chk = sw.world.getChunkFromBlockCoords(pos);
		if(chk == null || !chk.isLoaded())
			return false;

		if(bridges.size() >= 2048)
			return false;

		if(!SwarmControl.instance.canSwarmPlaceBlock(sw.world, x, y, z))
			return false;
		IBlockState state = sw.world.getBlockState(pos);
		Block type = state.getBlock();
		if(type != null && type != Blocks.AIR)
			type.dropBlockAsItemWithChance(sw.world, pos, state, 1f, 0);
		
		sw.world.setBlockState(pos, bridge.getDefaultState(), 2);
		if(!sw.world.provider.doesWaterVaporize())
			bridges.put(new Vec3I(x, y, z), time);
		
		return true;
	}
	
	public boolean damageLights(Vec3I loc, int height) {
		if(loc.getLightBlocks(sw.world) < 8)
			return false;
		
		boolean damaged = false;
		for (int xs = -1; xs <= 1; xs++) {
			for (int ys = 0; ys < height; ys++) {
				for (int zs = -1; zs <= 1; zs++) {
					Vec3I rel = loc.getRelative(xs, ys, zs);
					
					rel.getData(sw.world).getLightValue(sw.world, new BlockPos(rel.x, rel.y, rel.z));
					if(rel.getData(sw.world).getLightValue() > 7) {
						damage(rel, 1);
						damaged = true;
					}
				}
			}
		}
		return damaged;
    }

	public abstract void process(EntityCreature entity, Vec3I to);
	
	public void update() {
    	damaged.reduce(null);
    	bridges.reduce(observer);

		Iterator<Entry<EntityCreature, Mob>> iter = mobs.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<EntityCreature, Mob> next = iter.next();
			if(!next.getKey().isEntityAlive()) {
				iter.remove();
			} else {
			}
		}
	}
	
	private ReduceObserver<Vec3I> observer = new ReduceObserver<Vec3I>() {
        public void removed(Vec3I loc) {
	        if(loc.getBlock(sw.world) == bridge)
	        	loc.setBlock(sw.world, Blocks.AIR);
        }
	};


	public boolean blockHarvested(BlockPos pos) {
		Vec3I vec = new Vec3I(pos);
	    return bridges.remove(vec, -1) != -1;
    }
	
	/* returns true if block should not drop, removes it from list */
	public boolean blockHarvested(int x, int y, int z) {
		Vec3I vec = new Vec3I(x, y, z);
	    return bridges.remove(vec, -1) != -1;
    }
	
	Map<EntityCreature, Mob> mobs = new HashMap<EntityCreature, Mob>();
	
	public Mob getMob(EntityCreature ent) {
		Mob mob = mobs.get(ent);
		if(mob != null)
			return mob;
		//System.out.println("NEW MOB");
		mob = new Mob(ent);
		mobs.put(ent, mob);
		return mob;
	}
	
	class Mob {
		int time;
		int x,y,z;
		
		public Mob(EntityCreature ent) {
	        set(ent.posX, ent.posY, ent.posZ);
        }

		public void set(double x, double y, double z) {
			this.x = (int) x;
			this.y = (int) y;
			this.z = (int) z;
		}

		public boolean isNear(Vec3I vec) {
	        int dx = Math.abs(vec.x - this.x);
	        int dy = Math.abs(vec.y - this.y);
	        int dz = Math.abs(vec.z - this.z);
	        return dx<4 && dy<3 && dz<4;
        }
		
		public String toString() {
			return "mob["+x+","+y+","+z+"]";
		}
		
	}
}
