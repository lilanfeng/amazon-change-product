package com.kevin.lee.dto.response;

import lombok.Data;

/**
 * @author lilanfeng
 */
@Data
public class ConditionGatingStatus {
    private String condition;

    private String displayLabel;

    private boolean hasPathForward;


    private boolean gated;

}
