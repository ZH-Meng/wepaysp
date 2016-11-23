package com.zbsp.wepaysp.common.http.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zbsp.wepaysp.common.http.common.HttpConfig;
import com.zbsp.wepaysp.common.http.common.HttpConfig.ParamType;
import com.zbsp.wepaysp.common.http.common.HttpMethods;
import com.zbsp.wepaysp.common.http.common.Utils;
import com.zbsp.wepaysp.common.http.exception.HttpProcessException;
import com.zbsp.wepaysp.common.http.httpclient.builder.HCB;

/**
 * 使用HttpClient模拟发送（http/https）请求
 * 
 * @author arron
 * @date 2015年11月4日 下午4:10:59
 * @version 1.0
 */
public class HttpClientUtil {
    
    public static void main(String[] args) {
        System.out.println(1);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    // 默认采用的http协议的HttpClient对象
    private static HttpClient client4HTTP;

    // 默认采用的https协议的HttpClient对象
    private static HttpClient client4HTTPS;

    static {
        try {
            client4HTTP = HCB.custom().build();
            client4HTTPS = HCB.custom().ssl().build();
        } catch (HttpProcessException e) {
            LOGGER.error("创建https协议的HttpClient对象出错：{}", e);
        }
    }

    /**
     * 判断url是http还是https，直接返回相应的默认client对象
     * 
     * @return 返回对应默认的client对象
     * @throws HttpProcessException
     */
    private static HttpClient create(String url)
        throws HttpProcessException {
        if (url.toLowerCase().startsWith("https://")) {
            return client4HTTPS;
        } else {
            return client4HTTP;
        }
    }

    /**
     * 以Get方式，请求资源或服务
     * 
     * @param client client对象
     * @param url 资源地址
     * @param headers 请求头信息
     * @param context http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException
     */
    public static String get(HttpClient client, String url, Header[] headers, HttpContext context, String encoding)
        throws HttpProcessException {
        return send(HttpConfig.custom(ParamType.NONE).client(client).url(url).method(HttpMethods.GET).headers(headers)
            .context(context).encoding(encoding));
    }

    /**
     * 以Get方式，请求资源或服务
     * 
     * @param config 请求参数配置
     * @return
     * @throws HttpProcessException
     */
    public static String get(HttpConfig config)
        throws HttpProcessException {
        return send(config.method(HttpMethods.GET));
    }

    /**
     * 以Post方式，请求资源或服务
     * 
     * @param client client对象
     * @param url 资源地址
     * @param parasMap 请求参数
     * @param headers 请求头信息
     * @param context http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException
     */
    public static String post(HttpClient client, String url, Header[] headers, Map<String, Object> parasMap,
        HttpContext context, String encoding)
        throws HttpProcessException {
        return send(HttpConfig.custom(ParamType.Map).client(client).url(url).method(HttpMethods.POST).headers(headers).map(parasMap)
            .context(context).encoding(encoding));
    }
    
    /**
     * 以Post方式，请求资源或服务
     * 
     * @param client client对象
     * @param url 资源地址
     * @param parasMap 请求参数
     * @param headers 请求头信息
     * @param context http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException
     */
    public static String post(HttpClient client, String url, Header[] headers, String stringParam,
        HttpContext context, String encoding)
        throws HttpProcessException {
        return send(HttpConfig.custom(ParamType.String).client(client).url(url).method(HttpMethods.POST).headers(headers).stringParam(stringParam)
            .context(context).encoding(encoding));
    }

    /**
     * 以Post方式，请求资源或服务
     * 
     * @param config 请求参数配置
     * @return
     * @throws HttpProcessException
     */
    public static String post(HttpConfig config)
        throws HttpProcessException {
        return send(config.method(HttpMethods.POST));
    }

    /**
     * 以Put方式，请求资源或服务
     * 
     * @param client client对象
     * @param url 资源地址
     * @param parasMap 请求参数
     * @param headers  请求头信息
     * @param context http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException
     */
    public static String put(HttpClient client, String url, Map<String, Object> parasMap, Header[] headers,
        HttpContext context, String encoding)
        throws HttpProcessException {
        return send(HttpConfig.custom(ParamType.Map).client(client).url(url).method(HttpMethods.PUT).headers(headers).map(parasMap)
            .context(context).encoding(encoding));
    }

    /**
     * 以Put方式，请求资源或服务
     * 
     * @param config 请求参数配置
     * @return
     * @throws HttpProcessException
     */
    public static String put(HttpConfig config)
        throws HttpProcessException {
        return send(config.method(HttpMethods.PUT));
    }

    /**
     * 以Delete方式，请求资源或服务
     * 
     * @param client client对象
     * @param url 资源地址
     * @param headers 请求头信息
     * @param context http上下文，用于cookie操作
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException
     */
    public static String delete(HttpClient client, String url, Header[] headers, HttpContext context, String encoding)
        throws HttpProcessException {
        return send(HttpConfig.custom(ParamType.NONE).client(client).url(url).method(HttpMethods.DELETE).headers(headers)
            .context(context).encoding(encoding));
    }

    /**
     * 以Delete方式，请求资源或服务
     * 
     * @param config 请求参数配置
     * @return
     * @throws HttpProcessException
     */
    public static String delete(HttpConfig config)
        throws HttpProcessException {
        return send(config.method(HttpMethods.DELETE));
    }

    /**
     * 请求资源或服务
     * 
     * @param config
     * @return
     * @throws HttpProcessException
     */
    public static String send(HttpConfig config)
        throws HttpProcessException {
        return fmt2String(execute(config), config.outenc());
    }

    /**
     * 请求资源或服务
     * 
     * @param client client对象
     * @param url 资源地址
     * @param httpMethod 请求方法
     * @param parasMap 请求参数
     * @param headers 请求头信息
     * @param encoding 编码
     * @return 返回处理结果
     * @throws HttpProcessException
     */
    private static HttpResponse execute(HttpConfig config)
        throws HttpProcessException {
        if (config.client() == null) {// 检测是否设置了client
            config.client(create(config.url()));
        }
        HttpResponse resp = null;
        try {
            // 创建请求对象
            HttpRequestBase request = getRequest(config.url(), config.method());

            // 设置header信息
            request.setHeaders(config.headers());

            // 判断是否支持设置entity(仅HttpPost、HttpPut、HttpPatch支持)
            if (HttpEntityEnclosingRequestBase.class.isAssignableFrom(request.getClass())) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                // 检测url中是否存在参数
                config.url(Utils.checkHasParas(config.url(), nvps, config.inenc()));

                // 装填参数
                
                if (!config.paramType().equals(ParamType.NONE)) {
                    HttpEntity entity = null;
                    
                    if (config.paramType().equals(ParamType.Map)) {
                        entity = Utils.map2List(nvps, config.map(), config.inenc());
                    } else {
                        entity =  new StringEntity(config.stringParam(), config.encoding());
                    }

                    // 设置参数到请求对象中
                    ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
                }

                LOGGER.info("请求地址：" + config.url());
                if (nvps.size() > 0) {
                    LOGGER.info("请求参数：" + nvps.toString());
                }
            } else {
                int idx = config.url().indexOf("?");
                LOGGER.info("请求地址：" + config.url().substring(0, (idx > 0 ? idx : config.url().length())));
                if (idx > 0) {
                    LOGGER.info("请求参数：" + config.url().substring(idx + 1));
                }
            }
            // 执行请求操作，并拿到结果（同步阻塞）
            resp = (config.context() == null) ? config.client().execute(request)
                : config.client().execute(request, config.context());

            if (config.isReturnRespHeaders()) {
                // 获取所有response的header信息
                config.headers(resp.getAllHeaders());
            }

            // 获取结果实体
            return resp;

        } catch (IOException e) {
            throw new HttpProcessException(e);
        }
    }

    /**
     * 转化为字符串
     * 
     * @param entity 实体
     * @param encoding 编码
     * @return
     * @throws HttpProcessException
     */
    private static String fmt2String(HttpResponse resp, String encoding)
        throws HttpProcessException {
        String body = "";
        try {
            if (resp.getEntity() != null) {
                // 按指定编码转换结果实体为String类型
                body = EntityUtils.toString(resp.getEntity(), encoding);
                LOGGER.debug(body);
            }
            EntityUtils.consume(resp.getEntity());
        } catch (IOException e) {
            throw new HttpProcessException(e);
        } finally {
            close(resp);
        }
        return body;
    }

    /**
     * 转化为流
     * 
     * @param entity 实体
     * @param out 输出流
     * @return
     * @throws HttpProcessException
     */
    public static OutputStream fmt2Stream(HttpResponse resp, OutputStream out)
        throws HttpProcessException {
        try {
            resp.getEntity().writeTo(out);
            EntityUtils.consume(resp.getEntity());
        } catch (IOException e) {
            throw new HttpProcessException(e);
        } finally {
            close(resp);
        }
        return out;
    }

    /**
     * 根据请求方法名，获取request对象
     * 
     * @param url 资源地址
     * @param method 请求方式
     * @return
     */
    private static HttpRequestBase getRequest(String url, HttpMethods method) {
        HttpRequestBase request = null;
        switch (method.getCode()) {
            case 0:// HttpGet
                request = new HttpGet(url);
                break;
            case 1:// HttpPost
                request = new HttpPost(url);
                break;
            case 2:// HttpHead
                request = new HttpHead(url);
                break;
            case 3:// HttpPut
                request = new HttpPut(url);
                break;
            case 4:// HttpDelete
                request = new HttpDelete(url);
                break;
            case 5:// HttpTrace
                request = new HttpTrace(url);
                break;
            case 6:// HttpPatch
                request = new HttpPatch(url);
                break;
            case 7:// HttpOptions
                request = new HttpOptions(url);
                break;
            default:
                request = new HttpPost(url);
                break;
        }
        return request;
    }

    /**
     * 尝试关闭response
     * 
     * @param resp HttpResponse对象
     */
    private static void close(HttpResponse resp) {
        try {
            if (resp == null)
                return;
            // 如果CloseableHttpResponse 是resp的父类，则支持关闭
            if (CloseableHttpResponse.class.isAssignableFrom(resp.getClass())) {
                ((CloseableHttpResponse) resp).close();
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
