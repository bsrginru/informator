package avttrue.informator;

import java.util.Set;

import avttrue.informator.ModConfigGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GUIFactory implements IModGuiFactory
{
	
	    @Override
	    public void initialize(Minecraft minecraftInstance)
	    {

	    }

	    @Override
	    public boolean hasConfigGui()
	    {
	        return true;
	    }
	    
	    @Override
	    public GuiScreen createConfigGui(GuiScreen parentScreen)
	    {
	    	return new ModConfigGUI(parentScreen);
	    }

	    @Override
	    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	    {
	        return null;
	    }

//	    @Override
//	    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
//	    {
//	        return null;
//	    }
	
}
