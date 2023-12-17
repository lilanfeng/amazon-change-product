package com.kevin.lee.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author lilanfeng
 */
@Data
public class UnsellableReason {

    private List<String> conditionList;

    private String helpUrl;

    private String qualificationMessage;

}
