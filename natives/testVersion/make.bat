echo Run this from the visual c++ commandline

mkdir build_WINx32
mkdir build_WINx64


cd build_WINx32

   REM # 32-Bit version: #
REM ### set the path:   ###
set LIB=C:\Programme\Microsoft Visual Studio 10.0\VC\LIB;C:\Programme\Microsoft SDKs\Windows\v7.1\Lib;
REM ### compile: 		###
cl.exe /l "C:\Programme\Microsoft SDKs\Windows\v7.1\Lib\uuid.lib" /EHcs -o mod_MumbleLink.dll ..\MumbleJniLinkDll.cpp /I "C:\Programme\Microsoft SDKs\Windows\v7.1\Include" /I "C:\Programme\Microsoft Visual Studio 8\VC\include" /I C:\Programme\Java\jdk1.6.0_24\include /I C:\Programme\Java\jdk1.6.0_24\include\win32 /link /DLL


cd ..\build_WINx64

  REM # 64-Bit version: #
REM ### set the path:   ###
set LIB=C:\Programme\Microsoft Visual Studio 10.0\VC\lib\amd64;C:\Programme\Microsoft SDKs\Windows\v7.1\Lib\x64
REM ### compile: 		###
"C:\Programme\Microsoft Visual Studio 10.0\VC\bin\x86_amd64\cl.exe" /l "C:\Programme\Microsoft SDKs\Windows\v7.1\Lib\x64\Uuid.lib" /EHcs -o mod_MumbleLink_x64.dll ..\MumbleJniLinkDll.cpp /I "C:\Programme\Microsoft SDKs\Windows\v7.1\Include" /I "C:\Programme\Microsoft Visual Studio 10.0\VC\include" /I C:\Programme\Java\jdk1.6.0_24\include /I C:\Programme\Java\jdk1.6.0_24\include\win32 /link /DLL


cd ..

   REM # Check the dependencies (if new LIBs need to be included): #
REM ### 32-Bit version: ###
"C:\Programme\Microsoft Visual Studio 10.0\VC\bin\x86_amd64\dumpbin.exe" /IMPORTS "build_WINx32\mod_MumbleLink.dll"
REM ### 64-Bit version: ###
"C:\Programme\Microsoft Visual Studio 10.0\VC\bin\x86_amd64\dumpbin.exe" /IMPORTS "build_WINx64\mod_MumbleLink_x64.dll"





































