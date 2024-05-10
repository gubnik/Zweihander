package xyz.nikgub.zweihander.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import xyz.nikgub.zweihander.Zweihander;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Zweihander.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistriesDataGeneration extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DamageTypeDatagen::generate);

    private RegistriesDataGeneration(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", Zweihander.MOD_ID));
    }

    public static void addProviders(boolean isServer, DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        generator.addProvider(isServer, new RegistriesDataGeneration(output, provider));
        generator.addProvider(isServer, new PoiTypesDatagen(output, provider, helper));
        generator.addProvider(isServer, new SoundsDefinitionsDatagen(output, helper));
        generator.addProvider(isServer, new DamageTypeDatagen(output, provider.thenApply(RegistriesDataGeneration::append), helper));
    }

    private static HolderLookup.Provider append(HolderLookup.Provider original) {
        return RegistriesDataGeneration.BUILDER.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
    }
}
