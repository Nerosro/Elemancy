package be.nerosro.elemancy.client.tome;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.client.tome.views.TomeTabView;

/**
 * Registry for managing Tome tabs.
 * Maintains insertion order and provides tab lookup by ID.
 */
public class TabRegistry {
    private final Map<String, TomeTabView> tabs = new LinkedHashMap<>();

    /**
     * Registers a tab view.
     *
     * @param tab Tab view to register
     * @throws IllegalArgumentException if tab ID already registered
     */
    public void register(TomeTabView tab) {
        String id = tab.getId();
        if (tabs.containsKey(id)) {
            throw new IllegalArgumentException("Tab already registered: " + id);
        }
        tabs.put(id, tab);
    }

    /**
     * Gets all registered tabs that are currently visible.
     *
     * @return List of visible tabs in registration order
     */
    public List<TomeTabView> getVisibleTabs() {
        return tabs.values().stream()
            .filter(TomeTabView::isVisible)
            .toList();
    }

    /**
     * Gets tab by ID, or null if not registered.
     *
     * @param tabId Tab identifier
     * @return Tab view or null
     */
    @Nullable
    public TomeTabView getById(String tabId) {
        return tabs.get(tabId);
    }
}
