package be.nerosro.elemancy.client.tome.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.soulmark.network.ClientSkillTreeData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

/**
 * Lightweight runtime loader for Tome entry JSON files.
 */
public final class TomeEntryLoader {
    private TomeEntryLoader() {
    }

    public record PageRequirements(@Nullable String node) {
        public boolean isSatisfied() {
            if (node == null) return true;
            try {
                return ClientSkillTreeData.isUnlocked(Identifier.parse(node));
            } catch (Exception e) {
                return false; // Invalid node ID → section stays locked
            }
        }
    }

    public record RecipeDisplay(String type, List<String> grid, String result) {
    }

    public record Section(String content, @Nullable PageRequirements requirements, @Nullable RecipeDisplay recipe) {
        public boolean isVisible() {
            return requirements == null || requirements.isSatisfied();
        }
    }

    public record Page(List<Section> sections) {
        public List<Section> getVisibleSections() {
            return sections.stream().filter(Section::isVisible).toList();
        }
    }

    public record TomeEntry(String id, String title, @Nullable PageRequirements requirements, List<Page> pages) {
        /**
         * Returns true if entry requirements are satisfied (or no requirements).
         */
        public boolean isVisible() {
            return requirements == null || requirements.isSatisfied();
        }

        /**
         * Returns true if entry has at least one visible section.
         */
        public boolean hasVisibleContent() {
            return pages.stream().anyMatch(page -> !page.getVisibleSections().isEmpty());
        }

        /**
         * Extract the result item ID from the first recipe found in this entry.
         * Used for grid index display of craftable items.
         */
        public String getResultItemId() {
            for (Page page : pages) {
                for (Section section : page.sections()) {
                    if (section.recipe() != null) {
                        return section.recipe().result();
                    }
                }
            }
            return "minecraft:barrier"; // Fallback for entries without recipes
        }
    }

    public static List<TomeEntry> loadKnowledgeEntries() {
        List<TomeEntry> result = new ArrayList<>();

        Optional<JsonObject> indexOpt = readJson(id("tome/entries/knowledge/index.json"));
        if (indexOpt.isEmpty()) return result;

        JsonArray entries = getArray(indexOpt.get(), "entries");
        if (entries == null) return result;

        for (JsonElement element : entries) {
            if (!element.isJsonPrimitive()) continue;
            String entryId = element.getAsString();
            loadKnowledgeEntry(entryId).ifPresent(result::add);
        }

        return result;
    }

    public static List<TomeEntry> loadCraftingEntries() {
        List<TomeEntry> result = new ArrayList<>();

        Optional<JsonObject> indexOpt = readJson(id("tome/entries/crafting/index.json"));
        if (indexOpt.isEmpty()) return result;

        JsonArray entries = getArray(indexOpt.get(), "entries");
        if (entries == null) return result;

        for (JsonElement element : entries) {
            if (!element.isJsonPrimitive()) continue;
            String entryId = element.getAsString();
            loadCraftingEntry(entryId).ifPresent(result::add);
        }

        return result;
    }

    public static Optional<TomeEntry> loadKnowledgeEntry(String entryId) {
        return loadEntryFromCategory("knowledge", entryId);
    }

    public static Optional<TomeEntry> loadCraftingEntry(String entryId) {
        return loadEntryFromCategory("crafting", entryId);
    }

    public static Optional<TomeEntry> loadSpellEntry(Identifier spellNodeId) {
        return loadEntryFromCategory("spells", spellNodeId.getPath());
    }

    public static Optional<TomeEntry> loadPassiveEntry(Identifier nodeId) {
        return loadEntryFromCategory("passives", nodeId.getPath());
    }

    public static Optional<TomeEntry> loadScarEntry(String scarId) {
        return loadEntryFromCategory("scars", scarId);
    }

    private static Optional<TomeEntry> loadEntryFromCategory(String category, String entryId) {
        return readEntry(id("tome/entries/" + category + "/" + entryId + ".json"));
    }

    private static Optional<TomeEntry> readEntry(Identifier resourceId) {
        Optional<JsonObject> rootOpt = readJson(resourceId);
        if (rootOpt.isEmpty()) return Optional.empty();

        JsonObject root = rootOpt.get();
        String id = getString(root, "id").orElse("");
        String title = getString(root, "title").orElse(id);

        // Parse optional entry-level requirements
        PageRequirements requirements = null;
        if (root.has("requirements") && root.get("requirements").isJsonObject()) {
            JsonObject reqObj = root.getAsJsonObject("requirements");
            String node = getString(reqObj, "node").orElse(null);
            requirements = new PageRequirements(node);
        }

        List<Page> pages = new ArrayList<>();
        JsonArray pageArray = getArray(root, "pages");
        if (pageArray != null) {
            for (JsonElement element : pageArray) {
                if (element.isJsonObject()) {
                    JsonObject pageObj = element.getAsJsonObject();
                    pages.add(parsePage(pageObj));
                }
            }
        }

        if (pages.isEmpty()) {
            pages.add(new Page(List.of(new Section("No page content found.", null, null))));
        }

        return Optional.of(new TomeEntry(id, title, requirements, pages));
    }

    private static Page parsePage(JsonObject pageObj) {
        List<Section> sections = new ArrayList<>();
        JsonArray sectionsArray = getArray(pageObj, "sections");

        if (sectionsArray != null) {
            for (JsonElement sectionElement : sectionsArray) {
                if (sectionElement.isJsonObject()) {
                    sections.add(parseSection(sectionElement.getAsJsonObject()));
                }
            }
        }

        if (sections.isEmpty()) {
            sections.add(new Section("Empty page.", null, null));
        }

        return new Page(sections);
    }

    private static Section parseSection(JsonObject sectionObj) {
        // Parse content paragraphs
        List<String> paragraphs = new ArrayList<>();
        JsonArray contentArray = getArray(sectionObj, "content");
        if (contentArray != null) {
            for (JsonElement para : contentArray) {
                if (para.isJsonPrimitive()) {
                    paragraphs.add(para.getAsString());
                }
            }
        }
        String content = String.join("\n\n", paragraphs);

        // Parse optional requirements
        PageRequirements requirements = null;
        if (sectionObj.has("requirements") && sectionObj.get("requirements").isJsonObject()) {
            JsonObject reqObj = sectionObj.getAsJsonObject("requirements");
            String node = getString(reqObj, "node").orElse(null);
            requirements = new PageRequirements(node);
        }

        // Parse optional recipe display
        RecipeDisplay recipe = null;
        if (sectionObj.has("recipe") && sectionObj.get("recipe").isJsonObject()) {
            JsonObject recipeObj = sectionObj.getAsJsonObject("recipe");
            String type = getString(recipeObj, "type").orElse("shaped_3x3");
            String result = getString(recipeObj, "result").orElse("");

            List<String> grid = new ArrayList<>();
            JsonArray gridArray = getArray(recipeObj, "grid");
            if (gridArray != null) {
                for (JsonElement elem : gridArray) {
                    if (elem.isJsonPrimitive()) {
                        grid.add(elem.getAsString());
                    }
                }
            }

            recipe = new RecipeDisplay(type, grid, result);
        }

        return new Section(content, requirements, recipe);
    }

    private static Optional<JsonObject> readJson(Identifier resourceId) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            Optional<Resource> resourceOpt = minecraft.getResourceManager().getResource(resourceId);
            if (resourceOpt.isEmpty()) return Optional.empty();

            try (InputStream stream = resourceOpt.get().open();
                 Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return Optional.of(JsonParser.parseReader(reader).getAsJsonObject());
            }
        } catch (Exception e) {
            Elemancy.LOGGER.warn("Failed to load tome entry: {}", resourceId, e);
            return Optional.empty();
        }
    }

    private static Optional<String> getString(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive()) return Optional.empty();
        return Optional.of(object.get(key).getAsString());
    }

    private static JsonArray getArray(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonArray()) return null;
        return object.getAsJsonArray(key);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("elemancy", path);
    }
}
