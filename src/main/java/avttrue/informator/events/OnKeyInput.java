package avttrue.informator.events;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import avttrue.informator.Informator;
import avttrue.informator.KeyBindings;
import avttrue.informator.config.ModSettings;

public class OnKeyInput 
{
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (KeyBindings.InformatorSwitch.isPressed())
        {
            ModSettings.GENERAL.Global_ON.set(!ModSettings.GENERAL.Global_ON.get());
            if (ModSettings.GENERAL.Global_ON.get())
                Informator.TOOLS.SendMessageToUser(Informator.TRANSLATOR.field_enabled, (new Style()).setColor(TextFormatting.DARK_GRAY));
            else
                Informator.TOOLS.SendMessageToUser(Informator.TRANSLATOR.field_disabled, (new Style()).setColor(TextFormatting.DARK_GRAY));
        }
        
        else if (KeyBindings.InformatorSurfaceCheckerSwitch.isPressed())
        {
            ModSettings.GENERAL.Global_IlluminationOnSurface.set(!ModSettings.GENERAL.Global_IlluminationOnSurface.get());
            if (ModSettings.GENERAL.Global_ON.get())
                Informator.TOOLS.SendMessageToUser(Informator.TRANSLATOR.field_illumination_enabled, (new Style()).setColor(TextFormatting.DARK_GRAY));
            else
                Informator.TOOLS.SendMessageToUser(Informator.TRANSLATOR.field_illumination_disabled, (new Style()).setColor(TextFormatting.DARK_GRAY));
        }
        
        /***else if (KeyBindings.InformatorDebug.isPressed())
        {
            //!!!debug only
            kuk++; 
            //Informator.TimeBar_alignMode = kuk % 4;
            //Informator.TimeBarWeatherPretty_Show = ((kuk / 4) % 2) == 0;
            //Informator.TimeBarMoon_Show = ((kuk / 8) % 2) == 0;
            //Informator.TimeBarWeather_Show = ((kuk / 16) % 2) == 0;
            //Informator.TimeBar_Show = ((kuk / 32) % 2) == 0;
            ModSettings.GENERAL.BlockBar_alignMode.set((ModSettings.GENERAL.BlockBar_alignMode.get() + 1) % 4);
            ModSettings.GENERAL.BlockBar_ShowElectricity.set(((kuk / 4) % 2) == 0);
            ModSettings.GENERAL.BlockBar_ShowIcons.set(((kuk / 8) % 2) == 0);
            ModSettings.GENERAL.BlockBar_ShowPlayerOffset.set(((kuk / 16) % 2) == 0);
            ModSettings.GENERAL.BlockBar_ShowName.set(((kuk / 32) % 2) == 0);
        }/***/
    }

    static int kuk = 0;
}
