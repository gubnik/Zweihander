package xyz.nikgub.zweihander.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import xyz.nikgub.zweihander.Zweihander;

import java.util.concurrent.CompletableFuture;

public class DamageTypeDatagen extends TagsProvider<DamageType> {

    public DamageTypeDatagen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, Zweihander.MOD_ID, existingFileHelper);
    }

    public static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Zweihander.MOD_ID, name));
    }

    public static final ResourceKey<DamageType> MUSKET_SHOT = register("musket_shot");

    public static void generate(BootstapContext<DamageType> bootstrap) {
        bootstrap.register(MUSKET_SHOT, new DamageType(MUSKET_SHOT.location().getPath(), DamageScaling.NEVER, 0.1f));
    }

    public static TagKey<DamageType> create(String name)
    {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Zweihander.MOD_ID, name));
    }

    public static final TagKey<DamageType> IS_BLEED = create("is_bleed");

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(DamageTypeTags.IS_PROJECTILE)
                .add(MUSKET_SHOT);
    }
}