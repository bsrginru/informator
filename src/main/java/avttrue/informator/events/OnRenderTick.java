package avttrue.informator.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import avttrue.informator.Informator;
import avttrue.informator.config.ModSettings;

public class OnRenderTick
{
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (!ModSettings.GENERAL.Global_ON.get()) return; // если выключены
        try
        {
            if (event.phase == TickEvent.Phase.START)
            {
                Informator.held_items.collectDataDuringTick(Informator.realTimeTick);
                Informator.weather.collectDataDuringTick(Informator.realTimeTick);
                Informator.enchantments.collectDataDuringTick(
                        Informator.realTimeTick,
                        ModSettings.ENCHANTS.EnchantBar_ShowHands.get(),
                        ModSettings.ENCHANTS.EnchantBar_ShowBody.get());
            }
            else //подразумевается: if (event.phase == TickEvent.Phase.END)
            {
                Informator.velocity.collectDataDuringTick();
            }
        }
        catch(Exception e)
        {
            ModSettings.GENERAL.Global_ON.set(false);
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }
}
