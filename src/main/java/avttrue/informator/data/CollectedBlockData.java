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

    public void refresh(boolean with_electricity)
    {
        // определяем сущность, на которую смотрим
        data.valid = false;

        //было:vEntity = mc.getRenderViewEntity();
        //было:block.rayTrace = vEntity.rayTrace(Informator.Global_DistanceView, 1);
        RayTraceResult rtr = mc.objectMouseOver;
        if (rtr == null) return;
        // определяем блок на который смотрим
        if (rtr.getType() != RayTraceResult.Type.BLOCK) return;

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
}
