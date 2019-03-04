package avttrue.informator.Events;

import avttrue.informator.Informator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnRenderWorldLastEvent 
{
	/*
	 * Сделано на базе
	 * http://minecraft.curseforge.com/projects/light-level-overlay-reloaded 
	 */	
	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent evt) 
	{
		if (Informator.Gobal_ON && 
			Informator.SurfaceCheckerIsActive) 
		{
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if (player == null) return;
			double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * evt.getPartialTicks();
	        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * evt.getPartialTicks();
	        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * evt.getPartialTicks();
	        Informator.overlayRenderer.RenderLight(x, y, z, Informator.surfaceChecker.surfaceOverlays);
		}
	}
}
