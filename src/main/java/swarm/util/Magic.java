package swarm.util;

import static swarm.Log.*;

import java.util.List;

import net.minecraft.block.Block;

public class Magic {

	public static Class findClass(String name) {
		try {
	        return Class.forName(name);
        } catch (ClassNotFoundException e) {
        	println("Class not found: " + name);
        }
		return null;
	}
	
	
	public static void addClass(List<Class> list, String name) {
		Class cls = findClass(name);
		if(cls!= null) {
			list.add(cls);
		}
	}
	
	public static void setResist(String block, float resist) {
		Block type = Block.getBlockFromName(block);
		if(type != null) {
			type.setResistance(resist);
			println("Resistance of '" + block + "' set to " + resist);
		} else {
			println("Block '" + block + "' not found.");
		}
	}

}
