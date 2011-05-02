@echo off
REM ****************************************************************************
REM     This script will copy the .java-file from your local copy of the 
REM         svn repository to your MCP source directory 
REM     Then it will attempt to recompile and reobfuscate. 
REM     In addition it will copy the result to your svn trunk again.
REM ****************************************************************************

REM !!! UPDATE THESE PATHS !!!
set svn_path=C:\modmumblelink\SOURCES\trunk
set mcp_path=C:\mcp31




@echo on

copy "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\*.java" "%mcp_path%\src\minecraft\net\minecraft\src"


call recompile.bat

call reobfuscate.bat



copy "%mcp_path%\reobf\minecraft\mod_*.*" "%svn_path%\mod_MumbleLink\mcp\reobf\minecraft"


pause