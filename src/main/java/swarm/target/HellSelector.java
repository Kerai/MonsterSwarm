package swarm.target;

import net.minecraft.entity.*;
import net.minecraft.item.Item;
import swarm.SwarmWorld;

public class HellSelector extends TargetSelector {

	public HellSelector(SwarmWorld sw) {
	    super(sw);
	    radius = 200;
    }
	
	@Override
	public boolean canAttack(EntityLiving mob, EntityLivingBase ent, double hdist, double ydist) {
	    return true;
	}
	
}
