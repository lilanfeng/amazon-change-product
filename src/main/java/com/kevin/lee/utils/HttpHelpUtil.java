package com.kevin.lee.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.kevin.lee.App;
import com.kevin.lee.dto.InputProductInfo;
import com.kevin.lee.dto.ProductInfoData;
import com.kevin.lee.dto.response.ConditionGatingStatus;
import com.kevin.lee.dto.response.Product;
import com.kevin.lee.dto.response.ProductInfo;
import com.kevin.lee.dto.response.UnsellableReason;
import com.kevin.lee.enums.CountryWebEnum;
import com.kevin.lee.worker.ProductChangeWork;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lilanfeng
 */
public class HttpHelpUtil {

    private static final Log logger = LogFactory.get();
    private static final String CHANGE_PRODUCT_URL = "https://{HOST}" +
            "/productsearch/v2/search?q={asin}&page=1";
    private static final String PRODUCT_LIST_URL = "https://{HOST}" +
            "/productsearch/v2/search?q={key}&page={pageSize}&pageToken={pageToken}";
    private static final String REFERER_URL = "https://{HOST}" +
            "/product-search/search?q={key}&ref_=xx_catadd_dnav_xx&mons_redirect=stck_reroute";

    /**
     *  http请求超时时间 默认60 s
     */
    private static final int HTTP_SEND_TIME_OUT = 360000;

    public static boolean getChangeProduct(ProductChangeWork work,InputProductInfo inputProductInfo, String cookies, InputProductInfo outProductInfo) {

        ProductInfo productInfo = null;
        String result = null;
        try {
            if (StrUtil.isBlank(inputProductInfo.getAsin())) {
                work.publishEvent(ConsoleUtil.console("HttpHelpUtil  asin is empty:"));
                return false;
            }
            BeanUtil.copyProperties(inputProductInfo, outProductInfo);

            //休眠1秒，不要太频繁请求接口
            if (App.config.getSleepTime(App.config.getCountryWeb()) > 0) {
                Thread.sleep(App.config.getSleepTime(App.config.getCountryWeb()));
            }

            String url = StrUtil.replace(CHANGE_PRODUCT_URL, "{asin}", inputProductInfo.getAsin());
            url = changeHostUrl(url);
            logger.info("HttpHelpUtil.request url:{}", url);
            //work.publishEvent(ConsoleUtil.console("HttpHelpUtil.request url:"+url));
            HttpRequest httpRequest = HttpUtil.createGet(url);
            Map<String, String> headerMap = new HashMap<>(1);
            headerMap.put("Cookie", cookies);
            //String refererUrl = StrUtil.replace(REFERER_URL, "{asin}", inputProductInfo.getAsin());
            //refererUrl = changeHostUrl(refererUrl);
            //headerMap.put("Referer",refererUrl);
            //headerMap.put("Sec-Ch-Ua","\"Google Chrome\";v=\"119\",\"Not?A_Brand\";v=\"24\"");
            //headerMap.put("Sec-Ch-Mobile","?0");
            //headerMap.put("Sec-Ch-Platform","\"Windows\"");
            //headerMap.put("Sec-Fetch-Dest","empty");
            //headerMap.put("Sec-Fetch-Mode","cors");
            //headerMap.put("Sec-Fetch-Site","same-origin");
            //headerMap.put("User-Agent","Mozilla/5.0(Windows NT 10.0;Win64;x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/119.0.0.0 Saari/5.3.7.36");
            HttpResponse httpResponse = httpRequest.addHeaders(headerMap).timeout(HTTP_SEND_TIME_OUT).execute();

            if (!httpResponse.isOk()) {
                //logger.error("HttpHelpUtil.response status:{} body:{}", httpResponse.getStatus(), httpResponse.body());
                work.publishEvent(ConsoleUtil.console("Asin:"+inputProductInfo.getAsin()+" HttpHelpUtil.response status"
                        +httpResponse.getStatus()+"body" ));
                sleep(work);
                return false;
            }
            result = httpResponse.body();
            if (StrUtil.isBlank(result) || result.contains("!DOCTYPE")) {
                work.publishEvent(ConsoleUtil.console("HttpHelpUtil.exception asin:" + inputProductInfo.getAsin() + " " +
                        "请求太频繁需要验证码进行验证处理!"));
                //logger.info("HttpHelpUtil.response asin:{} 请求太频繁需要验证码进行验证处理", inputProductInfo.getAsin() );
                sleep(work);
                return false;
            }
            productInfo = JSON.parseObject(httpResponse.body(), ProductInfo.class);

        } catch (Exception e) {
            logger.error(e, "HttpHelpUtil asin:{}", inputProductInfo.getAsin());
           // sleep(textArea);
            return false;
        }

        /**
        String data = "\n" +
                "\n" +
                "\n" +
                "{\n" +
                "    \"numberOfResults\": 1,\n" +
                "    \"refinementSummary\": {\n" +
                "        \"absURL\": \"/\",\n" +
                "        \"refinementsData\": [\n" +
                "            {\n" +
                "                \"partURL\": \"all\",\n" +
                "                \"subRefinements\": [],\n" +
                "                \"type\": \"BROWSE_NODE\",\n" +
                "                \"path\": [\n" +
                "                    {\n" +
                "                        \"relativeUrl\": \"all\",\n" +
                "                        \"displayName\": \"all\",\n" +
                "                        \"id\": \"all\",\n" +
                "                        \"numberOfResults\": 0,\n" +
                "                        \"index\": 0\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"partURL\": \"all\",\n" +
                "                \"subRefinements\": [],\n" +
                "                \"type\": \"BRAND\",\n" +
                "                \"path\": [\n" +
                "                    {\n" +
                "                        \"relativeUrl\": \"all\",\n" +
                "                        \"displayName\": \"all\",\n" +
                "                        \"id\": \"all\",\n" +
                "                        \"numberOfResults\": 0,\n" +
                "                        \"index\": 0\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"invalidRefinementSummary\": false\n" +
                "    },\n" +
                "    \"products\": [\n" +
                "        {\n" +
                "            \"asin\": \"B00DHNJ724\",\n" +
                "            \"detailPageURL\": \"http://www.amazon.com/dp/B00DHNJ724\",\n" +
                "            \"imageUrl\": \"https://m.media-amazon.com/images/I/51AmLRRE6nL.jpg\",\n" +
                "            \"marketplaceToVariationAsins\": {},\n" +
                "            \"title\": \"Triumph A9788013 Street Triple 675/765 Frame Protector Kit # " +
                "A9788013\",\n" +
                "            \"ean\": null,\n" +
                "            \"upc\": null,\n" +
                "            \"isbn\": null,\n" +
                "            \"gtin\": null,\n" +
                "            \"salesRank\": \"1598982\",\n" +
                "            \"offerCounts\": {\n" +
                "                \"new\": 0,\n" +
                "                \"used\": 0,\n" +
                "                \"club\": 0,\n" +
                "                \"refurbished\": 0,\n" +
                "                \"collectible\": 0\n" +
                "            },\n" +
                "            \"qualificationMessages\": [\n" +
                "                {\n" +
                "                    \"qualificationMessage\": null,\n" +
                "                    \"helpUrl\": null,\n" +
                "                    \"conditionList\": null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"qualificationMessage\": \"You cannot list the product in this condition" +
                ".\",\n" +
                "                    \"helpUrl\": null,\n" +
                "                    \"conditionList\": \"Collectible, Refurbished, Used, Used, Used, Used " +
                "conditions\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"conditionGatingStatuses\": [\n" +
                "                {\n" +
                "                    \"condition\": \"new\",\n" +
                "                    \"displayLabel\": \"New\",\n" +
                "                    \"hasPathForward\": true,\n" +
                "                    \"gated\": false\n" +
                "                },\n" +
                "                {\n" +
                "                    \"condition\": \"collectible\",\n" +
                "                    \"displayLabel\": \"Collectible\",\n" +
                "                    \"hasPathForward\": false,\n" +
                "                    \"gated\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"condition\": \"refurbished\",\n" +
                "                    \"displayLabel\": \"Refurbished\",\n" +
                "                    \"hasPathForward\": false,\n" +
                "                    \"gated\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"condition\": \"used\",\n" +
                "                    \"displayLabel\": \"Used\",\n" +
                "                    \"hasPathForward\": false,\n" +
                "                    \"gated\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"condition\": \"used\",\n" +
                "                    \"displayLabel\": \"Used\",\n" +
                "                    \"hasPathForward\": false,\n" +
                "                    \"gated\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"condition\": \"used\",\n" +
                "                    \"displayLabel\": \"Used\",\n" +
                "                    \"hasPathForward\": false,\n" +
                "                    \"gated\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"condition\": \"used\",\n" +
                "                    \"displayLabel\": \"Used\",\n" +
                "                    \"hasPathForward\": false,\n" +
                "                    \"gated\": true\n" +
                "                }\n" +
                "            ],\n" +
                "            \"unsellableReasons\": [\n" +
                //"                {\n" +
                //"                    \"qualificationMessage\": \"Sorry, the ability to create a listing for this " +
                //"item is restricted.\",\n" +
                //"                    \"helpUrl\": null,\n" +
                //"                    \"conditionList\": null\n" +
                //"                }\n" +
                "            ],\n" +
                "            \"restrictedForAnyCondition\": true,\n" +
                "            \"restrictedForAllConditions\": true,\n" +
                "            \"pathToSellUrl\": \"/hz/approvalrequest/restrictions/approve?asin=B00DHNJ724\",\n" +
                "            \"productToken\": null,\n" +
                "            \"parent\": false\n" +
                "        }\n" +
                "    ],\n" +
                "    \"pagination\": {\n" +
                "        \"nextToken\": null,\n" +
                "        \"previousToken\": null\n" +
                "    },\n" +
                "    \"marketplaceId\": null,\n" +
                "    \"debugInfo\": null\n" +
                "}";
        productInfo = JSON.parseObject(data,ProductInfo.class); **/
        if (productInfo == null) {
            logger.error("HttpHelpUtil..productInfo.is.null");
            return false;
        }

        List<Product> productList = productInfo.getProducts();

        if (productList == null || productList.size() <= 0) {
            logger.error("HttpHelpUtil..productList.size().is.null.or.zero,asin:{}",inputProductInfo.getAsin());
            return false;
        }

        Product product = productList.get(0);

        if (product == null) {
            logger.error("HttpHelpUtil.product.is.null,asin:{}", inputProductInfo.getAsin());
            return false;
        }

        List<ConditionGatingStatus> conditionGatingStatusList = product.getConditionGatingStatuses();
        /**
         * 可以销售的状态
         *                 {
         *                     "condition": "new",
         *                     "displayLabel": "New", 全新
         *                     "hasPathForward": true,
         *                     "gated": false
         *                 },
         *              product.restrictedForAllCondition = false
         */
        AtomicBoolean isNew = new AtomicBoolean(false);
        for (ConditionGatingStatus conditionGatingStatus : conditionGatingStatusList) {
            if ("new".equalsIgnoreCase(conditionGatingStatus.getCondition()) &&
                    conditionGatingStatus.isHasPathForward() && !conditionGatingStatus.isGated()) {
                isNew.set(true);
                break;
            }
        }
        List<UnsellableReason> unsellableReasons = product.getUnsellableReasons();

        if (StrUtil.isNotBlank(product.getUpc())) {
            outProductInfo.setUpc(product.getUpc());
        }
        if (StrUtil.isNotBlank(product.getEan())) {
            outProductInfo.setEan(product.getEan());
        }
        //详情页面加载数据
        //getFirstProductDetailInfo(product.getDetailPageURL(), outProductInfo);

        if (isNew.get() && !product.isRestrictedForAllConditions() && (unsellableReasons == null || unsellableReasons.size() <= 0)) {
            return true;
        }
        work.publishEvent(ConsoleUtil.console("Asin:"+inputProductInfo.getAsin()+" not new"));
        //logger.info("HttpHelpUtil.this.is.not.available:{}",inputProductInfo.getAsin());
        return false;
    }

    /**
     *
     * @param work
     */
    private static void sleep(ProductChangeWork work){
        //休眠1秒，不要太频繁请求接口
        if (App.config.getTimeout(App.config.getCountryWeb()) > 0) {
            try {
                work.publishEvent(ConsoleUtil.console("本次超时或异常的间隔为：" + App.config.getTimeout(App.config.getCountryWeb()) + " ms"));
                Thread.sleep(App.config.getTimeout(App.config.getCountryWeb()));
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
                logger.error(interruptedException, "Sleep time out exception");
            }
        }
    }

    /**
     * 搜索关键字数据处理
     * @param work
     * @param key
     * @param cookies
     * @return
     */
    public static List<ProductInfoData> getProductList(ProductChangeWork work, String key, String cookies){
        ProductInfo productInfo = getProductSearch(work,key,1,"",cookies);
        List<ProductInfoData> inputProductInfoList = new ArrayList<>();
        parseProductInfo(productInfo,work,1,key,cookies,inputProductInfoList);
        return inputProductInfoList;
    }

    private static void parseProductInfo(ProductInfo productInfo,ProductChangeWork work,int currentPage,String key, String cookies,List<ProductInfoData> resultList){

        if (productInfo == null) {
            return;
        }

        List<Product> productList = productInfo.getProducts();
        if (productList == null || productList.size() <= 0) {
            logger.info("HttpHelpUtil..productList.size().is.null.or.zero,key:{},page:{}",key,currentPage);
            return ;
        }
        productList.stream().forEach(product -> {

            if (product == null) {
                logger.error("HttpHelpUtil.product.is.null,key:{}", key);
                return ;
            }
            List<ConditionGatingStatus> conditionGatingStatusList = product.getConditionGatingStatuses();
            /**
             * 可以销售的状态
             *                 {
             *                     "condition": "new",
             *                     "displayLabel": "New", 全新
             *                     "hasPathForward": true,
             *                     "gated": false
             *                 },
             *
             */
            AtomicBoolean isNew = new AtomicBoolean(false);
            conditionGatingStatusList.stream().forEach(conditionGatingStatus -> {
                if ("new".equalsIgnoreCase(conditionGatingStatus.getCondition()) &&
                        conditionGatingStatus.isHasPathForward() && !conditionGatingStatus.isGated()) {
                    isNew.set(true);
                    return;
                }
            });
            List<UnsellableReason> unsellableReasons = product.getUnsellableReasons();
            if (isNew.get() && (unsellableReasons == null || unsellableReasons.size() <= 0)) {
                ProductInfoData info = new ProductInfoData();
                if (StrUtil.isNotBlank(product.getUpc())) {
                    info.setUpc(product.getUpc());
                }
                if (StrUtil.isNotBlank(product.getEan())){
                    info.setEan(product.getEan());
                }
                info.setTitle(product.getTitle());
                info.setAsin(product.getAsin());
                info.setBrand("");
                info.setCommentsCount("");
                info.setScore("");

                resultList.add(info);
                return ;
            }
            work.publishEvent(ConsoleUtil.console("Asin:"+product.getAsin()+" not new"));
            logger.info("HttpHelpUtil.this.is.not.available:{}",product.getAsin());
        });

        String pageToken = productInfo.getPagination().getNextToken();
        if(StrUtil.isEmpty(pageToken)){
            logger.error("HttpHelpUtil.parseProductInfo.pageToken.empty");
            return;
        }
        int nextPage = currentPage +1;
        ProductInfo nextProductInfo = getProductSearch(work,key,nextPage,pageToken,cookies);
        parseProductInfo(nextProductInfo,work,nextPage,key,cookies,resultList);

    }

    /**
     * 拉取列表数据文件
     * @param work
     * @param key
     * @param page
     * @param pageToken
     * @param cookies
     * @return
     */
    private static  ProductInfo getProductSearch(ProductChangeWork work,String key,int page,String pageToken,String cookies){
        ProductInfo productInfo = null;
        String url = "";
        try {
            //休眠1秒，不要太频繁请求接口
            if (App.config.getSleepTime(App.config.getCountryWeb()) > 0) {
                Thread.sleep(App.config.getSleepTime(App.config.getCountryWeb()));
            }
            url = StrUtil.replace(PRODUCT_LIST_URL, "{key}", key);
            url = StrUtil.replace(url,"{pageSize}",String.valueOf(page));
            url = changeHostUrl(url);
            if(page == 1){
                url = StrUtil.replace(url,"&pageToken={pageToken}",pageToken);
            }else {
                url = StrUtil.replace(url,"{pageToken}",pageToken);
            }

            work.publishEvent(ConsoleUtil.console("url:"+url));
            logger.info("HttpHelpUtil.request url:{}", url);
            HttpRequest httpRequest = HttpUtil.createGet(url);
            Map<String, String> headerMap = new HashMap<>(1);
            headerMap.put("Cookie", cookies);
            HttpResponse httpResponse = httpRequest.addHeaders(headerMap).timeout(HTTP_SEND_TIME_OUT).execute();

            if (!httpResponse.isOk()) {
                logger.error("HttpHelpUtil.response status:{} body:{}", httpResponse.getStatus(), httpResponse.body());
                return null;
            }

            productInfo = JSON.parseObject(httpResponse.body(), ProductInfo.class);

        } catch (Exception e) {
            logger.error(e, "HttpHelpUtil url:{}", url);
            work.publishEvent(ConsoleUtil.console("HttpHelpUtil.exception url:" + url + " " +
                    "message:" + e.getMessage()));
            if (e.getCause() instanceof SocketTimeoutException) {
                //休眠1秒，不要太频繁请求接口
                if (App.config.getTimeout(App.config.getCountryWeb()) > 0) {
                    try {
                        work.publishEvent(ConsoleUtil.console("本次超时间隔为：" + App.config.getTimeout(App.config.getCountryWeb()) + " ms"));
                        Thread.sleep(App.config.getTimeout(App.config.getCountryWeb()));
                    } catch (InterruptedException interruptedException) {
                        e.printStackTrace();
                        logger.error(interruptedException, "fileTimeout exception");
                    }
                }
            }
            return null;
        }
        return productInfo;
    }

    public static ProductInfoData getChangeProduct(ProductChangeWork work,ProductInfoData productInfoData, String cookies) {
        if (StrUtil.isBlank(productInfoData.getAsin())) {
            logger.info("HttpHelpUtil asin is empty:{}", productInfoData);
            return null;
        }
        ProductInfoData outProductInfo = new ProductInfoData();
        BeanUtil.copyProperties(productInfoData, outProductInfo);

        ProductInfo productInfo = null;
        try {
            //休眠1秒，不要太频繁请求接口
            if (App.config.getSleepTime(App.config.getCountryWeb()) > 0) {
                Thread.sleep(App.config.getSleepTime(App.config.getCountryWeb()));
            }

            String url = StrUtil.replace(CHANGE_PRODUCT_URL, "{asin}", productInfoData.getAsin());
            url = changeHostUrl(url);
            logger.info("HttpHelpUtil.request url:{}", url);
            //work.publishEvent(ConsoleUtil.console("HttpHelpUtil.request url:"+url);
            HttpRequest httpRequest = HttpUtil.createGet(url);
            Map<String, String> headerMap = new HashMap<>(1);
            headerMap.put("Cookie", cookies);
            HttpResponse httpResponse = httpRequest.addHeaders(headerMap).timeout(HTTP_SEND_TIME_OUT).execute();

            if (!httpResponse.isOk()) {
                logger.error("HttpHelpUtil.response status:{} body:{}", httpResponse.getStatus(), httpResponse.body());
                return null;
            }

            productInfo = JSON.parseObject(httpResponse.body(), ProductInfo.class);

        } catch (Exception e) {
            logger.error(e, "HttpHelpUtil asin:{}", productInfoData.getAsin());
            work.publishEvent(ConsoleUtil.console("HttpHelpUtil.exception asin:" + productInfoData.getAsin() + " " +
                    "message:" + e.getMessage()));

            //if (e.getCause() instanceof SocketTimeoutException) {
                //未返回正常数据统一等待异常处理，休眠1秒，不要太频繁请求接口
                if (App.config.getTimeout(App.config.getCountryWeb()) > 0) {
                    try {
                        work.publishEvent(ConsoleUtil.console("本次超时间隔为：" + App.config.getTimeout(App.config.getCountryWeb()) + " ms"));
                        Thread.sleep(App.config.getTimeout(App.config.getCountryWeb()));
                    } catch (InterruptedException interruptedException) {
                        e.printStackTrace();
                        logger.error(interruptedException, "fileTimeout exception");
                    }
                }
            //}

            return null;
        }

        /**
         String data = "\n" +
         "\n" +
         "\n" +
         "{\n" +
         "    \"numberOfResults\": 1,\n" +
         "    \"refinementSummary\": {\n" +
         "        \"absURL\": \"/\",\n" +
         "        \"refinementsData\": [\n" +
         "            {\n" +
         "                \"partURL\": \"all\",\n" +
         "                \"subRefinements\": [],\n" +
         "                \"type\": \"BROWSE_NODE\",\n" +
         "                \"path\": [\n" +
         "                    {\n" +
         "                        \"relativeUrl\": \"all\",\n" +
         "                        \"displayName\": \"all\",\n" +
         "                        \"id\": \"all\",\n" +
         "                        \"numberOfResults\": 0,\n" +
         "                        \"index\": 0\n" +
         "                    }\n" +
         "                ]\n" +
         "            },\n" +
         "            {\n" +
         "                \"partURL\": \"all\",\n" +
         "                \"subRefinements\": [],\n" +
         "                \"type\": \"BRAND\",\n" +
         "                \"path\": [\n" +
         "                    {\n" +
         "                        \"relativeUrl\": \"all\",\n" +
         "                        \"displayName\": \"all\",\n" +
         "                        \"id\": \"all\",\n" +
         "                        \"numberOfResults\": 0,\n" +
         "                        \"index\": 0\n" +
         "                    }\n" +
         "                ]\n" +
         "            }\n" +
         "        ],\n" +
         "        \"invalidRefinementSummary\": false\n" +
         "    },\n" +
         "    \"products\": [\n" +
         "        {\n" +
         "            \"asin\": \"B00DHNJ724\",\n" +
         "            \"detailPageURL\": \"http://www.amazon.com/dp/B00DHNJ724\",\n" +
         "            \"imageUrl\": \"https://m.media-amazon.com/images/I/51AmLRRE6nL.jpg\",\n" +
         "            \"marketplaceToVariationAsins\": {},\n" +
         "            \"title\": \"Triumph A9788013 Street Triple 675/765 Frame Protector Kit # " +
         "A9788013\",\n" +
         "            \"ean\": null,\n" +
         "            \"upc\": null,\n" +
         "            \"isbn\": null,\n" +
         "            \"gtin\": null,\n" +
         "            \"salesRank\": \"1598982\",\n" +
         "            \"offerCounts\": {\n" +
         "                \"new\": 0,\n" +
         "                \"used\": 0,\n" +
         "                \"club\": 0,\n" +
         "                \"refurbished\": 0,\n" +
         "                \"collectible\": 0\n" +
         "            },\n" +
         "            \"qualificationMessages\": [\n" +
         "                {\n" +
         "                    \"qualificationMessage\": null,\n" +
         "                    \"helpUrl\": null,\n" +
         "                    \"conditionList\": null\n" +
         "                },\n" +
         "                {\n" +
         "                    \"qualificationMessage\": \"You cannot list the product in this condition" +
         ".\",\n" +
         "                    \"helpUrl\": null,\n" +
         "                    \"conditionList\": \"Collectible, Refurbished, Used, Used, Used, Used " +
         "conditions\"\n" +
         "                }\n" +
         "            ],\n" +
         "            \"conditionGatingStatuses\": [\n" +
         "                {\n" +
         "                    \"condition\": \"new\",\n" +
         "                    \"displayLabel\": \"New\",\n" +
         "                    \"hasPathForward\": true,\n" +
         "                    \"gated\": false\n" +
         "                },\n" +
         "                {\n" +
         "                    \"condition\": \"collectible\",\n" +
         "                    \"displayLabel\": \"Collectible\",\n" +
         "                    \"hasPathForward\": false,\n" +
         "                    \"gated\": true\n" +
         "                },\n" +
         "                {\n" +
         "                    \"condition\": \"refurbished\",\n" +
         "                    \"displayLabel\": \"Refurbished\",\n" +
         "                    \"hasPathForward\": false,\n" +
         "                    \"gated\": true\n" +
         "                },\n" +
         "                {\n" +
         "                    \"condition\": \"used\",\n" +
         "                    \"displayLabel\": \"Used\",\n" +
         "                    \"hasPathForward\": false,\n" +
         "                    \"gated\": true\n" +
         "                },\n" +
         "                {\n" +
         "                    \"condition\": \"used\",\n" +
         "                    \"displayLabel\": \"Used\",\n" +
         "                    \"hasPathForward\": false,\n" +
         "                    \"gated\": true\n" +
         "                },\n" +
         "                {\n" +
         "                    \"condition\": \"used\",\n" +
         "                    \"displayLabel\": \"Used\",\n" +
         "                    \"hasPathForward\": false,\n" +
         "                    \"gated\": true\n" +
         "                },\n" +
         "                {\n" +
         "                    \"condition\": \"used\",\n" +
         "                    \"displayLabel\": \"Used\",\n" +
         "                    \"hasPathForward\": false,\n" +
         "                    \"gated\": true\n" +
         "                }\n" +
         "            ],\n" +
         "            \"unsellableReasons\": [\n" +
         //"                {\n" +
         //"                    \"qualificationMessage\": \"Sorry, the ability to create a listing for this " +
         //"item is restricted.\",\n" +
         //"                    \"helpUrl\": null,\n" +
         //"                    \"conditionList\": null\n" +
         //"                }\n" +
         "            ],\n" +
         "            \"restrictedForAnyCondition\": true,\n" +
         "            \"restrictedForAllConditions\": true,\n" +
         "            \"pathToSellUrl\": \"/hz/approvalrequest/restrictions/approve?asin=B00DHNJ724\",\n" +
         "            \"productToken\": null,\n" +
         "            \"parent\": false\n" +
         "        }\n" +
         "    ],\n" +
         "    \"pagination\": {\n" +
         "        \"nextToken\": null,\n" +
         "        \"previousToken\": null\n" +
         "    },\n" +
         "    \"marketplaceId\": null,\n" +
         "    \"debugInfo\": null\n" +
         "}";
         productInfo = JSON.parseObject(data,ProductInfo.class); **/
        if (productInfo == null) {
            logger.error("HttpHelpUtil..productInfo.is.null");
            return null;
        }

        List<Product> productList = productInfo.getProducts();

        if (productList == null || productList.size() <= 0) {
            logger.error("HttpHelpUtil..productList.size().is.null.or.zero,asin:{}",productInfoData.getAsin());
            return null;
        }

        Product product = productList.get(0);

        if (product == null) {
            logger.error("HttpHelpUtil.product.is.null,asin:{}", productInfoData.getAsin());
            return null;
        }

        List<ConditionGatingStatus> conditionGatingStatusList = product.getConditionGatingStatuses();
        /**
         * 可以销售的状态
         *                 {
         *                     "condition": "new",
         *                     "displayLabel": "New", 全新
         *                     "hasPathForward": true,
         *                     "gated": false
         *                 },
         *
         */
        AtomicBoolean isNew = new AtomicBoolean(false);
        conditionGatingStatusList.stream().forEach(conditionGatingStatus -> {
            if ("new".equalsIgnoreCase(conditionGatingStatus.getCondition()) &&
                    conditionGatingStatus.isHasPathForward() && !conditionGatingStatus.isGated()) {
                isNew.set(true);
                return;
            }
        });
        List<UnsellableReason> unsellableReasons = product.getUnsellableReasons();
        if (isNew.get() && (unsellableReasons == null || unsellableReasons.size() <= 0)) {
            if (StrUtil.isNotBlank(product.getUpc())) {
                outProductInfo.setUpc(product.getUpc());
            }
            if (StrUtil.isNotBlank(product.getEan())){
                outProductInfo.setEan(product.getEan());
            }
            return outProductInfo;
        }
        work.publishEvent(ConsoleUtil.console("Asin:"+productInfoData.getAsin()+" not new"));
        logger.info("HttpHelpUtil.this.is.not.available:{}",productInfoData.getAsin());
        return null;
    }


    /**
     * 替换不同国家站点域名信息
     * @param url
     * @return
     */
    private static String changeHostUrl(String url){
        return StrUtil.replace(url, "{HOST}", CountryWebEnum.getChooseHost(App.config.getCountryWeb()));

    }

    /**
     * 解析详情页面处理逻辑
     * @param url
     * @param inputProductInfo
     */
    public static void getFirstProductDetailInfo(String url, InputProductInfo inputProductInfo) {

        try{
            String html = HttpUtil.get(url);
            Document document = Jsoup.parse(html);
            Element commentsElement = document.getElementById("acrCustomerReviewText");
            if (commentsElement != null) {
                /**
                 * 评论数量
                 */
                String comments = commentsElement.text().replace("ratings","");
                if(!comments.isEmpty()){
                    inputProductInfo.setCommentsCount(comments);
                }
            }
            //品牌
            Element brandElement = document.getElementById("bylineInfo");
            if(brandElement != null && !brandElement.text().isEmpty()){
                inputProductInfo.setBrand(brandElement.text());
            }
            //acrPopover
            Element scoreElement = document.getElementById("acrPopover");
            if(scoreElement != null){
                String score = scoreElement.attr("title");
                String[] scores = score.split(" ");
                score = scores.length > 0 ? scores[0] : "";
                inputProductInfo.setScore(score);
            }

        }catch (Exception e){
            logger.error("getFirstProductDetailInfo:",e);
        }
    }


    /**
     * 解析详情页面处理逻辑
     * @param url
     * @param productInfoData
     */
    public static void getProductDetailInfo(String url, ProductInfoData productInfoData) {
        String html = HttpUtil.get(url);
        Document document = Jsoup.parse(html);
        Element commentsElement = document.getElementById("acrCustomerReviewText");
        if (commentsElement != null) {
            productInfoData.setCommentsCount(commentsElement.text().replace("ratings",""));
        }
        Element shopCartElement = document.getElementById("add-to-cart-button");
        if(shopCartElement == null){
            productInfoData.setShopCartNum("0");
        }else {
            productInfoData.setShopCartNum("1");
        }


        //acrPopover
        Element scoreElement = document.getElementById("acrPopover");
        String score = scoreElement.attr("title");
        String[] scores = score.split(" ");
        score = scores.length > 0 ? scores[0] : "";
        productInfoData.setScore(score);
        //productTitle
    }

    public static  void getProductDetailInfoDebug(String url,ProductInfoData productInfoData){

        //System.setProperty("webdriver.chrome.driver", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        //WebDriver driver = new ChromeDriver();
        try {
            String html = "";
            //driver.navigate().to(url);
            //html = driver.getPageSource();
            /**
             HttpRequest httpRequest = HttpUtil.createGet(url);
             Map<String, String> headerMap = new HashMap<>(1);
             headerMap.put("Cookie", App.config.getCookies());
             headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
             HttpResponse httpResponse = httpRequest.addHeaders(headerMap).timeout(HTTP_SEND_TIME_OUT).execute();


             if (!httpResponse.isOk()) {
             logger.error("HttpHelpUtil.response status:{} body:{}", httpResponse.getStatus(), httpResponse.body());
             }else {
             html = httpResponse.body();
             }

             URL url1 = new URL(url);
             HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
             connection.setRequestMethod("GET");
             connection.setRequestProperty("Accept","text/html");
             connection.connect();
             BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
             StringBuilder sb = new StringBuilder();
             for (String line;(line = br.readLine()) != null;) {
             sb.append(line);
             }
             html = sb.toString();
             **/
            html = HttpUtil.get(url);

            Document document = Jsoup.parse(html);
            Element commentsElement = document.getElementById("acrCustomerReviewText");

            if (commentsElement != null) {
                productInfoData.setCommentsCount(commentsElement.text());
            }


            //}catch (IOException e){
//
//
            //}
            /**   } catch (MalformedURLException e) {
             e.printStackTrace();
             } catch (ProtocolException e) {
             e.printStackTrace();
             } catch (IOException e) {
             e.printStackTrace();
             */
        } finally {
            //driver.close();
        }

    }

    public static void main(String[] args) {

        String url =  "https://www.amazon.com/dp/B08BCSS3GR";
        ProductInfoData productInfoData = new ProductInfoData();
        productInfoData.setAsin("B08BCSS3GR");
        getProductDetailInfo(url,productInfoData);

        if(3 * 0.1 == 0.3){
            System.out.println("true");
        }else {
            System.out.println("false'");
        }

    }
}
