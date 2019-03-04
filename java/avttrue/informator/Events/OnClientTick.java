package avttrue.informator.Events;

import avttrue.informator.Informator;
import avttrue.informator.Tools.Functions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OnClientTick 
{
    private Minecraft mc = Minecraft.getMinecraft();
    
    @SubscribeEvent
    public void onTick(ClientTickEvent event)
    {
    	try
        {
    		// если игра ещё не начата вдруг
    		if (mc.theWorld == null) 
    		{
    			Informator.worldTime = -1;
    			return;
    		}
    		
    		if(!Informator.Gobal_ON) // если выключены
    			return;
    		
    		if (event.phase.equals(Phase.START))
        	{
    			//
        		// вычисляем время
        		//
    			if(mc.thePlayer.dimension == 0) // если в обычном мире
        		{
        			Informator.worldTime = mc.theWorld.getWorldTime();	
        		}
        		
        		if (Informator.worldTime != -1) // если не в обычном мире и время уже определено
        		{
        			Informator.worldTime++;
        		}
        		
        	}
        	
        	if (event.phase.equals(Phase.END))
        	{
        		//
        		// вычисляем скорость
        		//
        		if(Informator.lastXYZTick > -1 &&
        			Informator.SpeedBar_Show) 
        		{
        			Informator.Speed = Functions.CulcSpeed(Informator.oldPayerX, mc.thePlayer.posX,
        														Informator.oldPayerY, mc.thePlayer.posY,
        														Informator.oldPayerZ, mc.thePlayer.posZ,
        														Informator.lastXYZTick, mc.theWorld.getWorldTime());
        				
        			// пересохраняем координаты
        			Informator.lastXYZTick = mc.theWorld.getWorldTime();
        			Informator.oldPayerX = mc.thePlayer.posX;
        			Informator.oldPayerY = mc.thePlayer.posY;
        			Informator.oldPayerZ =  mc.thePlayer.posZ;
        		}
        		else // если координаты и время ещё не определялись
        		{
        			Informator.oldPayerX = mc.thePlayer.posX;
            		Informator.oldPayerY = mc.thePlayer.posY;
            		Informator.oldPayerZ = mc.thePlayer.posZ;
            		Informator.lastXYZTick = mc.theWorld.getWorldTime();
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
}
