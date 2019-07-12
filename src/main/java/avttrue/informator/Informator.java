package avttrue.informator;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.translation.LanguageMap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import avttrue.informator.data.CollectedClockData;
import avttrue.informator.data.CollectedWeatherData;
import avttrue.informator.events.OnClientTick;
import avttrue.informator.events.OnKeyInput;
import avttrue.informator.events.OnRenderGameOverlay;
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

    // используется для контроля игрового времени
    public static CollectedClockData clock = new CollectedClockData();
    public static CollectedWeatherData weather = new CollectedWeatherData();

    // используется в рассчётах скорости
    public static double prevPayerX = 0;
    public static double prevPayerY = 0;
    public static double prevPayerZ = 0;
    public static long lastXYZTick = -1;
    public static double velocity = 0;

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

    public Informator() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new OnKeyInput());
        MinecraftForge.EVENT_BUS.register(new OnClientTick());
        MinecraftForge.EVENT_BUS.register(new OnRenderGameOverlay());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        LOGGER.info("DIRT TranstationKey >> {}", Blocks.DIRT.getTranslationKey());
        LOGGER.info("DIRT translated to >> {}", LanguageMap.getInstance().translateKey(Blocks.DIRT.getTranslationKey()));
        LOGGER.info("DIRT translated to >> {}", (new TranslationTextComponent(Blocks.DIRT.getTranslationKey())).getString());
  
        KeyBindings.Initialization();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("avttrue_informator", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void hadDrinken(PlayerInteractEvent.RightClickBlock event)
    {
        LOGGER.info("hadDrinken {}", event.toString());

        final ClientWorld world = Minecraft.getInstance().world;
        PlayerEntity player = (PlayerEntity) event.getEntity();
        //ItemStack is = player.getHeldItem(Hand.MAIN_HAND);
        
        RayTraceResult rtr = rayTrace(world, player, true);
        LOGGER.info("hadDrinken rtr {}", rtr.toString());

        if (rtr.getType() == RayTraceResult.Type.ENTITY)
        {
            Entity lookedEntity = ((EntityRayTraceResult)rtr).getEntity();
            String s = lookedEntity.getName().toString();
            LOGGER.info("RayTraceResult entity {}", s);
        }
        else if (rtr.getType() == RayTraceResult.Type.BLOCK)
        {
            //BlockPos bp = ((BlockRayTraceResult)rtr).getPos();
            //BlockState bs = world.getBlockState(bp);
            //String s = bs.toString();
            //перегрузка:LOGGER.info("RayTraceResult block at {}", s, bp);
        }
    }

    protected RayTraceResult rayTrace(ClientWorld worldIn, PlayerEntity playerIn, boolean useLiquids)
    {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        LOGGER.info("rayTrace vec3d {}", vec3d.toString());
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        //if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP)
        //    d3 = ((net.minecraft.entity.player.EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance();
        Vec3d vec3d1 = vec3d.add(f6 * d3, f5 * d3, f7 * d3);
        LOGGER.info("rayTrace vec3d1 {}", vec3d1.toString());
        RayTraceContext.BlockMode bm = useLiquids ? RayTraceContext.BlockMode.COLLIDER : RayTraceContext.BlockMode.OUTLINE;
        RayTraceContext.FluidMode fm = useLiquids ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE;
        RayTraceContext ctx = new RayTraceContext(vec3d1, vec3d1, bm, fm, playerIn);
        LOGGER.info("rayTrace ctx {}", ctx.toString());
        return worldIn.rayTraceBlocks(ctx);
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
