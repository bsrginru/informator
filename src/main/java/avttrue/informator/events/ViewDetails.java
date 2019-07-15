package avttrue.informator.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import avttrue.informator.Informator;
import avttrue.informator.config.ModSettings;

public class ViewDetails
{
    private Minecraft mc = Minecraft.getInstance();

    class BlockDetails
    {
        public boolean valid;
        // следующие переменные валидны только при valid==true
        public BlockRayTraceResult target; // бывший targetBlock
        public BlockPos pos; // бывший tBlockPosition
        public boolean isAir;
        // следующие переменные не null только при isAir==false
        public BlockState state; // бывший tBlockState
        public Block block; // бывший tBlock
        // следующие переменные не null только при block!=null
        public ItemStack stack;
        // следующие переменные не null только при stack!=null
        public Item item;
    }
    public BlockDetails block = new BlockDetails();

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

    public void refresh()
    {
        try
        {
            // определяем сущность, на которую смотрим
            block.valid = false;

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
Informator.R4.clear();
                // на всякий случай (повторно) проверяем, что блок действительно является блоком
                // (ещё он может быть "внутри головы", см. isInside(), но пока это здесь не контролируем... это связано со строительными лесами)
                block.target = ((BlockRayTraceResult)rtr);
                block.valid = block.target.getType() != RayTraceResult.Type.MISS;
                if (!block.valid) return;

                final ClientWorld world = mc.world;

                block.pos = block.target.getPos();
Informator.R4.add(block.target.getFace().getName() + " | " + block.pos.getX()+"x"+block.pos.getY()+"x"+block.pos.getZ() + " | " + ((block.target.getType()==RayTraceResult.Type.BLOCK)?"block":"miss") + " | " + (block.target.isInside()?"inside":""));
                block.isAir = world.isAirBlock(block.pos);
                block.state = null;
                block.block = null;
                block.stack = null; // ItemStack.EMPTY;
                block.item = null;
                if (!block.isAir)
                {
                    block.state = world.getBlockState(block.pos);
                    if (block.state != null)
                    {
                        block.block = block.state.getBlock();
                        if (block.block != null)
                        {
                            block.stack = block.state.getBlock().getPickBlock(block.state, block.target, world, block.pos, mc.player);
                            if ((block.stack != null) && !block.stack.isEmpty())
                            {
                                block.item = block.stack.getItem();
                            }
                        }
                    }
                }
            }
            else
            {
                Informator.R4.clear();
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
        catch(Exception e)
        {
//            Informator.lastmob = null;
//            ISee = false;
            ModSettings.GENERAL.TargetMobBar_Show.set(false);
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
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
