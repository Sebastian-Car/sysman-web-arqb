package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;
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
 * Session Bean implementation class ContabilizarAlmacenCero
 */
@Stateless
@LocalBean

public class EjbContabilizarAlmacenCero
                implements EjbContabilizarAlmacenCeroRemote,
                EjbContabilizarAlmacenCeroLocal {
    /**
     * Default constructor.
     */
    public EjbContabilizarAlmacenCero() {
    }

    @Override
    public String contabilizarAlmcnH(
        String companiaOrigen,
        String companiaDestino,
        Date fechaInterf,
        String tipo,
        int numero,
        String tercero,
        String sucursal,
        String centrocosto,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIAORIGEN    =>'", companiaOrigen,
                                    "', ",
                                    "UN_COMPANIADESTINO   =>'", companiaDestino,
                                    "', ",
                                    "UN_FECHINTERF        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    String.valueOf(numero), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_CENTROCOSTO       =>'", centrocosto,
                                    "', ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };

            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIZAR_ALMACEN.FC_AJUSTESNIVELESNIIF",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException | ParseException e) {

            throw new SystemException(e);

        }

    }

    @Override
    public String contabilizarAlmcnH(
        String compania,
        int ano,
        int mes,
        Date fechaInterf,
        String tipo,
        BigDecimal numero,
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
                                                    fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "'" };
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIZAR_ALMACEN.FC_CONTABILIZARALMCNH",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarHNiveles(
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
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_ALMACEN.FC_CONTABILIZARHNIVELES",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
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
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
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
    public String contabilizarArmConsltHNvles(
        String compania,
        Date fechaInterf,
        boolean niif)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHINTERF        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NIIF              =>",
                                    niif ? "-1" : "0", ""
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_ALMACEN.FC_CONTBLIZARARMCONSLTHNVLES",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarArmConsltHNvlesCC(
        String compania,
        Date fechaInterf,
        boolean niif)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHINTERF        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NIIF              =>",
                                    niif ? "-1" : "0", ""
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_ALMACEN.FC_CONTBLIZARARMCONSLTHNVLESCC",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarRetiroActivos(
        String compania,
        int ano,
        int mes,
        Date fechinterf,
        String tipo,
        int numero,
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
                                    String.valueOf(numero), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };

            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIZAR_ALMACEN.FC_CONTABILIZARRETIROACTIVOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException | ParseException e) {

            throw new SystemException(e);

        }

    }

    @Override
    public void actualizarBodegaTipoActivo(
        String compania,
        String elemento,
        int ano,
        String centrocosto,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ELEMENTO          =>'", elemento, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CENTROCOSTO       =>'", centrocosto, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIZAR_ALMACEN.PR_ACT_BODEGATIPOACTIVO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String insertarComprobTransicion(
        String compania,
        int ano,
        int mes,
        String tipo,
        Date fecha,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_ALMACEN.FC_INTERFAZ_TRANSICION",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    

    @Override
    public void insertaAlmacenContabilidadCC(
    		String compania,
    		String codigoelemento,
    		String tipo,
    		String centrocosto,
    		String fuenterecurso,
    		int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOELEMENTO    =>'", codigoelemento, "', ",
                                "UN_TIPO              =>'", tipo, "', ",                              
                                "UN_CENTROCOSTO       =>'", centrocosto, "', ",
                                "UN_FUENTERECURSO     =>'" , fuenterecurso , "', ",
                                "UN_ANO               =>", Integer.toString(ano), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                         "PCK_CONTABILIZAR_ALMACEN.PR_INSERTA_ALMCONTABILIDADCC",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void insertaAlmacenContabilidad(
    		String compania,
    		String codigoelemento,
    		String tipo,
    		int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOELEMENTO    =>'", codigoelemento, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_ANO               =>", Integer.toString(ano), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIZAR_ALMACEN.PR_INSERTA_ALMACENCONTABILIDAD",
                        SysmanFunciones.concatenar(parametros));
    }


    @Override
    public  void  insertaAlmacenContabilidadFR(
    		String compania, 
    		String codigoelemento, 
    		String tipo, 
    		String fuenterecurso, 
    		int ano) 
    				throws SystemException {
    	String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
    			, "UN_CODIGOELEMENTO    =>'" , codigoelemento , "', "
    			, "UN_TIPO              =>'" , tipo , "', "
    			, "UN_FUENTERECURSO     =>'" , fuenterecurso , "', "
    			, "UN_ANO               =>" , Integer.toString(ano) , ""
    	};
    	AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTABILIZAR_ALMACEN.PR_INSERTAR_ALMCONT_FR",
    			SysmanFunciones.concatenar(parametros));
    }



}