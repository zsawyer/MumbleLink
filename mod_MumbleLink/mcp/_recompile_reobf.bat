@echo off
REM ****************************************************************************
REM     This script will copy the .java-file from your local copy of the 
REM         svn repository to your MCP source directory 
REM     Then it will attempt to recompile and reobfuscate. 
REM     In addition it will copy the result to your svn trunk again.
REM     It also copies the result to the minecraft directory
REM ****************************************************************************

REM !!! UPDATE THESE PATHS !!!
set svn_path=D:\mod_MumbleLink\SOURCES\trunk
set mcp_path=D:\mcp62

cd "%mcp_path%"


@echo on

copy "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\*.java" "%mcp_path%\src\minecraft\net\minecraft\src"
mkdir "%mcp_path%\src\minecraft\net\minecraft\src\mumblelink"
copy "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\mumblelink\*.java" "%mcp_path%\src\minecraft\net\minecraft\src\mumblelink"


@echo | call recompile.bat

@echo | call reobfuscate.bat



rem copy "%mcp_path%\src\minecraft\net\minecraft\src\*.java" "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\"
rem copy "%mcp_path%\src\minecraft\net\minecraft\src\mumblelink\*.java" "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\mumblelink"

copy "%mcp_path%\reobf\minecraft\*.class" "%svn_path%\mod_MumbleLink\mcp\reobf\minecraft"
mkdir "%svn_path%\mod_MumbleLink\mcp\reobf\minecraft\mumblelink"
copy "%mcp_path%\reobf\minecraft\mumblelink\*.class" "%svn_path%\mod_MumbleLink\mcp\reobf\minecraft\mumblelink"

copy "%mcp_path%\reobf\minecraft\*.class" "%appdata%\.minecraft\mods\MumbleLink"
mkdir "%appdata%\.minecraft\mods\MumbleLink\mumblelink"
copy "%mcp_path%\reobf\minecraft\mumblelink\*.class" "%appdata%\.minecraft\mods\MumbleLink\mumblelink"


pause