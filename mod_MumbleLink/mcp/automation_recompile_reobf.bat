@echo off
REM ****************************************************************************
REM     This script will copy the .java-file from your local copy of the
REM         svn repository to your MCP source directory
REM     Then it will attempt to recompile and reobfuscate.
REM     In addition it will copy the result to your svn trunk again.
REM     It also copies the result to the minecraft directory
REM ****************************************************************************

set /P check="Minecraft updated? Modloader installed? Eclipse closed? [y/n] "
IF /I %check% NEQ y exit

REM !!! UPDATE THESE PATHS !!!
:svnPathPoint
set svn_path=D:\mod_MumbleLink\SOURCES\trunk
set /P svn_path_query="SVN-Path [%svn_path%]: "
IF /I "%svn_path_query%" NEQ "" (
    set svn_path=%svn_path_query%
)
IF NOT EXIST "%svn_path%" (
    @echo Error: SVN-Path is invalid!
    goto svnPathPoint
)


set mcp_path=D:\mcp725
:mcpPathPoint
set /P mcp_path_query="MCP-Path [%mcp_path%]: "
IF /I "%mcp_path_query%" NEQ "" (
    set mcp_path=%mcp_path_query%
)
IF NOT EXIST "%mcp_path%" (
    @echo Error: MCP-Path is invalid!
    goto mcpPathPoint
)



REM !!! script should NOT need changed below here - double check what you change here


cd "%mcp_path%"

@echo on

REM   ###  generate exclude file
echo \.svn\ > excludes.txt


REM get minecraft libraries required for MCP
rd /S /Q "%mcp_path%\jars\bin"
xcopy "%appdata%\.minecraft\bin\*.*" "%mcp_path%\jars\bin" /E /I /H /EXCLUDE:excludes.txt

REM get minecraft libraries required for MCP
rd /S /Q "%mcp_path%\jars\resources"
xcopy "%appdata%\.minecraft\resources\*.*" "%mcp_path%\jars\resources" /E /I /H /EXCLUDE:excludes.txt


@echo | call decompile.bat

REM import mod into MCP src folder
xcopy "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src\*.*" "%mcp_path%\src\minecraft\net\minecraft\src" /E /I /H /EXCLUDE:excludes.txt

@echo | call recompile.bat

@echo | call reobfuscate.bat


REM update svn with modified files from MCP src folder
REM xcopy "%mcp_path%\src\minecraft\net\minecraft\src\*.*" "%svn_path%\mod_MumbleLink\mcp\src\minecraft\net\minecraft\src" /E /I /H /EXCLUDE:excludes.txt

REM update svn with reobfed files from MCP folder
xcopy "%mcp_path%\reobf\minecraft\*.*" "%svn_path%\mod_MumbleLink\mcp\reobf\minecraft" /E /I /H /EXCLUDE:excludes.txt

REM update minecraft installation with reobfed files from MCP folder
xcopy "%mcp_path%\reobf\minecraft\*.*" "%appdata%\.minecraft\mods\MumbleLink" /E /I /H /EXCLUDE:excludes.txt


REM   ###  cleanup exclude file
del /F excludes.txt

pause