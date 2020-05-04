package com.pixiv.util;

import com.pixiv.image.image.PixivImageRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: http请求工具类   
 * Copyright: Copyright (c) 2017  
 * Company: BMAC
 * Contact: http://www.bmac.com
 *
 * @author: lhq  
 * @version: v1.0 2017年11月6日 下午4:51:14
 */
public class HttpRequestUtil {
	
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TOTAL = 90000;
    private static final int DEFAULT_MAX_PER_ROUTE = 50000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 50000;
    private final static int HTTP_CONNECT_TIMEOUT_SECOND = 500000;			// 连接超时
    private final static int HTTP_SOCKET_TIMEOUT_SECOND = 700000;				// Socket请求超时设置
//	private final static boolean STALE_CONNECTION_CHECK_ENABLED = ServiceConfig.Http.staleConnectionCheckEnabled;
    private final static String cookie = "first_visit_datetime_pc=2018-11-25+19%3A22%3A53; p_ab_id=4; p_ab_id_2=6; p_ab_d_id=1880913283; _ga=GA1.2.2137416718.1543141373; a_type=0; b_type=1; d_type=1; module_orders_mypage=%5B%7B%22name%22%3A%22sketch_live%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22tag_follow%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22recommended_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22everyone_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22following_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22mypixiv_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22spotlight%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22fanbox%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22featured_tags%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22contests%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22user_events%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22sensei_courses%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22booth_follow_items%22%2C%22visible%22%3Atrue%7D%5D; yuid_b=QoUFKAc; login_ever=yes; __utmv=235335808.|2=login%20ever=yes=1^3=plan=normal=1^5=gender=male=1^6=user_id=13902468=1^9=p_ab_id=4=1^10=p_ab_id_2=6=1^11=lang=zh=1; ki_r=; adr_id=sdk5P96LmfVgAPP5bgafWreDseNjR6knRc2EhcL5GCHUZV6U; ki_u=0daa177f-fe98-dcd4-40be-4595; ki_s=193385%3A0.0.0.0.0%3B196049%3A0.0.0.0.0%3B196842%3A1.0.0.0.1; c_type=23; __gads=ID=b289c007dd54de24:T=1585576882:S=ALNI_MbI-uyIro-jPx6VFLxoG2iIxp1syw; _td=0995594c-62ba-4126-9680-32d562f68c54; is_sensei_service_user=1; __utmc=235335808; _gid=GA1.2.1932986966.1588408242; tag_view_ranking=RTJMXD26Ak~Lt-oEicbBr~LJo91uBPz4~ETjPkL0e6r~pEXFxboc77~uusOs0ipBx~jhuUT0OJva~K8esoIs2eW~jH0uD88V6F~CrFcrMFJzz~pa4LoD4xuT~D0nMcn6oGk~KN7uxuR89w~BU9SQkS-zU~eVxus64GZU~pzzjRSV6ZO~QQhA-V4ka6~Ie2c51_4Sp~HY55MqmzzQ~zIv0cf5VVk~azESOjmQSV~BX1TR43HEh~G_f4j5NH8i~aKhT3n4RHZ~y8GNntYHsi~SmLlffZW7P~nqXiFPp5zI~tgP8r-gOe_~65aiw_5Y72~qtVr8SCFs5~pYlUxeIoeg~0Sds1vVNKR~_pwIgrV8TB~nQRrj5c6w_~rkLi5JvRDj~sylWziJEvL~RcahSSzeRf~1WWsg9dsgg~CnMEbUISIj~ERVOGLWznn~1iCpfIbtCX~zyKU3Q5L4C~faHcYIP1U0~QzKFCsGzn-~PQy2PXtK-S~RNN9CgGExV~xk2iz-RUOD~bXMh6mBhl8"+"~oJAJo4VO5E~I8PKmJXPGb~dehCwztzpj~yPNaP3JSNF~28gdfFXlY7~KOnmT1ndWG~XDEWeW9f9i~m3EJRa33xU~osjGBvsNDJ~jfnUZgnpFl~SQZaakhtVv~5oPIfUbtd6~NpsIVvS-GF~-StjcwdYwv~tlXeaI4KBb~jEyj-WOP42~VJ2-8NDjhH~FqVQndhufZ~skx_-I2o4Y~N_FXiFMRLD~Hry6GxyqEm~NJkV1n35OS~Vgp1Qch3kM~jaDJOpjiMv~VpGKjINmq7~RjyWcTb8JF~09hQLMMXVU~XZow2cjxD2~HcBlC3F1Sy~ZFrNlZeV53~PwDMGzD6xn~ISuyUUYa5J~KXBoOxiwKt~ua2BSn-Kwj~OcphT8vZeM~zx-g5-W1ik~6JdItHiE-P~LoDIs84uJh~EUwzYuPRbU~ZjY9GaY5ff~qiO14cZMBI~fg8EOt4owo~QjJSYNhDSl~75zhzbk0bS~VN7cgWyMmg~2pZ4K1syEF~v3nOtgG77A~0xsDLqCEW6~r0q4yFTWtg~Q959js6mBM~G-1lNBdD_I~H9CT0Rros8; login_bc=1; device_token=c53bd89872f3f942aad7afe34ef6b92e; categorized_tags=3ze0RLmk59~6sZKldb07K~BU9SQkS-zU~CADCYLsad0~ERVOGLWznn~HRnhV4P3Qr~Hry6GxyqEm~IVwLyT8B6k~OEXgaiEbRa~OT-C6ubi9i~QegY-rxSTl~RcahSSzeRf~RsIQe1tAR0~b8b4-hqot7~bXMh6mBhl8~d2q7U_vmMw~jfnUZgnpFl~m3EJRa33xU~mt-cXqHhAM~pvU1D1orJa~tXJuez3UbS~y8GNntYHsi~yPNaP3JSNF; PHPSESSID=13902468_U0Sqnbi1NLDsz6M2FVWEM0v1J8J8pdgI; privacy_policy_agreement=2; __utma=235335808.2137416718.1543141373.1588408241.1588423699.31; __utmz=235335808.1588423699.31.2.utmcsr=accounts.pixiv.net|utmccn=(referral)|utmcmd=referral|utmcct=/login; ki_t=1544192952735%3B1588408258940%3B1588423722036%3B17%3B39";
    @SuppressWarnings("unused")
	private static final String APPLICATION_FORM = "application/x-www-form-urlencoded;CHARSET=UTF-8";
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
	private static final String CHARSET = null;
    
	static {
		try {
			SSLContextBuilder sslContextBuilder = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

					// 信任所有站点 直接返回true
					return true;
				}
			});
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" }, null, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", new PlainConnectionSocketFactory())
					.register("https", sslConnectionSocketFactory)
					.build();

			// 设置连接池
			connMgr = new PoolingHttpClientConnectionManager(registryBuilder);

			// 设置连接池大小
			connMgr.setMaxTotal(MAX_TOTAL);
			connMgr.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

			RequestConfig.Builder configBuilder = RequestConfig.custom();

			// 设置连接超时
			configBuilder.setConnectTimeout(HTTP_CONNECT_TIMEOUT_SECOND);
			// 设置读取超时
			configBuilder.setSocketTimeout(HTTP_SOCKET_TIMEOUT_SECOND);
			// 设置从连接池获取连接实例的超时
			configBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
			// 请求前测试连接是否可用
//			configBuilder.setStaleConnectionCheckEnabled(STALE_CONNECTION_CHECK_ENABLED);

			requestConfig = configBuilder.build();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
    
    /** 
     * 通过连接池获取HttpClient 
     *  
     * @return 
     */  
    private static CloseableHttpClient getHttpClient() {  
//      HttpClient httpclient = new DefaultHttpClient();
//      CloseableHttpClient httpClient = HttpClients.createDefault();
        return HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
    }
    
    /**
     * 发送 GET 请求（HTTP），不带输入数据
     * @param url
     * @return
     */
    public static Response doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     * @param url
     * @param params
     * @return
     */
    public static Response doGet(String url, Map<String, Object> params) {
        String apiUrl = url;
        if (null != params) {
        	int i = 0;
        	StringBuffer param = new StringBuffer();
        	for (String key : params.keySet()) {
        		if (i == 0)
        			param.append("?");
        		else
        			param.append("&");
        		param.append(key).append("=").append(params.get(key));
        		i++;
        	}
        	apiUrl += param;
        }

        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTP GET请求发生异常,返回状态码:" + statusCode);
				return error(HttpRequestErrorCode.ERROR_CODE, "HTTP GET请求发生异常,返回状态码:" + statusCode);
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	System.out.println("HTTP GET请求发生异常");
        		return error(HttpRequestErrorCode.ERROR_CODE, "HTTP GET请求发生异常");
            }
            
//        	InputStream instream = entity.getContent();
//        	return IOUtils.toString(instream, CHARSET);
            return new Response(EntityUtils.toString(entity, CHARSET));
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("HTTP GET请求发生异常"+e);
        } finally {
        	if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	e.printStackTrace();
                	System.out.println("HTTP GET请求发生异常"+e);
                }
            }
        }
        
        System.out.println("HTTP GET请求发生异常");
		return error(HttpRequestErrorCode.ERROR_CODE, "HTTP GET请求发生异常");
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     * @param apiUrl
     * @return
     */
    public static Response doPost(String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static Response doPost(String apiUrl, Map<String, String> params) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Entry<String, String> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(),entry.getValue().toString());
                pairList.add(pair);
            }
            
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(CHARSET)));
            response = httpClient.execute(httpPost);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTP POST请求发生异常,返回状态码:" + statusCode);
				return error(HttpRequestErrorCode.ERROR_CODE, "HTTP POST请求发生异常,返回状态码:" + statusCode);
            }
            
            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	System.out.println("HTTP POST请求发生异常");
        		return error(HttpRequestErrorCode.ERROR_CODE, "HTTP POST请求发生异常");
            }
            
            return new Response(EntityUtils.toString(entity, CHARSET));
        } catch (IOException e) {
        	e.printStackTrace();
        	System.out.println("HTTP POST请求发生异常"+e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	e.printStackTrace();
                	System.out.println("HTTP POST请求发生异常"+e);
                }
            }
        }
        
        System.out.println("HTTP POST请求发生异常");
		return error(HttpRequestErrorCode.ERROR_CODE, "HTTP POST请求发生异常");
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     * @param apiUrl
     * @param json json对象
     * @return
     */
    public static Response doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), CHARSET);//解决中文乱码问题
            stringEntity.setContentEncoding(CHARSET);
            stringEntity.setContentType(APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTP POST请求发生异常,返回状态码:" + statusCode);
				return error(HttpRequestErrorCode.ERROR_CODE, "HTTP POST请求发生异常,返回状态码:" + statusCode);
            }
            
            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	System.out.println("HTTP POST请求发生异常");
        		return error(HttpRequestErrorCode.ERROR_CODE, "HTTP POST请求发生异常");
            }
            
            return new Response(EntityUtils.toString(entity, CHARSET));
        } catch (IOException e) {
        	e.printStackTrace();
        	System.out.println("HTTP POST请求发生异常"+e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	System.out.println("HTTP POST请求发生异常"+e);
                    e.printStackTrace();
                }
            }
        }

        System.out.println("HTTP POST请求发生异常");
		return error(HttpRequestErrorCode.ERROR_CODE, "HTTP POST请求发生异常");
    }

    public static Response doGetSSL(String apiUrl) {
    	return doGetSSL(apiUrl, null);
    }
    
    /**
     * 发送 SSL GET 请求（HTTPS），json格式
     * @param apiUrl API接口URL
     * @param json json对象
     * @return
     */
    public static Response doGetSSL(String apiUrl, Map<String, Object> params, PixivImageRequest pixivImageRequest) {
        if (null != params) {
        	int i = 0;
        	StringBuffer param = new StringBuffer();
        	for (String key : params.keySet()) {
        		if (i == 0)
        			param.append("?");
        		else
        			param.append("&");
        		param.append(key).append("=").append(params.get(key));
        		i++;
        	}
        	apiUrl += param;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpGet.setConfig(requestConfig);
            httpGet.setHeader("Host",pixivImageRequest.getHost());
            httpGet.setHeader("Referer",pixivImageRequest.getReferer());
            httpGet.setHeader("Cookie",cookie);
            response = httpClient.execute(httpGet);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTPS GET请求发生异常,返回状态码:" + statusCode);
				return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS GET请求发生异常,返回状态码:" + statusCode);
            }
            
            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	System.out.println("HTTPS GET请求发生异常");
        		return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS GET请求发生异常");
            }
            
            return new Response(EntityUtils.toString(entity, CHARSET));
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("HTTPS GET请求发生异常"+e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("HTTPS GET请求发生异常"+e);
                }
            }
        }
        
        System.out.println("HTTPS GET请求发生异常");
		return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS GET请求发生异常");
    }

    /**
     * 发送 SSL GET 请求（HTTPS），json格式
     * @param apiUrl API接口URL
     * @param json json对象
     * @return
     */
    public static Response doGetSSL(String apiUrl, Map<String, Object> params) {
        if (null != params) {
            int i = 0;
            StringBuffer param = new StringBuffer();
            for (String key : params.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(params.get(key));
                i++;
            }
            apiUrl += param;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpGet.setConfig(requestConfig);
            httpGet.setHeader("Cookie",cookie);
            response = httpClient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("HTTPS GET请求发生异常,返回状态码:" + statusCode);
                return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS GET请求发生异常,返回状态码:" + statusCode);
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                System.out.println("HTTPS GET请求发生异常");
                return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS GET请求发生异常");
            }

            return new Response(EntityUtils.toString(entity, CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HTTPS GET请求发生异常"+e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("HTTPS GET请求发生异常"+e);
                }
            }
        }

        System.out.println("HTTPS GET请求发生异常");
        return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS GET请求发生异常");
    }
    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static Response doPostSSL(String apiUrl, Map<String, String> params) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Entry<String, String> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(),entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(CHARSET)));
            response = httpClient.execute(httpPost);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTPS POST请求发生异常,返回状态码:" + statusCode);
				return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS POST请求发生异常,返回状态码:" + statusCode);
            }
            
            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	System.out.println("HTTPS POST请求发生异常");
        		return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS POST请求发生异常");
            }

            return new Response(EntityUtils.toString(entity, CHARSET));
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("HTTPS POST请求发生异常"+e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	e.printStackTrace();
                	System.out.println("HTTPS POST请求发生异常"+e);
                }
            }
        }
        
        System.out.println("HTTPS POST请求发生异常");
		return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS POST请求发生异常");
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     * @param apiUrl API接口URL
     * @param json JSON对象
     * @return
     */
    public static Response doPostSSL(String apiUrl, Object json) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), CHARSET);//解决中文乱码问题
            stringEntity.setContentEncoding(CHARSET);
            stringEntity.setContentType(APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTPS POST请求发生异常,返回状态码:" + statusCode);
				return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS POST请求发生异常,返回状态码:" + statusCode);
            }
            
            HttpEntity entity = response.getEntity();
            if (entity == null) {
            	System.out.println("HTTPS POST请求发生异常");
        		return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS POST请求发生异常");
            }
            
            return new Response(EntityUtils.toString(entity, CHARSET));
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("HTTPS POST请求发生异常"+e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	e.printStackTrace();
                	System.out.println("HTTPS POST请求发生异常"+e);
                }
            }
        }
        
        System.out.println("HTTPS POST请求发生异常");
		return error(HttpRequestErrorCode.ERROR_CODE, "HTTPS POST请求发生异常");
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
	@SuppressWarnings("unused")
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
//			sslsf = new SSLConnectionSocketFactory(sslContext);
//			sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
 
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        
        return sslsf;
    }
	
	@SuppressWarnings("unused")
	private static SSLConnectionSocketFactory createSSLConnSocketFactory2() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			X509TrustManager tm = new X509TrustManager() {
			    @Override
			    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			    }

			    @Override
			    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			    }

			    @Override
			    public X509Certificate[] getAcceptedIssuers() {
			        return null;
			    }
			};
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			// 初始化SSL上下文
			sslContext.init(null, new TrustManager[] { tm }, null);
			// SSL套接字连接工厂,NoopHostnameVerifier为信任所有服务器
			sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        
        return sslsf;
	}
	
	public static Response get(String requestURL, Map<String, Object> params) {
		if (null == requestURL) {
			System.out.println("Illegal Arguments!请求参数不正确,请求地址:" + requestURL + ",请求数据:" + params);
			throw new IllegalArgumentException("请求参数不正确,请求地址:" + requestURL + ",请求数据:" + params);
		}
		
		if (null != params) {
        	int i = 0;
        	StringBuffer param = new StringBuffer();
        	for (String key : params.keySet()) {
        		if (i == 0)
        			param.append("?");
        		else
        			param.append("&");
        		param.append(key).append("=").append(params.get(key));
        		i++;
        	}
        	requestURL += param;
        }
		
		System.out.printf("req:{method:\"{}\",url:\"{}\"}", "get", requestURL);
		
		Response result = null;
		try {
			URL url = new URL(requestURL);
			if (url.getProtocol().equals("https")) {
				result = doGetSSL(requestURL);
			} else {
				result = doGet(requestURL);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = error(HttpRequestErrorCode.ERROR_CODE, "请求发生异常");
		}
		
		System.out.printf("resp:{{}}", result);
		return result;
	}
	
	public static Response postJson(String requestURL, String json) {
		if (null == requestURL || "".equals(requestURL.trim()) || null == json || "".equals(json.trim())) {
			System.out.println("Illegal Arguments!请求参数不正确,请求地址:" + requestURL + ",请求数据:" + json);
			throw new IllegalArgumentException("请求参数不正确,请求地址:" + requestURL + ",请求数据:" + json);
		}
		
		System.out.printf("req:{method:\"{}\",url:\"{}\",data:{}}", "post", requestURL, json);
		
		Response result = null;
		try {
			URL url = new URL(requestURL);
			if (url.getProtocol().equals("https")) {
				result = doPostSSL(requestURL, json);
			} else {
				result = doPost(requestURL, json);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = error(HttpRequestErrorCode.ERROR_CODE, "请求发生异常");
		}
		
		System.out.printf("resp:{{}}", result);
		return result;
	}
	
	public static Response postForm(String requestURL, Map<String, Object> params) {
		if (null == requestURL || "".equals(requestURL.trim()) || null == params) {
			System.out.println("Illegal Arguments!请求参数不正确,请求地址:" + requestURL + ",请求数据:" + params);
			throw new IllegalArgumentException("请求参数不正确,请求地址:" + requestURL + ",请求数据:" + params);
		}
		
		System.out.printf("req:{method:\"{}\",url:\"{}\",data:{}}", "post", requestURL, params);
		
		Response result = null;
		try {
			URL url = new URL(requestURL);
			if (url.getProtocol().equals("https")) {
				result = doPostSSL(requestURL, params);
			} else {
				result = doPost(requestURL, params);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = error(HttpRequestErrorCode.ERROR_CODE, "请求发生异常");
		}
		
		System.out.printf("resp:{{}}", result);
		return result;
	}
	
	public static Response postJson(String requestURL, Map<Object, Object> params) {
		return postJson(requestURL, JsonUtils.getJsonString4Object(params));
	}
	
	public static Response postJson4Map(String requestURL, Map<String, Object> params) {
		return postJson(requestURL, JsonUtils.getJsonString4Object(params));
	}
	
	private static Response error(int HttpRequestErrorCode, String errorMsg) {
		return new Response(HttpRequestErrorCode+errorMsg);
	}
	
	/**
	 * 参数组合
	 * 
	 * @param paramMap
	 * @return
	 */
	public static String getParam(Map<String, String> paramMap) {
		Set<Entry<String, String>> set = paramMap.entrySet();
		Iterator<Entry<String, String>> itera = set.iterator();
		StringBuffer sb = new StringBuffer();
		Entry<String, String> entry;
		while (itera.hasNext()) {
			entry = itera.next();
			sb.append(entry.getKey() + "=" + entry.getValue() + "&");
		}
		
		return sb.substring(0, sb.length() - 1);

	}
	
	class HttpRequestErrorCode {

		// 正常
		public final static int OK_CODE = 0;
		
		// (业务)错误,http连接异常
		public final static int ERROR_CODE = -1;

		// http连接超时错误
		public final static int HTTP_CONNECT_TIMEOUT_CODE = -2;
		
		// http连接socket超时错误
		public final static int HTTP_SOCKET_TIMEOUT_CODE = -3;
		

	}
	
    public static void main(String[] args) throws Exception {
    	System.out.println(doGet("https://www.baidu.com"));
    	
    	// 设置参数，参数含义不需要理解
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("txnType","00");
	    map.put("signMethod","01");
	    map.put("certId","68759663125");
	    map.put("encoding","UTF-8");
	    map.put("merId","777290058110048");
	    map.put("bizType","000201");
	    map.put("txnSubType","00");
	    map.put("signature","k0lrWgeLK%2Fx%2B8ajj15QCYfmdQxZSKBjXUJN0bLt17rp87ptogxWgHAAq7EUt8RlEbxD6GaRngwtdLGiy6are45Gj1dBLJBtW2841WIq4Ywzx3oK6538Kfh9ll91GJcZJGYz8LuJoZfii7HFPlpl1ZsPZbbdKP6WFVHNMnGnL9nk9QSa%2BihXGpyK%2Fy1FA42AJpfc%2FTT3BV6C%2FxpoEhXzVckHnniVnCpLdGnPfZOd76wK%2Fa%2BALNmniwUZmMj9uNPwnONIIwL%2FFqrqQinQArolW%2FrcIt9NL7qKvQujM%2BdRvd1fboAHI5bZC3ktVPB0s5QFfsRhSRFghVi4RHOzL8ZG%2FVQ%3D%3D");
	    map.put("orderId","20160309145206");
	    map.put("version","5.0.0");
	    map.put("txnTime","20160309145206");
	    map.put("accessType","0");
	    System.out.println(doPostSSL("https://api.ffan.com/qrpay/v1/teminalSendTrans", map));
    	System.out.println(doPostSSL("https://101.231.204.80:5000/gateway/api/queryTrans.do", map));
    	System.out.println(doGetSSL("https://localhost:19443/bn/v1/allcitygo/test"));
    }
    
}