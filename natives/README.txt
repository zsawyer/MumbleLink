HOW TO:

### Windows ###

# setup #
1. install Windows SDK: http://msdn.microsoft.com/en-us/windows/bb980924
2. install Visual C++ 2010 Express: http://www.microsoft.com/express/Downloads/#2010-Visual-CPP

# editing #
3. do not modify "mod_MumbleLink.h" only replace it by a newly generated one (see javah) (may add GPL notice)
4. do not modify "linkedMem.h" unless you want to use a different mumble-Link-plugin other than 1.2.0
5. do edit MumbleJniLinkDll.cpp and MumbleJniLinkDll.h

# compiling #
6. update make.bat to represent the SKD and VC installation directories
7. run make.bat 
8. check the output if additional libraries (other than KERNEL32.DLL) are required 
9. include required DLLs

# installing #
10. follow mod installation instructions: http://www.minecraftforum.net/viewtopic.php?f=1032&t=235800



### Linux ###

# setup #
1. install sun version of jdk: 
    a) $ sudo add-apt-repository "deb http://archive.canonical.com/ lucid partner"
    b) $ sudo apt-get update
    c) $ sudo apt-get install sun-java6-jdk
    d) required for compiling 64-bit library on 32-bit system
        $ sudo ln -s /usr/lib64/libstdc++.so.6 /usr/lib64/libstdc++.so
    

# editing #
2. do not modify "mod_MumbleLink.h" only replace it by a newly generated one (see javah) (may add GPL notice)
3. do not modify "linkedMem.h" unless you want to use a different mumble-Link-plugin other than 1.2.0
4. do edit MumbleJniLinkDll.cpp and MumbleJniLinkDll.h

# compiling #
5. run make.sh

# installing #
10. follow mod installation instructions: http://www.minecraftforum.net/viewtopic.php?f=1032&t=235800



### MAC OSX ###

# setup #
1. probably requires the Apple SDK
2. install g++ compiler

# editing #
2. do not modify "mod_MumbleLink.h" only replace it by a newly generated one (see javah) (may add GPL notice)
3. do not modify "linkedMem.h" unless you want to use a different mumble-Link-plugin other than 1.2.0
4. do edit MumbleJniLinkDll.cpp and MumbleJniLinkDll.h

# compiling #
5. run make_osx.sh

# installing #
10. follow mod installation instructions: http://www.minecraftforum.net/viewtopic.php?f=1032&t=235800