package avttrue.informator.Events;

import avttrue.informator.Informator;
import avttrue.informator.Tools.Functions;
import avttrue.informator.Tools.TxtRes;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class OnClientConnectedToServer 
{

	@SubscribeEvent 
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
		ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, Informator.MINECRAFTING_URL);
		Style clickableChatStyle = new Style().setClickEvent(versionCheckChatClickEvent);
		Functions.SendMessageToUser(Informator.MODNAME + " v. \u00A76" + Informator.MODVER, null);
    	Functions.SendMessageToUser("\u00A79" + TxtRes.GetLocalText("avttrue.informator.29", 
    								"" + "Welcom to russian Minecraft community") + 
    										": \u00A7n" + Informator.MINECRAFTING_URL, clickableChatStyle);
    }
}
