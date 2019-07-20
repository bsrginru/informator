package avttrue.informator.tools;

import java.nio.charset.Charset;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TextTranslation
{
    public ITextComponent field_thesaurus = new TranslationTextComponent("avttrue.thesaurus.1");
    public ITextComponent field_velocity = new TranslationTextComponent("avttrue.informator.velocity");
    public ITextComponent field_blocks_per_sec = new TranslationTextComponent("avttrue.informator.blocks_per_sec");
    public ITextComponent field_moon_phase = new TranslationTextComponent("avttrue.informator.moon_phase");
    public ITextComponent[] field_moon_phases = {
            new TranslationTextComponent("avttrue.informator.moon_phase.0"),
            new TranslationTextComponent("avttrue.informator.moon_phase.1"),
            new TranslationTextComponent("avttrue.informator.moon_phase.2"),
            new TranslationTextComponent("avttrue.informator.moon_phase.3"),
            new TranslationTextComponent("avttrue.informator.moon_phase.4"),
            new TranslationTextComponent("avttrue.informator.moon_phase.5"),
            new TranslationTextComponent("avttrue.informator.moon_phase.6"),
            new TranslationTextComponent("avttrue.informator.moon_phase.7")};
    public ITextComponent field_fatal_error = new TranslationTextComponent("avttrue.informator.fatal_error");
    public ITextComponent field_enabled = new TranslationTextComponent("avttrue.informator.enabled");
    public ITextComponent field_disabled = new TranslationTextComponent("avttrue.informator.disabled");
    public ITextComponent field_illumination_of_block = new TranslationTextComponent("avttrue.informator.illumination_of_block");
    public ITextComponent field_luminosity_of_block = new TranslationTextComponent("avttrue.informator.luminosity_of_block");
    public ITextComponent field_block_lighting = new TranslationTextComponent("avttrue.informator.block_lighting");
    public ITextComponent field_of_block = new TranslationTextComponent("avttrue.informator.of_block");
    public ITextComponent field_provide_power_level = new TranslationTextComponent("avttrue.informator.provide_power_level");
    public ITextComponent field_wire_level = new TranslationTextComponent("avttrue.informator.wire_level");
    public ITextComponent field_power_level = new TranslationTextComponent("avttrue.informator.power_level");
    public ITextComponent field_powered = new TranslationTextComponent("avttrue.informator.powered");
    public ITextComponent field_behind = new TranslationTextComponent("avttrue.informator.behind");
    public ITextComponent field_infront = new TranslationTextComponent("avttrue.informator.infront");
    public ITextComponent field_onleft = new TranslationTextComponent("avttrue.informator.onleft");
    public ITextComponent field_onright = new TranslationTextComponent("avttrue.informator.onright");
    public ITextComponent field_ontop = new TranslationTextComponent("avttrue.informator.ontop");
    public ITextComponent field_below = new TranslationTextComponent("avttrue.informator.below");

    //
    // возвращает название профессии селянина по ID
    //
    public static String GetVillagerProfession(String id_profession)
    {
        if (id_profession == "unemployed") return I18n.format("avttrue.informator.57");
        else if (id_profession == "armorer") return I18n.format("avttrue.informator.24");
        else if (id_profession == "butcher") return I18n.format("avttrue.informator.25");
        else if (id_profession == "cartographer") return I18n.format("avttrue.informator.58");
        else if (id_profession == "cleric") return I18n.format("avttrue.informator.23");
        else if (id_profession == "farmer") return I18n.format("avttrue.informator.21");
        else if (id_profession == "fisherman") return I18n.format("avttrue.informator.59");
        else if (id_profession == "fletcher") return I18n.format("avttrue.informator.60");
        else if (id_profession == "leatherworker") return I18n.format("avttrue.informator.61");
        else if (id_profession == "librarian") return I18n.format("avttrue.informator.22");
        else if (id_profession == "mason") return I18n.format("avttrue.informator.62");
        else if (id_profession == "nitwit") return I18n.format("avttrue.informator.63");
        else if (id_profession == "shepherd") return I18n.format("avttrue.informator.64");
        else if (id_profession == "toolsmith") return I18n.format("avttrue.informator.65");
        else if (id_profession == "weaponsmith") return I18n.format("avttrue.informator.66");
        else if (id_profession == "home") return I18n.format("avttrue.informator.67");
        else if (id_profession == "meeting") return I18n.format("avttrue.informator.68");
        return I18n.format("avttrue.informator.69", id_profession); // Профессия %s
    }
    
    public static String RemoveFormat(String s)
    {
        byte[] bytes = s.getBytes();
        String retValue = new String(bytes, Charset.forName("UTF-8"));
        //retValue = s.replaceAll("\u00A7[0-9a-fk-or]", "").replace("\n", "");
        retValue = s.replace("\u00A70", "")
                    .replace("\u00A71", "")
                    .replace("\u00A72", "")
                    .replace("\u00A73", "")
                    .replace("\u00A74", "")
                    .replace("\u00A75", "")
                    .replace("\u00A76", "")
                    .replace("\u00A77", "")
                    .replace("\u00A78", "")
                    .replace("\u00A79", "")
                    .replace("\u00A7a", "")
                    .replace("\u00A7b", "")
                    .replace("\u00A7c", "")
                    .replace("\u00A7d", "")
                    .replace("\u00A7e", "")
                    .replace("\u00A7f", "")
                    .replace("\u00A7k", "")
                    .replace("\u00A7l", "")
                    .replace("\u00A7m", "")
                    .replace("\u00A7n", "")
                    .replace("\u00A7o", "")
                    .replace("\u00A7r", "")
                    .replace("\n", "");
        //System.out.println("\n" + s + " | " + retValue);
        return retValue;
    }

    // синглтон транслятора
    private static final TextTranslation instance = new TextTranslation();
    public static TextTranslation getInstance()
    {
        return instance;
    }
}
