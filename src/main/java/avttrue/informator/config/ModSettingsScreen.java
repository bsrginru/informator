package avttrue.informator.config;

import avttrue.informator.Informator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList.CategoryEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ModSettingsScreen extends Screen
{
	private Informator mod;
	Screen last;
	private Button btnOn;
	private Button btnIllumination;
	private Button btnHeldPanel;
	private Button btnBlockPanel;
	private Button exitButton, exportButton;
	private TextFieldWidget textureSize, outputFolder;
	public ModSettingsScreen(Minecraft mc, Screen last, Informator mod)
	{
        super(new TranslationTextComponent("avttrue_informator.name"));
        minecraft = mc;
        this.last = last;
        this.mod = mod;
    }
	private void onBooleanButtonClick(Button btn, ForgeConfigSpec.BooleanValue option, ITextComponent basis_caption)
	{
		ITextComponent on = Informator.TRANSLATOR.field_gui_on, off = Informator.TRANSLATOR.field_gui_off;
		option.set(!option.get());
        final String caption = ForgeI18n.parseMessage(basis_caption.getFormattedText(), option.get() ? on.getFormattedText() : off.getFormattedText());
		btn.setMessage(caption);
	}
	@Override
    protected void init()
	{
        super.init();

        final int btn_left_offset = 20, btn_center_offset = 5;
        final int btn_width = this.width / 2 - btn_left_offset - btn_center_offset, btn_height = 20;
        int btn_top_offset = 30;

        this.addButton(btnOn = new GuiButtonExt(
        		btn_left_offset, btn_top_offset,
        		btn_width, btn_height,
                I18n.format("avttrue.informator.gui.enable"),
                b -> this.onBooleanButtonClick(btnOn, ModSettings.GENERAL.Global_ON, Informator.TRANSLATOR.field_gui_enable)));
        this.addButton(btnIllumination = new GuiButtonExt(
        		this.width / 2 + btn_center_offset, btn_top_offset,
        		btn_width, btn_height,
        		I18n.format("avttrue.informator.gui.illumination"),
                b -> this.onBooleanButtonClick(btnIllumination, ModSettings.GENERAL.Global_IlluminationOnSurface, Informator.TRANSLATOR.field_gui_illumination)));
        btn_top_offset += btn_height + 10;
        
        this.addButton(btnHeldPanel = new GuiButtonExt(
        		btn_left_offset, btn_top_offset,
        		btn_width, btn_height,
                I18n.format("avttrue.informator.gui.held_panel"),
                b -> this.onBooleanButtonClick(btnHeldPanel, ModSettings.HELD.HeldItemDetails_Show, Informator.TRANSLATOR.field_gui_held_panel)));
        this.addButton(btnBlockPanel = new GuiButtonExt(
        		this.width / 2 + btn_center_offset, btn_top_offset,
        		btn_width, btn_height,
        		I18n.format("avttrue.informator.gui.block_panel"),
                b -> this.onBooleanButtonClick(btnBlockPanel, ModSettings.BLOCK.BlockBar_Show, Informator.TRANSLATOR.field_gui_block_panel)));
        btn_top_offset += btn_height + 10;

        this.addButton(exportButton = new GuiButtonExt(
        		this.width / 2 + 5, this.height - 38,
        		this.width / 2 - 55, 20,
                I18n.format("avttrue.informator.moon_phase.0"),
                null/*b -> new Processing()*/));
        this.addButton(exitButton = new GuiButtonExt(
        		50, this.height - 38,
        		this.width / 2 - 55, 20,
                I18n.format((last == null) ? /*м.б. иная надпись*/"avttrue.informator.gui.close_settings" : "avttrue.informator.gui.close_settings"),
                b -> minecraft.displayGuiScreen(last)));
        this.addButton(textureSize = new TextFieldWidget(font, 55 + font.getStringWidth(I18n.format("avttrue.informator.moon_phase.1")),
                  									 56, 50, 16, null, "128"));
        textureSize.setMaxStringLength(4);
        textureSize.setText("128");
        
//        this.configOptionsList = new ConfigOptionsList(this, this.minecraft);
//        this.children.add(arg0);
        
        final CategoryEntry clientCategoryEntry = new CategoryEntry(Informator.MODID + ".config.client");
		entries.add(clientCategoryEntry);
		this.addEntry(clientCategoryEntry); 
    }
	@Override
    public void render(int mouseX, int mouseY, float partialT) {
        renderBackground();
        drawCenteredString(font, title.getFormattedText(), width / 2, 15, 0x00ffffff);
        super.render(mouseX, mouseY, partialT);
    }
}
