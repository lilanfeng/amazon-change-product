package com.kevin.lee.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RefinementSummary {

    private String absURL;

    private List<Refinement> refinementsData;

    private boolean invalidRefinementSummary;
}
