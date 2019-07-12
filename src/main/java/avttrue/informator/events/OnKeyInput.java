package avttrue.informator.events;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import avttrue.informator.Informator;
import avttrue.informator.KeyBindings;

public class OnKeyInput 
{
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (KeyBindings.InformatorSwitch.isPressed())
        {
            Informator.Global_ON = ! Informator.Global_ON;
            if (Informator.Global_ON)
                Informator.TOOLS.SendMessageToUser(Informator.TRANSLATOR.field_enabled, (new Style()).setColor(TextFormatting.DARK_GRAY));
            else
                Informator.TOOLS.SendMessageToUser(Informator.TRANSLATOR.field_disabled, (new Style()).setColor(TextFormatting.DARK_GRAY));
            
//!!!debug only
//static int kuk = 0;
//OnKeyInput.kuk++; 
//Informator.TimeBar_alignMode = OnKeyInput.kuk % 4;
//Informator.TimeBarWeatherPretty_Show = ((OnKeyInput.kuk / 4) % 2) == 0;
//Informator.TimeBarMoon_Show = ((OnKeyInput.kuk / 8) % 2) == 0;
//Informator.TimeBarWeather_Show = ((OnKeyInput.kuk / 16) % 2) == 0;
//Informator.TimeBar_Show = ((OnKeyInput.kuk / 32) % 2) == 0;
    
        }
        
        if (KeyBindings.InformatorSurfaceCheckerSwitch.isPressed())
        {
        //    Informator.SurfaceCheckerIsActive =! Informator.SurfaceCheckerIsActive;
        //    if (Informator.SurfaceCheckerIsActive)
        //    {
        //        
        //        Informator.LaunchSurfaceChecker();
        //        Functions.SendMessageToUser("\u00A7a" + TxtRes.GetLocalText("avttrue.informator.38", 
        //                "Surface properties indication is ON"), null);
        //    } 
        //    else
        //    {
        //        Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.39", 
        //                "Surface properties indication is OFF"), null);
        //    }
        }
    }
}
