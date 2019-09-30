/*
 * Copyright 2015 Tolriq / Genimee.
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

package tv.yatse.plugin.avreceiver.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import tv.yatse.plugin.avreceiver.api.AVReceiverPluginService
import tv.yatse.plugin.avreceiver.api.YatseLogger
import tv.yatse.plugin.avreceiver.sample.helpers.PreferencesHelper

/**
 * Sample SettingsActivity that handle correctly the parameters passed by Yatse.
 *
 *
 * You need to save the passed extra [AVReceiverPluginService.EXTRA_STRING_MEDIA_CENTER_UNIQUE_ID]
 * and return it in the result intent.
 *
 *
 * **Production plugin should make input validation and tests before accepting the user input and returning RESULT_OK.**
 */
class SettingsActivity : AppCompatActivity() {

    private var mMediaCenterUniqueId: String? = null
    private var mMediaCenterName: String? = null
    private var mMuted: Boolean = false

    @BindView(R.id.receiver_settings_title) internal lateinit var mViewSettingsTitle: TextView
    @BindView(R.id.receiver_ip) internal lateinit var mViewReceiverIP: EditText
    @BindView(R.id.btn_toggle_mute) internal lateinit var mViewMute: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)
        if (intent != null) {
            mMediaCenterUniqueId = intent.getStringExtra(AVReceiverPluginService.EXTRA_STRING_MEDIA_CENTER_UNIQUE_ID)
            mMediaCenterName = intent.getStringExtra(AVReceiverPluginService.EXTRA_STRING_MEDIA_CENTER_NAME)
        }
        if (TextUtils.isEmpty(mMediaCenterUniqueId)) {
            YatseLogger.getInstance(applicationContext).logError(TAG, "Error : No media center unique id sent")
            Snackbar.make(findViewById(R.id.receiver_settings_content), "Wrong data sent by Yatse !", Snackbar.LENGTH_LONG).show()
        }
        mViewSettingsTitle.text = getString(R.string.sample_plugin_settings) + " " + mMediaCenterName
        mViewReceiverIP.setText(PreferencesHelper.getInstance(applicationContext)!!.hostIp(mMediaCenterUniqueId))
    }

    @OnClick(R.id.btn_ok, R.id.btn_cancel, R.id.btn_vol_down, R.id.btn_toggle_mute, R.id.btn_vol_up)
    fun onClick(v: View) {
        val resultIntent: Intent
        when (v.id) {
            R.id.btn_toggle_mute -> {
                mViewMute.setImageResource(if (!mMuted) R.drawable.ic_volume_low else R.drawable.ic_volume_off)
                mMuted = !mMuted
                Snackbar.make(findViewById(R.id.receiver_settings_content), "Toggling mute", Snackbar.LENGTH_LONG).show()
            }
            R.id.btn_vol_down -> Snackbar.make(findViewById(R.id.receiver_settings_content), "Volume down", Snackbar.LENGTH_LONG).show()
            R.id.btn_vol_up -> Snackbar.make(findViewById(R.id.receiver_settings_content), "Volume up", Snackbar.LENGTH_LONG).show()
            R.id.btn_ok -> {
                PreferencesHelper.getInstance(applicationContext)!!.hostIp(mMediaCenterUniqueId, mViewReceiverIP.text.toString())
                resultIntent = Intent()
                resultIntent.putExtra(AVReceiverPluginService.EXTRA_STRING_MEDIA_CENTER_UNIQUE_ID, mMediaCenterUniqueId)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            R.id.btn_cancel -> {
                resultIntent = Intent()
                resultIntent.putExtra(AVReceiverPluginService.EXTRA_STRING_MEDIA_CENTER_UNIQUE_ID, mMediaCenterUniqueId)
                setResult(Activity.RESULT_CANCELED, resultIntent)
                finish()
            }
            else -> {
            }
        }
    }

    companion object {

        private val TAG = "SettingsActivity"
    }

}
