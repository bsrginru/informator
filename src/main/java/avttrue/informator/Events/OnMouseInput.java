package avttrue.informator.Events;

import org.lwjgl.input.Mouse;

import avttrue.informator.Informator;
import avttrue.informator.Thesaurus.ThesaurusWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class OnMouseInput 
{
	@SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) 
	{
		// обрабатываем клик по кнопке Тезауруса
		Minecraft mc = Minecraft.getMinecraft();
		if(Informator.ShowThesaurusButton &&
				mc.currentScreen instanceof GuiChat && 
				Mouse.isButtonDown(0))
		{
			int size = 20; //линейные размеры кнопки
			ScaledResolution scaledResolution = new ScaledResolution(mc);
			int x = Mouse.getX() / scaledResolution.getScaleFactor();
			int y = Mouse.getY() / scaledResolution.getScaleFactor();
			if(x <= size && y <= 2*size-5 && y >= size-5)
			{
				ThesaurusWindow.CreateMainWindow();
			}
		}
    }
}
