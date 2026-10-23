---
id: common-issues
title: Common Issues
sidebar_position: 2
---

Quick fixes for the questions that come up most. Longer explanations are on
[Troubleshooting](../advanced-optimization/troubleshooting.md).

## "You do not have permission to use this command."

Every command defaults to op. Give the node from
[Commands & Permissions](../core-reference/commands-permissions.md), for example
`/lp group staff permission set advancedvanish.vanish true`.

## Everyone with the vanish permission joins vanished

That's `vanish-on-join: true`, the default. Set it to `false`, or turn on `keep-vanish-state` so each
player's last choice is remembered.

## A vanished player can't open a door or press a button

`when-vanished.interact` is `false` by default. Run `/vanish interact` to lift it for yourself until
you unvanish, or set it to `true` for everyone.

## Items in a chest can't be taken while vanished

Silent containers are read-only, and `/vanish interact` doesn't change that. Set
`when-vanished.open-and-use-chests: true` to open containers normally (with the animation).

## Fake join/leave messages don't appear

Both are off by default. Enable them under `join-leave-messages`. They're sent for `/vanish` only,
never when a player joins vanished or quits.

## The action bar message won't go away

It shows while you're vanished. Set `messages.action-bar: ""` or `hooks.actionbar: false` to turn it
off.

## /vanish priority says priority isn't in use

Install LuckPerms and keep `priority.enable: true`.

## Changes to config.yml don't apply

Run `/vanish reload` after saving the file.
