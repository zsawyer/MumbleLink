#!/bin/bash

# 32 bit
rm -R build_linux32
mkdir build_linux32

g++  -o build_linux32/libmod_MumbleLink.so -shared \
     -Wl,-soname,build_linux32/libmod_MumbleLink.so  \
     -I/usr/lib/jvm/java-6-sun-1.6.0.24/include/\
     -I/usr/lib/jvm/java-6-sun-1.6.0.24/include/linux\
     -lrt\
     MumbleJniLinkDll.cpp\
       -static -lstdc++

# 64 bit
#rm -R build_linux64
#mkdir build_linux64
#
#g++  -o build_linux64/libmod_MumbleLink_x64.so -shared \
#     -Wl,-soname,build_linux64/libmod_MumbleLink_x64.so  \
#     -I/usr/lib/jvm/java-6-sun-1.6.0.24/include/\
#     -I/usr/lib/jvm/java-6-sun-1.6.0.24/include/linux\
#     -lrt\
#     MumbleJniLinkDll.cpp\
#       -static -lstdc++ \
#       -m64 
