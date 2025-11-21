package com.br.renzoluigi.ciprianjospdv;

import java.math.BigDecimal;

public class FormatPrice {
    public static String bigDecimalToString(BigDecimal price) {
        return "R$ " + price.toString().replace(".", ",");
    }

    public static BigDecimal stringToBigDecimal(String price) {
        String formattedPrice = price.trim().replace(",", ".");
        return new BigDecimal(formattedPrice);
    }
}
