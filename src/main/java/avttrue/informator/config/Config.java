package avttrue.informator.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.config.ModConfig;

import avttrue.informator.Informator;

// Настройки мода, которые хранятся в ./config/avttrue_informator-client.toml файле
// Кодирование .toml файла описано здесь https://github.com/toml-lang/toml
public class Config
{
    public static class General
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> ModEnabled;
        public final ForgeConfigSpec.ConfigValue<Integer> TorchDistance;

        public General(ForgeConfigSpec.Builder builder)
        {
            builder.push("General");
            ModEnabled = builder
                    .comment("Enables/Disables the whole Mod [false/true|default:true]")
                    .translation("enable.ocdtorcher.config")
                    .define("enableMod", true);
            TorchDistance = builder
                    .comment("sets the Reach of the Torcher [0..50|default:20]")
                    .translation("distance.ocdtorcher.config")
                    .defineInRange("TorcherDistance", 20, 0,50);
            builder.pop();
        }
    }

    public static final ForgeConfigSpec spec;
    public static final General GENERAL;
    static
    {
        final Pair<General, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(General::new);
        spec = specPair.getRight();
        GENERAL = specPair.getLeft();
    }

    // следующие события почему-то не срабатывают (разобраться), быть может версия Forge кривая?
    // (у них самих подобный кусок кода сейчас закомментирован)
    // а быть может Spec должен быть не Forge-вский, а свой?
    //---
    // Появляются вот такие сообщения, но нет реакции в моих методах:
    // [13:26:09.013] [Thread-1/DEBUG] [ne.mi.fm.co.ConfigFileTypeHandler/CONFIG]: Config file avttrue_informator-client.toml changed, sending notifies
    // [13:26:09.013] [Thread-1/FATAL] [ne.mi.co.ForgeConfig/CORE]: Forge config just got changed on the file system!

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        System.out.printf("Config Changed 2 %s %s", event.getModID(), event.getConfigID());
        if (Informator.R1 == null) Informator.R1 = new Integer(0); else Informator.R1++;
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent)
    {
        Informator.LOGGER.debug("Loaded Informator' config file {}", configEvent.getConfig().getFileName());
        if (Informator.R2 == null) Informator.R2 = new Integer(0); else Informator.R2++;
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent)
    {
        Informator.LOGGER.fatal("Informator' config just got changed on the file system!");
        if (Informator.R3 == null) Informator.R3 = new Integer(0); else Informator.R3++;
    }
}