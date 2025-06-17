package com.notifierservice.event;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
