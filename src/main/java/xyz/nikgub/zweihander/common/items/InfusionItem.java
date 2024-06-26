package xyz.nikgub.zweihander.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import xyz.nikgub.zweihander.client.item_extensions.InfusionClientExtension;
import xyz.nikgub.zweihander.common.mob_effect.InfusionMobEffect;
import xyz.nikgub.zweihander.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class InfusionItem extends Item {

    private final InfusionMobEffect effect;
    private final Ingredient ingredient;

    public InfusionItem(Properties properties, InfusionMobEffect effect, Ingredient ingredient) {
        super(properties);
        this.effect = effect;
        this.ingredient = ingredient;
    }

    public InfusionMobEffect getEffect() {
        return effect;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public static void makeRecipes ()
    {
        final Ingredient base = Ingredient.of(ItemRegistry.UNBOUND_BLOOD.get());
        for (Item item : ItemRegistry.ITEMS.getEntries().stream().map(RegistryObject::get).toList())
        {
            if (item instanceof InfusionItem infusionItem)
            {
                if (infusionItem.ingredient == Ingredient.EMPTY) continue;
                BrewingRecipeRegistry.addRecipe(base, infusionItem.ingredient, new ItemStack(infusionItem));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("tooltip.zweihander." + itemStack.getItem()).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand)
    {
        if (hand != InteractionHand.OFF_HAND || player.getItemInHand(InteractionHand.MAIN_HAND).getMaxStackSize() != 1 || player.getItemInHand(InteractionHand.MAIN_HAND).getUseDuration() != 0) return InteractionResultHolder.fail(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack)
    {
        return 30;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().toList();
        for (MobEffectInstance instance : effectInstances)
        {
            if (instance.getEffect() instanceof InfusionMobEffect infusionMobEffect)
            {
                livingEntity.removeEffect(infusionMobEffect);
            }
        }
        livingEntity.addEffect(new MobEffectInstance(effect, 1200, 0));
        itemStack.shrink(1);
        return itemStack;
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new InfusionClientExtension());
    }
}
