package avttrue.informator.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        // Панель информации о блоке
        public final ForgeConfigSpec.BooleanValue BlockBar_Show;
        public final ForgeConfigSpec.IntValue BlockBar_alignMode; // 0 top_left; 1 top_right; 2 bottom_left; 3 bottom_right
        public final ForgeConfigSpec.IntValue BlockBar_xOffset;
        public final ForgeConfigSpec.IntValue BlockBar_yOffset;
        public final ForgeConfigSpec.BooleanValue BlockBar_ShowName;
        public final ForgeConfigSpec.BooleanValue BlockBar_ShowIcons;
        public final ForgeConfigSpec.BooleanValue BlockBar_ShowPlayerOffset;
        public final ForgeConfigSpec.BooleanValue BlockBar_ShowElectricity;
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
        // Панели сущностей, на которые смотрим
        public final ForgeConfigSpec.BooleanValue TargetMobBar_Show;
        public final ForgeConfigSpec.BooleanValue TargetMobBar_ShowDistance;
        public final ForgeConfigSpec.IntValue TargetMobBar_DistanceView;
        public final ForgeConfigSpec.IntValue TargetMobBar_ScreenWidth;
        public final ForgeConfigSpec.IntValue TargetMobBar_xOffset;
        public final ForgeConfigSpec.IntValue TargetMobBar_yOffset;
        public final ForgeConfigSpec.IntValue TargetMobBar_alignMode; // 0 top_center; 1 top_left; 2 top_right
        public final ForgeConfigSpec.BooleanValue TargetMobBar_ShowPortrait;
//        public static boolean TargetMobBar_DrawBuffIcon;
//        public static int TargetMobBar_ViewDelay;
//        public static boolean TargetMobBar_SeachOwnerInWeb;
//        public static int TargetMobBar_OwnerDataPeriod;
        //----------------------------------------------------------------------

        public General(ForgeConfigSpec.Builder builder)
        {
            builder
                .comment(" Файл с настройками позволяет изменять количество отображаемой информации на экране.",
                         " Все настройки разделены на группы (held, block, velocity и т.д.), отображение каждой",
                         " из которых отключается соответствующим параметром (held_show, block_show, velocity_show и т.д.)",
                         "",
                         " Группы и сооветствующие им 'выключатели' отображения:",
                         "    * general_on - полное отключение работы мода;",
                         "    * time_show - панель времени, и расширяющие её дополнительные панели:",
                         "      * time_moon_show - панель фаз луны;",
                         "      * time_weather_show - панель погоды;",
                         "    * velocity_show - индикатор скорости перемещения;",
                         "    * held_show - индикаторы износа оружия, инструментов, брони;",
                         "    * enchants_show - чары, наложенные на предметы",
                         "    * block_show - информация о блоке на который направлен взгляд персонажа;",
                         "    * target_show - информация о существах, на которые направлен взгляд персонажа.",
                         "",
                         " В описании к каждому параметру указаны допустимые значения, которые он может принимать,",
                         " например:",
                         "    * block_name [false/true|default:true];",
                         "    * target_distance [4..64|default:24];",
                         "    * block_align [0:top_left,1:top_right,2:bottom_left,3:bottom_right];",
                         "    * и т.д.",
                         "",
                         " Для отключения, например, отображения информации о скорости персонажа необходимо указать:",
                         "   velocity_show = false",
                         " в этом случае все параметры, начинающиеся с префикса velocity_ будут проигнорированы.",
                         " И наоборот, включение упомянутого параметра приведёт к их учёту. В частности, параметр,",
                         " velocity_show_max=true будет учитываться тогда и только тогда, когда velocity_show=true.")
                .push("General");
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
            // Панель информации о блоке
            BlockBar_Show = builder
                    .comment("Включает/отключает отображение информации о блоке на который смотрит персонаж [false/true|default:true]")
                    .define("block_show", true);
            BlockBar_alignMode = builder
                    .comment("Расположение панели информации о блоке на экране [0:top_left,1:top_right,2:bottom_left,3:bottom_right]")
                    .defineInRange("block_align", 3, 0, 3);
            BlockBar_xOffset = builder
                    .comment("Смещение на экране панели информации о блоке по оси x [-9999..9999:default:0]")
                    .defineInRange("block_offset_x", 0, -9999, 9999);
            BlockBar_yOffset = builder
                    .comment("Смещение на экране панели информации о блоке по оси y [-9999..9999:default:0]")
                    .defineInRange("block_offset_y", 0, -9999, 9999);
            BlockBar_ShowName = builder
                    .comment("Наименование блока на который смотрит персонаж [false/true|default:true]")
                    .define("block_name", true);
            BlockBar_ShowIcons = builder
                    .comment("Иконка блока на который смотрит персонаж [false/true|default:true]")
                    .define("block_icon", true);
            BlockBar_ShowPlayerOffset = builder
                    .comment("Смещение персонажа относительно блока на который он смотрит [false/true|default:true]")
                    .define("block_player_offset", true);
            BlockBar_ShowElectricity = builder
                    .comment("Заряженность блока на который смотрит персонаж [false/true|default:true]")
                    .define("block_electricity", true);
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
            // Панели сущностей, на которые смотрим
            TargetMobBar_Show = builder
                    .comment("Включает/отключает информацию о сущностях (блоки и NPC), на которые смотрим [false/true|default:true]")
                    .define("target_show", true);
            TargetMobBar_ShowDistance = builder
                    .comment("Включает/отключает отображение дистанции до существа [false/true|default:true]")
                    .define("target_show_distance", true);
            TargetMobBar_DistanceView = builder
                    .comment("Дистанция видимости существ [4..64|default:24], учитывается при target_show_distance=true")
                    .defineInRange("target_distance", 32, 4, 64); // Minecraft.getInstance().playerController.getBlockReachDistance()
            TargetMobBar_ScreenWidth = builder
                    .comment("Размер информационной панели, на которой выводится информация о NPC, сущностях [10..100|default:30], в процентах от ширины экрана")
                    .defineInRange("target_width", 30, 10, 100);
            TargetMobBar_alignMode = builder
                    .comment("Расположение панели информации о блоке на экране [0:top_center,1:top_left,2:top_right]")
                    .defineInRange("target_align", 0, 0, 2);
            TargetMobBar_xOffset = builder
                    .comment("Смещение на экране панели информации о NPC, сущностях по оси x [-9999..9999:default:0]")
                    .defineInRange("target_offset_x", 0, -9999, 9999);
            TargetMobBar_yOffset = builder
                    .comment("Смещение на экране панели информации о NPC, сущностях  по оси y [-9999..9999:default:0]")
                    .defineInRange("target_offset_y", 0, -9999, 9999);
            TargetMobBar_ShowPortrait = builder
                    .comment("Отображение портрета существа на которого направлен взгляд персонажа [false/true|default:true]")
                    .define("target_show_portrait", true);
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