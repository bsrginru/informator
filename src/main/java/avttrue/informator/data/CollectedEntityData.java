package avttrue.informator.data;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import avttrue.informator.Informator;

public class CollectedEntityData
{
    public Data data = new Data();

    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid;
        // следующие переменные валидны только при valid==true
        public Entity entity;
        public String name;
        // следующие переменные валидны только при entity!=null
    }

    private Minecraft mc = Minecraft.getInstance();

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
        // нет никакой необходимости пользоваться RayTraceResult
        // т.к. весь функционал перенесён в GameRenderer.getMouseOver
        //RayTraceResult rtr = mc.objectMouseOver;
        //if (rtr == null) return;
        //if (rtr.getType() != RayTraceResult.Type.ENTITY) return;

        final ClientWorld world = mc.world;
        final ClientPlayerEntity player = mc.player;

Informator.R4.clear();
getTarget(8, null); // this.mc.playerController.getBlockReachDistance()

        // определяем сущность, на которую смотрим
        data.entity = mc.pointedEntity;
        data.valid = data.entity != null;
        if (!data.valid) return;

Informator.R4.add(data.entity.getDisplayName().getFormattedText());
//Informator.R4.add(String.valueOf((Object)Registry.ENTITY_TYPE.getKey(data.entity.getType())));
data.entity.getType().getTags().forEach(t -> Informator.R4.add("#" + t));

/*

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
    // см. http://gamedev.stackexchange.com/questions/59858/how-to-find-the-entity-im-looking-at
    // см. также getMouseOver в коде MC
    // см. пример в PointOfInterestDebugRenderer.func_217710_d()
    // см. также пример в ProjectileHelper.func_221273_a
    private LivingEntity getTarget(double distance, RayTraceResult CheckBlock)
    {
        Entity pointedEntity = null;
        double pointedDistance = distance;
        Vec3d pointedPosVec3d = null;

        Vec3d vec3d_EyePos =  mc.player.getEyePosition(1.0F);
Informator.R4.add(String.format("(getTarget) eye = %5.2f %5.2f %5.2f", vec3d_EyePos.x, vec3d_EyePos.y, vec3d_EyePos.z));
        Vec3d vec31 = mc.player.getLook(1.0F);
Informator.R4.add(String.format("(getTarget) look = %5.2f %5.2f %5.2f", vec31.x, vec31.y, vec31.z));
        Vec3d vec32 = vec3d_EyePos.add(vec31.scale(pointedDistance));
Informator.R4.add(String.format("(getTarget) sum = %5.2f %5.2f %5.2f", vec32.x, vec32.y, vec32.z));

        // следующий метод найдёт все сущность по направлению взгляда персонажа (за исключением spectator-а)
        AxisAlignedBB axisalignedbb0 = mc.player.getBoundingBox().expand(vec31.scale(pointedDistance))/*.grow(1.0D)*/;
        for(Entity entity1 : mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, axisalignedbb0))
        {
            float f2 = entity1.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().expand(f2, f2, f2);
            Informator.R4.add(String.format(
                    "(getTarget) %s at %5.2f %5.2f %5.2f distance %5.2f : %s",
                    entity1.getDisplayName().getFormattedText(),
                    entity1.posX, entity1.posY, entity1.posZ,
                    entity1.getDistance(mc.player),
                    axisalignedbb.toString()
                    ));

            // вот так можно отпределить, что box персонажа пересекается с box-ом найденной entity:
            //final boolean intersects = axisalignedbb.intersects(mc.player.getBoundingBox());

            Optional<Vec3d> optional = axisalignedbb.rayTrace(vec3d_EyePos, vec32);
            if (axisalignedbb.contains(vec3d_EyePos)) {
               if (pointedDistance >= 0.0D) {
                   pointedEntity = entity1;
                   pointedPosVec3d = optional.orElse(vec3d_EyePos);
                  pointedDistance = 0.0D;
Informator.R4.add(String.format("(getTarget) contains pointedEntity %s", pointedEntity.getDisplayName().getFormattedText()));
               }
            } else if (optional.isPresent()) {
               Vec3d newPosVec3d = optional.get();
               final double newDistance = vec3d_EyePos.distanceTo(newPosVec3d);
Informator.R4.add(String.format("(getTarget) distance %5.2f", newDistance));
               if (newDistance < pointedDistance || pointedDistance == 0.0D) {
                  if (entity1.getLowestRidingEntity() == mc.player.getLowestRidingEntity()) {
                     if (pointedDistance == 0.0D) {
                         pointedEntity = entity1;
                        pointedPosVec3d = newPosVec3d;
Informator.R4.add(String.format("(getTarget) lowest pointedEntity %s", pointedEntity.getDisplayName().getFormattedText()));
                     }
                  } else {
                      pointedEntity = entity1;
                     pointedPosVec3d = newPosVec3d;
                     pointedDistance = newDistance;
Informator.R4.add(String.format("(getTarget) else pointedEntity %s", pointedEntity.getDisplayName().getFormattedText()));
                  }
               }
            }
        }

        // теперь отпределяем куда направлен взгляд
        if (pointedEntity != null)
        {
Informator.R4.add(String.format("(getTarget) vec33 = %5.2f %5.2f %5.2f", pointedPosVec3d.x, pointedPosVec3d.y, pointedPosVec3d.z));
            float f = mc.player.rotationPitch;
            float __f1 = mc.player.rotationYaw;
Informator.R4.add(String.format("pitch = %5.2f, yaw = %5.2f, eye = %5.2f %5.2f %5.2f", f, __f1, vec3d_EyePos.x, vec3d_EyePos.y, vec3d_EyePos.z));
//            float __f2 = MathHelper.cos(-__f1 * ((float)Math.PI / 180F) - (float)Math.PI);
//            float f3 = MathHelper.sin(-__f1 * ((float)Math.PI / 180F) - (float)Math.PI);
//            float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
//            float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
//            float f6 = f3 * f4;
//            float f7 = __f2 * f4;
//Informator.R4.add(String.format("f2 = %5.2f, f3 = %5.2f, f4 = %5.2f, f5 = %5.2f, f6 = %5.2f, f7 = %5.2f", __f2,f3,f4,f5,f6,f7));
//            Vec3d vec3d1 = vec3d_EyePos.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
//Informator.R4.add(String.format("vec3d1 = %5.2f %5.2f %5.2f", vec33.x, vec33.y, vec33.z));
            RayTraceResult rtr = mc.world.rayTraceBlocks(new RayTraceContext(vec3d_EyePos, pointedPosVec3d, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, mc.player));
            if (rtr.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult brtr = ((BlockRayTraceResult)rtr);
Informator.R4.add(String.format("!!! block at %d %d %d", brtr.getPos().getX(), brtr.getPos().getY(), brtr.getPos().getZ()));
            }
        }
        return null;
    }
    
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
