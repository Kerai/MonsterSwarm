package swarm;

import org.apache.logging.log4j.*;


public class Log {
	static Logger logger  = LogManager.getLogger("MonsterSwarm");
	
	
	public static void println(String msg) {
		logger.info(msg);
	}
	public static void println(Object msg) {
		logger.info(msg);
	}
}
