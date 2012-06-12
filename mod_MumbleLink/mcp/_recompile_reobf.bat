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

rem copy "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\*.java" "%mcp_path%\src\minecraft\net\minecraft\src"


@echo | call recompile.bat

@echo | call reobfuscate.bat



copy "%mcp_path%\src\minecraft\net\minecraft\src\mod_*.java" "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\"

copy "%mcp_path%\reobf\minecraft\mod_*.*" "%svn_path%\mod_MumbleLink\mcp\reobf\minecraft"

copy "%mcp_path%\reobf\minecraft\mod_*.*" "%appdata%\.minecraft\mods\MumbleLink"


pause