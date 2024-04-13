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

import com.mojang.logging.LogUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import xyz.nikgub.zweihander.items.InfusionItem;
import xyz.nikgub.zweihander.items.ZweihanderItem;
import xyz.nikgub.zweihander.mob_effect.InfusionMobEffect;
import xyz.nikgub.zweihander.registries.EnchantmentRegistry;
import xyz.nikgub.zweihander.registries.ItemRegistry;
import xyz.nikgub.zweihander.registries.MobEffectRegistry;

@Mod(Zweihander.MOD_ID)
public class Zweihander
{
    public static final String MOD_ID = "zweihander";
    public static final Logger LOGGER = LogUtils.getLogger();



    public Zweihander()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::creativeTabEvent);
        ItemRegistry.ITEMS.register(modEventBus);
        EnchantmentRegistry.ENCHANTMENTS.register(modEventBus);
        MobEffectRegistry.MOB_EFFECTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void creativeTabEvent(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
            event.accept(ItemRegistry.ZWEIHANDER);
        for (RegistryObject<Item> registryObject: ItemRegistry.ITEMS.getEntries())
        {
            if (registryObject.isPresent() && registryObject.get() instanceof InfusionItem &&
                    (event.getTabKey() == CreativeModeTabs.COMBAT || event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES))
                event.accept(registryObject);
        }
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
        ItemStack mainHandItem = source.getMainHandItem();
        if (mainHandItem.getMaxStackSize() == 1) {
            for (MobEffectInstance instance : source.getActiveEffects())
            {
                if (instance.getEffect() instanceof InfusionMobEffect infusionMobEffect)
                {
                    infusionMobEffect.getInfusionEffect().effect(event);
                }
            }
        }
        if (!(mainHandItem.getItem() instanceof ZweihanderItem)) return;
        LivingEntity target = event.getEntity();
        if (mainHandItem.getEnchantmentLevel(EnchantmentRegistry.CURSE_OF_CHAOS.get()) != 0)
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
