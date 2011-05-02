
copy "C:\modmumblelink\SOURCES\trunk\mod_MumbleLink\mcp\sources\minecraft\net\minecraft\src\*.java" "C:\mcp31\src\minecraft\net\minecraft\src"


call recompile.bat

call reobfuscate.bat



copy "C:\mcp31\reobf\minecraft\mod_*.*" "C:\modmumblelink\SOURCES\trunk\mod_MumbleLink\mcp\final_out\minecraft"


pause