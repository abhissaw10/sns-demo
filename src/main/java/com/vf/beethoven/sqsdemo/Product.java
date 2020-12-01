package com.vf.beethoven.sqsdemo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class Product {
    private String productname;
    private String productType;
    private int quantity;
    private LocalDate expiryDate;
}
