package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCeroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosCero
 * 
 * @version 2.0, 10/06/2017, <strong>pespitia</strong>:<br>
 * Implementacion de la funcion SysmanFunciones.concatenar
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosCero implements EjbServiciosPublicosCeroRemote,
                EjbServiciosPublicosCeroLocal {

    @EJB
    private EjbServiciosPublicosCeroGeneralRemote ejbServiciosPublicosCeroGeneral;

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosCero() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public String asignarNombrePeriodo(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_PERIODO           =>'", periodo, "', ",
                         "UN_FRECUENCIA        =>", (frecuencia != null
                             ? SysmanFunciones.colocarComillas(frecuencia)
                             : null),
                         " " };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public String cambiarFechaPago(
        String compania,
        String bancoant,
        Date fechaant,
        String paqueteant,
        String banconue,
        Date fechanue,
        String paquetenue,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_BANCOANT          =>'", bancoant, "', ",
                             "UN_FECHAANT          =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechaant),
                             "','DD/MM/YYYY'), ", "UN_PAQUETEANT        =>'",
                             paqueteant, "', ", "UN_BANCONUE          =>'",
                             banconue, "', ",
                             "UN_FECHANUE          =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechanue),
                             "','DD/MM/YYYY'), ", "UN_PAQUETENUE        =>'",
                             paquetenue, "', ", "UN_USUARIO           =>'",
                             usuario, "' " };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.FC_CAMBIARFECHAPAGO",
                            SysmanFunciones.concatenar(par),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String insertarAuditoriaSinLectura(
        String compania,
        String usuario,
        String proceso,
        String ciclo)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_USUARIO           =>'", usuario, "', ",
                         "UN_PROCESO           =>'", proceso, "', ",
                         "UN_CICLO             =>'", ciclo, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_AUDITORIASINLECTURA",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public void reasignarConceptoDiferenteDeDoce(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHAINICIAL      =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechainicial),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHAFINAL        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechafinal),
                             "','DD/MM/YYYY'),",
                             "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_RECONSTRUIR_12",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarDetalladeDeAbonos(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHAINICIAL      =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechainicial),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHAFINAL        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechafinal),
                             "','DD/MM/YYYY'),",
                             "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_CHARLESPESO",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean insertarAuditoriaDelUsuario(
        String compania,
        int ciclo,
        String codigo,
        boolean valfinal,
        boolean valinicial,
        String campoActual,
        String campoAnterior,
        String periodo, String usuario)
                    throws SystemException {

        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ", "UN_CODIGO            =>'", codigo, "', ",
                         "UN_VALFINAL          =>", (valfinal ? "-1" : "0"),
                         ", ", "UN_VALINICIAL        =>",
                         (valinicial ? "-1" : "0"), ", ",
                         "UN_CAMPOACTUAL            =>'", campoActual, "', ",
                         "UN_CAMPOANTERIOR         =>'", campoAnterior, "', ",
                         "UN_PERIODO           =>'", periodo, "', ",
                         "UN_USUARIO           =>'", usuario, "' " };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_ACTUALIZAAUDI",
                        SysmanFunciones.concatenar(par),
                        Types.TINYINT);

        return rta != 0;
    }

    @Override
    public void actualizarDetalleDeRecaudo(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHAINICIAL      =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechainicial),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHAFINAL        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechafinal),
                             "','DD/MM/YYYY'),",
                             "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_CHARLESPRESORECAUDOS",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void reasignarDetalleDelRecaudo(
        String compania,
        Date fechainicial,
        Date fechafinal, String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHAINICIAL      =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechainicial),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHAFINAL        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechafinal),
                             "','DD/MM/YYYY'),",
                             "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_RECONSTRUIRD_RECAUDO",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarDetalleDelRecuadoEntreFechas(
        String compania,
        Date fechainicial,
        Date fechafinal, String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHAINICIAL      =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechainicial),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHAFINAL        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechafinal),
                             "','DD/MM/YYYY'),",
                             "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_RECRECAUDOS",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void pasarAbonosDeRecuados(
        String compania,
        Date fecha,
        String banco,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ", "UN_BANCO             =>'",
                             banco, "', ", "UN_USUARIO           =>'", usuario,
                             "'"
            };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_PASAABONOSRECAUDOS",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void validarBancoAbonos(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHAINICIAL      =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechainicial),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHAFINAL        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechafinal),
                             "','DD/MM/YYYY'), ", "UN_USUARIO           =>'",
                             usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_INTERFAZABONOS",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void ajustarPesoDeRecaudo(
        String compania,
        Date fechainicial,
        Date fechafinal, String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHAINICIAL      =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechainicial),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHAFINAL        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechafinal),
                             "','DD/MM/YYYY')', ", "UN_USUARIO           =>'",
                             usuario,
                             "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS.PR_AJUSTEPESORECAUDO",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String escribirPeriodo(
        String compania,
        BigDecimal periodo,
        int ano)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_PERIODO           =>", periodo.toString(), ", ",
                         "UN_ANO               =>", Integer.toString(ano), "" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_ESCRIBIRPERIODO",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public void controlarCopia(
        String compania,
        int ciclo,
        String codigo,
        String tipo,
        String aplica,
        String timpresion,
        String user)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ", "UN_CODIGO            =>'", codigo, "', ",
                         "UN_TIPO              =>'", tipo, "', ",
                         "UN_APLICA            =>'", aplica, "', ",
                         "UN_TIMPRESION        =>'", timpresion, "', ",
                         "UN_USER              =>'", user, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.PR_CONTROLCOPIA",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public boolean autorizarMicromedicion(
        String compania,
        String nit)
                    throws SystemException {

        return ejbServiciosPublicosCeroGeneral.autorizarMicromedicion(compania,
                        nit);
    }

    @Override
    public boolean generarMicroconsumos(
        String compania,
        int ciclo,
        String strperiodo,
        int intano)
                    throws SystemException {

        return ejbServiciosPublicosCeroGeneral.generarMicroconsumos(compania,
                        ciclo, strperiodo, intano);
    }

    @Override
    public String prepararAnoPeriodoSiguiente(
        String compania,
        int ano,
        String periodo,
        String tipoRetorno,
        String frecuencia)
                    throws SystemException {

        return ejbServiciosPublicosCeroGeneral.prepararAnoPeriodoSiguiente(
                        compania, ano, periodo, tipoRetorno, frecuencia);
    }

    @Override
    public String prepararAnoPeriodoAnterior(
        String compania,
        int ano,
        String periodo,
        String tipoRetorno,
        String frecuencia)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_PERIODO           =>'", periodo, "', ",
                         "UN_TIPO_RETORNO      =>'", tipoRetorno, "', ",
                         "UN_FRECUENCIA        =>", (frecuencia != null
                             ? SysmanFunciones.colocarComillas(frecuencia)
                             : null),
                         ""
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_ANTERIOR",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public String prepararPeriodoSiguiente(
        String compania,
        int intano,
        String strperiodo,
        int numperiodos,
        String frecuencia,
        int modulo)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_INTANO            =>", Integer.toString(intano),
                         ", ", "UN_STRPERIODO        =>'", strperiodo, "', ",
                         "UN_NUMPERIODOS       =>",
                         Integer.toString(numperiodos), ", ",
                         "UN_FRECUENCIA        =>'", frecuencia, "', ",
                         "UN_MODULO            =>", Integer.toString(modulo),
                         "" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_PERN",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public String prepararAnoSiguiente(
        String compania,
        int intano,
        String strperiodo,
        int numperiodos,
        String frecuencia,
        int modulo)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_INTANO            =>", Integer.toString(intano),
                         ", ", "UN_STRPERIODO        =>'", strperiodo, "', ",
                         "UN_NUMPERIODOS       =>",
                         Integer.toString(numperiodos), ", ",
                         "UN_FRECUENCIA        =>'", frecuencia, "', ",
                         "UN_MODULO            =>", Integer.toString(modulo), ""
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_ANON",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public String prepararCritica(
        String compania,
        int modulo,
        int ciclo,
        String strcodigoini,
        String strcodigofin,
        String consumoMenor,
        int ano,
        int periodo,
        double porcMenor,
        double porcMayor,
        boolean normales,
        boolean manual,
        boolean iguales,
        boolean desviacion,
        String usuario,
        boolean reporte)
                    throws SystemException {

        return ejbServiciosPublicosCeroGeneral.prepararCritica(compania, modulo,
                        ciclo, strcodigoini, strcodigofin, consumoMenor, ano,
                        periodo, porcMenor, porcMayor, normales, manual,
                        iguales, desviacion, usuario, reporte);
    }

    @Override
    public String asignarNombreUso(
        String compania,
        String codigo)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CODIGO            =>'", codigo, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_NOMBREUSO",
                        SysmanFunciones.concatenar(par),
                        Types.VARCHAR);
    }

    @Override
    public void actualizarFrecuencia(
        String compania,
        int ciclo,
        String codini,
        String codfin,
        String actividad,
        BigDecimal frecuencia,
        String usuario)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ", "UN_CODINI            =>'", codini, "', ",
                         "UN_CODFIN            =>'", codfin, "', ",
                         "UN_ACTIVIDAD         =>'", actividad, "', ",
                         "UN_FRECUENCIA        =>", frecuencia.toString(), ", ",
                         "UN_USUARIO           =>'", usuario, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.PR_ACTUALIZAFRECUENCIAS",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void desactivarServicioAseoUrbano(
        String compania,
        int ciclo,
        String codigoruta,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ", "UN_CODIGORUTA        =>'", codigoruta, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_PERIODO           =>'", periodo, "', ",
                         "UN_USUARIO           =>'", usuario, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.PR_DESACTIVAR_SERV_ASEOURB",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void activarServicioAseoUrbano(
        String compania,
        int ciclo,
        String codigoruta,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ", "UN_CODIGORUTA        =>'", codigoruta, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_PERIODO           =>'", periodo, "', ",
                         "UN_USUARIO           =>'", usuario, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.PR_ACTIVAR_SERV_ASEOURB",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public int actualizarChapetas(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        String usuario)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_CICLO             =>", Integer.toString(ciclo),
                         ", ", "UN_CODIGOINICIAL     =>'", codigoinicial,
                         "', ", "UN_CODIGOFINAL       =>'", codigofinal, "', ",
                         "UN_USUARIO           =>'", usuario, "'" };

        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS.FC_ACTUALIZACHAPETA",
                        SysmanFunciones.concatenar(par), Types.INTEGER);
    }

}