package eu.keray.swarm;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class EventHandling {

	public EventHandling() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityJoin(EntityJoinWorldEvent event) {
		if(event.getWorld().isRemote)
			return;


		if(!(event.getEntity() instanceof EntityCreature))
			return;

		EntityCreature ent = (EntityCreature) event.getEntity();


		for(Class cls : MonsterSwarmMod.excludedAttackers) {
			if(cls.isInstance(ent)) {
				return;
			}
		}

		boolean inst = false;
		for(Class cls : MonsterSwarmMod.includedAttackers) {
			if(cls.isInstance(ent)) {
				inst = true;
				break;
			}
		}

		if(inst) {
			//ent.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(90.0D);
			if(Config.ATTACK_ANIMALS && !(ent instanceof EntityCreeper)) {
				ent.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityAnimal>(ent, EntityAnimal.class, false, false));
				//ent.tasks.addTask(5, new EntityAIAttackOnCollide(ent, EntityAnimal.class, 1.0D, false));
			}

			ent.targetTasks.addTask(5, new EntityAINearestAttackableTarget<EntityGolem>(ent, EntityGolem.class, false, false));
			//ent.tasks.addTask(5, new EntityAIAttackOnCollide(ent, EntityGolem.class, 1.0D, true));
		}
	}



	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAttackTarget(LivingSetAttackTargetEvent event) {
		if(event.getEntity() instanceof EntityMob && event.getTarget() instanceof EntityMob) {
			((EntityMob)event.getEntity()).setAttackTarget(null);
		}

	}

}
