echo Run this from the visual c++ commandline

REM @echo off

set visualStudioDir=D:\Program Files (x86)\Microsoft Visual Studio 10.0
set windowsSdkDir=D:\Program Files\Microsoft SDKs\Windows\v7.1
set jdkDir=C:\Program Files\Java\jdk1.7.0



mkdir build_WINx32
mkdir build_WINx64

cd build_WINx32

   REM # 32-Bit version: #
REM ### set the path:   ###
set LIB=%visualStudioDir%\VC\LIB;%windowsSdkDir%\Lib;
REM ### compile: 		###
cl.exe /l "%windowsSdkDir%\Lib\uuid.lib" /EHcs -o mod_MumbleLink.dll ..\MumbleJniLinkDll.cpp /I "%windowsSdkDir%\Include" /I "%jdkDir%\include" /I "%jdkDir%\include\win32" /link /DLL


cd ..\build_WINx64

  REM # 64-Bit version: #
REM ### set the path:   ###
set LIB=%visualStudioDir%\VC\lib\amd64;%windowsSdkDir%\Lib\x64
REM ### compile: 		###
"%visualStudioDir%\VC\bin\x86_amd64\cl.exe" /l "%windowsSdkDir%\Lib\x64\Uuid.lib" /EHcs -o mod_MumbleLink_x64.dll ..\MumbleJniLinkDll.cpp /I "%windowsSdkDir%\Include" /I "%visualStudioDir%\VC\include" /I "%jdkDir%\include" /I "%jdkDir%\include\win32" /link /DLL


cd ..

   REM # Check the dependencies (if new LIBs need to be included): #
REM ### 32-Bit version: ###
"%visualStudioDir%\VC\bin\x86_amd64\dumpbin.exe" /IMPORTS "build_WINx32\mod_MumbleLink.dll"
REM ### 64-Bit version: ###
"%visualStudioDir%\VC\bin\x86_amd64\dumpbin.exe" /IMPORTS "build_WINx64\mod_MumbleLink_x64.dll"





































