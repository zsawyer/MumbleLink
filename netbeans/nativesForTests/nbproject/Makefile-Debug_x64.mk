#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc.exe
CCC=x86_64-w64-mingw32-g++
CXX=x86_64-w64-mingw32-g++
FC=gfortran.exe
AS=as.exe

# Macros
CND_PLATFORM=Cygwin_4.x-Windows
CND_CONF=Debug_x64
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/1640021005/MumbleJniLinkDll.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-mno-cygwin
CXXFLAGS=-mno-cygwin

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=/cygdrive/C/cygwin/lib/gcc/x86_64-w64-mingw32/4.5.3/libstdc++.a /cygdrive/C/cygwin/lib/gcc/x86_64-w64-mingw32/4.5.3/libgcc_s.a

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libnativesForTests.dll

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libnativesForTests.dll: /cygdrive/C/cygwin/lib/gcc/x86_64-w64-mingw32/4.5.3/libstdc++.a

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libnativesForTests.dll: /cygdrive/C/cygwin/lib/gcc/x86_64-w64-mingw32/4.5.3/libgcc_s.a

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libnativesForTests.dll: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	x86_64-w64-mingw32-g++ -shared -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libnativesForTests.dll ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/1640021005/MumbleJniLinkDll.o: ../../natives/testVersion/MumbleJniLinkDll.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1640021005
	${RM} $@.d
	$(COMPILE.cc) -g -I/cygdrive/C/Program\ Files/Java/jdk1.7.0/include -I/cygdrive/C/Program\ Files/Java/jdk1.7.0/include/win32  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/1640021005/MumbleJniLinkDll.o ../../natives/testVersion/MumbleJniLinkDll.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libnativesForTests.dll

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
