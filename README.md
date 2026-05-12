# CyberCore Rework Highlights

Welcome to the major rework of CyberCore! This update modernizes and streamlines the core, focusing on maintainability, modularity, and better dependency management.

## 🚩 Critical Points & Major Package Changes

- **Main Package Renamed:**  
  All sources have moved from `net.zerotoil.dev.cybercore` to `com.bitaspire.cybercore`. All class paths, imports, and usage should update accordingly.

- **Group/Artifact Metadata:**  
  - Maven groupId is now `com.bitaspire`.
  - Project version reset from `1.3.31` to `1.0`.
  - Java compatibility moved to 21 in properties, but source/target remains 1.8.

- **Build & Dependency Overhaul:**
  - BeansLib replaced by **Takion-shaded** as the main utility library.
  - **Metrics** is now managed as a dependency instead of a bundled, precompiled class.
  - **Config Updater** functionality is now in a separate dependency, not within CyberCore.
  - JetBrains Annotations, Lombok, Commons Lang, and Spigot dependencies updated and streamlined.
  - New Maven repository: `https://croabeast.github.io/repo/`.

## 🔥 Deleted, Replaced, or Moved Classes

### **Removed/Externalized**
- **Metrics class:**  
  The enormous `net.zerotoil.dev.cybercore.addons.Metrics` is **completely removed**. Metrics are now handled by a managed dependency.
- **ConfigUpdater and related config updater classes:**  
  No longer maintained in this repo—these are handled by an external library.
- **PlayerUtils:**  
  Removed. Placeholder management is now covered by `TakionLib`'s `PlaceholderManager`.
- **TimeUtils:**  
  Removed. All time utilities are provided by `TakionLib`'s time package.

### **Refactored and Renamed**
- **CyberCore:**  
  - Moved: `net.zerotoil.dev.cybercore.CyberCore` → `com.bitaspire.cybercore.CyberCore`
  - Modernized: Now uses new internal structure, simplified boot, and load logic.
- **CoreSettings:**  
  - Moved: `net.zerotoil.dev.cybercore.CoreSettings` → `com.bitaspire.cybercore.CoreSettings`
  - Refactored for TakionLib compatibility.
- **Lag:**  
  - Moved: `net.zerotoil.dev.cybercore.objects.Lag` → `com.bitaspire.cybercore.Lag`
  - Refactored for simpler TPS tracking and reflection via TakionLib.
- **TextSettings → TextLibrary:**  
  - Replaced: `net.zerotoil.dev.cybercore.TextSettings` → `com.bitaspire.cybercore.TextLibrary`
  - Now extends TakionLib for language and logger features.
- **FileManager/YAMLFile:**  
  - Replaced: `net.zerotoil.dev.cybercore.files.FileManager` and `YAMLFile` with `com.bitaspire.cybercore.file.FileManager` and `FileManagerImpl`, built around TakionLib and its ConfigurableFile.
- **GeneralUtils → RandomUtils:**  
  - Only random value generators retained, now in `com.bitaspire.cybercore.util.RandomUtils`.

### **Deleted**
- `net.zerotoil.dev.cybercore.files.configupdater.*`
- `net.zerotoil.dev.cybercore.utilities.PlayerUtils`
- `net.zerotoil.dev.cybercore.utilities.TimeUtils`
- `net.zerotoil.dev.cybercore.utilities.GeneralUtils`
- `net.zerotoil.dev.cybercore.files.YAMLFile` (all file operations are now via TakionLib)

## 🧩 Dependency Strategy

- **TakionLib** is the new backbone for all file, reflection, logger, time, and placeholder utilities.
- **Metrics** and **ConfigUpdater** are now **external dependencies**, no longer requiring you to maintain or update their code in CyberCore itself.

## 🚀 Why This Rocks

- **Lighter, faster, easier to maintain:** No more duplicated utility code; everything not unique to CyberCore is a proper dependency.
- **Modernized API:** Cleaner, more modular classes make integration and extension much easier for plugin developers.
- **Future-proof:** With TakionLib, you get regular updates and improvements for all core utilities.
- **Cleaner boot & logging:** Startup banners and logs use TakionLib’s logging layer, with better color/config support.

---

## ⚡ Migration Tips

- Adjust all imports to the new `com.bitaspire.cybercore` base package.
- Replace direct usage of removed utilities with their TakionLib counterparts.
- Any custom config update logic should now be implemented via the external config updater dependency.
- Metrics integration should use the new dependency approach as documented in TakionLib.
