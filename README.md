# FixChat [![Build Status](https://travis-ci.org/Codeski/FixChat.svg?branch=master)](https://travis-ci.org/Codeski/FixChat)

Simple plugin for Bukkit and Sponge that makes chatting with your friends in game, and on Dynmap much easier.

## Features

* Adds **aliases for the tell command** so players can use /tell, /t, /whisper, and /w to send private messages to others.
* Adds **reply functionality** so players can easily reply using the added /reply and /r commands.
* Adds **away from keyboard** detection and announcements so players know when others are idle or away (unless they are vanished).
* Adds **Dynmap integration** to show achievement unlocks, player deaths, /me messages, and /say messages.

## Commands

This plugin adds aliases for the tell command so you can use any of the following to send a whisper:

* `/tell <player> <message>`
* `/t <player> <message>`
* `/whisper <player> <message>`
* `/w <player> <message>`

You can also easily reply to the last player you received a whisper from by using the reply command:

* `/reply <message>`
* `/r <message>`

Operators or users with the appropriate permission can set the message of the day:

* `/motd <message>`

## Permissions

* `fixchat.motd` Allows the user to set the message of the day.

## Configuration

At this point there is no configuration needed - just drop it into your plugins directory.

## Links

* Website: <http://codeski.com/#fixchat>
* Issues: <https://github.com/Codeski/FixChat/issues>
* Source: <https://github.com/Codeski/FixChat>
* Builds: <https://travis-ci.org/Codeski/FixChat>
* Bukkit: <http://dev.bukkit.org/bukkit-plugins/fixchat>
