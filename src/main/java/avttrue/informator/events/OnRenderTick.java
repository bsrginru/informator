package avttrue.informator.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import avttrue.informator.Informator;

public class OnRenderTick
{
    private long lastUpdateRlTime = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.RenderTickEvent event)
    {
        if (!Informator.Global_ON) return; // если выключены
        try
        {
            // прореживаем обновления held_info (раз в где-то 250ms)
            if ((Informator.realTimeTick - lastUpdateRlTime) >= 5)
            {
                lastUpdateRlTime = Informator.realTimeTick;
                if (event.phase.equals(TickEvent.Phase.START))
                {
                    Informator.held_items.collectDataDuringTick();
                }
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
