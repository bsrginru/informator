package avttrue.informator.data;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;

import avttrue.informator.config.ModSettings;
import avttrue.informator.tools.Functions;

public class CollectedEntityData
{
    public Data data = new Data();

    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid;
        // следующие переменные валидны только при valid==true
        public Entity entity;
        public double distance;
        public Vec3d pointer; // точка, куда направлен взгляд персонажа, с учётом дистанции до entity
        // следующие переменные валидны только при entity!=null
        public String name;
        public ITextComponent nameCustom; // имя, данное с помощью бирки
        public String nameClass; // идентификатор класса сущности
        public boolean tamed; // признак того, что животное приручено
        public String nameOwner; // валидно при tamed=true, но м.б. null, если не удалось загрузить данные об игроке
        public boolean movementPresent;
        public double movementSpeed; // валидно для лошадей (при movementPresent=true)
        public double jumpHeight; // валидно для лошадей (при movementPresent=true)
        public boolean isSkeletonTrap; // скелето-лошадь является ловушкой
        public boolean isCreeperPowered; // крипер заряжен
        public boolean isLiving; // признак того, что это сущность типа LivingEntity
        public boolean isChild; // признак того, что это ребёнок (валидно при isLiving=true)
        public boolean isUndead; // признак того, что это нежить (валидно при isLiving=true)
        public float health; // жизнь entity (валидно при isLiving=true)
        public float healthMax; // максимальная жизнь entity (валидно при isLiving=true)
        public int armor; // броня entity (валидно при isLiving=true)
        // можно также получить у LivingEntity.getHeldItem (что в руках)
        public boolean isVillager; // признак того, что entity является крестьянином
        public VillagerProfession villagerProfession; // профессия крестьянина (валидно при isVillager = true), но м.б. неизвестна
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
/**Informator.R4.clear();/**/

        //final ClientWorld world = mc.world;
        final ClientPlayerEntity player = mc.player;

        // определяем сущность, на которую смотрим (сначала идём по простому пути, учитывая REACH_DISTANCE контроллера
        // используемого в игровом процессе, потом пытаемся использовать настройки пользователя
        data.entity = mc.pointedEntity;
        data.valid = data.entity != null;
        if (data.valid)
        {
            data.distance = data.entity.getDistance(player);
            data.pointer = player.getEyePosition(1.0F).add(player.getLook(1.0F).scale(data.distance));
        }
        else
        {
            // если метод searchTarget выдал true, то сущность на которую смотрим найдена и сохранена
            // в структуре data, кроме того, сохранена точка, куда направлен взгляд персонажа, а также
            // сохранена дистанция до цели
            if (!searchTarget(ModSettings.TARGET.TargetMobBar_DistanceView.get())) return; // mc.playerController.getBlockReachDistance()
        }
        if (!data.valid) return;

        final LivingEntity lentity = (data.entity instanceof LivingEntity) ? ((LivingEntity)data.entity) : null;

        data.name = data.entity.getDisplayName().getFormattedText();
        data.nameCustom =  data.entity.hasCustomName() ? data.entity.getCustomName() : null;
        data.nameClass = data.entity.getEntityString();
        data.tamed = false;
        data.movementPresent = false;
        data.isSkeletonTrap = false;
        data.isLiving = lentity != null;
        data.isChild = data.isLiving ? lentity.isChild() : false;
        data.isUndead = data.isLiving ? lentity.isEntityUndead() : false;
        data.health = data.isLiving ? lentity.getHealth() : -1.0F;
        data.healthMax = data.isLiving ? lentity.getMaxHealth() : -1.0F;
        data.armor = data.isLiving ? lentity.getTotalArmorValue() : -1;
        data.isVillager = false;
 
/**Informator.R4.add(String.format(
    "%s at %5.2f %5.2f %5.2f distance %5.2f",
    data.name,
    data.pointer.x,
    data.pointer.y,
    data.pointer.z,
    data.distance));
data.entity.getType().getTags().forEach(t -> Informator.R4.add("#" + t));/**/

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
*/

        // конь
        if (data.entity instanceof AbstractHorseEntity)
        {
            final AbstractHorseEntity horse = (AbstractHorseEntity)data.entity;
            double jstrength = horse.getHorseJumpStrength();

            // метод вычисления скорости из изысканий Румикона
            // (43.0D - волшебная константа из изысканий Румикона)
            // также совпало с исследованиями bsrgin и SATALIN по исходникам Spigot-а в феврале 2019
            double jheight= 0;
            while (jstrength > 0)
            {
                 jheight += jstrength;
                 jstrength = (jstrength - 0.08D) * 0.9800000190734863D;
            }
            final double speed = 43.0D * horse.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
            final UUID owner = horse.getOwnerUniqueId();
            data.tamed = owner != null;
            if (data.tamed)
            {
                data.nameOwner = Functions.getUsernameByUUID(owner);
            }
            data.movementPresent = true;
            data.movementSpeed = speed; //new BigDecimal(speed).setScale(2, RoundingMode.UP).doubleValue();
            data.jumpHeight = jheight; //new BigDecimal(jheight).setScale(3, RoundingMode.UP).doubleValue();
            data.isSkeletonTrap = horse instanceof SkeletonHorseEntity;
        }
        // приручаемые животные : волк, котик
        else if (data.entity instanceof TameableEntity)
        {
            final UUID owner = ((TameableEntity)data.entity).getOwnerId();
            data.tamed = owner != null;
            if (data.tamed)
            {
                data.nameOwner = Functions.getUsernameByUUID(owner);
            }
        }
        // криппер
        else if (data.entity instanceof CreeperEntity)
        {
            data.isCreeperPowered = ((CreeperEntity)data.entity).getPowered();
        }
        // крестьянин
        else if(data.entity instanceof VillagerEntity)
        {
            final VillagerEntity villager = (VillagerEntity)data.entity;
            data.isVillager = true;
            data.villagerProfession = villager.getVillagerData().getProfession(); // profession.getPointOfInterest().toString()); // "unemployed", "cleric", "farmer",...
        }

/*
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
    private boolean searchTarget(final double distanceMax)
    {
        final ClientWorld world = mc.world;
        final ClientPlayerEntity player = mc.player;

        data.valid = false;
        data.entity = null;
        data.distance = 0;
        data.pointer = null;

        final Vec3d vec3d_EyePos =  player.getEyePosition(1.0F);
        final Vec3d vec3d_Look = player.getLook(1.0F);
        final Vec3d vec3d_MaxViewPoint = vec3d_EyePos.add(vec3d_Look.scale(distanceMax));

/**Informator.R4.add(String.format("eye = %5.2f %5.2f %5.2f", vec3d_EyePos.x, vec3d_EyePos.y, vec3d_EyePos.z));
Informator.R4.add(String.format("look = %5.2f %5.2f %5.2f", vec3d_Look.x, vec3d_Look.y, vec3d_Look.z));
Informator.R4.add(String.format("max = %5.2f %5.2f %5.2f", vec3d_MaxViewPoint.x, vec3d_MaxViewPoint.y, vec3d_MaxViewPoint.z));/**/

        // следующий метод найдёт все сущность по направлению взгляда персонажа (за исключением spectator-а)
        AxisAlignedBB axisalignedbb0 = player.getBoundingBox().expand(vec3d_Look.scale(distanceMax))/*.grow(1.0D)*/;
        for (final Entity entityFound : world.getEntitiesWithinAABBExcludingEntity(player, axisalignedbb0))
        {
            // получение границ найденной entity
            final float entityBorder = entityFound.getCollisionBorderSize();
            // получение box-а координат найденной entity
            final AxisAlignedBB axisalignedbb = entityFound.getBoundingBox().expand(entityBorder, entityBorder, entityBorder);
            // вот так можно определить, что box персонажа пересекается с box-ом найденной entity:
            //final boolean intersects = axisalignedbb.intersects(player.getBoundingBox());

/**Informator.R4.add(String.format(
    "%s at [%5.2f %5.2f %5.2f -> %5.2f %5.2f %5.2f] distance %5.2f",
    entityFound.getDisplayName().getFormattedText(),
    // раньне было так, позиция entity, а теперь смотрим её box: entityFound.posX, entityFound.posY, entityFound.posZ,
    axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
    axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ,
    entityFound.getDistance(player)
    ));/**/

            Optional<Vec3d> optional = axisalignedbb.rayTrace(vec3d_EyePos, vec3d_MaxViewPoint);
            if (axisalignedbb.contains(vec3d_EyePos))
            {
                if (data.distance >= 0.0D)
                {
                    data.entity = entityFound;
                    data.pointer = optional.orElse(vec3d_EyePos);
                    data.distance = 0.0D;
                }
            }
            else if (optional.isPresent())
            {
                final Vec3d newPosVec3d = optional.get();
                final double newDistance = vec3d_EyePos.distanceTo(newPosVec3d);
                if (newDistance < data.distance || data.distance == 0.0D)
                {
                    if (entityFound.getLowestRidingEntity() == player.getLowestRidingEntity())
                    {
                        if (data.distance == 0.0D)
                        {
                            data.entity = entityFound;
                            data.pointer = newPosVec3d;
                        }
                    }
                    else
                    {
                        data.entity = entityFound;
                        data.pointer = newPosVec3d;
                        data.distance = newDistance;
                    }
                }
            }
        }

        data.valid = data.entity != null;

        // теперь отпределяем куда направлен взгляд, если между взглядом на entity есть block, то приравниваем
        // это обстоятельство к препятствию, а смотреть "через стены нельзя", см. пункт 1.4 http://minecrafting.ru/page/rules
        // "...На сервере запрещено использование любых программ, плагинов, модификаций и ресурс-паков (далее - программного
        // обеспечения, ПО), дающих преимущество в игре, а также использование багов игры в личных целях. Все вместе это
        // называется "читерство" и ведет к моментальному бану без права на амнистию и реабилитацию..."
        if (data.valid)
        {
            // (на пямять) Есть ещё один способ определения направления взгляда персонажа, и вычисления векторов:
            // (например с целью поиска пересечений в направлении взгляда)
            //float f = player.rotationPitch;
            //float f1 = player.rotationYaw;
            //float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            //float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            //float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
            //float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
            //float f6 = f3 * f4;
            //float f7 = f2 * f4;
            //Vec3d vec3d1 = vec3d_EyePos.add((double)f6 * data.distance, (double)f5 * data.distance, (double)f7 * data.distance);

            RayTraceResult rtr = world.rayTraceBlocks(new RayTraceContext(
                            vec3d_EyePos,
                            data.pointer,
                            RayTraceContext.BlockMode.COLLIDER,
                            RayTraceContext.FluidMode.ANY,
                            player));

            if (rtr.getType() == RayTraceResult.Type.BLOCK)
            {
                data.valid = false;
/**BlockRayTraceResult brtr = ((BlockRayTraceResult)rtr);
Informator.R4.add(String.format("!!! block at %d %d %d", brtr.getPos().getX(), brtr.getPos().getY(), brtr.getPos().getZ()));/**/
            }
        }

        return data.valid;
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
