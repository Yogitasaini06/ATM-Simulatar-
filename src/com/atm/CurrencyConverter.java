package com.atm;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class CurrencyConverter {

    /** [DEFAULT_CURRENCY] - base/reference currency for all rates. */
    public static final String BASE_CURRENCY = "USD";

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_EVEN);
    private final Map<String, BigDecimal> ratesPerBase = new HashMap<>();

    public CurrencyConverter(Map<String, BigDecimal> rates) {
        for (Map.Entry<String, BigDecimal> e : rates.entrySet()) {
            if (e.getValue() == null || e.getValue().signum() <= 0) {
                throw new IllegalArgumentException("Rate must be positive for " + e.getKey());
            }
            ratesPerBase.put(e.getKey().trim().toUpperCase(), e.getValue());
        }
        ratesPerBase.put(BASE_CURRENCY, BigDecimal.ONE); // base is always 1:1
    }

    
    public static CurrencyConverter withSampleRates() {
        Map<String, BigDecimal> r = new HashMap<>();
        r.put("EUR", new BigDecimal("0.92"));
        r.put("GBP", new BigDecimal("0.79"));
        r.put("INR", new BigDecimal("83.20"));
        r.put("JPY", new BigDecimal("150.00"));
        return new CurrencyConverter(r);
    }

    
    public String normalize(String code) {
        if (code == null) throw new UnknownCurrencyException("null");
        String c = code.trim().toUpperCase();
        if (!ratesPerBase.containsKey(c)) throw new UnknownCurrencyException(code);
        return c;
    }

    public Set<String> supportedCurrencies() {
        return new TreeSet<>(ratesPerBase.keySet());
    }

    
    public BigDecimal convert(BigDecimal amount, String from, String to) {
        String f = normalize(from);
        String t = normalize(to);
        if (f.equals(t)) return amount.setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal inBase = amount.divide(ratesPerBase.get(f), MC);
        return inBase.multiply(ratesPerBase.get(t), MC).setScale(2, RoundingMode.HALF_EVEN);
    }
}
