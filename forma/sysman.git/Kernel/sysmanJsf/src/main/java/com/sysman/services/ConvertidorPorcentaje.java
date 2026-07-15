package com.sysman.services;

import java.math.BigDecimal;

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
@FacesConverter("convertidorPorc")
public class ConvertidorPorcentaje implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
        String value) {
        return Double.parseDouble(value) / 100;

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
        Object value) {
        BigDecimal valor = new BigDecimal(0);
        if (value instanceof String) {
            valor = new BigDecimal((String) value);
        }
        else if (value instanceof Double) {
            valor = BigDecimal.valueOf((Double) value);
        }
        else if (value instanceof Float) {
            valor = BigDecimal.valueOf((Float) value);
        }
        else if (value instanceof BigDecimal) {
            valor = (BigDecimal) value;
        }

        BigDecimal val = valor.multiply(BigDecimal.valueOf(100));

        return val.toString();

    }
}
