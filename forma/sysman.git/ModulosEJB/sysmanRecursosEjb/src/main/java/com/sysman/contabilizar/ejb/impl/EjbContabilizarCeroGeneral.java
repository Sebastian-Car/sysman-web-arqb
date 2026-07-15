/*-
 * EjbContabilizarCeroGeneral.java
 *
 * 1.0
 * 
 * 5/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarCeroGeneralLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarCeroGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * 
 * 
 * @version 1.0, 5/02/2018
 * @author jrodrigueza
 *
 */
@Stateless
@LocalBean

public class EjbContabilizarCeroGeneral
                implements EjbContabilizarCeroGeneralLocal,
                EjbContabilizarCeroGeneralRemote {

    /**
     * Default constructor.
     */
    public EjbContabilizarCeroGeneral() {
        // constructor sin parametros
    }

    @Override
    public String contabilizarPorPlano(
        String compania,
        boolean resumido,
        boolean sinpptal,
        boolean terceroDetalle,
        boolean conciliar,
        String plano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RESUMIDO          =>",
                                resumido ? "-1" : "0", ", ",
                                "UN_SINPPTAL          =>",
                                sinpptal ? "-1" : "0", ", ",
                                "UN_TERCERODETALLE    =>",
                                terceroDetalle ? "-1" : "0", ", ",
                                "UN_CONCILIAR         =>",
                                conciliar ? "-1" : "0", ", ",
                                "UN_PLANO             =>", plano, ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIZAR.FC_CONTABILIZARPORPLANO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);

        }
    }

    @Override
    public String contabilizarPorPlanoEsp(
        String compania,
        boolean resumido,
        boolean sinpptal,
        boolean terceroDetalle,
        boolean conciliar,
        String plano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RESUMIDO          =>",
                                resumido ? "-1" : "0", ", ",
                                "UN_SINPPTAL          =>",
                                sinpptal ? "-1" : "0", ", ",
                                "UN_TERCERODETALLE    =>",
                                terceroDetalle ? "-1" : "0", ", ",
                                "UN_CONCILIAR         =>",
                                conciliar ? "-1" : "0", ", ",
                                "UN_PLANO             =>", plano, ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIZAR.FC_CONTABILIZARPORPLANOESP",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);

        }
    }
    
    @Override
    public String contabilizarHNivelesCC(
        String compania,
        int ano,
        int mes,
        Date fechinterf,
        String tipo,
        BigDecimal numero,
        boolean niif,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_FECHINTERF        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechinterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_NIIF              =>",
                                    niif ? "-1" : "0", ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_ALMACEN.FC_CONTABILIZARHNIVELESCC",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public String contabilizarPlanoSIOT(
        String compania,
        boolean conciliar,
        String plano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCILIAR         =>",
                                conciliar ? "-1" : "0", ", ",
                                "UN_PLANO             =>", plano, ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIZAR.FC_CONTABILIZARPLANOSIOT",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);

        }
    }

}
