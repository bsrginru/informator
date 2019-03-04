package avttrue.informator.Events;

import avttrue.informator.Informator;
import avttrue.informator.KeyBindings;
import avttrue.informator.Tools.Functions;
import avttrue.informator.Tools.TxtRes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class OnKeyInput 
{
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) 
	{
        if(KeyBindings.InformatorSwitch.isPressed())
        {
        	Informator.Gobal_ON = ! Informator.Gobal_ON;
        	if(Informator.Gobal_ON)
        		Functions.SendMessageToUser("\u00A7a" + TxtRes.GetLocalText("avttrue.informator.27", 
        				"The mod Informator is ON"), null);
        	else
        		Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.28", 
        				"The mod Informator is OFF"), null);
        	
        }
        
        if (KeyBindings.InformatorSurfaceCheckerSwitch.isPressed()) 
        {
        	Informator.SurfaceCheckerIsActive =! Informator.SurfaceCheckerIsActive;
        	if (Informator.SurfaceCheckerIsActive)
			{
				
				Informator.LaunchSurfaceChecker();
				Functions.SendMessageToUser("\u00A7a" + TxtRes.GetLocalText("avttrue.informator.38", 
        				"Surface properties indication is ON"), null);
			} 
			
			else
			{
				Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.39", 
        				"Surface properties indication is OFF"), null);
			}
		}
    }
}
