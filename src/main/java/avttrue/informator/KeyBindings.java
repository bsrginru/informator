package avttrue.informator;

import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings
{
    // какие-то проблемы с загрузкой ресурсов? (стандартные ресурсы "key.categories.movement", "key.fullscreen" читаются)
    public static KeyBinding InformatorSwitch = new KeyBinding("avttrue_informator.key.keyboard.enable", 73, "avttrue_informator.name"); // "key.keyboard.i"
    public static KeyBinding InformatorSurfaceCheckerSwitch = new KeyBinding("avttrue_informator.key.keyboard.lighting", 301, "avttrue_informator.name"); // "key.keyboard.f12"
    /***public static KeyBinding InformatorDebug = new KeyBinding("Debug only", 74, "avttrue_informator.name"); // "key.keyboard.j"/***/

    public static void Initialization()
    {
        ClientRegistry.registerKeyBinding(InformatorSwitch);
        ClientRegistry.registerKeyBinding(InformatorSurfaceCheckerSwitch);
        /***ClientRegistry.registerKeyBinding(InformatorDebug);/***/
    }
}