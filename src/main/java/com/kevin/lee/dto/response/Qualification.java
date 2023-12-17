package com.kevin.lee.dto.response;

import lombok.Data;

@Data
public class Qualification {

    private String qualificationMessage;

    private String helpUrl;

    /**
     * "Collectible, Refurbished, Used, Used, Used, Used conditions"
     */
    private String conditionList;
}
