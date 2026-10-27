# Local cosmetics research

## Scope and handling

Inspected the supplied `C:\Users\ACER\.lunarclient` directory by file metadata and selected JSON structure. The inventory is local technical reference only. No Lunar Client JAR was executed, decompiled, or unpacked, and no Lunar textures, models, fonts, shaders, sounds, UI files, or cosmetic definitions were copied into ClientPulse.

The root contains 17 directories plus `my_cosmetics.json` and `patcher.log` (19 entries total). The recursive inventory counted 66,420 files in those directories. Approximate folder sizes below are rounded by PowerShell and therefore do not sum to an exact total.

| Directory | Files | Approx. size | Notable contents |
|---|---:|---:|---|
| `cache` | 70 | 0.014 GB | PNG cache |
| `db` | 1 | <0.001 GB | Local database; contents not read |
| `game-cache` | 208 | 0.003 GB | PNGs and one JSON file |
| `jit` | 3,843 | 0.162 GB | Cached WebP images, JSON, animation metadata, and other extension types |
| `jre` | 984 | 0.443 GB | Runtime binaries, native libraries, and bundled notices |
| `launcher-cache` | 233 | 0.569 GB | Launcher assets, native libraries, and package files |
| `licenses` | 57 | <0.001 GB | Third-party notices, license texts, and attributions |
| `logs` | 14 | 0.007 GB | Launcher logs; contents not read |
| `mx-cache` | 0 | 0 GB | Empty |
| `notes` | 0 | 0 GB | Empty |
| `offline` | 107 | 0.252 GB | Versioned native archives, libraries, JARs, and JSON metadata |
| `profiles` | 3,989 | 0.766 GB | Minecraft-version profiles, options, logs, and client components |
| `settings` | 86 | <0.001 GB | JSON and text settings, including skin-preset metadata |
| `shared` | 56,313 | 0.908 GB | Minecraft asset indexes and hash-named asset objects |
| `sounds` | 0 | 0 GB | Empty |
| `textures` | 464 | 0.035 GB | Cosmetic indexes, WebP images, model data, particle schemes, and shaders |
| `ui` | 51 | 0.012 GB | HTML/UI bundle, fonts, images, and a sound |

## Minecraft and cosmetic formats

- `profiles` includes directories for versions `1.7`, `1.8`, `1.12`, `1.16` through `1.21`, `26`, and several launcher-specific profiles. A directory named `1.8` exists, but its name alone does not establish an exact 1.8.9 game build. Its options include an OptiFine options file. Profile server lists and logs are user data and were not read.
- `textures/assets/lunar/cosmetics.json` contains 5,681 definition records. Its schema includes identifiers, display names, resource references, category/index information, morph and animation metadata, color options, tags, and scalability flags. The catalog references WebP and JSON resources; these are Lunar catalog data, not a public ClientPulse API.
- The catalog includes categories such as cloaks (2,689 entries), hats (499), pets (471), bandannas (449), dragon wings (419), masks (255), suits (210), and backpacks (164), among others. The catalog's category names and formats are useful for understanding feature breadth, not for copying definitions.
- `textures/assets/lunar` also contains cosmetic index JSON, `.bobj` model data, `.molang` expressions, animated-texture `.mcmeta`, emote models and textures, particle scheme JSON and textures, badges, and cloak fragment shaders. These are client-specific runtime formats; their presence does not grant redistribution rights or demonstrate an integration API.
- The local textures directory contains 337 `.webp`, 89 `.json`, 29 `.bobj`, two `.molang`, and two `.mcmeta` files, plus a small number of other files. The wider catalog refers to more resources than this cached directory contains.
- The local cosmetic-selection JSON exposes category keys including cape, wings, shield, hat, bow, sword, spray, pet, aura, and boots. Only key names were inspected; personal selection values were not recorded.

## Licenses, APIs, and reuse assessment

The 57 files in `licenses` document or link to notices for bundled third-party projects, including Minecraft Forge, Fabric, LWJGL, GeckoLib, and other libraries/mods. `licenses/README.txt` points to Lunar Client's third-party-mod notices and source-access information. These notices apply to their named components; they do not establish permission to redistribute Lunar's cosmetic artwork, catalog, UI, shaders, or proprietary model data.

No asset-specific license or documented public cosmetics SDK was found in the inspected material. Accordingly, no supplied asset or proprietary implementation is reused. Uninspected local databases, server lists, account/profile values, logs, and executable/runtime files are excluded from ClientPulse. If a particular resource is later authorized for redistribution, its explicit license and attribution requirements must be verified individually before adding it.

## ClientPulse applicability

The version layout, category taxonomy, animated-resource metadata, and separation between cosmetic definitions and rendered resources informed a clean ClientPulse-owned catalog and renderer boundary. ClientPulse currently provides original procedural cape, wings, halo, trail, and badge shapes with configurable tint and timing. It does not load, parse, or claim compatibility with Lunar's proprietary `.bobj`, `.molang`, shader, catalog, or network formats.

The local selection file's category keys were inspected only to understand user-facing feature families. They were not used as ClientPulse identifiers or copied into its catalog. The local `profiles/1.8` directory shows an 1.8-family profile and OptiFine options, but does not establish an authorized Lunar integration point or a verified match to ClientPulse's Forge runtime.
