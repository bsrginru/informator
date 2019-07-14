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
        //----------------------------------------------------------------------
        // Настройки мода общего назначения
        public final ForgeConfigSpec.BooleanValue Global_ON;
        public final ForgeConfigSpec.BooleanValue Global_HideInDebugMode;
        public final ForgeConfigSpec.BooleanValue Global_ShowPanel;
        public final ForgeConfigSpec.IntValue Global_DistanceView;
        //----------------------------------------------------------------------
        // Панель износа оружия, инструментов и брони
        public final ForgeConfigSpec.BooleanValue HeldItemDetails_Show;
        //public static int HeldItemDetails_xOffset = 0;
        //public static int HeldItemDetails_yOffset = 0;
        public final ForgeConfigSpec.IntValue HeldItemDetails_DamageAlarm;
        public final ForgeConfigSpec.IntValue HeldItemDetails_DamageWarning;
        //public static float  = 0.1F;
        //public static String HeldItemDetails_alignMode; // default
        //----------------------------------------------------------------------
        //InfoBlockBar
//      public static boolean InfoBlockBar_Show;
//      public static boolean InfoBlockBar_ShowIcons;
//      public static int InfoBlockBar_xPos;
//      public static int InfoBlockBar_yPos;
//      public static String InfoBlockBar_alignMode;
//      public static boolean InfoBlockBar_ShowName;
        //----------------------------------------------------------------------
        // Панель скорости перемещения персонажа
        public final ForgeConfigSpec.BooleanValue VelocityBar_Show;
        public final ForgeConfigSpec.IntValue VelocityBar_xOffset;
        public final ForgeConfigSpec.IntValue VelocityBar_yOffset;
        public final ForgeConfigSpec.BooleanValue VelocityBar_ShowMax;
        //----------------------------------------------------------------------
        // Панель времени
        public final ForgeConfigSpec.BooleanValue TimeBar_Show;
        public final ForgeConfigSpec.IntValue TimeBar_alignMode; // 0 top_left; 1 top_right; 2 bottom_left; 3 bottom_right
        public final ForgeConfigSpec.IntValue TimeBar_xOffset;
        public final ForgeConfigSpec.IntValue TimeBar_yOffset;
        public final ForgeConfigSpec.BooleanValue TimeBarMoon_Show;
        public final ForgeConfigSpec.BooleanValue TimeBarWeather_Show;
        public final ForgeConfigSpec.BooleanValue TimeBarWeatherPretty_Show;
        public final ForgeConfigSpec.BooleanValue TimeBarWeather_WithMoonPhases;
        public final ForgeConfigSpec.BooleanValue TimeBarBed_Show;
        //----------------------------------------------------------------------
        // Панель зачарований на предметах персонажа
        public final ForgeConfigSpec.BooleanValue EnchantBar_Show;
        public final ForgeConfigSpec.IntValue EnchantBar_xOffset;
        public final ForgeConfigSpec.IntValue EnchantBar_yOffset;
        public final ForgeConfigSpec.BooleanValue EnchantBar_ShowHands;
        public final ForgeConfigSpec.BooleanValue EnchantBar_ShowBody;
        //----------------------------------------------------------------------
        // TargetMob Bar
//        public static int TargetMobBar_WidthScreenPercentage;
//        public static int TargetMobBar_yPos;
//        public static int TargetMobBar_xPos;
//        public static boolean TargetMobBar_Show = true;
//        public static boolean TargetMobBar_DrawMobPortrait;
//        public static boolean TargetMobBar_DrawBuffIcon;
//        public static String TargetMobBar_alignMode;
//        public static int TargetMobBar_ViewDelay;
//        public static boolean TargetMobBar_SeachOwnerInWeb;
//        public static int TargetMobBar_OwnerDataPeriod;
        //----------------------------------------------------------------------

        public General(ForgeConfigSpec.Builder builder)
        {
            builder.push("General");
            //----------------------------------------------------------------------
            // Настройки мода общего назначения
            Global_ON = builder
                    .comment("Включает/отключает работу мода [false/true|default:true]")
                    .translation("avttrue_informator.name")
                    .define("general_on", true);
            Global_HideInDebugMode = builder
                    .comment("Включает/отключает Информатор в режиме отладки по F3 [false/true|default:true]")
                    .define("general_hide_on_debug", true);
            Global_ShowPanel = builder
                    .comment("Надписи Информатора выводятся на панелях [false/true|default:true]")
                    .define("general_show_panel", true);
            Global_DistanceView = builder
                    .comment("Дистанция видимости объектов [1..64|default:32]")
                    .defineInRange("general_view_distance", 32, 1, 64);
            //----------------------------------------------------------------------
            // Панель износа оружия, инструментов и брони
            HeldItemDetails_Show = builder
                    .comment("Включает/отключает индикаторы износа оружия, инструментов и брони [false/true|default:true]")
                    .define("held_show", true);
            HeldItemDetails_DamageAlarm = builder
                    .comment("Процент износа оружия, инструментов и брони по достижении которого зажигается тревога [1..50|default:10]")
                    .defineInRange("held_damage_alarm", 10, 1, 50);
            HeldItemDetails_DamageWarning = builder
                    .comment("Процент износа оружия, инструментов и брони по достижении которого зажигается предупреждение [2..50|default:15]")
                    .defineInRange("held_damage_warning", 15, 2, 50);
            //----------------------------------------------------------------------
            // Панель скорости перемещения персонажа
            VelocityBar_Show = builder
                    .comment("Включает/отключает отображение скорости перемещения персонажа [false/true|default:true]")
                    .define("velocity_show", true);
            VelocityBar_ShowMax = builder
                    .comment("Отображение максимальной зарегистрированной скорости на интервале движения персонажа [false/true|default:true]")
                    .define("velocity_show_max", true);
            VelocityBar_xOffset = builder
                    .comment("Смещение на экране индикатора скорости по оси x [-9999..9999:default:0]")
                    .defineInRange("velocity_offset_x", 0, -9999, 9999);
            VelocityBar_yOffset = builder
                    .comment("Смещение на экране индикатора скорости по оси y [-9999..9999:default:0]")
                    .defineInRange("velocity_offset_y", 0, -9999, 9999);
            //----------------------------------------------------------------------
            // Панель скорости перемещения персонажа
            TimeBar_Show = builder
                    .comment("Включает/отключает отображение панели времени [false/true|default:true]")
                    .define("time_show", true);
            TimeBar_alignMode = builder
                    .comment("Расположение панели времени на экране [0:top_left,1:top_right,2:bottom_left,3:bottom_right]")
                    .defineInRange("time_align", 0, 0, 3);
            TimeBar_xOffset = builder
                    .comment("Смещение на экране панели времени по оси x [-9999..9999:default:0]")
                    .defineInRange("time_offset_x", 0, -9999, 9999);
            TimeBar_yOffset = builder
                    .comment("Смещение на экране панели времени по оси y [-9999..9999:default:0]")
                    .defineInRange("time_offset_y", 0, -9999, 9999);
            TimeBarMoon_Show = builder
                    .comment("Отображение фазы луны, влияющей на агрессивность мобов [false/true|default:true]",
                             "Параметр можно не использовать, если установлен time_weather_pretty=true, в этом случае",
                             "фаза луны будет показана на панели времени")
                    .define("time_moon_show", true);
            TimeBarWeather_Show = builder
                    .comment("Отображение погодных условий в верхнем мире [false/true|default:true]",
                             "Параметр можно не использовать, если установлен time_weather_pretty=true, в этом случае",
                             "погода будет показана на панели времени")
                    .define("time_weather_show", true);
            TimeBarWeatherPretty_Show = builder
                    .comment("Отображение погоды 'как на карте сервера' [false/true|default:true], учитывается при time_weather_show=true")
                    .define("time_weather_pretty", true);
            TimeBarWeather_WithMoonPhases = builder
                    .comment("Иконка погоды в ночное время показывает фазу луны [false/true|default:true],",
                             "учитывается при time_weather_pretty=true")
                    .define("time_weather_moon_phases", true);
            TimeBarBed_Show = builder
                    .comment("Индикатор 'пора поваляться в кровати, иначе скоро прилетят Фантомы' [false/true|default:true]")
                    .define("time_to_bed", true);
            //----------------------------------------------------------------------
            // Панель зачарований на предметах персонажа
            EnchantBar_Show = builder
                    .comment("Включает/отключает отображение зачарований на предметах персонажа [false/true|default:true]")
                    .define("enchants_show", true);
            EnchantBar_xOffset = builder
                    .comment("Смещение на экране панели зачарований по оси x [-9999..9999:default:0]")
                    .defineInRange("enchants_offset_x", 0, -9999, 9999);
            EnchantBar_yOffset = builder
                    .comment("Смещение на экране панели зачарований по оси y [-9999..9999:default:0]")
                    .defineInRange("enchants_offset_y", 0, -9999, 9999);
            EnchantBar_ShowHands = builder
                    .comment("Отображение списка зачарований оружия и инструментов [false/true|default:true]")
                    .define("enchants_hands", true);
            EnchantBar_ShowBody = builder
                    .comment("Отображение списка зачарований одежды [false/true|default:true]")
                    .define("enchants_body", true);
            //----------------------------------------------------------------------
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