ReadMe for MumbleLink ${version}
at http://www.minecraftforum.net/viewtopic.php?f=1032&t=235800
by zsawyer, ${date}



About:

This is a Minecraft mod based on "Minecraft Forge". It's purpose is to be able to use
Minecraft in conjunction with Mumble's positional audio feature.
(http://mumble.sourceforge.net/)
This means: Directional and positionally attenuated VOIP in relation to
the game world.
It uses the Mumble Link Plugin v1.2.0 and adds native mumble support.
Tested with Mumble 1.2.2 - 1.2.4 and PR_Mumble1.0 .
It also uses JNA (https://github.com/twall/jna).
There is an interface that other mods can use to inject their custom Mumble
context and/or identity.



Prerequisites:

- Minecraft ${mcversion}+
- Minecraft Forge installed (http://www.minecraftforge.net/wiki/)
- Mumble + advanced option "Link to Game and Transmit Position" activated
     (Configure -> Settings -> Tick "Advanced" -> "Plugins"
        -> Tick "Link to Game and Transmit Position")



Installing the Mod:

1. Close both Mumble and Minecraft.
2. install Minecraft Forge
3. Put the MumbleLink.jar into your mods folder (DO NOT EXTRACT THE JAR!)
      examples:
        Windows: %Appdata%\\.minecraft\\mods\\MumbleLink.jar
        Linux: ~/.minecraft/mods/MumbleLink.jar
4. setup Mumble:
    1. On Mumble's main screen select Configure -> Settings
    2. In Mumble Configuration dialog enable "Advanced" (bottom left)
    3. In Plugins: (see http://i.imgur.com/8K59Rlw.png)
        1. Options section: enable "Link to Game and Transmit Position",
                            if this was disabled, restart after setting this option
        2. in Plugins section: find and enable "Link v1.2.0"
    4. In Audio Output: (see http://i.imgur.com/c5uiVxg.png)
        1. in Positional Audio section: enable Positional Audio´
    5. optional advanced Mumble settings can be found below


Upgrading from earlier Versions:

1. Close both Mumble and Minecraft.
2. install latest Minecraft Forge
3. replace the old MumbleLink.jar with the new one





Advanced Mumble Configuration

Instead of hearing people from indefinitely far away you can set up mumble to
only hear them when they are close. In return you (and they!) will need to bind
an extra key to communicate with those far away (imagine it being a
"walkie-talkie" button).
To set this up you will need to do some special settings which differ from the
defaults, in return it might enhance your audio experience.

    1. In Audio Output: (see http://i.imgur.com/c6eRKYQ.png)
        1. Positional Audio section: Maximum Distance: 70m
            (this value will determine when others are too far away to be heard)
        2. Positional Audio section: Minimum Volume: 0%
            (this value makes you not hear people beyond Maximum Distance)
        3. Audio Output section: disable "Attenuate applications by... " "while other users talk" and "while you talk"
            (mumble would dampen ArmA's audio if enabled)

    2. In Shortcuts (see http://i.imgur.com/MxSu237.png)
        1. bind a key ("Shortcut") to "Shout/Wisper" "Shout to Channel" select "Current" Channel and make sure to check "Ignore positional audio"
            (this will enable you to speak to everyone no matter how far away they are or what their settings are)





Notes:

- suggested Mumble settings (differing from defaults):
-- advanced option "Link to Game and Transmit Position" activated
    (restart after setting this option)
-- Maximum Distance: 70m
-- Minimum Volume: 0%
-- untick "Attenuate applications by..." "while other users talk" and "while you talk"

- link delay: 10 seconds - normal link delay appears to be about 10 seconds after
    joining a minecraft server. This may vary depending on the PC. The confirmation
    that the plugin linked can however show before the effect kicks in.

- starting order: does not matter,
    Minecraft or Mumble - either can be started first



Troubleshooting:

- Please check the logs in your .minecraft-folder for an error message.
      for Forge: ForgeModLoader-client-0.log
-- messages from this mod will be indicated by "[MumbleLink]" infront
    of the message

- Errors will occur as "Minecraft has crashed!"  or a black window
-- This either indicates incompatibility to your operating system
-- or you are using an outdated version of the mod
-- check the error report or log files (s.a.) for details



FAQ:

- How do I know that I am linked/the mod works?
    After connecting to a Minecraft-Server (joining a game) Mumble's log will
    show a message "Minecraft linked." after a few seconds. Additionally
    within Minecraft you will see a chat message "Mumble linked." after you have
    joined any world.

- Mumble does not Link:
    - check the logs and check if MumbleLink (Forge)
        - you might not have Forge installed
        - note that Risugami's ModLoader is not supported anymore

- Minecraft crashes with:
    "Minecraft has crashed!
    ...
    java.lang.NoSuchFieldError: g
        at UpdateData.set(UpdateData.java:XXX)
    ..."
  OR
    "Minecraft has crashed!
    ...
    -- Head --
    Stacktrace:
        at UpdateData.set(UpdateData.java:XXX)
    ..."
        - You are using an incompatible mod version.
        - Get the appropriated one from https://sourceforge.net/projects/modmumblelink/files/
        - there might not be one yet if you just updated Minecraft


    "Minecraft has crashed!
    ...
    Minecraft has stopped running because it encountered a problem; Error in mod
    MumbleLink: Couldn't load library.
    ...
    java.lang.UnsatisfiedLinkError: ..."
        - None of the found libraries can be loaded on your system. Your operating
            system is not supported by this mod. Sorry.
            Please try to compile the required libraries yourself or submit a
            request. (If you successfully compiled them yourself please share :D)


- the log sais:
    "SEVERE: [MumbleLink][SEVERE] Required library could not be loaded,
            available libraries are incompatible!"
        - None of the found libraries can be loaded on your system. Your operating
            system is not supported by this mod. Sorry.
            Please try to compile the required libraries yourself or submit a
            request. (If you successfully compiled them yourself please share :D)


- Mumble sais "Minecraft linked." but I still don't hear people positionally.
    Multiple possibilities:
        1. Mumble is not setup correctly:
            Make sure "Link to Game and Transmit Position" is checked
                (Configure -> Settings -> Tick "Advanced" -> "Plugins"
                    -> Tick "Link to Game and Transmit Position")

        2. The others are not using this mod. Get them to install it! ;)

- Minecraft has no sound/it is really low now
    this is probably due to Mumble itself, it will lower other applications'
    volumes by default
    In Mumble advanced settings, within "Audio Output" untick both:
        "Attenuate applications by..." "while other users talk"
        and "while you talk"



Developing Addons for this Mod:

- Please also refer to changelog of v4.0.2 and the source code comments.

- The interface "MumbleLinkAPI" allows your addon to inject their custom Mumble
    context and/or identity. Use this interface to register your callback
    function handlers. You will have to implement the callback interfaces. It is
    suggested that your addon implements the "Activatable" interface to allow
    other (child) addons to disable your handlers and thus overriding/replacing
    them. This is neccessary since the context and identity fields have a very
    limited length.
    The ExtendedPASupport mod should serve as a sample implementation.
    Please note the javadoc of these files.

    For more information about context and identity consult mumble's doc:
        http://mumble.sourceforge.net/Link#Context




Tested Platforms:

- Windows 7 Home Premium 64-bit with Forge
    - Forge ${forgeversion} (MC ${mcversion})



Known Issues:

- Does not work with MicDoodleCore. Libraries will not be loading properly. 
    A workaround is described in the forum thread. 
- Does not work with PRMumble(0.5Beta) even after copying the link.dll (1.2.0)
    into PRMumble's plugin folder - it will say "linked" but the audio
    will not be positional (Deprecated since PR_Mumble1.0 is supported now :))
    


Change Log:

v4.1.1
- updated for Minecraft 1.7.10 (Forge 10.13.0.1180)

v4.1.0
- code structure rework to conform to new forge gradle build system

v4.0.5
- beta build
- updated for Minecraft 1.7.2 (+ MCP 9.01 + Forge 10.12.0.991)

v4.0.4
- fixed crash on OSX 64-Bit (UnsatisfiedLinkError)
- now using a single universal dylib for OSX (for a clean one-package-solution)

v4.0.3
- updated for Minecraft 1.6.4 (+ MCP 8.09 + Forge 9.11.0.897)
- fixed crash when running Minecraft with JRE6 (Java 1.6)
- unified 32-Bit and 64-Bit OSX version into one package
- updated to JNA 4.0.0

v4.0.2
- updated for Minecraft 1.6.2 (+ MCP 8.02 + Forge 9.10.0.777)
- IMPORTANT: dropped direct support for Risugami's ModLoader (will now only work with Forge)
- utilizing srg-reobfuscation; new builds are now (fairly) universal, this means that updates are now needed less frequently
- added API interface (MumbleLinkAPI) for addons to inject their own context and identity
    (this means that for example a faction-based mod can now tell mumble which faction
    the player is on, a custom MUMO can then do its job accordingly)
    - ExtendedPASupport.java is a sample implementation
- added an addon which injects identity and context as json data for extended positional audio support for vanilla minecraft
- context and identity now use JSON formatting
- fixed 3D positional audio bugs when looking straight up or down
- added config options to enable the Forge-based mod(s) and set their debugging mode

v3.0.4
- updated for Minecraft 1.6.1 (+ MCP 8.02 + Forge 8.9.0.762)

v3.0.3
- updated for Minecraft 1.5.2 (+ MCP7.51 + FML)

a3.0.2 (alpha)
- fixed native library not being loaded when using Risugami's ModLoader

a3.0.1 (alpha)
- fixed that Risugami's ModLoader would not recognize the mod_MumbleLink.class as a mod

a3.0.0 (alpha)
- now using JNA instead of JNI
- now directly supporting Minecraft Forge and (still) ModLoader
- does not support OSX 32 and 64 bit at the same time anymore (separate download will be needed)

v2.5.7
- updated for Minecraft 1.5.1 (+ MCP7.44 + ModLoader)

v2.5.6
- updated for Minecraft 1.5 (+ MCP7.42 + ModLoader)

v2.5.5
- updated for Minecraft 1.4.6 (+ MCP7.25 + ModLoader)
- compatible with Minecraft 1.4.7

v2.5.4
- updated for Minecraft 1.4.4 (+ MCP7.21 + ModLoader)
- compatible with Minecraft 1.4.5

v2.5.3
- updated for Minecraft 1.4.2 (+ MCP7.19 + ModLoader)

v2.5.2
- updated for Minecraft 1.3.2 (+ MCP7.2 + ModLoader (preliminary))

v2.5.1
- updated for Minecraft 1.3.1 (+ MCP7.0a + ModLoader)

v2.5
- complete reworking of the code (unreleased)
- using json to transport the mumble context
- extended use of settings file,
    - mod_MumbleLink.conf
    - a simple text file that you can put into your minecraft execution directory
    - syntax follows a key value pair seperated by colon (:) per line
- introduced new settings:
    - mumbleContext:[MinecraftAllTalk|world]
        - default: MinecraftAllTalk
        - specifies which group of players you belong to (which context you will
            use)
        - if the contexts of two players match then mumble will calculate
            positional audio
        - MinecraftAllTalk: a general, non-specific context saying "i am playing
            minecraft"
        - world: a world specific context, only players in the same world will
            hear each other positionally
    - libraryName:someFileName
        - default: mod_MumbleLink
        - you can specifiy to use a certain native library useful if there is a
            naming conflict
        - the mod will also try OSX, Windows and Unix as well as x64 specific
            filename variations if the file could not be found right away
    - libraryFolderPath:/some/folder/path/natives/
        - default: none
        - you can specify which directory should be searched for the required
            native library files
        - useful when running minecraft from a custom directory

v2.4.4
- updated for Minecraft 1.2.5 snapshot (+ MCP6.2 + ModLoader)

v2.4.3
- updated for Minecraft 1.2.4 (+ MCP6.1 + ModLoader)

v2.4.2
- updated for Minecraft 1.2.3 (+ MCP6.0 + ModLoader)

v2.4.1
- updated for Minecraft 1.1 (+ MCP5.6 + ModLoader)

v2.4
- updated for Minecraft 1.0.0 (+ MCP5.0 + ModLoader)
- added: chat notification when linked

v2.3.4
- update for MACOSX 64-Bit (added .dylib file)

v2.3.3
- update for MACOSX 32-Bit (added .dylib file)

v2.3.2
- recompiled with MCP3.1 + ModLoader

v2.3.1
- update for Linux 64-Bit, updated and recompiled linux libraries (.so-files)

v2.3
- using the mods-folder as supported by latest ModLoader

v2.2.2
- updated for Minecraft beta 1.5_01 (+ MCP2.12_test1 + ModLoader)

v2.2.1
- updated for Minecraft beta 1.4_01 (+ MCP2.11 + ModLoader)

v2.2
- updated for Minecraft beta 1.4 (+ MCP2.10 + ModLoader)
- fixed: starting order of Mumble vs Minecraft now does not matter
- added: optional feature: world dependent linking (for future use)

v2.1
- fixed some remnants of data corruption
- Hotfix for WinXP32 EXCEPTION_ACCESS_VIOLATION (and possibly on other
    systems as well)

v2.0
- fixed internal data corruption
- added support for Ubuntu 10.10 32-bit (and possibly other distros)

v1.1
- added 64-Bit Java support (new x64-bit DLL)
- optimized DLLs
    - now there is no need for the msvcr100.dll -> removed
- sources ready for release

v1.0
- initial release (no sources)




Additional:

This project is present at both SourceForge and GitHub. The main development 
    will be done using GitHub but the distribution is done through SourceForge.
TheSkorm has forked this Project (v2.3.3, r72).
    https://github.com/TheSkorm/mod_mumblelink




Acknowledgements:

Mumble Team     : Thanks for Mumble!
Mojang          : Thanks for Minecraft!
The MCP Team    : Thanks for Minecraft Coder Pack!
Forge Team      : Thanks for Forge!
AbrarSyed       : Thanks for ForgeGradle!

SilentWalker    : Thanks for testing on XP64, Ubuntu32 and other invaluable support.
talkingBEERmug  : Thanks for testing and decompiling the 128-bit encryption
Steeve          : Thanks for providing the Mumble-Server we tested on
xDownSetx, Zebra: Thanks for helping with testing
dalawrence      : Thanks for providing the source code of a similar project
                    (http://www.minecraftforum.net/viewtopic.php?f=1&t=41506)
                    which helped in optimizing the DLLs and compiling for
                    multiple platforms
theskorm        : Thanks for helping with compiling libraries for linux 64 bit
                    and providing libs for MACOSX (and testing) and helping with
                    recompiling/reobfuscating
davr            : Thanks for jumping in and helping with recompiling/reobfuscating
twall           : Thanks for JNA!
iSuchtel        : Thanks for helping with compiling the universal dylib for OSX
