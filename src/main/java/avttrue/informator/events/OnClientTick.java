package avttrue.informator.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import avttrue.informator.Informator;

public class OnClientTick
{
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (!Informator.Global_ON) return; // если выключены
        try
        {
            if (event.phase.equals(Phase.START))
            {
                Informator.clock.collectDataDuringTick();
                Informator.weather.collectDataDuringTick();
            }
            else if (event.phase.equals(Phase.END))
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
