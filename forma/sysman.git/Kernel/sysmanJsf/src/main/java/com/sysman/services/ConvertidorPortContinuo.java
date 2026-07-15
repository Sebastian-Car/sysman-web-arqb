package com.sysman.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cmanrique
 */
@FacesConverter("convertidorPortContinuo")
public class ConvertidorPortContinuo implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
        String value) {

        return value;

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
        Object value) {
        BigDecimal val = null;
        if (value instanceof BigDecimal) {
            val = ((BigDecimal) value).multiply(BigDecimal.valueOf(100));
        }
        else if (value instanceof Double) {
            val = BigDecimal.valueOf(((Double) value) * 100);
        }
        else if (value instanceof Float) {
            val = BigDecimal.valueOf(((Float) value) * 100);
        }
        else if (value instanceof BigInteger) {
            val = new BigDecimal(((BigInteger) value).multiply(BigInteger.valueOf(100)).toString());
        }
        return NumberFormat.getNumberInstance(Locale.US).format(val) + "%";

    }
}
