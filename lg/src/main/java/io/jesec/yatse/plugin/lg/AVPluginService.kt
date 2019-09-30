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

package io.jesec.yatse.plugin.lg

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast

import java.util.ArrayList

import tv.yatse.plugin.avreceiver.api.AVReceiverPluginService
import tv.yatse.plugin.avreceiver.api.PluginCustomCommand
import tv.yatse.plugin.avreceiver.api.YatseLogger
import io.jesec.yatse.plugin.lg.helpers.PreferencesHelper

/**
 * Sample AVReceiverPluginService that implement all functions with dummy code that displays Toast and logs to main Yatse log system.
 *
 *
 * See [AVReceiverPluginService] for documentation on all functions
 */
class AVPluginService : AVReceiverPluginService() {
    private val handler = Handler(Looper.getMainLooper())

    private var mHostUniqueId: String? = null
    private var mHostName: String? = null
    private var mHostIp: String? = null

    private var mIsMuted = false
    private var mVolumePercent = 50.0

    override fun onCreate() {
        super.onCreate()
    }

    override fun getVolumeUnitType(): Int {
        return AVReceiverPluginService.UNIT_TYPE_PERCENT
    }

    override fun getVolumeMinimalValue(): Double {
        return 0.0
    }

    override fun getVolumeMaximalValue(): Double {
        return 100.0
    }

    override fun setMuteStatus(status: Boolean): Boolean {
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Setting mute status : %s", status)
        displayToast("Setting mute status : $status")
        mIsMuted = status
        return true
    }

    override fun getMuteStatus(): Boolean {
        return mIsMuted
    }

    override fun toggleMuteStatus(): Boolean {
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Toggling mute status")
        displayToast("Toggling mute status")
        mIsMuted = !mIsMuted
        return true
    }

    override fun setVolumeLevel(volume: Double): Boolean {
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Setting volume level : %s", volume)
        displayToast("Setting volume : $volume")
        mVolumePercent = volume
        return true
    }

    override fun getVolumeLevel(): Double {
        return mVolumePercent
    }

    override fun volumePlus(): Boolean {
        mVolumePercent = Math.min(100.0, mVolumePercent + 5)
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Calling volume plus")
        displayToast("Volume plus")
        return true
    }

    override fun volumeMinus(): Boolean {
        mVolumePercent = Math.max(0.0, mVolumePercent - 5)
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Calling volume minus")
        displayToast("Volume minus")
        return true
    }

    override fun refresh(): Boolean {
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Refreshing values from receiver")
        return true
    }

    override fun getDefaultCustomCommands(): List<PluginCustomCommand> {
        val source = getString(R.string.plugin_unique_id)
        val commands = ArrayList<PluginCustomCommand>()
        // Plugin custom commands must set the source parameter to their plugin unique Id !
        commands.add(PluginCustomCommand().title("Sample command 1").source(source).param1("Sample command 1").type(0))
        commands.add(PluginCustomCommand().title("Sample command 2").source(source).param1("Sample command 2").type(1).readOnly(true))
        return commands
    }

    override fun executeCustomCommand(customCommand: PluginCustomCommand): Boolean {
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Executing CustomCommand : %s", customCommand.title())
        displayToast(customCommand.param1())
        return false
    }

    private fun displayToast(message: String) {
        handler.post { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }

    override fun connectToHost(uniqueId: String?, name: String?, ip: String?) {
        mHostUniqueId = uniqueId
        mHostName = name
        mHostIp = ip
        val receiverIp = PreferencesHelper.getInstance(applicationContext)!!.hostIp(mHostUniqueId)
        if (TextUtils.isEmpty(receiverIp)) {
            YatseLogger.getInstance(applicationContext).logError(TAG, "No configuration for %s", name)
        }
        YatseLogger.getInstance(applicationContext).logVerbose(TAG, "Connected to : %s / %s ", name, mHostUniqueId)
    }

    override fun getSettingsVersion(): Long {
        return PreferencesHelper.getInstance(applicationContext)!!.settingsVersion()
    }

    override fun getSettings(): String {
        return PreferencesHelper.getInstance(applicationContext)!!.settingsAsJSON
    }

    override fun restoreSettings(settings: String, version: Long): Boolean {
        val result = PreferencesHelper.getInstance(applicationContext)!!.importSettingsFromJSON(settings, version)
        if (result) {
            connectToHost(mHostUniqueId, mHostName, mHostIp)
        }
        return result
    }

    companion object {
        private val TAG = "AVPluginService"
    }
}
