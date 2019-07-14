package avttrue.informator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import avttrue.informator.config.Config;
import avttrue.informator.data.CollectedClockData;
import avttrue.informator.data.CollectedEnchantmentsData;
import avttrue.informator.data.CollectedHeldItemsData;
import avttrue.informator.data.CollectedVelocityData;
import avttrue.informator.data.CollectedWeatherData;
import avttrue.informator.events.OnClientTick;
import avttrue.informator.events.OnKeyInput;
import avttrue.informator.events.OnRenderGameOverlay;
import avttrue.informator.events.OnRenderTick;
import avttrue.informator.tools.Functions;
import avttrue.informator.tools.TextTranslation;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("avttrue_informator")
public class Informator
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    // Статический транслятор, который регистрирует "самопереводящиеся" текстовые ресурсы
    public static final TextTranslation TRANSLATOR = TextTranslation.getInstance();
    // Статические функции модуля, в которых есть всяко-разно для упрощения кода функциональных шклассов-обработчиков
    public static final Functions TOOLS = Functions.getInstance();
    // Синглтон мода (устанавливается в конструкторе-инициализаторе)
    private static Informator INSTANCE;
    public static Informator getInstance()
    {
        return INSTANCE;
    }

    // время, которое идёт со скоростью ClientTick (около 20 тиков в секунду) и не зависит от нахождения игрока в аду/краю
    public static volatile long realTimeTick = 0;
    // используется для контроля игрового времени
    public static CollectedClockData clock = new CollectedClockData();
    public static CollectedWeatherData weather = new CollectedWeatherData();
    // используется для контроля скорости перемещения игрока
    public static CollectedVelocityData velocity = new CollectedVelocityData();
    // используется для контроля удерживаемых и надетых предметов
    public static CollectedHeldItemsData held_items = new CollectedHeldItemsData();
    // используется для контроля заколдованных предметов
    public static CollectedEnchantmentsData enchantments = new CollectedEnchantmentsData();

    //Global
    public static boolean Global_HideInDebugMode = true;
    public static boolean Global_ShowPanel = true; // отображать градиентную панель под надписями (если false, то надписи прозрачные)
    //public static int Global_DistanceView;
    public static boolean Global_ON = true;
    
    //HeldItemBar
    public static boolean HeldItemDetails_Show = true;
    //public static int HeldItemDetails_xOffset = 0;
    //public static int HeldItemDetails_yOffset = 0;
    public static float HeldItemDetails_DamageAlarm = 0.1F;
    //public static String HeldItemDetails_alignMode; // default

    //InfoBlockBar
//    public static boolean InfoBlockBar_Show;
//    public static boolean InfoBlockBar_ShowIcons;
//    public static int InfoBlockBar_xPos;
//    public static int InfoBlockBar_yPos;
//    public static String InfoBlockBar_alignMode;
//    public static boolean InfoBlockBar_ShowName;

    //SpeedBar
    public static boolean VelocityBar_Show = true;
    public static int VelocityBar_xOffset = 0;
    public static int VelocityBar_yOffset = 0;
    public static boolean VelocityBar_ShowMax = true;

    //TimeBar
    public static boolean TimeBar_Show = true;
    public static int TimeBar_xOffset = 0;
    public static int TimeBar_yOffset = 0;
    public static boolean TimeBarMoon_Show = true;
    public static boolean TimeBarWeather_Show = true;
    public static boolean TimeBarWeatherPretty_Show = true;
    public static int TimeBar_alignMode = 0; // 0 top_left; 1 top_right; 2 bottom_left; 3 bottom_right
    public static boolean TimeBarWeather_WithMoonPhases = true;
    public static boolean TimeBarBed_Show = true;
    
    // EnchantBar
    public static int EnchantBar_xOffset = 0;
    public static int EnchantBar_yOffset = 0;
    public static boolean EnchantBar_Show = true;
    public static boolean EnchantBar_ShowHands = true;
    public static boolean EnchantBar_ShowBody = true;
    
    // TargetMob Bar
//    public static int TargetMobBar_WidthScreenPercentage;
//    public static int TargetMobBar_yPos;
//    public static int TargetMobBar_xPos;
    public static boolean TargetMobBar_Show = true;
//    public static boolean TargetMobBar_DrawMobPortrait;
//    public static boolean TargetMobBar_DrawBuffIcon;
//    public static String TargetMobBar_alignMode;
//    public static int TargetMobBar_ViewDelay;
//    public static boolean TargetMobBar_SeachOwnerInWeb;
//    public static int TargetMobBar_OwnerDataPeriod;

    // отладочные регистры, чтобы смотреть всякую отладочную ерунду в рантайме
    //public static float R0 = 0;
    public static Integer R1 = null, R2 = null, R3 = null;

    // Хватит руться в Интернете!
    // правильный (актуальный) пример (и последовательность) инициализации см. в net.minecraftforge.common.ForgeMod
    // в ModExample инициализация неправильная!!!
    public Informator()
    {
        LOGGER.info("Informator mod loading, version {}, for Forge {} MC {} with MCP {}",
                "???",
                ForgeVersion.getVersion(),
                MCPVersion.getMCVersion(),
                MCPVersion.getMCPVersion());
        INSTANCE = this;
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, Config.spec);
        modEventBus.register(Config.class);
    }

    // Преинициализация
    //---
    // Called before {@link FMLClientSetupEvent} or {@link FMLDedicatedServerSetupEvent} during mod startup.
    // Called after {@link net.minecraftforge.event.RegistryEvent.Register} events have been fired.
    // Most non-specific mod setup will be performed here. Note that this is a parallel dispatched event - you cannot
    // interact with game state in this event.
    // See net.minecraftforge.fml.DeferredWorkQueue to enqueue work to run on the main game thread after this event has
    // completed dispatch.
    private void preInit(final FMLCommonSetupEvent event)
    {
        LOGGER.info("PreInit");

        MinecraftForge.EVENT_BUS.register(new OnKeyInput());
        MinecraftForge.EVENT_BUS.register(new OnClientTick());
        MinecraftForge.EVENT_BUS.register(new OnRenderTick());
        MinecraftForge.EVENT_BUS.register(new OnRenderGameOverlay());
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Нечто, что делается только на клиентской стороне: получение настроек игры, key bindings
    //---
    // Called before {@link InterModEnqueueEvent}
    // Called after {@link FMLCommonSetupEvent}
    // Called on {@link net.minecraftforge.api.distmarker.Dist#CLIENT} - the game client.
    // Do client only setup with this event, such as KeyBindings.
    // This is a parallel dispatch event.
    private void doClientStuff(final FMLClientSetupEvent event)
    {
        LOGGER.info("Game Settings {}", event.getMinecraftSupplier().get().gameSettings);
        KeyBindings.Initialization();
    }
}
