/*
 * Copyright 2015 Tolriq / Genimee.
 * Copyright 2019 Jesse Chan
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.jesec.yatse.plugin.lg.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import android.text.TextUtils

import org.json.JSONException
import org.json.JSONObject

import tv.yatse.plugin.avreceiver.api.YatseLogger

/**
 * Sample PreferencesHelper that shows an easy way to correctly support the API
 *
 *
 * - Support configuration per media center via the media center uniqueId<br></br>
 * - Support backup / restore of settings via Yatse<br></br>
 * - Integrate an automated settings versioning for easier integration<br></br>
 */
 class PreferencesHelper protected constructor(private val mContext:Context) {

private val mPreferences:SharedPreferences

 val settingsAsJSON:String
get() {
val settings = JSONObject()
try
{
for (entry in mPreferences.getAll().entries)
{
val `val` = entry.value
if (`val` == null)
{
settings.put(entry.key, null)
}
else
{
settings.put(entry.key, (entry.value).toString())
}
}
}
catch (e:JSONException) {
YatseLogger.getInstance(mContext).logError(TAG, "Error encoding settings", e)
}

return settings.toString()
}

init{
mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
}

 fun importSettingsFromJSON(settings:String, version:Long):Boolean {
try
{
val data = JSONObject(settings)
val keys = data.keys()

val mEditor = mPreferences.edit()
while (keys.hasNext())
{
val key = keys.next()
if (!TextUtils.equals(key, "settings_version"))
{
mEditor.putString(key, data.getString(key))
}
}
mEditor.apply()
settingsVersion(version)
}
catch (e:JSONException) {
YatseLogger.getInstance(mContext).logError(TAG, "Error decoding settings", e)
}

return true
}


 fun hostIp(hostUniqueId:String?):String? {
return mPreferences.getString("host_ip_$hostUniqueId", "")
}

 fun hostIp(hostUniqueId:String?, ip:String) {
if (!TextUtils.equals(hostIp(hostUniqueId), ip))
{
settingsVersion(settingsVersion() + 1)
}
val mEditor = mPreferences.edit()
mEditor.putString("host_ip_" + hostUniqueId, ip)
mEditor.apply()
}

 fun settingsVersion():Long {
return mPreferences.getLong("settings_version", 0)
}

 fun settingsVersion(settingsVersion:Long) {
val mEditor = mPreferences.edit()
mEditor.putLong("settings_version", settingsVersion)
mEditor.apply()
}

companion object {
@Volatile private var INSTANCE:PreferencesHelper? = null

private val TAG = "PreferencesHelper"

 fun getInstance(context:Context):PreferencesHelper? {
if (INSTANCE == null)
{
synchronized (PreferencesHelper::class.java) {
if (INSTANCE == null)
{
INSTANCE = PreferencesHelper(context)
}
}
}
return INSTANCE
}
}

}