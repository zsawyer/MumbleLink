/*
    mod_Mumble - Positional Audio Communication for Minecraft with Mumble
    Copyright (C) 2011 zsawyer (http://sourceforge.net/users/zsawyer)

    This file is part of mod_MumbleLink
        (http://sourceforge.net/projects/modmumblelink/).

    mod_MumbleLink is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mod_MumbleLink is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with mod_MumbleLink.  If not, see <http://www.gnu.org/licenses/>.

 */
 
/**
 * defines the exported functions for the DLL application.
 *
 * @author zsawyer, 2010-03-20
 */

 #ifdef _WIN32
 #define WIN32
 #endif
 
#include <jni.h>

#include <stdio.h>
#include <string.h>
#include <wchar.h>

#include <locale> // std::use_facet, std::ctype

#ifndef WIN32

    #include <stdint.h> // uint32_t
    #include <sys/mman.h> // shm_open, PROT_READ, PROT_WRITE, MAP_SHARED mmap
    #include <unistd.h> // getuid
    #include <sys/stat.h> // S_IRUSR, S_IWUSR
    #include <fcntl.h> // O_RDWR
 
#else
    #include <windows.h>
#endif


#include "mod_MumbleLink.h"
#include "linkedMem.h"
#include "MumbleJniLinkDll.h"

JNIEXPORT void JNICALL
Java_mod_1MumbleLink_initMumble(JNIEnv* env, jobject obj) {

#ifdef WIN32
    HANDLE hMapObject = OpenFileMappingW(FILE_MAP_ALL_ACCESS, FALSE, L"MumbleLink");
    if (hMapObject == NULL)
        return;

    lm = (LinkedMem *) MapViewOfFile(hMapObject, FILE_MAP_ALL_ACCESS, 0, 0, sizeof (LinkedMem));
    if (lm == NULL) {
        CloseHandle(hMapObject);
        hMapObject = NULL;
        return;
    }
#else
    char memname[256];
    snprintf(memname, 256, "/MumbleLink.%d", getuid());

    int shmfd = shm_open(memname, O_RDWR, S_IRUSR | S_IWUSR);

    if (shmfd < 0) {
        return;
    }

    lm = (LinkedMem *) (mmap(NULL, sizeof (struct LinkedMem), PROT_READ | PROT_WRITE, MAP_SHARED, shmfd, 0));

    if (lm == (void *) (-1)) {
        lm = NULL;
        return;
    }
#endif

}

JNIEXPORT void JNICALL
Java_mod_1MumbleLink_updateLinkedMumble(JNIEnv* env, jobject,
        jfloatArray p_fAvatarPosition, jfloatArray p_fAvatarFront,
        jfloatArray p_fAvatarTop, jstring p_name, jstring p_description, jfloatArray p_fCameraPosition,
        jfloatArray p_fCameraFront, jfloatArray p_fCameraTop, jstring p_identity,
        jstring p_context) {


    if (!lm)
        return;

    if (lm->uiVersion != 2) {

        
        copyConvertWCharT(env, (lm->name), p_name);

        copyConvertWCharT(env, (lm->description), p_description);
         
        lm->uiVersion = 2;
    }
    lm->uiTick++;


    (env)->GetFloatArrayRegion(p_fAvatarFront, 0, 3, lm->fAvatarFront);

    (env)->GetFloatArrayRegion(p_fAvatarTop, 0, 3, lm->fAvatarTop);

    (env)->GetFloatArrayRegion(p_fAvatarPosition, 0, 3, lm->fAvatarPosition);

    (env)->GetFloatArrayRegion(p_fCameraPosition, 0, 3, lm->fCameraPosition);

    (env)->GetFloatArrayRegion(p_fCameraFront, 0, 3, lm->fCameraFront);

    (env)->GetFloatArrayRegion(p_fCameraTop, 0, 3, lm->fCameraTop);


    
    copyConvertWCharT(env, (lm->identity), p_identity);

    lm->context_len = (size_t) env->GetStringLength(p_context);
    copyConvertUC(env, (lm->context), p_context);

    // DEBUG: values from linked memory
    //printf("\nUpdate: \nname: %ls\ndescription: %ls\nidentity: %ls\ncontext: %s\n", lm->name, lm->description, lm->identity, lm->context);
    //printf("fCameraFront: %f, %f, %f\n", lm->fCameraFront[0], lm->fCameraFront[1], lm->fCameraFront[2]);
 

    return;

}

void copyConvertWCharT(JNIEnv* env, wchar_t* target, jstring source) {

    // convert jni to primitive datatype
    const char *str = env->GetStringUTFChars(source, NULL);

    // convert const char* to wchar_t*
    std::use_facet< std::ctype<wchar_t> >(std::locale()).widen(str,str+strlen(str),target); 

    // release unneeded jni representation
    env->ReleaseStringUTFChars(source, (char*) str);
}

void copyConvertUC(JNIEnv* env, unsigned char* target, jstring source) {

    // get the string as c primitives
    const char * utf16_name_cc = env->GetStringUTFChars(source, NULL);
    // get the length of the string
    size_t size = strlen(utf16_name_cc);
    
    // get the wchar_t representation
    const unsigned char * utf16_name_cuc = (unsigned char*) utf16_name_cc;
    
    // copy to linked mem
    memcpy(target, utf16_name_cuc, (size) * (sizeof(const char)) );

    // release unneeded jni representation
    env->ReleaseStringUTFChars(source, utf16_name_cc);

}
