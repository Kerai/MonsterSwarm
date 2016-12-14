package swarm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import swarm.EntitySwarmRocket;

public class RocketRenderFactory implements IRenderFactory<EntitySwarmRocket> {

	@Override
	public Render<? super EntitySwarmRocket> createRenderFor(RenderManager manager) {
		return  new RenderSnowball<EntitySwarmRocket>(manager, Items.FIREWORKS, Minecraft.getMinecraft().getRenderItem());
	}

}
