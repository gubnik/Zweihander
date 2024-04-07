/*
        Zweihander, Minecraft modification
        Copyright (C) 20224  nikgub

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.nikgub.zweihander;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Zweihander.MOD_ID)
public class Zweihander
{
    public static final String MOD_ID = "zweihander";
    //public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);
    public static final EnchantmentCategory ZWEIHANDER_CATEGORY = EnchantmentCategory.create("zweihander", (item -> item instanceof ZweihanderItem));

    public static final RegistryObject<Item> ZWEIHANDER = ITEMS.register("zweihander", () -> new ZweihanderItem(new Item.Properties()));

    public static final RegistryObject<Enchantment> WEIGHT = ENCHANTMENTS.register("weight",
            () -> new Enchantment(Enchantment.Rarity.COMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 5; }
            });
    public static final RegistryObject<Enchantment> POISE = ENCHANTMENTS.register("poise",
            () -> new Enchantment(Enchantment.Rarity.COMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 5; }
            });
    public static final RegistryObject<Enchantment> GIANT = ENCHANTMENTS.register("giant",
            () -> new Enchantment(Enchantment.Rarity.UNCOMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 3; }
            });
    public static final RegistryObject<Enchantment> CURSE_OF_CHAOS = ENCHANTMENTS.register("curse_of_chaos",
            () -> new Enchantment(Enchantment.Rarity.COMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 1; }
                @Override
                public boolean isTreasureOnly()
                { return true; }
                @Override
                public boolean isCurse()
                { return true; }
            });

    public Zweihander()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::creativeTabEvent);
        ITEMS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void creativeTabEvent(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
            event.accept(ZWEIHANDER);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void attackEntityEvent(AttackEntityEvent event)
    {
        Player player = event.getEntity();
        if (!(player.getMainHandItem().getItem() instanceof ZweihanderItem)) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;
        player.getMainHandItem().getOrCreateTag().putBoolean("ProperSwing", (player.getAttackStrengthScale(0) == 1));
        if (player.getAttackStrengthScale(0) != 1) event.setCanceled(true);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void livingHurtEvent (LivingHurtEvent event)
    {
        if (!(event.getSource().getEntity() instanceof LivingEntity source)) return;
        ItemStack zweihander = source.getMainHandItem();
        if (!(zweihander.getItem() instanceof ZweihanderItem)) return;
        LivingEntity target = event.getEntity();
        if (zweihander.getEnchantmentLevel(CURSE_OF_CHAOS.get()) != 0)
        {
            float mod;
            if (target instanceof Player)
            {
                target.setSecondsOnFire(5);
                mod = 1.25F;
            } else mod = 0.5F;
            event.setAmount(event.getAmount() * mod);
        }
    }

}