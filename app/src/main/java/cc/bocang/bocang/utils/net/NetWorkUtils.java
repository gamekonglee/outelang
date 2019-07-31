package cc.bocang.bocang.utils.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * @author Jun
 * @time 2016/9/1  18:40
 * @desc （原生） 网络请求
 */
public class NetWorkUtils {
    /**
     * GET的网络请求
     *
     * @param urlPath
     * @return
     */
    public static String doGet(String urlPath) {
        String result = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200) {
                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    /**
     * POST的网络请求
     *
     * @param urlPath
     * @param paramsMap
     * @return
     */
    public static String doPost(String urlPath, Map<String, String> paramsMap) {
        String result = "";
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            String parames = "";
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                parames += ("&" + entry.getKey() + "=" + entry.getValue());
            }
            conn.getOutputStream().write(parames.substring(1).getBytes());
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    private static int TIME_OUT = 10 * 1000;   //超时时间
    private static String CHARSET = "utf-8"; //设置编码


}
