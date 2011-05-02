#!/bin/bash

# 32 bit
rm -R build_osx32
mkdir build_osx32

g++ -dynamiclib -Wl -headerpad_max_install_names -undefined dynamic_lookup \
    -compatibility_version 1.0 -current_version 1.0 \
    -install_name /usr/local/lib/libmod_MumbleLink.dylib \
    -o build_osx32/libmod_MumbleLink.dylib \
    -I/Developer/SDKs/MacOSX10.5.sdk/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/ \
    -m32 \
    MumbleJniLinkDll.cpp 


--------------------------------------------------------------------------------


# 64 bit
rm -R build_osx64
mkdir build_osx64

g++ -dynamiclib -Wl -headerpad_max_install_names -undefined dynamic_lookup \
    -compatibility_version 1.0 -current_version 1.0 \
    -install_name /usr/local/lib/libmod_MumbleLink.dylib \
    -o build_osx64/libmod_MumbleLink.dylib \
    -I/Developer/SDKs/MacOSX10.5.sdk/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/ \
    -m64 \
    MumbleJniLinkDll.cpp 


