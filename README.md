# NoAFK Plugin

**NoAFK** is a Spigot/Paper plugin that automatically manages players' AFK (Away From Keyboard) status, with notifications, automated commands, kick/redirect actions, teleportation, and PlaceholderAPI support.

---

## 📦 Version

- **Version:** 1.0.0
- **Minecraft API:** 1.19+
- **Author:** crxsto
- **Dependencies:** PlaceholderAPI (optional)

---

## ⚡ Main Features

- Automatic AFK detection via movement and chat
- Manual AFK commands for players and admins
- View list of currently AFK players
- Customizable notifications for staff
- Execute console commands automatically when a player goes AFK
- Automatic kick of AFK players
- Automatic redirect to Bungee/Velocity servers
- Automatic teleportation to a specific location
- Track AFK time (current session & total)
- PlaceholderAPI integration

---

## 🛠 Commands

| Command | Description | Permission |
|---------|------------|------------|
| `/afk [player]` | Set AFK for yourself or another player | `noafk.afk.self`, `noafk.afk.others` |
| `/afkstatus` | Show a list of currently AFK players | `noafk.afkstatus` |
| `/afkreload` | Reload the plugin configuration | `noafk.reload` |
| `/noafk alerts` | Toggle AFK notifications | `noafk.alerts` |
| `/noafk updateconfig` | Update config.yml with missing keys | `noafk.updateconfig` |

---

## 🔑 Permissions

- `noafk.afk.self` → Set yourself AFK  
- `noafk.afk.others` → Set other players AFK  
- `noafk.afkstatus` → View AFK list  
- `noafk.reload` → Reload configuration  
- `noafk.alerts` → Receive AFK notifications  
- `noafk.updateconfig` → Update configuration  

> Recommended setup:  
> Regular players: `noafk.afk.self` + `noafk.afkstatus`  
> Moderators: add `noafk.afk.others`  
> Admins: all permissions

---

## ⚙ Configuration (`config.yml`)

Key options:

```yaml
afk:
  time: 300
  commands: []
  commands_enabled: true
  kick_enabled: false
  kick_reason: "You have been kicked for being AFK."
  bungee_redirect_enabled: false
  bungee_server: "lobby"

commands:
  teleport:
    enabled: false
    world: "world"
    x: 0.5
    y: 64.0
    z: 0.5
    yaw: 0.0
    pitch: 0.0

types: ["movement","chat"]
movement:
  blocks: 5

log: true

messages:
  no_permission: '&cYou don''t have permission.'
  player_not_found: '&cPlayer not found.'
  only_player: '&cOnly players can use this command.'
  afk_enter: '&eYou are now &6AFK&7.'
  afk_exit: '&aYou are no longer AFK.'
  afk_exit_movement: '&aYou are no longer AFK (&bmovement&a).'
  afk_exit_chat: '&aYou are no longer AFK (&bchat&a).'
  afk_set_other: '&e%target% has been set AFK.'
  afk_set_self: '&eYou are now AFK.'
  afk_already_other: '&e%target% is already AFK.'
  afk_already_self: '&eYou are already AFK.'
  status_header: '&6AFK Players:'
  status_line: '&7- &e%player% &7(&b%time%s&7)'
  status_none: '&7No AFK players.'
  alert_afk: '&6[NoAFK] &e%player% &7is now &6AFK&7.'
  alerts_enabled: '&aAFK alerts reception &6enabled&7.'
  alerts_disabled: '&cAFK alerts reception &6disabled&7.'
  reload_done: '&aConfiguration reloaded.'```

Messages support Minecraft color codes (&a, &c, etc.) and the %player% placeholder.

📊 PlaceholderAPI
Available placeholders:

%noafk_afk% → true/false if the player is AFK

%noafk_time% → Current AFK time in seconds

%noafk_total% → Total AFK time in seconds

🚀 Installation
Place NoAFK.jar in the plugins folder

Start the server (config.yml will be generated automatically)

Configure settings in plugins/NoAFK/config.yml

Reload with /afkreload if needed

⚠ Compatibility
Spigot/Paper 1.19+ → Full support

Bukkit Vanilla → Limited support

Forge/Fabric → Not supported

Bungee/Velocity → Redirect supported via "BungeeCord" channel

💡 Technical Notes
AFK check runs every 2 seconds (40 ticks)

Heavy operations run asynchronously

Uses ConcurrentHashMap for thread safety

Minimal memory usage (~10KB per player)

AFK data is not persistent on disk

📄 License
MIT License

🛠 Support
For bug reports or support, contact the author: crxsto