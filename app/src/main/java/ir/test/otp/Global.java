package ir.test.otp;



/**
 * global variables are defined here
 **/
public class Global {

    public interface OnSmsReceived {
        public void onNewSms(String code);
    }

    public  static boolean isReadSms = false;
    public static OnSmsReceived smsDelegate;


}
