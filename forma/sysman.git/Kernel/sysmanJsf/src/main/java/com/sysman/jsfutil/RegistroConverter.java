package com.sysman.jsfutil;

import com.sysman.dao.Registro;
import com.sysman.kernel.api.clientwso2.beans.Parameter;

import java.util.ArrayList;
import java.util.List;

public class RegistroConverter {

    private RegistroConverter() {
    }

    public static Registro toRegistro(Parameter parameter) {

        Registro reg = null;
        if (parameter != null) {
            reg = new Registro();
            reg.setCampos(parameter.getFields());
        }
        return reg;
    }

    public static Registro toRegistro(Parameter parameter,
        String[] nombreLlave) {

        Registro reg = null;
        if (parameter != null) {
            reg = new Registro();
            reg.setCampos(parameter.getFields());
            reg.asignarLlave(nombreLlave);
        }
        return reg;
    }

    public static List<Registro> toListRegistro(List<Parameter> parameters) {
        List<Registro> list = new ArrayList<>();
        for (Parameter par : parameters) {
            Registro reg = new Registro();
            reg.setCampos(par.getFields());
            list.add(reg);
        }
        return list;
    }

    public static List<Registro> toListRegistro(List<Parameter> parameters,
        String[] llave) {
        List<Registro> list = new ArrayList<>();
        for (Parameter par : parameters) {
            Registro reg = new Registro();
            reg.setCampos(par.getFields());
            reg.asignarLlave(llave);
            list.add(reg);
        }
        return list;
    }

}
