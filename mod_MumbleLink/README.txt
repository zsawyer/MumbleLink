HOW TO:

!!!CAUTION!!!
If you are modifying the native methods be sure to create a new .h for JNI 
1. run: javah -jni mod_MumbleLink
2. update mod_MumbleLinkDLL project



## setup ##
1. get MCP from http://mcp.ocean-labs.de/index.php/MCP_Releases
2. follow the instructions on how to setup MCP: read the readme.txt and 
    http://mcp.ocean-labs.de/index.php/MCP_Releases#Prerequisites, 
    http://mcp.ocean-labs.de/index.php/Making_ModLoader_mods_with_MCP
3. run decompile script
4. copy contents of mod_MumbleLink/mcp from the repository into your extracted 
    mcp-folder confirm merges

## editing ##                    
6. modify "mcp/src/minecraft/net/minecraft/src/mod_MumbleLink.java", this is the Minecraft Mod
(see below for instructions on how to set up NetBeans for easier editing)

## compiling ##
7. run "recompile" script
8. run "reobufscate" script
9. get the new mod_MumbleLink.class from "mcp/reobf/minecraft/mod_MumbleLink.class"

## installing ##
10. follow mod installation instructions: http://www.minecraftforum.net/viewtopic.php?f=1032&t=235800




## Using NetBeans (6.9.1) ##
1. create a new Java Application (no sources, do not generate any classes/files)
2. go to project properties and for "Source Package Folders" "Add Folder..." 
    "/mcp/src/minecraft" (source folder of your MCP installation)