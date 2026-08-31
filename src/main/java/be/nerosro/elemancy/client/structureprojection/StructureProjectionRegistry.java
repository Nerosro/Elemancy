package be.nerosro.elemancy.client.structureprojection;

import java.util.Map;
import java.util.Optional;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.resources.Identifier;

/** Resolves data-declared structure IDs to consumer-registered projection definitions. */
public final class StructureProjectionRegistry {
    private static final Map<Identifier, StructureProjectionDefinition> PROJECTIONS = Map.of(
        id("attunement_ritual"), StructureProjectionDefinition.standard(
            be.nerosro.elemancy.ritual.attunement.StructureTemplate.TEMPLATE),
        id("conversion_ritual"), StructureProjectionDefinition.standard(
            be.nerosro.elemancy.ritual.conversion.StructureTemplate.TEMPLATE)
    );

    private StructureProjectionRegistry() {
    }

    public static Optional<StructureProjectionDefinition> get(Identifier id) {
        return Optional.ofNullable(PROJECTIONS.get(id));
    }

    public static boolean contains(String id) {
        try {
            return get(Identifier.parse(id)).isPresent();
        } catch (Exception exception) {
            return false;
        }
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, path);
    }
}