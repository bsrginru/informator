package avttrue.informator;

import net.minecraft.client.gui.GuiScreen;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModConfigGUI extends GuiConfig 
{
  public ModConfigGUI(GuiScreen parent) 
  {
   
    super(parent, 
    		(new ConfigElement(Informator.configFile.getCategory(Configuration.CATEGORY_GENERAL))).getChildElements(),
    		Informator.MODID, false, false, GuiConfig.getAbridgedConfigPath(Informator.configFile.toString())); 
    
  }
}
