#!/usr/bin/env bash

FPS=10
OUTPUT="../output.mp4"

INPUT_PATTERN="%08d.png"

cd gif

ffmpeg -framerate $FPS -i $INPUT_PATTERN -vcodec libx264 "$OUTPUT" -vf "pad=ceil(iw/2)*2:ceil(ih/2)*2"

# height not divisible by 2 error:
# https://stackoverflow.com/questions/20847674/ffmpeg-libx264-height-not-divisible-by-2
