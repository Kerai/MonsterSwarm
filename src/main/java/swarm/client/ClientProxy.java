package swarm.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import swarm.CommonProxy;
import swarm.EntitySwarmRocket;

public class ClientProxy extends CommonProxy {
       
        @Override
        public void registerRenderers() {
            RenderingRegistry.registerEntityRenderingHandler(EntitySwarmRocket.class, new RocketRenderFactory());
        }
       
}
