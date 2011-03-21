HOW TO:

## setup ##
1. get MCP from http://mcp.ocean-labs.de/index.php/MCP_Releases
2. follow the instructions on how to setup MCP: read the readme.txt and http://mcp.ocean-labs.de/index.php/MCP_Releases#Prerequisites
3. run decompile.bat
4. run enable_modloader.bat
5. copy contents of mcp29a from the repository into your extracted mcp29a-folder 
    confirm overrides: modloader.jar
                       client_obfuscation.txt
                       minecraft.rgs                       

## editing ##                    
6. modify "mcp29a\sources\minecraft\net\minecraft\src\mod_MumbleLink.java", this is the Minecraft Mod

## compiling ##
7. run recompile.bat
8. run reobf.bat
9. get the new mod_MumbleLink.class from "mcp29a\final_out\minecraft\mod_MumbleLink.class"

## installing ##
10. follow mod installation instructions: http://www.minecraftforum.net/viewtopic.php?f=1032&t=235800


!!!CAUTION!!!
If you are modifying the native methods be sure to create a new .h for JNI 
1. run: javah -jni mod_MumbleLink
2. update mod_MumbleLinkDLL project