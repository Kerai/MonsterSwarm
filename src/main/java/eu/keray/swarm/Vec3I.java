package eu.keray.swarm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;

public class Vec3I {
	public int x;
	public int y;
	public int z;
	
	public Vec3I() {
		
	}
	
	public Vec3I(Vec3I that) {
		x = that.x;
		y = that.y;
		z = that.z;
	}

	public Vec3I(BlockPos pos) {
	    this.x = pos.getX();
	    this.y = pos.getY();
	    this.z = pos.getZ();
    }

	public Vec3I(int x, int y, int z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
    }
	
	public Vec3I(Entity ent) {
		this.x = (int) ent.posX;
		this.y = (int) (ent.posY + 0.2);
		this.z = (int) ent.posZ;
		
		if(ent.posX<0)
			this.x--;
		if(ent.posZ<0)
			this.z--;
	}
	
	public BlockPos getPos() {
		return new BlockPos(x, y, z);
	}
	
	
	public Block getBlock(World w) {
		return w.getBlockState(getPos()).getBlock();
	}
	
	public IBlockState getData(World w) {
		return w.getBlockState(getPos());
	}
	
	public int getLightSky(World w) {
		return w.getLightFor(EnumSkyBlock.SKY, getPos());
	}
	public int getLightBlocks(World w) {
		return w.getLightFor(EnumSkyBlock.BLOCK, getPos());
	}
	
	
	public Vec3I set(int x, int y, int z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    return this;
    }
	
	public Vec3I set(Vec3I that) {
	    this.x = that.x;
	    this.y = that.y;
	    this.z = that.z;
	    return this;
    }

	public double length() {
	    return MathHelper.sqrt(x*x + y*y + z*z);
    }
	
	public Vec3I scale(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return this;
	}
	
	public Vec3I getRelative(int x, int y, int z) {
		return new Vec3I(this.x+x, this.y+y, this.z+z);
	}

	public void setBlock(World w, Block type) {
		w.setBlockState(getPos(), type.getDefaultState(), 3);
    }
	
	
	@Override
	public int hashCode() {
		return x ^ (y<<14) ^ (z<<20);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vec3I) {
			Vec3I that = (Vec3I) obj;
			return this.x == that.x && this.y == that.y && this.z == that.z;
		}
	    return false;
	}
	public boolean equals(Vec3I that) {
		return this.x == that.x && this.y == that.y && this.z == that.z;
	}
	public String toString() {
		return "["+x+","+y+","+z+"]";
	}

//	public Vec3I moveDirection(int direction, int by) {
//		BlockDirectional.
//	    x += Direction.offsetX[direction] * by;
//	    z += Direction.offsetZ[direction] * by;
//	    return this;
//    }
}
