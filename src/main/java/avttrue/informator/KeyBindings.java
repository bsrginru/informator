package avttrue.informator;

import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

import avttrue.informator.Tools.TxtRes;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindings 
{
    public static KeyBinding InformatorSwitch = null;
    public static KeyBinding InformatorSurfaceCheckerSwitch = null;

    public static void Initialization() 
    {
    	InformatorSurfaceCheckerSwitch = new KeyBinding(TxtRes.GetLocalText("avttrue.informator.44",
    														"Informator - Light Level indicator"), 
    			 										Keyboard.KEY_F12, 
    			 										"AVTTRUE:Informator");
    	InformatorSwitch = new KeyBinding(TxtRes.GetLocalText("avttrue.informator.43",
    											"Informator On/Off"), 
											Keyboard.KEY_I,
    										"AVTTRUE:Informator");

    	ClientRegistry.registerKeyBinding(InformatorSurfaceCheckerSwitch);
        ClientRegistry.registerKeyBinding(InformatorSwitch);        
    }
}