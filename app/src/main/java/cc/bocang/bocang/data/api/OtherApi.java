package cc.bocang.bocang.data.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.bocang.bocang.global.Constant;

/**
 * Created by thinkpad on 2016/9/2.
 */
public class OtherApi {
    public static String getAppVersion() {
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        String returnCode = "-1";// 默认-1表示提交失败
        try {
            URL url = new URL(Constant.VERSION_URL);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000); // 连接的超时时间
            conn.setReadTimeout(30000); // 读数据的超时时间
            conn.setDoOutput(true); // 必须设置此方法, 允许输出
            conn.setRequestProperty("Content-Type", "application/text"); // 设置请求头消息,可以设置多个
            conn.connect();

            int responseCode = conn.getResponseCode();
            returnCode = responseCode + "";
            if (responseCode == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                returnCode = baos.toString(); // 把流中的数据转换成字符串, 采用的编码是: utf-8
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos)
                    baos.close();
                if (null != is)
                    is.close();
                if (null != conn)
                    conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnCode;
    }


    public static String getAppGoodsClass() {
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        String returnCode = "-1";// 默认-1表示提交失败
        try {
            URL url = new URL(Constant.GOODSCLASS);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000); // 连接的超时时间
            conn.setReadTimeout(30000); // 读数据的超时时间
            conn.setDoOutput(true); // 必须设置此方法, 允许输出
            conn.setRequestProperty("Content-Type", "application/text"); // 设置请求头消息,可以设置多个
            conn.connect();

            int responseCode = conn.getResponseCode();
            returnCode = responseCode + "";
            if (responseCode == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                returnCode = baos.toString(); // 把流中的数据转换成字符串, 采用的编码是: utf-8
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos)
                    baos.close();
                if (null != is)
                    is.close();
                if (null != conn)
                    conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnCode;
    }


    /**
     * GET的网络请求
     * @param urlPath
     * @return
     */
    public static String doGet(String urlPath){
        String result="";
        BufferedReader reader=null;
        HttpURLConnection conn=null;
        try{
            URL url=new URL(urlPath);
            conn= (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000); // 连接的超时时间
            conn.setReadTimeout(30000); // 读数据的超时时间
            conn.setDoOutput(true); // 必须设置此方法, 允许输出
            conn.setRequestProperty("Content-Type", "application/text"); // 设置请求头消息,可以设置多个
            conn.connect();

            int code = conn.getResponseCode();
            if(code==200){
                reader=new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));
                String line="";
                while((line=reader.readLine())!=null) {
                    result+=line;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                conn.disconnect();
            }
        }

        return result;
    }

}
