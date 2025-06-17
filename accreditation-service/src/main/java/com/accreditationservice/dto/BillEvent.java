package com.accreditationservice.dto;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BillEvent implements Serializable {
    private String email;
    private String pointOfSaleName;
    private double amount;
    private Date date;
}