package be.nerosro.elemancy.client.structureprojection;

import be.nerosro.elemancy.ritual.shared.StructureRotationTemplate;

/** Immutable client projection definition registered by a consuming feature. */
public record StructureProjectionDefinition(
    StructureRotationTemplate template,
    int maximumAnchorDistance,
    boolean dismissWhenSatisfied
) {
    public static StructureProjectionDefinition standard(StructureRotationTemplate template) {
        return new StructureProjectionDefinition(template, 64, true);
    }
}