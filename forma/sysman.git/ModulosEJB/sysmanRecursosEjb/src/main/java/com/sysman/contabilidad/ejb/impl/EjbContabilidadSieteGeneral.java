package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadSieteGeneralLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class EjbContabilidadSeisGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbContabilidadSieteGeneral
                implements EjbContabilidadSieteGeneralRemote,
                EjbContabilidadSieteGeneralLocal {

    @Override
    public String generarPlanoH(
        String compania,
        String fechainicial,
        String fechafinal,
        String fuenteinicial,
        String fuentefinal,
        String chequeInicial,
        String chequeFinal, 
        String comprobanteInicial,
        String comprobanteFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINICIAL      =>'", fechainicial,
                                    "', ",
                                    "UN_FECHAFINAL        =>'", fechafinal,
                                    "', ",
                                    "UN_FUENTEINICIAL     =>'", fuenteinicial,
                                    "', ", "UN_FUENTEFINAL       =>'",
                                    fuentefinal,
                                    "', ", "UN_CHEQUEI       =>'", chequeInicial,
                                    "', ", "UN_CHEQUEF       =>'", chequeFinal,
                                    "', ", "UN_COMPROBANTEI       =>'", comprobanteInicial,
                                    "', ", "UN_COMPROBANTEF       =>'", comprobanteFinal,
                                    "'"
            };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_PLANOH",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoH2018(
        String compania,
        String fechainicial,
        String fechafinal,
        String fuenteinicial,
        String fuentefinal)
                    throws SystemException {

        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINICIAL      =>'", fechainicial,
                                    "', ",
                                    "UN_FECHAFINAL        =>'", fechafinal,
                                    "', ",
                                    "UN_FUENTEINICIAL     =>'", fuenteinicial,
                                    "', ", "UN_FUENTEFINAL       =>'",
                                    fuentefinal,
                                    "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_PLANOH_2018",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));

        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoHAdi(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String fuenteinicial,
        String fuentefinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FUENTEINICIAL     =>'", fuenteinicial,
                                    "', ", "UN_FUENTEFINAL       =>'",
                                    fuentefinal, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_PLANOH_ADI",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoHNomina(String compania,
        String fechainicial,
        String fechafinal,
        String fuenteinicial,
        String fuentefinal)
                    throws SystemException {

        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINICIAL      =>'", fechainicial,
                                    "', ",
                                    "UN_FECHAFINAL        =>'", fechafinal,
                                    "', ",
                                    "UN_FUENTEINICIAL     =>'", fuenteinicial,
                                    "', ", "UN_FUENTEFINAL       =>'",
                                    fuentefinal,
                                    "'" };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_PLANOH_NOMINA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));

        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
    
    
    @Override
    public void actualizarAbono(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        int consecutivo,
        BigDecimal abono,
        Date fechaabono,
        String usuario)
                    throws SystemException, ParseException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                                "UN_COMPROBANTE       =>",
                                comprobante.toString(), ", ",
                                "UN_CONSECUTIVO       =>",
                                Integer.toString(consecutivo), ", ",
                                "UN_ABONO             =>", abono.toString(),
                                ", ", "UN_FECHAABONO         =>TO_DATE('",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaabono),
                                "','DD/MM/YYYY') ",
                                ", ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.PR_CAMBIOABONO",
                        SysmanFunciones.concatenar(parametros));
    }
    
    

}
