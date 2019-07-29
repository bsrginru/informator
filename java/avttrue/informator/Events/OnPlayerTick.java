package avttrue.informator.Events;

import avttrue.informator.Informator;
import avttrue.informator.Tools.Functions;
import avttrue.informator.Tools.TxtRes;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class OnPlayerTick
{
	private Minecraft mc = Minecraft.getMinecraft();
	
	@SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) 
    {
		// если игра ещё не начата вдруг
		if (mc.world == null || 
			mc.player == null) 
		{
			return;
		}
		
    	// после проверки обновлений обрабатываем клик по чату
    	if (!Informator.haveWarnedVersionOutOfDate &&
    		event.player.world.isRemote && 
    		!Informator.versionChecker.isLatestVersion())
    	{
    		ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, Informator.INFORMATORDOWNLOAD_URL);
    		Style clickableChatStyle = new Style().setClickEvent(versionCheckChatClickEvent);
    		Functions.SendMessageToUser("\u00A7n" + Informator.MODNAME + " " + 
    									String.format(TxtRes.GetLocalText("avttrue.informator.30", 
    												"mod can update again! (ver.: %1$s) Click here"),
    												Informator.versionChecker.getLatestVersion()),
    									clickableChatStyle);
    		
    		Informator.haveWarnedVersionOutOfDate = true;
    	}
    	
    	if (!Informator.Gobal_ON) // выключили по горячей клавише
				return;
    	
    	// получение опыта
    	if (Informator.playertotalxp == -1)
    	{
    		Informator.playertotalxp = mc.player.experienceTotal;
    	}
    	else if (Informator.PickupedXP_Show)
    	{
    		int newplayertotalxp =  mc.player.experienceTotal;
    		
    		if (Informator.playertotalxp < newplayertotalxp)
    		{
    			String xpmessage = String.format(TxtRes.GetLocalText("avttrue.informator.35",
						"\u00A76Experience total %1$d (level %2$d), pickuped %3$d"), 
    					newplayertotalxp, 
						mc.player.experienceLevel, 
						newplayertotalxp - Informator.playertotalxp);
    			Functions.SendMessageToUser("\u00A76" + xpmessage, null);
    		}
    		if (Informator.playertotalxp > newplayertotalxp)
    		{
    			String xpmessage = String.format(TxtRes.GetLocalText("avttrue.informator.36",
						"\u00A76Experience total %1$d (level %2$d), pickuped %3$d"), 
    					newplayertotalxp, 
						mc.player.experienceLevel, 
						Informator.playertotalxp - newplayertotalxp);
    			Functions.SendMessageToUser("\u00A76" + xpmessage, null);
    		}
    		
    	}
    	Informator.playertotalxp = mc.player.experienceTotal;
    }
}
