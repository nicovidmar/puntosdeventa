package com.posservice.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PointOfSale implements Serializable {
    @Id
    private int id;
    private String name;

    public PointOfSale(String name) {
        this.name = name;
    }

}
