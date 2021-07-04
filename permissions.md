
# Permissions Documentation

## Overview

All commands and sub-commands have their own permissions node in the form:

`essentialcommands.<command>.<subcommand>`

Grant access to all subcommands using wildcards, like so:

`essentialcommands.<command>.*`

## Command Permission Nodes

Command | Permission | Description
--------|------------|------------
/tpa \<player>        |   `essentialcommands.tpa`   |   Request to teleport to a player.
/tpaccept \<player>   |   `essentialcommands.tpaccept`  |   Accept player's teleport request.
/tpdeny \<player>     |   `essentialcommands.tpdeny`    |   Deny Player's teleport request.
/home set \<home_name>    |   `essentialcommands.home.set`  |   Set a personal home location.
/home tp \<home_name>     |   `essentialcommands.home.tp`   |   Teleport to your home.
/home delete \<home_name> |   `essentialcommands.home.delete`   |   Delete your home.
/warp set \<warp_name>      |   `essentialcommands.warp.set`    |   Set a server-wide warp locaiton.
/warp tp \<warp_name>       |   `essentialcommands.warp.tp` |   Teleport to a warp.
/warp delete \<warp_name>   |   `essentialcommands.warp.delete` |   Delete a warp.
/back     |   `essentialcommands.back`  |   Teleport to your previous location.
/spawn tp \|\| /spawn     |   `essentialcommands.spawn.tp`  |   Teleport to the server spawn.
/spawn set              |   `essentialcommands.spawn.set`   |   Set the server spawn.
/nickname set \<nickname>                   | `essentialcommands.nickname.self`     | Set your own nickname to specified MinecraftText.
/nickname set \<target-player> \<nickname>  | `essentialcommands.nickname.others`   | Set target player's nickname to specified MinecraftText.
/nickname clear                             | `essentialcommands.nickname.self`     | Clear your own nickname.
/nickname clear \<target-player>            | `essentialcommands.nickname.others`   | Clear target player's nickname.
/nickname reveal \<player-nickname>         | `essentialcommands.nickname.reveal`   | Get list of players with the provided nickname (String, case-insensitive).
N/A | `essentialcommands.nickname.style.color` | Allows setting colorful nicknames.
N/A | `essentialcommands.nickname.style.fancy` | Allows setting nicknames that have special formatting (italic, bold, etc.)
N/A | `essentialcommands.nickname.style.hover` | Allows setting nicknames that show text on hover.
N/A | `essentialcommands.nickname.style.click` | Allows setting nicknames that execute an action on click.
/essentialcommands config reload    | `essentialcommands.config.reload`   |   Reload essentialcommands config.

## Rules/Config Bypass Permissions

These permissions allow players to bypass rules defined in the [Essential Commands config](https://github.com/John-Paul-R/Essential-Commands/wiki/Config-Documentation).

Permission | Description
-----------|------------
`essentialcommands.bypass.teleport_delay` | Ignore `teleport_delay`.
`essentialcommands.bypass.allow_teleport_between_dimensions` | Ignore `allow_teleport_between_dimensions`.
`essentialcommands.bypass.teleport_interrupt_on_damaged` | Ignore `teleport_interrupt_on_damaged`.

## Types

### MinecraftText

Essentially, any value that works for `/tellraw`'s message field. (JSON text or string enclosed by quotes)

You can use a tellraw generator like [MinecraftJson](https://www.minecraftjson.com/) to create this JSON text with a graphical interface and preview.

Examples: `"Alexandra"`, `{"text":"Alex","color":"green","bold":true}`
