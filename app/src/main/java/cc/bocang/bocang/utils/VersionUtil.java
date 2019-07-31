package cc.bocang.bocang.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by thinkpad on 2016/9/2.
 */
public class VersionUtil {
    /**
     * 判断需不需要更新app
     *
     * @param versionLocal
     *            app本地版本名称 0.0.0
     * @param versionServer
     *            app服务器存的版本 0.0.0
     * @return true 需要更新；false 不需要更新
     */
    public static boolean isNeedUpdate(String versionLocal, String versionServer)
    {
        if(TextUtils.isEmpty(versionServer)){
            return  false;
        }
        String[] localDigits = versionLocal.split("\\.");
        String[] serverDigits = versionServer.split("\\.");

        int firstLocalDigit = 0;
        int secondLocalDigit = 0;
        int thirdLocalDigit = 0;
        int firstServerDigit = 0;
        int secondServerDigit = 0;
        int thirdServerDigit = 0;
        for (int i = 0; i < localDigits.length; i++)
        {
            if (i == 0)
            {
                firstLocalDigit = Integer.parseInt(localDigits[0]);
            } else if (i == 1)
            {
                secondLocalDigit = Integer.parseInt(localDigits[1]);
            } else if (i == 2)
            {
                thirdLocalDigit = Integer.parseInt(localDigits[2]);
            }
        }

        for (int j = 0; j < serverDigits.length; j++)
        {
            if (j == 0)
            {

                firstServerDigit = Integer.parseInt(serverDigits[0]);
                Log.v("520","firstServerDigit"+firstServerDigit);
            } else if (j == 1)
            {
                secondServerDigit = Integer.parseInt(serverDigits[1]);
            } else if (j == 2)
            {
                thirdServerDigit = Integer.parseInt(serverDigits[2]);
            }
        }

        if (firstLocalDigit < firstServerDigit)
            return true;
        else if (firstLocalDigit == firstServerDigit
                && secondLocalDigit < secondServerDigit)
            return true;
        else if (firstLocalDigit == firstServerDigit
                && secondLocalDigit == secondServerDigit
                && thirdLocalDigit < thirdServerDigit)
            return true;

        return false;
    }
}
