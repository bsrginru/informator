package avttrue.informator.data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class CollectedBlockData
{
    public Data data = new Data();

    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid;
        // следующие переменные валидны только при valid==true
        public BlockRayTraceResult target; // бывший targetBlock
        public BlockPos pos; // бывший tBlockPosition
        public PowerDetails power = new PowerDetails(); // питание блока (частично известно, когда есть данные по pos)
        public boolean isAir;
        // следующие переменные не null только при isAir==false
        public BlockState state; // бывший tBlockState
        public Block block; // бывший tBlock
        // следующие переменные не null только при block!=null
        public ItemStack stack;
        // следующие переменные не null только при stack!=null
        public Item item;
    }

    public class PowerDetails
    {
        public boolean wire; // аналог canProvidePower
        public int strong_level;
        public boolean powered;
        // следующие переменные валидны только при powered==true
        public boolean strong; // сильно-заряженный блок может активировать провод; слабо-заряженный не может
        public int level;
        public Direction facing; // направление взгляда персонажа
        public Direction direction; // направление, откуда пришёл сигнал
    };

    private Minecraft mc = Minecraft.getInstance();
    private final static Direction[] FACING_VALUES = Direction.values();

//public EntityRayTraceResult targetEntity = null;
//    public int MobHealth = 0;
//    public int MobMaxHealth = 0;
//    public int MobTotalArmor = 0;
//    public double DistToPlayer = 0;
//    public String MobName = null;
//    public String MobOwner = null;
//    public double MobMovementSpeed = 0;
//    public double MobJumpHeight = 0;
//    public boolean IsCreeperPowered = false;
//    public boolean IsHorseSkeletonTrap = false;
//    public boolean ISee = false; // флаг, что кого-то видим вообще
//    public boolean ISeeNow = false;  // флаг, что кого-то видим прямо сейчас (учёт индикации задержки)
//    public LivingEntity elb = null;
//    private Entity vEntity = null;

    public void refresh(boolean with_electricity)
    {
        // определяем сущность, на которую смотрим
        data.valid = false;

        //было:vEntity = mc.getRenderViewEntity();
        //было:block.rayTrace = vEntity.rayTrace(Informator.Global_DistanceView, 1);
        RayTraceResult rtr = mc.objectMouseOver;
        if (rtr == null) return;

        if (rtr.getType() == RayTraceResult.Type.ENTITY)
        {
//                vEntity = ((EntityRayTraceResult)rtr).getEntity();
//Informator.R4.clear();
//if (vEntity != null) Informator.R4.add(vEntity.getDisplayName().getFormattedText());
        }
        // определяем блок на который смотрим
        else if (rtr.getType() == RayTraceResult.Type.BLOCK)
        {
            // на всякий случай (повторно) проверяем, что блок действительно является блоком
            // (ещё он может быть "внутри головы", см. isInside(), но пока это здесь не контролируем... это связано со строительными лесами)
            data.target = ((BlockRayTraceResult)rtr);
            data.valid = data.target.getType() != RayTraceResult.Type.MISS;
            if (!data.valid) return;

            final ClientWorld world = mc.world;
            final ClientPlayerEntity player = mc.player;

            data.pos = data.target.getPos();
            data.isAir = world.isAirBlock(data.pos);
            if (with_electricity)
            {
                data.power.wire = false; // проводник : это свойство блока, способность проводить энергию (дверь, когда открывается сигналом, не является проводником)
                data.power.strong_level = world.getStrongPower(data.pos);
                data.power.powered = false; // заряжен : признак world.isBlockPowered, который является world.getRedstonePower(с-любого-из-направлений, по результатам world.getStrongPower или block.state.getWeakPower
                data.power.strong = false; // заряжен сильно : признак world.getStrongPower, который явлется рекурсивным поиском во всех направлениях любого активного блока (с уровнем >= 15)
                data.power.level = 0; // уровень заряда : максимальный из уровней заряда во всех направлениях (по результатам world.getStrongPower или block.state.getWeakPower)
                data.power.direction = null; // направление : с которого пришёл максимальный уровень заряда
                data.power.facing = player.getHorizontalFacing(); // направление взгляда персонажа
            }
            data.state = null;
            data.block = null;
            data.stack = null; // ItemStack.EMPTY;
            data.item = null;
            if (!data.isAir)
            {
                data.state = world.getBlockState(data.pos);
                if (data.state != null)
                {
                    if (with_electricity)
                    {
                        data.power.wire = data.state.canProvidePower();
                    }
                    data.block = data.state.getBlock();
                    if (data.block != null)
                    {
                        data.stack = data.state.getBlock().getPickBlock(data.state, data.target, world, data.pos, player);
                        if ((data.stack != null) && !data.stack.isEmpty())
                        {
                            data.item = data.stack.getItem();
                        }
                    }
                }
            }

            // получение данных по электрификации блока (механизмы и схемы)
            // список блоков со свойством canProvidePower:
            //  +AbstractButtonBlock
            //   AbstractPressurePlateBlock
            //  +  WeightedPressurePlateBlock
            //  +  PressurePlateBlock
            //  +DaylightDetectorBlock
            //  +DetectorRailBlock
            //   LadderBlock
            //   LecternBlock
            //  +LeverBlock
            //   ObserverBlock
            //   RailBlock
            //   RedstoneBlock
            //   RedstoneDiodeBlock
            //  +  ComparatorBlock
            //  +  RepeaterBlock
            //  +RedstoneTorchBlock
            //  +RedstoneWireBlock
            //   TrappedChestBlock
            //  +TripWireHookBlock
            //   WorldEntitySpawner
            if (with_electricity)
            {
                for(Direction direction : FACING_VALUES)
                {
                    int level;
                    final BlockPos pos = data.pos.offset(direction);
                    final BlockState state = world.getBlockState(pos);
                    if (state.shouldCheckWeakPower(world, pos, direction))
                    {
                        level = world.getStrongPower(pos);
                        if (level > 0)
                        {
                            data.power.powered = true;
                            if (level > 15) level = 15;
                            if (level > data.power.level)
                            {
                                data.power.strong = true;
                                data.power.level = level;
                                data.power.direction = direction;
                            }
                        }
                    }
                    else
                    {
                        level = state.getWeakPower(world, pos, direction);
                        if (level > 0)
                        {
                            data.power.powered = true;
                            if (level > 15) level = 15;
                            if (level > data.power.level)
                            {
                                data.power.strong = false;
                                data.power.level = level;
                                data.power.direction = direction;
                            }
                        }
                    }
                }
            }
        }
        else
        {
/**Informator.R4.clear();*/
        }

/*
        // на кого смотрим
        elb = getTarget(ModSettings.GENERAL.TargetMobBar_DistanceView.get(), 1, block.rayTrace);

        if (elb != null) // сохраняем время и моба для показа с задержкой
        {
            Informator.lastmobtime = new Date();
            Informator.lastmob = elb;
            ISeeNow = true;
        }
            
        // реализуем задержку при показе
        if (elb == null &&                     // если в текущий момент моба не наблюдаем
            Informator.lastmob != null &&     // последний виденный моб есть?
            mc.player.isAlive() &&            // игрок жив ли?
            Informator.lastmob.isAlive() &&    // моб жив ли?
            Informator.lastmob.getDistance(mc.player) <= Informator.Global_DistanceView) // дистанция в рамках настроек? (различие миров не учитывается)
        {
            ISeeNow = false;
            Date currtime = new Date(); // текущее время
            long deltatime = (currtime.getTime()  - Informator.lastmobtime.getTime()) / 1000; // в секундах
            if(deltatime < Informator.TargetMobBar_ViewDelay) // в рамках настроек
                elb = Informator.lastmob;
            else
                Informator.lastmob = null; // не обязательно, но чтобы лишний раз не заходить в эту ветку
        }
    
        if (elb == null)        // моба не увидели, закончили
        {
            ISee = false;
            return;
        }
        
        ISee = true;
        MobHealth = (int)(elb.getHealth());
        MobMaxHealth = (int)(elb.getMaxHealth());
        DistToPlayer = new BigDecimal(elb.getDistance(mc.player)).setScale(1,RoundingMode.UP).doubleValue();
        MobTotalArmor = elb.getTotalArmorValue();
        
        // конь
        if(elb instanceof AbstractHorseEntity)
        {
            double jstrength = ((AbstractHorseEntity)elb).getHorseJumpStrength();
            
            // TODO проверять в новых версиях
            // метод вычисления скорости из изысканий Румикона
            // (проверено по исходникам Spigot-а в феврале 2019)
            double jheight= 0;
            while (jstrength > 0)
            {
                 jheight += jstrength;
                 jstrength = (jstrength - 0.08D) * 0.9800000190734863D;
            }
            
            // 43 - волшебная константа из изысканий Румикона
            double speed = 43.0D * ((AbstractHorseEntity)elb).getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
            
            MobOwner = Functions.getUsernameByUUID(((AbstractHorseEntity)elb).getOwnerUniqueId());
            MobMovementSpeed = new BigDecimal(speed).setScale(2, RoundingMode.UP).doubleValue();
            MobJumpHeight = new BigDecimal(jheight).setScale(3, RoundingMode.UP).doubleValue();
            
            if (elb instanceof SkeletonHorseEntity)
            {
                IsHorseSkeletonTrap = true;
            }
            else
            {
                IsHorseSkeletonTrap = false;
            }
        }
        // волк
        else if (elb instanceof WolfEntity)
        {
            MobOwner = Functions.getUsernameByUUID(((WolfEntity)elb).getOwnerId());
        }
        // котик
        else if(elb instanceof CatEntity)
        {
            MobOwner = Functions.getUsernameByUUID(((CatEntity)elb).getOwnerId());
        }
        // криппер
        else if(elb instanceof CreeperEntity)
        {
            IsCreeperPowered = ((CreeperEntity)elb).getPowered();
        }
    
        // имя
        if(elb.hasCustomName())
        {
            MobName =  "\'" + elb.getCustomName() + "\'";
        }
        // крестьянин
        else if(elb instanceof VillagerEntity)
        {
            VillagerEntity villager = (VillagerEntity)elb;
            VillagerProfession profession = villager.getVillagerData().getProfession();
            MobName = TxtRes.GetVillagerProfession(profession.getPointOfInterest().toString()); // "unemployed", "cleric", "farmer",...        
        }
        else
        {
            MobName = elb.getEntityString();
        }
    
        // добавки к имени
        if(elb.isChild())
            MobName += ", " + TxtRes.GetLocalText("avttrue.informator.18", "baby");
        
        if(IsCreeperPowered)
            MobName += ", " + TxtRes.GetLocalText("avttrue.informator.52", "Powered");
        
        if(IsHorseSkeletonTrap)
            MobName += ", " + TxtRes.GetLocalText("avttrue.informator.53", "Trap");
*/
    }

    // TODO определяем сущность, на которую смотрим
    // http://gamedev.stackexchange.com/questions/59858/how-to-find-the-entity-im-looking-at
    // см. также getMouseOver в коде MC
/*
    private LivingEntity getTarget(double distance, 
            float tick, 
            RayTraceResult CheckBlock)
    {
        if(vEntity instanceof LivingEntity)
            return (LivingEntity)vEntity;
        else
            return null;
    }
    private LivingEntity getTarget(double distance, 
                                        float tick, 
                                        RayTraceResult CheckBlock)
    {
        Entity pointedEntity;
        double d0 = distance;
        double d1 = d0;
        float f1 = 1.0F;
        
        RayTraceResult rtr = vEntity.rayTrace(d0, tick);
        
        Vec3d vec3 =  vEntity.getPositionEyes(tick);
        Vec3d vec31 = vEntity.getLook(tick);
        Vec3d vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
        Vec3d vec33 = null;
        pointedEntity = null;
       
        try
        {
            List list = mc.world.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), 
                    vEntity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).
                    expand((double)f1, (double)f1, (double)f1));
            double d2 = d1;

            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity = (Entity)list.get(i);
                if (entity.canBeCollidedWith())
                {
                    float f2 = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double)f2, (double)f2, (double)f2);
                    RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                    if (axisalignedbb.isVecInside(vec3))
                    {
                        if (0.0D < d2 || d2 == 0.0D)
                        {
                            pointedEntity = entity;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (movingobjectposition != null)
                    {
                        double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                        if (d3 < d2 || d2 == 0.0D)
                        {
                            if (entity == vEntity.getRidingEntity() && //
                                    !entity.canRiderInteract())
                            {
                                if (d2 == 0.0D)
                                {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            }
                            else
                            {
                                pointedEntity = entity;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
            }
        
            if (pointedEntity != null && 
                    (d2 < d1 || rtr == null))
            {
                rtr = new RayTraceResult(pointedEntity, vec33);

                if (pointedEntity instanceof LivingEntity || 
                        pointedEntity instanceof EntityItemFrame)
                {
                    mc.pointedEntity = pointedEntity;
                }
            }
       
            if (rtr != null &&
                rtr.typeOfHit == RayTraceResult.Type.ENTITY &&
                rtr.entityHit instanceof LivingEntity)
            {
                // проверка, что сущность дальше блока
                // чтобы не смотреть сквозь стены
                if (CheckBlock != null)
                {
                    // вычисляем дистанцию до блока
                    double deltax = mc.thePlayer.posX - CheckBlock.getBlockPos().getX();
                    double deltay = mc.thePlayer.posY - CheckBlock.getBlockPos().getY();
                    double deltaz = mc.thePlayer.posZ - CheckBlock.getBlockPos().getZ();
                    float disttoblock = (float)Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2) + Math.pow(deltaz, 2));
                    
                    if(rtr.entityHit.getDistanceToEntity(mc.thePlayer) > disttoblock)
                    {
                        return null;
                    }
                }
                return (LivingEntity)rtr.entityHit;
            }
        }
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }
*/
    
//    
// TODO повреждение блока
//    
/*
     public int GetBlockDamage()
    {
        if(block.state == null || 
                tBlock == null)
            return -1;    
        
        try
        {
            int bDamage = -1;
            Iterator<IProperty<?>> iterator = tBlock.getBlockState().getProperties().iterator();
            while (iterator.hasNext())
            {
                IProperty ip = iterator.next();
                if (ip.getName().equals("damage"))
                {
                    PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);
                    bDamage = ((Integer)block.state.getValue(DAMAGE)).intValue();
                    break;
                }
            }
            return bDamage;
        }
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
*/
}
