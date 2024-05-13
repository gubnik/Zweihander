package xyz.nikgub.zweihander.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nikgub.zweihander.Zweihander;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ItemTagsDatagen extends ItemTagsProvider {
    public ItemTagsDatagen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, blockProvider, Zweihander.MOD_ID, existingFileHelper);
    }

    private static TagKey<Item> make (String s) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(s));
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider p_256380_) {

    }
}
