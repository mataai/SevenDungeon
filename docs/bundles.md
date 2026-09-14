# Dungeon bundles

Every dungeon is a self-contained folder under the plugin's data directory:

```
plugins/SevenDungeons/dungeons/<dungeonId>/
  dungeon.yml     settings: world name, lives, team size, time, weather, ...
  anchors.yml     positions of the command signs
  messages.yml    texts shown by message signs, keyed by message id
```

The folder is the source of truth for everything except the template world
itself, which stays a normal world folder in the server directory and is named in
`dungeon.yml`. Copying the bundle folder together with its world to another
server copies the dungeon. The MySQL database is no longer used for dungeons.

## dungeon.yml

```yaml
format: 1         # bundle layout version, managed by the plugin
id: greenhouse
version: 1.0.0    # version of the dungeon content, managed by the author
world: dg_greenhouse
name: Greenhouse
players: 2        # team size that starts a game (1-8)
life: -1          # lives per player, -1 = infinite
timeLimit: 1800   # seconds, -1 = none
time: 6000        # world time at start
timeStop: true
weather: clear    # clear | rain | thunder
hunger: true
godmode: false
magic: false
gem: 0            # 0 = quartz, 1 = emerald
```

Written by `/7d createdungeon` and `/7d editdungeon`. Hand edits are picked up on
the next plugin load.

### Versioning

Two independent numbers live in `dungeon.yml`:

- **`format`** is the version of the bundle layout itself. The plugin writes it and
  compares it on load. A bundle written by an older plugin is migrated in memory and
  rewritten in the current format, with a console line saying so. A bundle newer than
  the plugin understands is skipped with an error so it is never rewritten by mistake.
  Bundles without the key, from before versioning existed, count as format 0.
- **`version`** is the version of the dungeon content: the map, its signs and messages.
  The plugin never changes it on its own. New dungeons start at `1.0.0`. It is shown by
  `/7d dungeoninfo` and changed with `/7d editdungeon <id> version <value>`, where
  `<value>` is either an explicit string (letters, digits, `.`, `-`, `_`, up to 20
  characters) or `bump`, which increments the last number: `1.0.2` becomes `1.0.3`.

## anchors.yml

Command signs are signs whose first line starts with `=` (for example `=c`,
`=sm`, `=xfp`). Their positions are called **anchors**.

```yaml
dungeon: greenhouse
world: dg_greenhouse
anchors:
- 12 64 -3
- 12 64 -4
```

When a game starts, the plugin copies the template world, reads this file and
compiles exactly the listed positions. The world is never scanned. If a listed
position does not hold a command sign in the instance, the console logs a
warning naming the coordinates and the entry is skipped.

The file is kept in sync automatically while editing the template world:

- Writing a sign whose first line starts with `=` registers its position.
- Rewriting a registered sign so it no longer starts with `=` unregisters it.
- Breaking a registered sign unregisters it.

## messages.yml

```yaml
messages:
  1: "Welcome to the greenhouse."
  2: "&cThe oxygen is running out!"
```

Message signs (`=m`, `=bm`) reference these ids.

## Commands

All need the `seven.dungeons` permission.

| Command | Effect |
|---|---|
| `/7d createdungeon <id>` | Creates the bundle for the world you are standing in. |
| `/7d editdungeon <id> <property> <value>` | Changes a setting and rewrites `dungeon.yml`. |
| `/7d deldungeon <id>` | Deletes the bundle folder. The world is kept. |
| `/7d dungeoninfo <id>` | Prints the settings, anchor and message counts, and the bundle path. |
| `/7d anchors <id>` | Lists anchors and flags any that no longer point at a command sign. |
| `/7d addanchor <id>` | Registers the command sign you are looking at. |
| `/7d delanchor <id>` | Unregisters the block you are looking at. |
| `/7d importsigns <id>` | Migration: scans the template world up to your position and rewrites `anchors.yml`. `/7d savesigns` is an alias. |
| `/7d importdb <id\|all>` | Migration: rebuilds bundles from the legacy MySQL tables. |

## Migrating from the database

1. Start the server with the database still reachable.
2. Run `/7d importdb all`. Each dungeon row becomes a bundle with its messages and
   sign positions.
3. Check with `/7d dungeoninfo <id>` and `/7d anchors <id>`.
4. The database can then be turned off. Portals are the only thing still stored
   there; without a connection the plugin starts with no portals and logs a warning.
