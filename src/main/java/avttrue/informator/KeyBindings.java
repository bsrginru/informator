package avttrue.informator;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindings 
{
	// какие-то проблемы с загрузкой ресурсов? (стандартные ресурсы "key.categories.movement", "key.fullscreen" читаются)
    public static KeyBinding InformatorSwitch = new KeyBinding("avttrue_informator.key.keyboard.enable", 73, "avttrue_informator.name"); // "key.keyboard.i"
    public static KeyBinding InformatorSurfaceCheckerSwitch = new KeyBinding("avttrue_informator.key.keyboard.lighting", 301, "avttrue_informator.name"); // "key.keyboard.f12"

    public static void Initialization() 
    {
        ClientRegistry.registerKeyBinding(InformatorSwitch);        
    	ClientRegistry.registerKeyBinding(InformatorSurfaceCheckerSwitch);
    }
}