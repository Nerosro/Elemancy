# List of all items and textures status

Soulmark is a library mod (elements, traits, attachments) and registers no items/blocks itself.
All in-game items/blocks below come from Elemancy.

## Items (from ElemancyItems.java)

| Item | Texture status |
| --- | --- |
| `energized_stick` | OK |
| `infused_ingot` | ok |
| `propolis` | OK |
| `tome` | OK |
| `affinity_paper` | OK |
| `ashen_stick` | OK |
| `ashen_wand` | OK |
| `arcane_vessel` | OK |
| `soulvial` | 🔧 need rework |
| `elemetal_ingot_fire` | 🔧 need rework |
| `elemetal_ingot_water` | 🔧 need rework |
| `elemetal_ingot_earth` | 🔧 need rework |
| `elemetal_ingot_air` | 🔧 need rework |
| `elemetal_ingot_light` | 🔧 need rework |
| `elemetal_ingot_dark` | 🔧 need rework |
| `robe_helmet` | 🔧 need rework |
| `robe_chestplate` | 🔧 need rework |
| `robe_leggings` | 🔧 need rework |
| `robe_boots` | 🔧 need rework |
| `infused_pickaxe` | OK |
| `icecream_cocoa` | OK |
| `amulet_of_deep_focus` | ❌ Missing |
| `charm_of_steady_flow` | ❌ Missing |
| `bracelet_of_enduring_mana` | ❌ Missing |
| `belt_of_rolling_tides` | ❌ Missing |
| `necklace_of_sunken_reserves` | ❌ Missing |
| `gauntlet_of_subtle_weave` | ❌ Missing |

## Blocks / Block Items (from ElemancyBlocks.java)

| Block | Texture status |
| --- | --- |
| `paradox_flower` | 🔧 need rework |
| `ashen_sapling` | OK |
| `ashen_leaves` | OK |
| `ashen_log` | OK |
| `ashen_wood` | OK (derived from log texture, no dedicated file needed) |
| `stripped_ashen_log` | OK |
| `stripped_ashen_wood` | OK (derived from log texture, no dedicated file needed) |
| `ashen_planks` | OK |
| `ashen_stairs` | OK (reuses planks texture) |
| `ashen_slab` | OK (reuses planks texture) |
| `ashen_fence` | OK (reuses planks texture) |
| `ashen_fence_gate` | OK (reuses planks texture) |
| `ashen_door` | OK |
| `ashen_trapdoor` | OK |
| `ashen_pressure_plate` | OK (reuses planks texture) |
| `ashen_button` | OK (reuses planks texture) |
| `infused_metal_block` | OK |
| `infused_wool` | OK |
| `mirror` | ❌ Missing |
| `soft_glow` | OK (`soft_glow.png` is particles, not a real block) |
| `elemetal_block_fire` | 🔧 need rework |
| `elemetal_block_water` | 🔧 need rework |
| `elemetal_block_earth` | 🔧 need rework |
| `elemetal_block_air` | 🔧 need rework |
| `elemetal_block_light` | 🔧 need rework |
| `elemetal_block_dark` | 🔧 need rework |

## Tome Nodes and Icons

Ability and passive nodes use `textures/gui/skills/<node_id>.png` when an icon is assigned. Nodes without one use Tome's built-in text marker. Scar and tab icons use Minecraft's built-in mob-effect textures.

### Visible Nodes

| Node | Type | Icon status |
| --- | --- | --- |
| `elementize` | Utility | ❌ Missing (built-in `✦` marker) |
| `elemental_blast` | Spell | ❌ Missing |
| `fire_blast` | Spell | ❌ Missing |
| `water_jet` | Spell | ❌ Missing |
| `pebble_shot` | Spell | 🔧 need rework |
| `gust_slash` | Spell | 🔧 need rework |
| `light_dart` | Spell | ❌ Missing |
| `shadow_flick` | Spell | ❌ Missing |
| `smoldering_power` | Passive | ❌ Missing |
| `vital_currents` | Passive | ❌ Missing |
| `earthen_poise` | Passive | ❌ Missing |
| `breeze_tread` | Passive | ❌ Missing |
| `soft_glow` | Passive | ❌ Missing |
| `night_sight` | Passive | ❌ Missing |
| `attunement_ritual` | Ritual | ❌ Missing (built-in `●` marker) |

### Hidden Discovery Nodes

These nodes are registered but intentionally never render in the Tome UI.

| Node | Icon status |
| --- | --- |
| `discovery/paradox_flower` | No icon required (hidden) |
| `discovery/infused_beehive` | No icon required (hidden) |

### Crafting Entries

Crafting entries render the resulting item's inventory icon rather than a dedicated Tome-node texture.

| Entry | Unlock requirement | Icon status |
| --- | --- | --- |
| `arcane_vessel` | `attunement_ritual` | 🔧 need rework |

### Scar Icons

| Scar | Icon status |
| --- | --- |
| `physical_scars` | 🔧 need rework (Minecraft `slowness.png`) |
| `arcane_tremor` | 🔧 need rework (Minecraft `unluck.png`) |
| `spell_drift` | 🔧 need rework (Minecraft `weakness.png`) |
| `channel_disruption` | 🔧 need rework (Minecraft `hunger.png`) |
| `mana_burn` | 🔧 need rework (Minecraft `wither.png`) |
| `arcane_fatigue` | 🔧 need rework (Minecraft `mining_fatigue.png`) |
| `spell_weakness` | 🔧 need rework (Minecraft `instant_damage.png`) |
| `mana_collapse` | 🔧 need rework (Minecraft `bad_omen.png`; ) |

### Tome Tab Icons

| Tab | Icon status |
| --- | --- |
| `identity` | 🔧 need rework (Minecraft `invisibility.png`) |
| `scars` | 🔧 need rework (Minecraft `glowing.png`) |
| `spells` | 🔧 need rework (Minecraft `wind_charged.png`) |
| `passives` | 🔧 need rework (Minecraft `luck.png`) |
| `crafting` | 🔧 need rework (Minecraft `haste.png`) |
| `knowledge` | 🔧 need rework (Minecraft `conduit_power.png`) |
| `rituals` | 🔧 need rework (Minecraft `trial_omen.png`;) |
