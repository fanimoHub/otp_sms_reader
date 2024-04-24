package ir.test.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSBroadCastReceiver : BroadcastReceiver() {

    private var onReceiveCode: ((String) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            Log.e("dani", "onReceive")
            when (status.statusCode) {

                CommonStatusCodes.SUCCESS -> {
                    Log.e("dani", "SUCCESS")
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Log.e("dani", "message1" + message)
                    onReceiveCode?.let {
                        it(message.filter { it.isDigit() })
                    }
                }
            }
        }
    }

    fun setOnCodeReceiveListener(listener: ((String) -> Unit)? = null) {
        onReceiveCode = listener
    }
}