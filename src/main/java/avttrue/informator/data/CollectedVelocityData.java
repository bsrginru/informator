package avttrue.informator.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

import avttrue.informator.Informator;


public class CollectedVelocityData
{
    public Data data = new Data();

    public class Location
    {
        public double x;
        public double y;
        public double z;
        public long tick = -1;
    }
    
    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid = false;
        // данные полученные из мира
        private static final int NUM_LOCATIONS = 15; // можно менять это число (для сглаживания статистики)
        private Location[] locations = {
                new Location(), new Location(), new Location(), new Location(), new Location(),
                new Location(), new Location(), new Location(), new Location(), new Location(),
                new Location(), new Location(), new Location(), new Location(), new Location() };
        private int locCursor;
        private int locNum = 0;
        // данные, вычисленные в результате анализа
        public double velocity = 0;
        public String sVelocity;
        public String sVelocityPrefix;
        public String sVelocityPostfix;
        // признак того, что в настоязий момент персонаж не двигается
        public boolean isMotionless;
        // мгновенная максимальная скорость, напопленная за время перемещений, от начала движения
        public double velocityMax;
        // максимальная скорость, накопленная за всё время движения от начала перемещений и
        // хранящаяся до тех пор, пока персонаж не двигается
        public boolean knownVelocityPrevMax;
        public String sVelocityPrevMax;
    }

    // если приращение по всем x,y,z-осям составит не больше этого значения, то им будет соответствовать
    // скорость не более 0.01 блока/секунду - константа используется для определения того, что персонаж
    // не двигается (актуально для работы с множеством накопленных локаций для усреднения скорости, и
    // их мгновенного зануления, как только обнаруживается, что персонаж оказался там, же где был, наприме
    // персонаж мог бы подпрыгнуть и вернуться в ту же точку)
    private static final double SAME_LOCATION_INACCURACY = 2.8571428571428571428571428571429e-4;

    public void collectDataDuringTick()
    {
        if (!Informator.VelocityBar_Show)
        {
            data.valid = false;
            data.locCursor = 0;
            data.locNum = 0;
            return;
        }
        final Minecraft mc = Minecraft.getInstance();
        final ClientWorld world = mc.world;
        final PlayerEntity player = mc.player;
        // если игра ещё не начата вдруг
        if (world == null || player == null)
        {
            if (data.valid)
            {
                data.valid = false;
                data.locCursor = 0;
                data.locNum = 0;
            }
            return;
        }
        // сохраняем позицию игрока
        // поскольку всякая позиция сюда приходит каждый следующий tick, то неправильно считать "скорость в течении тика", т.к.
        // каждое подпрыгивание персонажа будет резко увеличивать скорость и резко уменьшать, и потому сведения о скорости окажутся
        // слишком быстро меняющимися, так что понять что будет написано на экране будет трудоёмко
        Location loc = data.locations[data.locCursor];
        loc.x = player.posX;
        loc.y = player.posY;
        loc.z = player.posZ;
        loc.tick = world.dimension.getWorldTime();
        data.locCursor = (data.locCursor + 1) % Data.NUM_LOCATIONS;
        if (data.locNum != Data.NUM_LOCATIONS) ++data.locNum;
        // вычисляем скорость
        refreshCalculatedData();
    }

    public void refreshCalculatedData()
    {
        if (data.locNum <= 1)
        {
            data.velocity = 0;
            formatVelocityDesc();
            data.valid = true;
            return;
        }
        // берём теперь уже предыдущую запись и считаем её текущей
        final int currIndex = (data.locCursor + Data.NUM_LOCATIONS - 1) % Data.NUM_LOCATIONS;
        final Location curr = data.locations[currIndex];
        // берём предыдущее местоположение, с поможью которого будет контролировать прекращение движения для сброса статистики
        final int prevIndex = (currIndex + Data.NUM_LOCATIONS - 1) % Data.NUM_LOCATIONS;
        final Location prev = data.locations[prevIndex];
        // ищем самую старую запись
        final int oldestIndex = (currIndex + Data.NUM_LOCATIONS - data.locNum + 1) % Data.NUM_LOCATIONS;
        final Location oldest = data.locations[oldestIndex];
        // проверяем, не оказались ли мы в точке, где уже были?
        // считаем, что персонаж не двигается, если скорость меньше или равна 0.01 б/с
        final boolean samePositionFirst =
                Math.abs(prev.x-curr.x)<=SAME_LOCATION_INACCURACY &&
                Math.abs(prev.y-curr.y)<=SAME_LOCATION_INACCURACY &&
                Math.abs(prev.z-curr.z)<=SAME_LOCATION_INACCURACY;
        boolean samePosition = samePositionFirst;
        if (samePosition)
        {
            // 2 последних позиции не поменялись, проверяем ещё 4 шт (итого 40+80=120ms нахождения в одном месте)
            for (int i = 1; (i <= 4) && samePosition; ++i)
            {
                final int motionlessIndex = (prevIndex + Data.NUM_LOCATIONS - i) % Data.NUM_LOCATIONS;
                final Location motionless = data.locations[motionlessIndex];
                samePosition =
                        Math.abs(motionless.x-curr.x)<=SAME_LOCATION_INACCURACY &&
                        Math.abs(motionless.y-curr.y)<=SAME_LOCATION_INACCURACY &&
                        Math.abs(motionless.z-curr.z)<=SAME_LOCATION_INACCURACY;
            }
        }
        // если подпрыгнули и вернулись в ту же точку, то скорость = 0, а кол-во накопленных локаций = 1 (сбрасываем)
        data.isMotionless = samePosition;
        if (data.isMotionless)
        {
            // сохраняем максимальную скорость, накопленную за всё время перемещений с момента начала движения
            // при этом проверяем, что персонаж двигался непрерывно длительное время
            if (data.locNum == Data.NUM_LOCATIONS)
                if (data.velocityMax > 6)
                {
                    formatVelocityMaxDesc();
                }
            data.velocityMax = 0;
            data.velocity = 0;
            data.locNum = 1;
        }
        else
        {
            data.velocity = CalcVelocity(oldest.x, curr.x, oldest.y, curr.y, oldest.z, curr.z, oldest.tick, curr.tick);
            // если персонаж начал двигаться непрерывно длительное время, то сбрасываем сведения
            // о накопленной ранее максимальной скорости
            if (data.locNum == Data.NUM_LOCATIONS)
                data.knownVelocityPrevMax = false;
            // сохраняем мгновенную максимальную скорость
            if (data.velocity > data.velocityMax)
                data.velocityMax = data.velocity;
        }
        formatVelocityDesc();

        //отладка измерения скорости:String str = String.format("velocity %5.2f from %.3f:%.3f:%.3f at %.3f:%.3f:%.3f to %.3f:%.3f:%.3f during %dms",
        //отладка измерения скорости:        data.velocity,
        //отладка измерения скорости:        oldest.x, oldest.y, oldest.z,     prev.x, prev.y, prev.z,     curr.x, curr.y, curr.z,
        //отладка измерения скорости:        20*(int)(curr.tick - oldest.tick));
        //отладка измерения скорости:if (samePositionFirst) str += ", same";
        //отладка измерения скорости:if (samePosition) str += ", RESET";
        //отладка измерения скорости:System.out.println(str);

        //отладка максимальной скорости:String str = String.format("velocity %4.2f, max %4.2f, stored %s, num %d",
        //отладка максимальной скорости:        data.velocity, data.velocityMax, data.sVelocityPrevMax, data.locNum);
        //отладка максимальной скорости:if (data.knownVelocityPrevMax) str += ", known";
        //отладка максимальной скорости:if (samePositionFirst) str += ", same";
        //отладка максимальной скорости:if (samePosition) str += ", RESET";
        //отладка максимальной скорости:System.out.println(str);

        data.valid = true;
    }

    private void formatVelocityDesc()
    {
        data.sVelocityPrefix = Informator.TRANSLATOR.field_velocity.getFormattedText();
        data.sVelocityPostfix = " " + Informator.TRANSLATOR.field_blocks_per_sec.getFormattedText();
        data.sVelocity = data.sVelocityPrefix + String.format("%5.2f", data.velocity) + data.sVelocityPostfix;
    }

    private void formatVelocityMaxDesc()
    {
        data.sVelocityPrevMax = String.format("%5.2f", data.velocityMax);
        data.knownVelocityPrevMax = true;
    }

    private static double CalcVelocity(double x1, double x2, double y1, double y2, double z1, double z2, long tick1, long tick2)
    {
        try 
        {
            final long deltat = tick1 - tick2;
            if (deltat == 0) return 0; // если на паузе

            final double deltax = x1 - x2;
            final double deltay = y1 - y2;
            final double deltaz = z1 - z2;

            if (deltax > 999 || deltay > 999 || deltaz > 999) // если случались телепорты
            {
                //debug:System.out.println("Teleport?");
                return 0;
            }
            return new BigDecimal(
                    20 * Math.sqrt(
                            Math.pow(deltax, 2) + // бо 1 сек = 20 тиков
                            Math.pow(deltay, 2) + 
                            Math.pow(deltaz, 2)) / (Math.abs(deltat))
                    ).setScale(2, RoundingMode.UP).doubleValue();
        }
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
