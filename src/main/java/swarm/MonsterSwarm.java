package swarm;


import static swarm.Log.println;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import swarm.util.Magic;
@Mod(modid = "monsterswarm", name = "Monster Swarm", version = "1.2.0", acceptableRemoteVersions = "1.2.*")
public class MonsterSwarm {

	public static final List<Class> excludedAttackers = new ArrayList<Class>();
	public static final List<Class> includedAttackers = new ArrayList<Class>();
	public static final List<Class> includedTargets = new ArrayList<Class>();
	public static final List<Class> includedDiggers = new ArrayList<Class>();

	List<SwarmWorld> worlds = new ArrayList<SwarmWorld>();
	public final Map<WorldServer, SwarmWorld> map = new HashMap<WorldServer, SwarmWorld>();

	// The instance of your mod that Forge uses.
	@Instance(value = "monsterswarm")
	public static MonsterSwarm INSTANCE;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "swarm.client.ClientProxy", serverSide = "swarm.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.preInit(event.getSuggestedConfigurationFile());
		
		EntityRegistry.registerModEntity(new ResourceLocation("SwarmRocket"), EntitySwarmRocket.class, "SwarmRocket", 1, this, 80, 1, true);
		//EntityRegistry.registerModEntity(OfflineVillager.class, "OfflineVillager", 2, this, 80, 60, true);

		ForgeChunkManager.setForcedChunkLoadingCallback(this, callback);
	}

	LoadingCallback callback = new LoadingCallback() {

		@Override
		public void ticketsLoaded(List<Ticket> tickets, World world) {
			for(Ticket t : tickets) {
				ForgeChunkManager.releaseTicket(t);
			}
		}
	};

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
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
			worlds.add(sw);
			map.put(ws, sw);

			println("Swarm for dimension " + ws.provider.getDimension());
		}
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new SwarmCommand());
	}
}