package avttrue.informator.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import avttrue.informator.Informator;

public class OnClientTick
{
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        Informator.realTimeTick++;
        if (!Informator.Global_ON) return; // если выключены
        if (event.phase != TickEvent.Phase.START)
        try
        {
            Informator.clock.collectDataDuringTick();
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
