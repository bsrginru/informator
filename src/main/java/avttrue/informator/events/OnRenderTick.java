package avttrue.informator.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import avttrue.informator.Informator;

public class OnRenderTick
{
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (!Informator.Global_ON) return; // если выключены
        try
        {
            if (event.phase == TickEvent.Phase.START)
            {
                Informator.held_items.collectDataDuringTick(Informator.realTimeTick);
                Informator.weather.collectDataDuringTick(Informator.realTimeTick);
                Informator.enchantments.collectDataDuringTick(Informator.realTimeTick, Informator.EnchantBar_ShowHands, Informator.EnchantBar_ShowBody);
            }
            else //подразумевается: if (event.phase == TickEvent.Phase.END)
            {
                Informator.velocity.collectDataDuringTick();
            }
        }
        catch(Exception e)
        {
            Informator.Global_ON = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }
}
