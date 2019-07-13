package avttrue.informator.data;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;


public class CollectedHeldItemsData
{
    public Data data = new Data();

    public class HeldItem
    {
        public boolean known;
        public boolean isDamageable;
        public int id;
        public int damageCurr;
        public int damageMax;
        public float damageFactor;
        public String damageDesc;
        public Rarity rarity;
        public int arrows;
        HeldItem()
        {
            storeChanged(null, -1);
        }
        HeldItem(ItemStack stack, int arrows)
        {
            storeChanged(stack, arrows);
        }
        public boolean isChanged(ItemStack stack, int arrows)
        {
            if (stack == null)
            {
                if (!known) return false;
                return true;
            }
            else
            {
                if (!known) return true;
                // далее требуется проверка содержимого стека
                final Item item = stack.getItem();
                if (item.isDamageable() ^ isDamageable) return true;
                if (Item.getIdFromItem(item) != id) return true;
                if (item.getDamage(stack) != damageCurr) return true;
                if (item.getMaxDamage(stack) != damageMax) return true;
                if (item == Items.BOW)
                    if (arrows != this.arrows) return true;
                if (item.getRarity(stack) != rarity) return true;
                return true;
            }
        }
        public void storeChanged(ItemStack stack, int arrows)
        {
            known = stack != null;
            if (!known) return;
            final Item item = stack.getItem();
            isDamageable = item.isDamageable();
            if (!isDamageable) return;
            id = Item.getIdFromItem(item);
            damageCurr = item.getDamage(stack);
            damageMax = item.getMaxDamage(stack);
            damageFactor = (float)(damageMax-damageCurr) / (float)damageMax; 
            if (item != Items.BOW)
                damageDesc = String.format("%d/%d", damageMax - damageCurr + 1, damageMax + 1);
            else // ищем стрелы
                damageDesc = String.format("%d/%d (%d)", damageMax - damageCurr + 1, damageMax + 1, arrows);
            rarity = item.getRarity(stack);
        }
    }

    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid = false;
        // данные полученные из мира
        public static final int MAINHAND = 0; // рука основная
        public static final int OFFHAND = 1; // рука вторая
        public static final int HEAD = 2; // голова
        public static final int BODY = 3; // тело
        public static final int LEGS = 4; // штаны
        public static final int FOOTS = 5; // ботинки
        public static final int MAX_HELD_ITEMS = 6;
        public HeldItem [] held = {
                new HeldItem(),
                new HeldItem(),
                new HeldItem(),
                new HeldItem(),
                new HeldItem(),
                new HeldItem()
        };
        public int heldNum;
        // данные, вычисленные в результате анализа
        private long lastUpdateRlTime;
        public ArrayList<HeldItem> held_damageable = new ArrayList<HeldItem>();
    }

    public void collectDataDuringTick(long realTimeTick)
    {
        final Minecraft mc = Minecraft.getInstance();
        final ClientWorld world = mc.world;
        final PlayerEntity player = mc.player;
        // если игра ещё не начата вдруг
        if (world == null || player == null)
        {
            if (data.valid)
            {
                data.valid = false;
                for (int i = 0; i < Data.MAX_HELD_ITEMS; ++i) data.held[i].known = false;
                data.held_damageable.clear();
                data.heldNum = 0;
            }
            return;
        }
        // прореживаем обновления данных (раз в где-то 4*50=200ms), чаще не надо;
        // простецким образом проверяем переход счётчика через 0
        if (Math.abs(realTimeTick - data.lastUpdateRlTime) < 3) return;
        data.lastUpdateRlTime = realTimeTick;

        ItemStack [] stacks = {
                player.getHeldItemMainhand(), // mainhand
                player.getHeldItemOffhand(), // offhand
                player.inventory.armorItemInSlot(3), // head
                player.inventory.armorItemInSlot(2), // body
                player.inventory.armorItemInSlot(1), // legs
                player.inventory.armorItemInSlot(0) // foots
        };

        final int arrows = getArrowsCount(Minecraft.getInstance().player.inventory);

        boolean anyDamageableChanged = false;
        data.heldNum = 0;
        for (int i = 0; i < Data.MAX_HELD_ITEMS; ++i)
        {
            HeldItem held = data.held[i];
            if (!held.isChanged(stacks[i], arrows)) continue;
            held.storeChanged(stacks[i], arrows);
            if (held.known && held.isDamageable)
                data.heldNum++;
            anyDamageableChanged = true;
        }
        
        // раз изменился какой-либо элемент в списке, то надо пересобрать весь список (важен порядок)
        if (anyDamageableChanged)
        {
            data.held_damageable.clear();
            for (int i = 0; i < CollectedHeldItemsData.Data.MAX_HELD_ITEMS; ++i)
            {
                HeldItem held = data.held[i];
                if (!data.held[i].known) continue;
                if (!data.held[i].isDamageable) continue;
                data.held_damageable.add(held);
            }
        }

        refreshCalculatedData();
    }
    
    public void refreshCalculatedData()
    {
        data.valid = true;
    }

    private int getArrowsCount(PlayerInventory inventory)
    {
        int arrows = 0;
        for (ItemStack its : inventory.mainInventory) 
        {
            if (its == null) continue;
            final Item item = its.getItem();
            if (item == Items.ARROW || item == Items.SPECTRAL_ARROW || item == Items.TIPPED_ARROW)
                arrows += its.getCount();
        }
        return arrows;
    }
}
