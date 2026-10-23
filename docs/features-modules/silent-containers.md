---
id: silent-containers
title: Silent Containers
sidebar_position: 3
---

Opening a chest plays an animation and a sound that everyone nearby notices. While
`when-vanished.open-and-use-chests` is `false` (the default), vanished players open containers
silently instead.

## What opens silently

| Block | What the player sees |
|---|---|
| Chests, trapped chests, copper chests | A read-only copy of the contents (54 slots for a double chest) |
| Barrels | A read-only copy of the contents |
| Shulker boxes (every colour) | A read-only copy of the contents |
| Ender chests | Their **own** ender chest, fully usable |

The player gets `opening-container-silently` with `%type%` set to `chest`, `barrel`, `shulker` or
`enderchest`.

The copy is **read-only**: items can't be taken, moved or added. It's a way to inspect a container
without being noticed. To actually use a container, set `open-and-use-chests: true`. Containers then
open normally, with the animation.

## Config

```yaml
when-vanished:
  open-and-use-chests: false
```
