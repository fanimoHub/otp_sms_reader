package ir.test.otp

import android.R.attr.label
import android.R.attr.text
import android.content.ClipData
import android.content.ClipboardManager
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import ir.test.otp.Global.OnSmsReceived
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val appSignatureHelper = AppSignatureHelper(this)
        val keys = appSignatureHelper.getAppSignatures()[0]
        Log.e("danisms", keys)
        findViewById<TextView>(R.id.tv1).text = "Use this at your sms: " + keys + " click to copy"
        findViewById<TextView>(R.id.tv1).setOnClickListener{
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("key", keys)
            clipboard.setPrimaryClip(clip)
        }


        Global.smsDelegate = null
        Global.smsDelegate = OnSmsReceived { code ->
            findViewById<TextView>(R.id.tv2).text = "sms code:  " + code
            // binding.otpView.otp = code
        }




        SmsRetriever.getClient(this).apply {
            startSmsRetriever().apply {
                addOnSuccessListener {
                    android.util.Log.e("dani","addOnSuccessListener")

                    val smsBroadCastReceiver = SMSBroadCastReceiver().apply {
                        setOnCodeReceiveListener { code ->
                            val p: Pattern = Pattern.compile("(|^)\\d{5}")
                            val m: Matcher = p.matcher(code)
                            if (m.find()) {
                                val smsCode = m.group(0)!!
                                if (!Global.isReadSms) {
                                    if (Global.smsDelegate != null)
                                        Global.smsDelegate!!.onNewSms(smsCode)
                                    Global.isReadSms = true
                                    Handler().postDelayed({
                                        Global.isReadSms = false
                                    }, 1000)
                                    //binding.otpView.otp = m.group(0)
                                }
                            }
                        }
                    }
                    val intentFilter = IntentFilter().apply {
                        addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        registerReceiver(smsBroadCastReceiver, intentFilter, RECEIVER_EXPORTED)
                        android.util.Log.e("dani","RECEIVER_NOT_EXPORTED")
                    } else {
                        registerReceiver(smsBroadCastReceiver, intentFilter)
                        android.util.Log.e("dani","intentFilter")
                    }

                }
                addOnFailureListener{
                    android.util.Log.e("dani",it.message.toString())
                }
            }
        }

    }


}