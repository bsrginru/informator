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

    public static EntityRayTraceResult func_221273_a(Entity p_221273_0_, Vec3d p_221273_1_, Vec3d p_221273_2_, AxisAlignedBB p_221273_3_, Predicate<Entity> p_221273_4_, double p_221273_5_) {
        World world = p_221273_0_.world;
        double d0 = p_221273_5_;
        Entity entity = null;
        Vec3d vec3d = null;

        for(Entity entity1 : world.getEntitiesInAABBexcluding(p_221273_0_, p_221273_3_, p_221273_4_)) {
Informator.R4.add(String.format(
                "(221273) %s at %5.2f %5.2f %5.2f distance %5.2f",
                entity1.getDisplayName().getFormattedText(),
                entity1.posX, entity1.posY, entity1.posZ,
                entity1.getDistance(p_221273_0_)
                ));
           AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
           Optional<Vec3d> optional = axisalignedbb.rayTrace(p_221273_1_, p_221273_2_);
           if (axisalignedbb.contains(p_221273_1_)) {
              if (d0 >= 0.0D) {
                 entity = entity1;
                 vec3d = optional.orElse(p_221273_1_);
                 d0 = 0.0D;
              }
           } else if (optional.isPresent()) {
              Vec3d vec3d1 = optional.get();
              double d1 = p_221273_1_.squareDistanceTo(vec3d1);
              if (d1 < d0 || d0 == 0.0D) {
                 if (entity1.getLowestRidingEntity() == p_221273_0_.getLowestRidingEntity()) {
                    if (d0 == 0.0D) {
                       entity = entity1;
                       vec3d = vec3d1;
                    }
                 } else {
                    entity = entity1;
                    vec3d = vec3d1;
                    d0 = d1;
                 }
              }
           }
        }

        if (entity == null) {
           return null;
        } else {
           return new EntityRayTraceResult(entity, vec3d);
        }
     }

    void debug()
    {
        float f = mc.player.rotationPitch;
        float f1 = mc.player.rotationYaw;
        Vec3d vec3d = mc.player.getEyePosition(1.0F);
Informator.R4.add(String.format("pitch = %5.2f, yaw = %5.2f, eye = %5.2f %5.2f %5.2f", f, f1, vec3d.x, vec3d.y, vec3d.z));
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
Informator.R4.add(String.format("f2 = %5.2f, f3 = %5.2f, f4 = %5.2f, f5 = %5.2f, f6 = %5.2f, f7 = %5.2f", f2,f3,f4,f5,f6,f7));
        double d0 = 36;//mc.player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
        Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
Informator.R4.add(String.format("vec3d1 = %5.2f %5.2f %5.2f", vec3d1.x, vec3d1.y, vec3d1.z));
        RayTraceResult rtr = mc.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, mc.player));
if (rtr.getType() == RayTraceResult.Type.ENTITY) Informator.R4.add("entity");
if (rtr.getType() == RayTraceResult.Type.BLOCK) Informator.R4.add("block");

        AxisAlignedBB axisalignedbb = mc.player.getBoundingBox().expand(vec3d1.scale(d0)).grow(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult etr = func_221273_a(mc.player, vec3d, vec3d1, axisalignedbb, (p_215312_0_) -> { return true; }, d0*d0);
if (etr != null)
{
    if (etr.getType() == RayTraceResult.Type.ENTITY) Informator.R4.add("entity");
    if (etr.getType() == RayTraceResult.Type.BLOCK) Informator.R4.add("block");
}
    }

    // см. пример в PointOfInterestDebugRenderer.func_217710_d()
    public static Optional<Entity> func_217728_a(@Nullable Entity player, int distance) {
        if (player == null) {
           return Optional.empty();
        } else {
           Vec3d vec3d = player.getEyePosition(1.0F);
Informator.R4.add(String.format("(217728) eye = %5.2f %5.2f %5.2f", vec3d.x, vec3d.y, vec3d.z));
           Vec3d vec3d1 = player.getLook(1.0F).scale((double)distance);
Informator.R4.add(String.format("(217728) look = %5.2f %5.2f %5.2f", vec3d1.x, vec3d1.y, vec3d1.z));
           Vec3d vec3d2 = vec3d.add(vec3d1);
Informator.R4.add(String.format("(217728) sum = %5.2f %5.2f %5.2f", vec3d2.x, vec3d2.y, vec3d2.z));
           AxisAlignedBB axisalignedbb = player.getBoundingBox().expand(vec3d1).grow(1.0D);
           int square_distance = distance * distance;
           Predicate<Entity> predicate = (found_entity) -> {
              return !found_entity.isSpectator() && found_entity.canBeCollidedWith();
           };
           EntityRayTraceResult entityraytraceresult = func_221273_a(player, vec3d, vec3d2, axisalignedbb, predicate, (double)square_distance);
           if (entityraytraceresult == null) {
              return Optional.empty();
           } else {
              return vec3d.squareDistanceTo(entityraytraceresult.getHitVec()) > (double)square_distance ? Optional.empty() : Optional.of(entityraytraceresult.getEntity());
           }
        }
    }

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
//debug
//func_217728_a(player, 8);
getTarget(8, null);

        // определяем сущность, на которую смотрим
        data.entity = mc.pointedEntity;
        data.valid = data.entity != null;
        if (!data.valid) return;

Informator.R4.add(data.entity.getDisplayName().getFormattedText());
//Informator.R4.add(String.valueOf((Object)Registry.ENTITY_TYPE.getKey(data.entity.getType())));
data.entity.getType().getTags().forEach(t -> Informator.R4.add("#" + t));

        float partialTicks = 1.0F;
        Vec3d vec3d_player_eye_pos = player.getEyePosition(1.0F);
        double reach_distance = (double)this.mc.playerController.getBlockReachDistance();
//Informator.R4.add(String.format("distance = %5.2f, eye = %5.2f %5.2f %5.2f", reach_distance, vec3d_player_eye_pos.x, vec3d_player_eye_pos.y, vec3d_player_eye_pos.z));
        double square_distance = reach_distance;
        if (this.mc.playerController.extendedReach()) {
           square_distance = 6.0D;
           reach_distance = square_distance;
        } /*else {
           if (d0 > 3.0D) {
              //flag = true;
           }
           //d0 = d0;
        }*/
//Informator.R4.add(String.format("d0 = %5.2f, d1 = %5.2f", reach_distance, square_distance));

        square_distance = square_distance * square_distance;
        //if (this.mc.objectMouseOver != null)
        //{
        //    d1 = this.mc.objectMouseOver.getHitVec().squareDistanceTo(vec3d_player_eye_pos);
        //}
//Informator.R4.add(String.format("d0 = %5.2f, d1 = %5.2f", reach_distance, square_distance));

        Vec3d vec3d1 = data.entity.getLook(1.0F);
        Vec3d vec3d2 = vec3d_player_eye_pos.add(vec3d1.x * reach_distance, vec3d1.y * reach_distance, vec3d1.z * reach_distance);
//Informator.R4.add(String.format("vec3d1 = %5.2f %5.2f %5.2f", vec3d1.x, vec3d1.y, vec3d1.z));
//Informator.R4.add(String.format("vec3d2 = %5.2f %5.2f %5.2f", vec3d2.x, vec3d2.y, vec3d2.z));

//        float f = 1.0F;
//        AxisAlignedBB axisalignedbb = data.entity.getBoundingBox().expand(vec3d1.scale(reach_distance)).grow(1.0D, 1.0D, 1.0D);
//EntityRayTraceResult entityraytraceresult = func_221273_a(data.entity, vec3d_player_eye_pos, vec3d2, axisalignedbb, (p_215312_0_) -> { return true; }, square_distance);
        /*
        if (entityraytraceresult != null)
        {
           Entity entity1 = entityraytraceresult.getEntity();
Informator.R4.add("true: " + entity1.getDisplayName().getFormattedText());
        }
        entityraytraceresult = ProjectileHelper.func_221273_a(data.entity, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> {
            return false;
         }, d1);
         if (entityraytraceresult != null)
         {
            Entity entity1 = entityraytraceresult.getEntity();
 Informator.R4.add("false: " + entity1.getDisplayName().getFormattedText());
            Vec3d vec3d3 = entityraytraceresult.getHitVec();
            double d2 = vec3d.squareDistanceTo(vec3d3);
            if (flag && d2 > 9.0D) {
               this.mc.objectMouseOver = BlockRayTraceResult.createMiss(vec3d3, Direction.getFacingFromVector(vec3d1.x, vec3d1.y, vec3d1.z), new BlockPos(vec3d3));
            } else if (d2 < d1 || this.mc.objectMouseOver == null) {
               this.mc.objectMouseOver = entityraytraceresult;
               if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity) {
                  this.mc.pointedEntity = entity1;
               }
            }
         }
*/


        // на кого смотрим
        //LivingEntity elb = getTarget(ModSettings.GENERAL.TargetMobBar_DistanceView.get(), 1, block.rayTrace);

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
*/

    // см. пример в PointOfInterestDebugRenderer.func_217710_d()
    // см. также пример в ProjectileHelper.func_221273_a
    private LivingEntity getTarget(double distance, RayTraceResult CheckBlock)
    {
/*
        Vec3d vec3d = player.getEyePosition(1.0F);
Informator.R4.add(String.format("(217728) eye = %5.2f %5.2f %5.2f", vec3d.x, vec3d.y, vec3d.z));
        Vec3d vec3d1 = player.getLook(1.0F).scale((double)distance);
Informator.R4.add(String.format("(217728) look = %5.2f %5.2f %5.2f", vec3d1.x, vec3d1.y, vec3d1.z));
        Vec3d vec3d2 = vec3d.add(vec3d1);
Informator.R4.add(String.format("(217728) sum = %5.2f %5.2f %5.2f", vec3d2.x, vec3d2.y, vec3d2.z));
        AxisAlignedBB axisalignedbb = player.getBoundingBox().expand(vec3d1).grow(1.0D);
        int square_distance = distance * distance;
        Predicate<Entity> predicate = (found_entity) -> {
           return !found_entity.isSpectator() && found_entity.canBeCollidedWith();
        };
        EntityRayTraceResult entityraytraceresult = func_221273_a(player, vec3d, vec3d2, axisalignedbb, predicate, (double)square_distance);
        if (entityraytraceresult == null) {
           return Optional.empty();
        } else {
           return vec3d.squareDistanceTo(entityraytraceresult.getHitVec()) > (double)square_distance ? Optional.empty() : Optional.of(entityraytraceresult.getEntity());
        }
*/
        Entity vEntity = mc.player;
        Entity pointedEntity;
        double d0 = distance;
        double d1 = d0;
        double d2 = d1;
        float f1 = 1.0F;
        
//        RayTraceResult rtr = vEntity.rayTrace(d0, tick);
        
        Vec3d vec3 =  mc.player.getEyePosition(1.0F);
Informator.R4.add(String.format("(getTarget) eye = %5.2f %5.2f %5.2f", vec3.x, vec3.y, vec3.z));
        Vec3d vec31 = mc.player.getLook(1.0F);
Informator.R4.add(String.format("(getTarget) look = %5.2f %5.2f %5.2f", vec31.x, vec31.y, vec31.z));
        Vec3d vec32 = vec3.add(vec31.scale(d0));
Informator.R4.add(String.format("(getTarget) sum = %5.2f %5.2f %5.2f", vec32.x, vec32.y, vec32.z));
        Vec3d vec33 = null;
        pointedEntity = null;

        // следующий метод найдёт все сущность по направлению взгляда персонажа (за исключением spectator-а)
        AxisAlignedBB axisalignedbb0 = mc.player.getBoundingBox().expand(vec31.scale(d0))/*.grow(1.0D)*/;
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

            Optional<Vec3d> optional = axisalignedbb.rayTrace(vec3, vec32);
            if (axisalignedbb.contains(vec3)) {
               if (d0 >= 0.0D) {
                   pointedEntity = entity1;
                   vec33 = optional.orElse(vec3);
                  d0 = 0.0D;
Informator.R4.add(String.format("(getTarget) contains pointedEntity %s", pointedEntity.getDisplayName().getFormattedText()));
               }
            } else if (optional.isPresent()) {
               Vec3d vec3d1 = optional.get();
               double d1_ = vec3.distanceTo(vec3d1);
Informator.R4.add(String.format("(getTarget) distance %5.2f", d1_));
               if (d1_ < d0 || d0 == 0.0D) {
                  if (entity1.getLowestRidingEntity() == mc.player.getLowestRidingEntity()) {
                     if (d0 == 0.0D) {
                         pointedEntity = entity1;
                        vec33 = vec3d1;
Informator.R4.add(String.format("(getTarget) lowest pointedEntity %s", pointedEntity.getDisplayName().getFormattedText()));
                     }
                  } else {
                      pointedEntity = entity1;
                     vec33 = vec3d1;
                     d0 = d1_;
Informator.R4.add(String.format("(getTarget) else pointedEntity %s", pointedEntity.getDisplayName().getFormattedText()));
                  }
               }
            }

        }
        
        
        if (pointedEntity != null)
        {
            float f = mc.player.rotationPitch;
            float __f1 = mc.player.rotationYaw;
            Vec3d vec3d = mc.player.getEyePosition(1.0F);
            Informator.R4.add(String.format("pitch = %5.2f, yaw = %5.2f, eye = %5.2f %5.2f %5.2f", f, __f1, vec3d.x, vec3d.y, vec3d.z));
            float __f2 = MathHelper.cos(-__f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            float f3 = MathHelper.sin(-__f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
            float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
            float f6 = f3 * f4;
            float f7 = __f2 * f4;
            Informator.R4.add(String.format("f2 = %5.2f, f3 = %5.2f, f4 = %5.2f, f5 = %5.2f, f6 = %5.2f, f7 = %5.2f", __f2,f3,f4,f5,f6,f7));
            Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
            Informator.R4.add(String.format("vec3d1 = %5.2f %5.2f %5.2f", vec3d1.x, vec3d1.y, vec3d1.z));
            RayTraceResult rtr = mc.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, mc.player));
            if (rtr.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult brtr = ((BlockRayTraceResult)rtr);
                Informator.R4.add(String.format("!!! block at %d %d %d", brtr.getPos().getX(), brtr.getPos().getY(), brtr.getPos().getZ()));
            }
        }


        
/*
        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = (Entity)list.get(i);
            if (entity.canBeCollidedWith())
            {
                float f2 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(f2);
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
*/
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
