package avttrue.informator.events;

import avttrue.informator.Informator;
import avttrue.informator.KeyBindings;
//import avttrue.informator.Tools.Functions;
//import avttrue.informator.tools.TxtRes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.InputEvent;

public class OnKeyInput 
{
	static int kuk = 0;
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) 
	{
        if (KeyBindings.InformatorSwitch.isPressed())
        {
//        	Informator.Global_ON = ! Informator.Global_ON;
        	//if (Informator.Global_ON)
        	//	Functions.SendMessageToUser("\u00A7a" + TxtRes.GetLocalText("avttrue.informator.27", 
        	//			"The mod Informator is ON"), null);
        	//else
        	//	Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.28", 
        	//			"The mod Informator is OFF"), null);
        	
//!!!debug only
OnKeyInput.kuk++; 
Informator.TimeBar_alignMode = OnKeyInput.kuk % 4;
Informator.TimeBarWeatherPretty_Show = ((OnKeyInput.kuk / 4) % 2) == 0;
Informator.TimeBarMoon_Show = ((OnKeyInput.kuk / 8) % 2) == 0;
Informator.TimeBarWeather_Show = ((OnKeyInput.kuk / 16) % 2) == 0;
Informator.TimeBar_Show = ((OnKeyInput.kuk / 32) % 2) == 0;
	
        }
        
        if (KeyBindings.InformatorSurfaceCheckerSwitch.isPressed())
        {
//!!!временно
Informator.Global_ON = ! Informator.Global_ON;

        //	Informator.SurfaceCheckerIsActive =! Informator.SurfaceCheckerIsActive;
        //	if (Informator.SurfaceCheckerIsActive)
		//	{
		//		
		//		Informator.LaunchSurfaceChecker();
		//		Functions.SendMessageToUser("\u00A7a" + TxtRes.GetLocalText("avttrue.informator.38", 
        //				"Surface properties indication is ON"), null);
		//	} 
		//	else
		//	{
		//		Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.39", 
        //				"Surface properties indication is OFF"), null);
		//	}
		}
    }
}
