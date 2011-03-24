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
 * declares some helper functions for converting jstring
 *
 * @author zsawyer, 2010-03-20
 */

#ifndef MUMBLEJNILINKDLL_H
#define	MUMBLEJNILINKDLL_H

#ifdef	__cplusplus
extern "C" {
#endif

/**
 * convert and copy the source jstring to an exsisting wchar_t array
 *
 * @param env jni environment
 * @param target memory location
 * @param source jstring
 */
void copyConvertWCharT(JNIEnv* env, wchar_t* target, jstring source);

/**
 * convert and copy the source jstring to an exsisting wchar_t array
 *
 * @param env jni environment
 * @param target memory location
 * @param source jstring
 */
void copyConvertUC(JNIEnv* env, unsigned char* target, jstring source);


#ifdef	__cplusplus
}
#endif

#endif	/* MUMBLEJNILINKDLL_H */

