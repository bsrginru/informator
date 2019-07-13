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
        if (Math.abs(prev.x-curr.x)<=SAME_LOCATION_INACCURACY &&
            Math.abs(prev.y-curr.y)<=SAME_LOCATION_INACCURACY &&
            Math.abs(prev.z-curr.z)<=SAME_LOCATION_INACCURACY)
        {
            // если подпрыгнули и вернулись в ту же точку, то скорость = 0, а кол-во накопленных локаций = 1
            data.velocity = 0;
            data.locNum = 1;
        }
        else
        {
            data.velocity = CalcVelocity(oldest.x, curr.x, oldest.y, curr.y, oldest.z, curr.z, oldest.tick, curr.tick);
        }
        formatVelocityDesc();
        data.valid = true;
    }

    private void formatVelocityDesc()
    {
        data.sVelocity = String.format(
                "%1$s: %2$5.2f %3$s",
                Informator.TRANSLATOR.field_velocity.getFormattedText(),
                data.velocity,
                Informator.TRANSLATOR.field_blocks_per_sec.getFormattedText()); 
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
