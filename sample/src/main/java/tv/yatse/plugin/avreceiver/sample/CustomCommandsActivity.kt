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

import android.os.Bundle
import android.view.View
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import tv.yatse.plugin.avreceiver.api.AVReceiverCustomCommandsAppCompatActivity

class CustomCommandsActivity : AVReceiverCustomCommandsAppCompatActivity() {

    @BindView(R.id.custom_command_title) internal lateinit var mViewTitle: TextView
    @BindView(R.id.custom_command_param1) internal lateinit var mViewParam1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_commands)

        ButterKnife.bind(this)
        if (isEditing) {
            mViewTitle.text = pluginCustomCommand.title()
            mViewParam1.text = pluginCustomCommand.param1()
        }
    }

    @OnClick(R.id.btn_save, R.id.btn_cancel)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> cancelAndFinish()
            R.id.btn_save -> {
                // Custom command source field must always equals to plugin uniqueId !!
                pluginCustomCommand.source(getString(R.string.plugin_unique_id))
                pluginCustomCommand.title(mViewTitle.text.toString())
                pluginCustomCommand.param1(mViewParam1.text.toString())
                saveAndFinish()
            }
        }
    }

}
