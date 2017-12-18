# Multi-room audio with smarthings and mpd

## Synopsis
High quality audio radio in each room with smart things integration

## Prolog
I recently renovated my flat, and wanted to hear music in every room using better quality audio.
To generate quality audio I decided to use in-wall 8" woofers, and a 35W amplifier.
I found the IQaudio project that adds a DAC and amplifier to a raspberrypi.
This bundle creates good sounding audio, but then comes the control issue.
The raspberrypi connects to the home network via wireless or Ethernet.
Basically I use MPD to play music on the raspberrypi, so I looked for a web interface to MPD.
I found Rompr does this, so I can open a browser on my smart phone and control the music in each room.
But I also wanted smartthings, to start/stop music for the rooms I am in.
So I wrote this device handler for smartthings.

## Todo
I will add a deb file to install all the required packages needed to have this working on the custom raspberry pi distro.

