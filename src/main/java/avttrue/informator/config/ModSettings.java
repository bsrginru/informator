package avttrue.informator.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.config.ModConfig;

import avttrue.informator.Informator;

// Настройки мода, которые хранятся в ./config/avttrue_informator-client.toml файле
// Кодирование .toml файла описано здесь: https://github.com/toml-lang/toml
// Подписка на события рассмотрена здесь: https://www.minecraftforge.net/forum/topic/68011-config-not-working/?tab=comments#comment-328444
// Пример реализации рассмотрен здесь: https://www.minecraftforge.net/forum/topic/68971-1132-mod-configuration-using-forgeconfigspec/
public class ModSettings
{
    public static class General
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> Global_ON;
        public final ForgeConfigSpec.ConfigValue<Boolean> Global_HideInDebugMode;
        public final ForgeConfigSpec.ConfigValue<Boolean> Global_ShowPanel;
        public final ForgeConfigSpec.ConfigValue<Integer> Global_DistanceView;

        public General(ForgeConfigSpec.Builder builder)
        {
            builder.push("General");
            Global_ON = builder
                    .comment("Включает/отключает работу мода [false/true|default:true]")
                    .translation("avttrue_informator.name")
                    .define("general_on", true);
            Global_HideInDebugMode = builder
                    .comment("Включает/отключает отображение GUI мода в режиме отладки по F3 [false/true|default:true]")
                    .translation("avttrue_informator.name")
                    .define("general_hide_on_debug", true);
            Global_ShowPanel = builder
                    .comment("Включает/отключает отображение градиентных панелей под надписями [false/true|default:true]")
                    .translation("avttrue_informator.name")
                    .define("general_show_panel", true);
            Global_DistanceView = builder
                    .comment("Дистанция видимости объектов [1..64|default:32]")
                    .translation("avttrue_informator.name")
                    .defineInRange("general_view_distance", 32, 1, 64);
            builder.pop();
        }
    }

    // === Два варианта инициализации ===
    // Первый (аналогично использованному в ForgeConfig) :
    //public static final ForgeConfigSpec spec;
    //public static final General GENERAL;
    //static
    //{
    //    final Pair<General, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(General::new);
    //    spec = specPair.getRight();
    //    GENERAL = specPair.getLeft();
    //}
    //---
    // Второй (аналогично рассмотреному во множестве примеров, в т.ч. на форуме minecraftforge)
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();
    // === === === ===

    // следующее событие не срабатывает (в Forge пока ВООБЩЕ нет вызовов этого метода)
    // видимо оно как-то связано с конфигурационным GUI, который тоже отсутствует в Forge
    // так что может быть это нормально (временно)
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        System.out.printf("Config Changed 2 %s %s", event.getModID(), event.getConfigID());
        if (Informator.R1 == null) Informator.R1 = new Integer(1); else Informator.R1++;
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent)
    {
        Informator.LOGGER.debug("Loaded Informator' config file {}", configEvent.getConfig().getFileName());
    }

    // это просто событие о том, что изменился файл
    // для того, чтобы изменения параметров доставились в код и переменные изменили свои состояния
    // необходимо (видимо) анализировать configEvent.getConfig().getConfigData()
    // но нормальных примеров по работе с этим нет, а Forge положил болт на подобные перечитывания
    // (или вообще пока не доделан...)
    //---
    // Поэтому ПРИНУДИТЕЛЬНО вызываем метод перечитывания файла с настройками, и это РАБОТАЕТ! :)
    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent)
    {
        Informator.LOGGER.fatal("Informator' config just got changed on the file system!");
        ((CommentedFileConfig)configEvent.getConfig().getConfigData()).load();
    }
}