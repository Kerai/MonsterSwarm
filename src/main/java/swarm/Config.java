package swarm;

import java.io.File;

import net.minecraftforge.common.config.*;

public class Config {
	public static boolean ENABLE_DIGGING = true;
	public static boolean ENABLE_BUILDING = true;
	public static boolean ENABLE_SOUNDS = true;
//	public static boolean SHOOT_ROCKET = true;
//	public static double  ROCKET_FREQUENCY = 0.05f;
	public static boolean KILL_MOBS_DAYTIME = true;
	public static boolean ATTACK_ANIMALS = true;
	public static boolean ENABLE_SPRINTING = true;
	public static boolean UNDERGROUND = true;
	public static int AGGRO_RANGE = 120;
	public static int MAX_RESISTANCE = 90;
	public static float MAX_RES = MAX_RESISTANCE/5;
	public static float RESISTANCE_MULTIPLIER;

	static Configuration config;
	public static boolean DEBUG = false;

	public static void preInit(File conf) {
		config = new Configuration(conf);

		reload();

		config.save();
	}


	private static void removeProperty(String category, String property) {
		ConfigCategory cat = config.getCategory(category);
		if(cat != null) {
			Property prop = cat.get(property);
			if(prop!=null) {
				cat.remove(prop);
			}
		}
	}

	public static void reload() {
		config.load();

		config.getCategory(Configuration.CATEGORY_GENERAL).setComment("");

//		{
//			config.renameProperty(Configuration.CATEGORY_GENERAL, "Creepers Explode", "Creepers Explode By Themselves");
//			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Creepers Explode By Themselves", true);
//			prop.comment = "If Set to True, creepers will explode by themselves if they get bored (stay in place for too long)";
//			CREEP_EXPLODE = prop.getBoolean();
//
//		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Swarm Underground", true);
			prop.setComment("If set to True, monsters will always swarm for targets that are underground (below level 40)");
			UNDERGROUND = prop.getBoolean();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Enable Digging", true);
			prop.setComment("If set to True, monsters will be able to digg through walls");
			ENABLE_DIGGING = prop.getBoolean();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Enable Building", true);
			prop.setComment("If set to True, monsters will be able to build bridges and staircases to get higher up");
			ENABLE_BUILDING = prop.getBoolean();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Enable Sprinting", true);
			prop.setComment("If set to True, monsters will sprint if their target sprints");
			ENABLE_SPRINTING = prop.getBoolean();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "All Mobs Burn", true);
			prop.setComment("If set to True, all swarming mobs will burn in daylight. (useful to get rid of creepers after a night)");
			KILL_MOBS_DAYTIME = prop.getBoolean();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Attack Animals", true);
			prop.setComment("If set to True, mobs will target animals.");
			ATTACK_ANIMALS = prop.getBoolean();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Enable Sounds", true);
			prop.setComment("If set to True, digging will play an indicating sound (this is server side config)");
			ENABLE_SOUNDS = prop.getBoolean();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Aggro Range", 120);
			prop.setComment("From how far away can monsters see you (Swarm Underground ignores this value!)");
			AGGRO_RANGE = prop.getInt();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Block Resistance Multiplier", 1.0);
			prop.setComment("Multiplier for block resistance agains swarm. Set it to 2.0 and walls will be twice as resistant to swarm. Set it to 0 and everything will fall on one hit");
			RESISTANCE_MULTIPLIER = (float) prop.getDouble();
		}
		{
			Property prop = config.get(Configuration.CATEGORY_GENERAL, "Maximum Swarm Resistance", 90);
			prop.setComment("Block resistance will be capped to this value when monsters digg it.");
			MAX_RESISTANCE = prop.getInt();
			MAX_RES = MAX_RESISTANCE/5f;
		}
	}

}
