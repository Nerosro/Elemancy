package be.nerosro.elemancy.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.items.WandAspect;
import be.nerosro.elemancy.items.WandItem;
import be.nerosro.elemancy.network.ClientEquippedSpellData;
import be.nerosro.elemancy.network.SpellSelectPayload;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.network.ClientSkillTreeData;
import be.nerosro.soulmark.skilltree.NodeType;
import be.nerosro.soulmark.skilltree.SkillNode;
import be.nerosro.soulmark.skilltree.SkillTreeRegistries;
import be.nerosro.soulmark.ui.RadialMenuEntry;
import be.nerosro.soulmark.ui.RadialMenuOpeners;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Registers wand items (via tag) as radial menu openers.
 * Soulmark handles the keybind and screen opening — we just provide the data.
 */
public final class SpellRadialHandler {
    private SpellRadialHandler() {
    }


    /**
     * Registers the wands tag with Soulmark's RadialMenuOpeners.
     * Any item in {@link ElemancyTags#WANDS} will open the spell radial.
     * Called during commonSetup.
     */
    public static void registerOpeners() {
        RadialMenuOpeners.registerTag(
            ElemancyTags.WANDS,
            SpellRadialHandler::buildEntries,
            SpellRadialHandler::onSpellSelected,
            SpellRadialHandler::getEquipped
        );
    }

    private static List<RadialMenuEntry> buildEntries(Player player) {
        WandAspect aspect = WandItem.getAspect(player.getOffhandItem());
        List<RadialNode> nodes = new ArrayList<>();

        for (Identifier nodeId : be.nerosro.soulmark.skilltree.SkillTreeUtil.getAllNodesInTree(SkillTreeEntries.TREE_ID)) {
            if (!ClientSkillTreeData.isUnlocked(nodeId)) continue;

            SkillNode node = SkillTreeRegistries.NODE_REGISTRY.getValue(nodeId);
            if (node != null
                && SkillTreeEntries.TREE_ID.equals(node.treeId())
                && (node.nodeType() == NodeType.ABILITY || node.nodeType() == NodeType.UTILITY)
                && aspect.canChannel(elementFromNode(node))) {
                nodes.add(new RadialNode(nodeId, node));
            }
        }

        nodes.sort(Comparator.comparingInt((RadialNode entry) -> entry.node().depth())
            .thenComparingInt(entry -> entry.node().lane()));
        return nodes.stream()
            .map(entry -> new RadialMenuEntry(
                entry.node().name(),
                entry.nodeId(),
                entry.node().element().argb(),
                resolveIcon(entry.node().nodeType()),
                entry.node().icon()
            ))
            .toList();
    }

    private record RadialNode(Identifier nodeId, SkillNode node) {
    }

    private static Identifier getEquipped(Player player) {
        return ClientEquippedSpellData.get();
    }

    private static String resolveIcon(NodeType type) {
        return switch (type) {
            case UTILITY -> "✦";
            case ABILITY -> "✴";
            case PASSIVE -> "◈";
            default -> "●";
        };
    }

    private static void onSpellSelected(Player player, RadialMenuEntry entry) {
        ClientEquippedSpellData.set(entry.nodeId());
        ClientPacketDistributor.sendToServer(new SpellSelectPayload(entry.nodeId().toString()));
    }

    /**
     * Derives the SpellElement from a node's element.
     * Nodes with NONE element map to SpellElement.NONE.
     */
    private static SpellElement elementFromNode(SkillNode node) {
        Element element = node.element();
        if (element == SoulmarkElements.FIRE.get()) return SpellElement.FIRE;
        if (element == SoulmarkElements.WATER.get()) return SpellElement.WATER;
        if (element == SoulmarkElements.EARTH.get()) return SpellElement.EARTH;
        if (element == SoulmarkElements.AIR.get()) return SpellElement.AIR;
        if (element == SoulmarkElements.LIGHT.get()) return SpellElement.LIGHT;
        if (element == SoulmarkElements.DARK.get()) return SpellElement.DARK;
        return SpellElement.NONE;
    }
}





