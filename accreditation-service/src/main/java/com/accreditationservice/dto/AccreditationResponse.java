package com.accreditationservice.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccreditationResponse {
    private Long id;
    private double amount;
    private int pointOfSaleId;
    private String pointOfSaleName;
    private Date date;
}
