package avttrue.informator.config;

import java.util.ArrayList;
import java.util.List;

import avttrue.informator.Informator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.gui.widget.list.KeyBindingList.CategoryEntry;

final class ModOptionsList extends AbstractOptionList<ModOptionsList.Entry>
{
	private final ModSettingsScreen configGui;

	public ModOptionsList(ModSettingsScreen configGui, Minecraft mc)
	{
		super(mc, configGui.width + 45, configGui.height, 43, configGui.height - 32, 20);
		this.configGui = configGui;

		final List<ModOptionsList.Entry> entries = new ArrayList<>();

		final CategoryEntry clientCategoryEntry = new CategoryEntry(Informator.MODID + ".config.client");
		entries.add(clientCategoryEntry);
		this.addEntry(clientCategoryEntry);
		getConfigValues(ConfigHolder.CLIENT).forEach((configValue, name) -> {
			final ValueEntry<?> e = createValueEntry(configValue, name, () -> ConfigHelper.clientConfig);
			entries.add(e);
			this.addEntry(e);
		});
	}
	
	abstract static class Entry extends AbstractOptionList.Entry<ModOptionsList.Entry>
	{
		abstract String getTranslatedText();

		void tick() {
		}
	} 
}