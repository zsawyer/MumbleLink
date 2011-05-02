#!/bin/bash
# ******************************************************************************
#   This script will copy the .java-file from your local copy of the 
#       svn repository to your MCP source directory 
#   Then it will attempt to recompile and reobfuscate. 
#   In addition it will copy the result to your svn trunk again.
# ******************************************************************************


# !!! UPDATE THESE PATHS !!!
svn_path=~/Desktop/modmumblelink
mcp_path=~/Desktop/mcp31


cp $svn_path/SOURCES/trunk/mod_MumbleLink/mcp/src/minecraft/net/minecraft/src/*.java $mcp_path/src/minecraft/net/minecraft/src


./recompile.bat

./reobfuscate.bat



cp $mcp_path/reobf/minecraft/mod_*.* $svn_path/SOURCES/trunk/mod_MumbleLink/mcp/reobf/minecraft


read -p "Press any key to start backup..."
