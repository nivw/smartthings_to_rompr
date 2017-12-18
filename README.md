# Multi-room audio with smarthings and mpd

## Synopsis
High quality audio radio in each room with smart things integration

## Hardware
To generate quality audio I decided to use in-wall 8" woofers, and a 35W amplifier.
I found the [IQaudio](http://iqaudio.co.uk/) project that adds a DAC and amplifier to a raspberrypi.
The raspberrypi connects to the home network via wireless or Ethernet.
As this is a raspberrypi running linux this allows many features.

## Features
1. Pulseaudio, so I can play music from my linux laptop to this raspberrypi over the home network.
2. UPNP renderer using gmrender-resurrect, so you can play music from your smartphone to the room.
3. Hear radio using internet streaming using MPD.
4. Device security.

## Software
This bundle creates good sounding audio, but then comes the control issue.
Basically I use MPD to play music on the raspberrypi, so I looked for a web interface to MPD.
I found [Rompr](https://sourceforge.net/projects/rompr/) does this, so I can open a browser on my smart phone and control the music in each room.

## Smarthings
Its great to have smartthings start music when I enter a room, and stops when I leave the room.
So I wrote this device handler for smartthings.

## Todo
I will add a deb file to install all the required packages needed to have this working on the custom raspberry pi distro.

