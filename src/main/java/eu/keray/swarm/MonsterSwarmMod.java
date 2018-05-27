package eu.keray.swarm;

import net.minecraft.init.Blocks;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

@Mod(modid = MonsterSwarmMod.MODID, name = MonsterSwarmMod.NAME, version = MonsterSwarmMod.VERSION)
public class MonsterSwarmMod
{
    public static final String MODID = "monsterswarm";
    public static final String NAME = "Monster Swarm";
    public static final String VERSION = "2.0";

    public static Logger logger;
    
    
	@Instance(value = MonsterSwarmMod.MODID)
	public static MonsterSwarmMod INSTANCE;
	
	

	public static final List<Class> excludedAttackers = new ArrayList<Class>();
	public static final List<Class> includedAttackers = new ArrayList<Class>();
	public static final List<Class> includedTargets = new ArrayList<Class>();
	public static final List<Class> includedDiggers = new ArrayList<Class>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
		Config.preInit(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
    

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//println("POST INIT");
		new EventHandling();

		Blocks.OBSIDIAN.setResistance(10);
		Blocks.BRICK_BLOCK.setResistance(18);
		Blocks.COBBLESTONE.setResistance(11);
		Blocks.STONEBRICK.setResistance(16);
		Blocks.BRICK_STAIRS.setResistance(15);
		Blocks.STONE_BRICK_STAIRS.setResistance(12);
		Blocks.SANDSTONE.setResistance(15);
		Blocks.SANDSTONE_STAIRS.setResistance(12);
		Blocks.RED_SANDSTONE.setResistance(15);
		Blocks.RED_SANDSTONE_STAIRS.setResistance(12);
		Blocks.WATER.setResistance(3);
		Blocks.STONE_SLAB.setResistance(12);
		Blocks.LAVA.setResistance(4);
		Blocks.NETHER_BRICK.setResistance(12);

		Magic.setResist("Railcraft:brick.infernal", 16);
		Magic.setResist("Railcraft:brick.abyssal", 16);
		Magic.setResist("Railcraft:brick.sandy", 16);
		Magic.setResist("Railcraft:brick.frostbound", 16);
		Magic.setResist("Railcraft:brick.quarried", 16);
		Magic.setResist("Railcraft:brick.bleachedbone", 16);
		Magic.setResist("Railcraft:brick.bloodstained", 16);
		Magic.setResist("Railcraft:brick.nether", 16);
		Magic.setResist("Railcraft:stair", 12);
		Magic.setResist("Railcraft:wall.beta", 12);
		Magic.setResist("Railcraft:wall.alpha", 12);
		Magic.setResist("Railcraft:slab", 12);
		Magic.setResist("BiomesOPlenty:mudBricks", 15);
		Magic.setResist("BiomesOPlenty:mudBricksStairs", 11);
		Magic.setResist("MineFactoryReloaded:brick", 16);

		//Magic.setResist("Railcraft:machine.alpha", 16);
		//Magic.setResist("IC2:blockWall", 16);
		//Magic.setResist("IC2:blockAlloy", 40);
		//Magic.setResist("IC2:blockDoorAlloy", 25);
		//Magic.setResist("IC2:blockAlloyGlass", 25);
		//Magic.setResist("IC2:blockDoorAlloy", 15);

		Magic.addClass(includedAttackers, "net.minecraft.entity.monster.EntityMob");
		Magic.addClass(includedAttackers, "net.minecraft.entity.monster.IMob"); // mob
		Magic.addClass(includedAttackers, "drzhark.mocreatures.entity.passive.MoCEntityBear");
		Magic.addClass(includedAttackers, "drzhark.mocreatures.entity.passive.MoCEntityBoar");
		Magic.addClass(includedAttackers, "drzhark.mocreatures.entity.passive.MoCEntityCrocodile");

		Magic.addClass(excludedAttackers, "drzhark.mocreatures.entity.monster.MoCEntityGolem");
		Magic.addClass(excludedAttackers, "drzhark.mocreatures.entity.monster.MoCEntityMiniGolem");
		Magic.addClass(excludedAttackers, "drzhark.mocreatures.entity.monster.MoCEntityOgre");
		Magic.addClass(excludedAttackers, "net.minecraft.entity.monster.EntityEnderman");
		Magic.addClass(excludedAttackers, "crazypants.enderzoo.entity.EntityOwl");

		Magic.addClass(includedDiggers, "drzhark.mocreatures.entity.monster.MoCEntitySilverSkeleton");
		Magic.addClass(includedDiggers, "net.minecraft.entity.monster.EntityZombie");
		Magic.addClass(includedDiggers, "net.minecraft.entity.monster.EntitySkeleton");
		Magic.addClass(includedDiggers, "com.gw.dm.entity.EntityLizalfos");
		Magic.addClass(includedDiggers, "com.gw.dm.entity.EntityRakshasa");
		Magic.addClass(includedDiggers, "com.gw.dm.entity.EntityCaveFisher");
		//Magic.addClass(includedDiggers, "net.minecraft.entity.monster.EntityCreeper");
		
		
		Magic.addClass(includedTargets, "net.minecraft.entity.player.EntityPlayer");
		Magic.addClass(includedTargets, "net.minecraft.entity.monster.EntityGolem");
		Magic.addClass(includedTargets, "net.minecraft.entity.passive.EntityVillager");
		Magic.addClass(includedTargets, "net.shadowmage.ancientwarfare.npc.entity.NpcBase");

		//		Iterator<Block> iterator = Block.blockRegistry.iterator();
		//		while(iterator.hasNext()) {
		//			Block block = iterator.next();
		//
		//			if(block instanceof BlockDecorativeBricks) {
		//				block.setResistance(15);
		//				System.out.println("set 15 for " + block.getUnlocalizedName());
		//			}
		//		}
	}

    
	@EventHandler
	public void serverLoad(FMLServerStartedEvent event)
	{
		for(WorldServer ws : DimensionManager.getWorlds()) {
			if(ws == null)
				continue;
			SwarmWorld sw = new SwarmWorld(ws);
			
//			worlds.add(sw);
//			map.put(ws, sw);
//
//			println("Swarm for dimension " + ws.provider.getDimension());
		}
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		//event.registerServerCommand(new SwarmCommand());
	}
}
