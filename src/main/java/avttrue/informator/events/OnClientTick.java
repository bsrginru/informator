package avttrue.informator.events;

import java.math.BigDecimal;
import java.math.RoundingMode;

import avttrue.informator.Informator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class OnClientTick 
{
    private Minecraft mc = Minecraft.getInstance();
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) 
    {
    	final ClientWorld world = mc.world;

    	try
        {
    		// если игра ещё не начата вдруг
    		if (world == null) 
    		{
    			Informator.worldTime = -1;
    			return;
    		}
    		
    		if (!Informator.Global_ON) return; // если выключены
    		
        	PlayerEntity player = mc.player;
        	
        	//final StatisticsManager stats = ((ClientPlayerEntity)player).getStats();
        	//for(Stat<ResourceLocation> stat : Stats.CUSTOM) {
        	//	if (stat.getValue() == Stats.TIME_SINCE_REST)
        	//	{
        	//	    // эта статистика почему-то не обновляется... запрашивается с сервера что ли? (замирает на последнем отображённом значении)
        	//		String s = stat.format(stats.getValue(stat));
        	//		break;
        	//	}
        	//}
        	
        	//Stats.TIME_SINCE_REST.toString();

        	if (event.phase.equals(Phase.START))
        	{
    			//
        		// вычисляем время
        		//
        		if (player.dimension.getId() == 0) // 0 в обычном мире, -1 в аду, 1 в енде
        		{
        			Informator.worldTime = world.dimension.getWorldTime();
        			if (player.isSleeping()) // поскольку в аду спать нельзя, то точка сохранения параметра только здесь
        			{
        				// Внимание! таймер сна на сервере запускается спустя 100 тиков
        				Informator.wakeUpTime = Informator.worldTime;
        			}
        		}
    			else if (Informator.worldTime != -1) // если не в обычном мире и время уже определено
        		{
        			Informator.worldTime++;
        		}
        	}
        	
        	else if (event.phase.equals(Phase.END))
        	{
        		//
        		// вычисляем скорость
        		//
        		final long tick = world.dimension.getWorldTime();
        		if ((Informator.lastXYZTick > -1) && Informator.VelocityBar_Show) 
        		{
        			Informator.velocity = CalcVelocity(
        					Informator.prevPayerX, player.posX,
        					Informator.prevPayerY, player.posY,
        					Informator.prevPayerZ, player.posZ,
        					Informator.lastXYZTick, tick);
        			// пересохраняем координаты
        			Informator.lastXYZTick = tick;
        			Informator.prevPayerX = player.posX;
        			Informator.prevPayerY = player.posY;
        			Informator.prevPayerZ = player.posZ;
        		}
        		else // если координаты и время ещё не определялись
        		{
        			Informator.prevPayerX = player.posX;
            		Informator.prevPayerY = player.posY;
            		Informator.prevPayerZ = player.posZ;
            		Informator.lastXYZTick = tick;
        		}
        	}
        }
        catch(Exception e)
        {
        	Informator.worldTime = -1;
        	System.out.println(e.getMessage());
        	e.printStackTrace();
        }
    }

	private static double CalcVelocity(double x1, double x2, double y1, double y2, double z1, double z2, long tick1, long tick2)
	{
		try 
		{
			final long deltat = tick1 - tick2;
			if (deltat == 0) return 0; // если на паузе
			
			final double deltax = x1 - x2;
			final double deltay = y1 - y2;
			final double deltaz = z1 - z2;
		
			if (deltax > 999 || deltay > 999 || deltaz > 999) // если случались телепорты
			{
				//debug:System.out.println("Teleport?");
				return 0;
			}
		
			return new BigDecimal(
					20 * Math.sqrt(
							Math.pow(deltax, 2) + // бо 1 сек = 20 тиков
							Math.pow(deltay, 2) + 
							Math.pow(deltaz, 2)) / (Math.abs(deltat))
					).setScale(2, RoundingMode.UP).doubleValue();
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}
}
