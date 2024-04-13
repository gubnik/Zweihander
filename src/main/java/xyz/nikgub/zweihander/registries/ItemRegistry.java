package xyz.nikgub.zweihander.registries;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.items.InfusionItem;
import xyz.nikgub.zweihander.items.ZweihanderItem;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Zweihander.MOD_ID);

    public static final RegistryObject<Item> ZWEIHANDER = ITEMS.register("zweihander", () -> new ZweihanderItem(new Item.Properties()));

    public static final RegistryObject<Item> FIERY_INFUSION = ITEMS.register("fiery_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.FIRE_INFUSION.get()));

    public static final RegistryObject<Item> ICE_INFUSION = ITEMS.register("ice_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.ICE_INFUSION.get()));

    public static final RegistryObject<Item> SILVER_INFUSION = ITEMS.register("silver_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.SILVER_INFUSION.get()));

    public static final RegistryObject<Item> CREEPER_INFUSION = ITEMS.register("creeper_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.CREEPER_INFUSION.get()));
}
