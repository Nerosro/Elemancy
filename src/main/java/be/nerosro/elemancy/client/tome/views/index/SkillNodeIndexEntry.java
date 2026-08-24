package be.nerosro.elemancy.client.tome.views.index;

import be.nerosro.soulmark.skilltree.SkillNode;
import net.minecraft.resources.Identifier;

/** Immutable skill node data shown in a Tome index view. */
public record SkillNodeIndexEntry(Identifier id, SkillNode node) {
}