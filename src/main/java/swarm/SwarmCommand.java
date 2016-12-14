package swarm;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import swarm.util.Vec3I;

public class SwarmCommand extends CommandBase {

	@Override
    public String getCommandName() {
	    return "swarm";
    }

	@Override
    public String getCommandUsage(ICommandSender sender) {
	    return "/swarm reload";
    }
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	    if(args.length<1)
	    	return;

	    if(args[0].equalsIgnoreCase("meta")) {
	    	EntityPlayer plr = ((EntityPlayer)sender);
	    	Vec3I vec = new Vec3I(plr);
	    	
//	    	int data = vec.getData(plr.worldObj);
//		    sender.addChatMessage(new ChatComponentText("block metadata: " + data));
	    }
	    if(args[0].equalsIgnoreCase("reload")) {
		    Config.reload();
		    sender.addChatMessage(new TextComponentString("swarm config reloaded"));
	    }
	    if(args[0].equalsIgnoreCase("debug")) {
		    Config.DEBUG = !Config.DEBUG;
		    sender.addChatMessage(new TextComponentString("Debug set to: " + Config.DEBUG));
	    }
	}

}
