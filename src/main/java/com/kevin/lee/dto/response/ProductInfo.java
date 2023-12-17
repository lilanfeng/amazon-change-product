package com.kevin.lee.dto.response;


import lombok.Data;

import java.util.List;

/**
 * @author lilanfeng
 */
@Data
public class ProductInfo {

    private Integer numberOfResults;

    private RefinementSummary refinementSummary;

    private List<Product> products;

    private Pagination pagination;

    private String marketplaceId;

    private String debugInfo;
}
