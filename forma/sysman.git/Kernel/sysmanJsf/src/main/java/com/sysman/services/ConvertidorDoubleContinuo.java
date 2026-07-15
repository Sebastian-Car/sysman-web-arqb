package com.sysman.services;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
@FacesConverter("convertidorDoubleContinuo")
public class ConvertidorDoubleContinuo implements Converter {

    private static DecimalFormat formateador;

    static {
        formateador = new DecimalFormat("###,###.######",
                        DecimalFormatSymbols
                                        .getInstance(Locale.US));
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
        String value) {

        return value;

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
        Object value) {
        if (value instanceof String) {
            BigDecimal valor = new BigDecimal(value.toString());
            return formateador.format(valor);
        }
        else {
            return formateador.format(value);
        }

    }

}
