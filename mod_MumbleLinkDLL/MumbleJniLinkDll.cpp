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

#include <jni.h>

#include <stdio.h>
#include <string.h>
#include <wchar.h>

/* probably needed for other platforms
#include <stdlib.h>
#include <sys/mman.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
 */
#include <windows.h>


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
    

    return;

}

void copyConvertWCharT(JNIEnv* env, wchar_t* target, jstring source) {

    // get the wchar_t representation
    const wchar_t * utf16_name = (wchar_t *)env->GetStringChars(source, NULL);
    // get the length of the string
    jsize size = env->GetStringLength(source);
    // copy to linked mem
    memcpy(target, utf16_name, ((size_t) size) * sizeof (jchar));
    // release unneeded jni representation
    env->ReleaseStringChars(source, (jchar*) utf16_name);

}

void copyConvertUC(JNIEnv* env, unsigned char* target, jstring source) {

    // get the wchar_t representation
    const unsigned char * utf16_name = (unsigned char*) env->GetStringChars(source, NULL);
    // get the length of the string
    jsize size = env->GetStringLength(source);
    // copy to linked mem
    memcpy(target, utf16_name, ((size_t) size) * sizeof (jchar));
    // release unneeded jni representation
    env->ReleaseStringChars(source, (jchar*) utf16_name);

}
