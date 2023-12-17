package com.kevin.lee.utils;

import com.keepa.api.backend.KeepaAPI;
import com.keepa.api.backend.helper.KeepaTime;
import com.keepa.api.backend.helper.ProductAnalyzer;
import com.keepa.api.backend.structs.AmazonLocale;
import com.keepa.api.backend.structs.Product;
import com.keepa.api.backend.structs.Request;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lilanfeng
 */
public class KeepaApiUtil {

    /**
     * Product Search
     * Token costs: 10 per result page (up to 10 results)
     *
     * Search for Amazon products using keywords with a maximum of 100 results per search term. By default, the
     * product search response contains the product objects of the found products.
     *
     * Query:
     *
     * /search?key=<yourAccessKey>&domain=<domainId>&type=product&term=<searchTerm>
     *
     * <yourAccessKey>
     * Your private API key.
     *
     * <domainId>
     * Integer value for the Amazon locale you want to access.
     * Valid values: [ 1: com | 2: co.uk 56 | 3: de | 4: fr | 5: co.jp | 6: ca | 8: it | 9: es | 10: in | 11: com.mx ]
     *
     * <searchTerm>
     * The term you want to search for. Should be URL encoded 664.
     *
     * Optional additional parameters:
     *
     * ✜ asins-only
     * If provided and has the value 1, only the ASINs of the found products will be provided (instead of the product
     * objects).
     *
     * ✜ page
     * Integer value. Valid values 0 - 9. Each search result page provides up to 10 results. To retrieve more
     * results, iterate the page parameter and keep all other parameters identical. Start with page 0 and stop when
     * the response contains less than 10 results or you have reached page 9, which is the limit. When not using the
     * page parameter, the first 40 results will be returned. Example: &page=0
     *
     * Example: &page=0
     *
     * ✜ stats
     * No extra token cost. If specified, the product object will have a stats field with quick access to current
     * prices, min/max prices, and the weighted mean values. You can provide the stats parameter in two forms:
     *
     * Last x days (positive integer value): calculates the stats of the last x days, where x is the value of the
     * stats parameter.
     * Interval: You can provide a date range for the stats calculation. You can specify the range via two timestamps
     * (unix epoch time milliseconds) or two date strings (ISO8601, with or without time in UTC).
     * Note: If there is insufficient historical data for a price type, the actual interval of the weighted mean
     * calculation may be shorter than specified. All data provided via the stats field are calculated using the
     * product object’s csv history field, so there is no new data provided through this parameter.
     * Example:
     * &stats=180 (the last 180 days)
     * &stats=2015-10-20,2015-12-24 (in the range from Oct. 20 to Dec. 24 in 2015)
     * &stats=1445299200000,1450915200000 (unix epoch time milliseconds, in the range from Oct. 20 to Dec. 24 in 2015)
     * ✜ update
     * Additional token cost: 0 or 1 per found product
     * Positive integer value. If the product’s last update is older than update hours, force a refresh. The default
     * value the API uses is 1 hour.
     *
     * Using this parameter, you can achieve the following:
     * Speed up requests if up-to-date data is not required by using a higher value than 1 hour. No extra token cost.
     * Always retrieve live data with the value 0. If our last update for the product was less than 1 hour ago, this
     * consumes 1 extra token for this product.
     * Example: &update=48 (only trigger an update if the product’s last update is older than 48 hours)
     * ✜ history
     * No extra token cost. Boolean value (0 = false, 1 = true). If specified and has the value 0, the product object
     * will not include the csv field. If you do not need the product object’s csv field, use this to remove it from
     * the response. This will improve processing time and considerably decrease the size of the response.
     *
     * Example: &history=0 (remove the csv history data field from the product object)
     * ✜ rating
     * Up to 1 extra token cost per found product - maximum of 5 additional tokens per search. Boolean value (0 =
     * false, 1 = true). If specified and has the value 1, the product object will include our existing RATING and
     * COUNT_REVIEWS history of the csv field. The extra token will only be consumed if our last update to both data
     * points is less than 14 days ago. Using this parameter does not trigger an update to those two fields, it only
     * gives access to our existent data if available. If you need up-to-date data, you have to use the offers
     * parameter of a separate product request. Use this if you need access to the rating data, which may be
     * outdated, but do not need any other data fields provided through the offers parameter to save tokens and speed
     * up the request. If there is no rating data returned, you can still make a separate product request using the
     * offers parameter.
     *
     * Example: &rating=1 (include the rating and review count data in the csv history data field of the product
     * object and respective fields of statistics object)
     */
    public static boolean getProductSearch(String term, int page,List<Product> list){

        AtomicBoolean hasNext = new AtomicBoolean(true);
        KeepaAPI api = new KeepaAPI(ConfigUtil.getInstance().getKeePaKey(ConfigUtil.getInstance().getCountryWeb()));
        Integer stats = 0;
        int update = 24;
        boolean history = false;
        boolean asinsOnly = false;

        Request r = Request.getProductSearchRequest(AmazonLocale.US, term,stats,update,history, asinsOnly, page);

        api.sendRequest(r).done(result -> {
                    switch (result.status) {
                        case OK:
                            if(result.products.length <= 0){
                                hasNext.set(false);
                            }
                            for (Product product : result.products){
                                // System.out.println(product);
                                if (product.productType == Product.ProductType.STANDARD.code) {
                                    //get basic data of product and print to stdout
                                    int currentAmazonPrice = ProductAnalyzer.getLast(product.csv[Product.CsvType.AMAZON.index], Product.CsvType.AMAZON);
                                    //check if the product is in stock -1 -> out of stock
                                    if (currentAmazonPrice == -1) {
                                        System.out.println(product.asin + " " + product.title + " is currently out of stock!");
                                    } else {
                                        System.out.println(product.asin + " " + product.title + " Current Amazon Price: " + currentAmazonPrice);
                                    }

                                    // get weighted mean of the last 90 days for Amazon
                                    int weightedMean90days = ProductAnalyzer.calcWeightedMean(product.csv[Product.CsvType.AMAZON.index], KeepaTime.nowMinutes(), 90, Product.CsvType.AMAZON);
                                    list.add(product);
                                }
                            }

                            break;
                        default:
                            System.out.println(result);
                    }
                })
                .fail(failure -> System.out.println(failure));

        return hasNext.get();
    }
}
