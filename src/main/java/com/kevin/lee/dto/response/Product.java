package com.kevin.lee.dto.response;

import lombok.Data;

import java.util.List;


@Data
public class Product {

    private String asin;

    private String detailPageURL;

    private String imageUrl;

    private String marketplaceToVariationAsins;

    private String title;

    private String ean;

    private String upc;

    private String isbn;

    private String gtin;

    private String salesRank;

    private OfferCounts offerCounts;


    private List<Qualification> qualificationMessages;


    /**
     * 可以销售的状态
     *              {
     *                     "condition": "used",
     *                     "displayLabel": "Used",
     *                     "hasPathForward": false,
     *                     "gated": true
     *                 },
     *                 {
     *                     "condition": "new",
     *                     "displayLabel": "New",
     *                     "hasPathForward": true,
     *                     "gated": false
     *                 },
     *
     */
    private List<ConditionGatingStatus> conditionGatingStatuses;

    private List<UnsellableReason> unsellableReasons;

    private boolean restrictedForAnyCondition;

    private boolean  restrictedForAllConditions;

    private String pathToSellUrl;

    private String productToken;

    private String parent;


}
