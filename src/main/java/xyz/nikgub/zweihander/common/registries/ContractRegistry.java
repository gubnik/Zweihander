package xyz.nikgub.zweihander.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.common.contracts.AccursedContractEntry;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ContractRegistry {
    public static final ResourceKey<Registry<AccursedContractEntry<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Zweihander.MOD_ID, "contracts"));
    public static final DeferredRegister<AccursedContractEntry<?>> CONTRACTS = DeferredRegister.create(REGISTRY_KEY, Zweihander.MOD_ID);
    public static final Supplier<IForgeRegistry<AccursedContractEntry<?>>> REGISTRY = CONTRACTS.makeRegistry(() -> new RegistryBuilder<AccursedContractEntry<?>>().disableOverrides());

    public static RegistryObject<AccursedContractEntry<?>> registerContractEntry(String name, AccursedContractEntry<?> entry) {
        return CONTRACTS.register(name, () -> entry);
    }

    public static Set<AccursedContractEntry<?>> getFromEntityType(EntityType<?> entityType) {
        return REGISTRY.get()
                .getValues()
                .stream()
                .filter(accursedContractEntry -> accursedContractEntry.getEntityToSummon() == entityType)
                .collect(Collectors.toSet());
    }

    public static RegistryObject<AccursedContractEntry<?>> ZOMBIE = CONTRACTS.register("zombie",
            () -> new AccursedContractEntry<>(EntityType.ZOMBIE, 1));
    public static RegistryObject<AccursedContractEntry<?>> SKELETON = CONTRACTS.register("skeleton",
            () -> new AccursedContractEntry<>(EntityType.SKELETON, 5));
    public static RegistryObject<AccursedContractEntry<?>> CREEPER = CONTRACTS.register("creeper",
            () -> new AccursedContractEntry<>(EntityType.CREEPER, 20));

}
