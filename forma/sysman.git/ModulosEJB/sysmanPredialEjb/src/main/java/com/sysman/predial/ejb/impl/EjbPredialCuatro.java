package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCuatroLocal;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialCuatro
 *
 * -- Modificado por lcortes 10/06/2017. Implementacion metodo
 * concatenar de la clase SysmanFunciones para el envio de parametros
 * a los diferentes procedimientos y funciones.
 */
@Stateless
@LocalBean
public class EjbPredialCuatro
                implements EjbPredialCuatroRemote, EjbPredialCuatroLocal {
    /**
     * Default constructor.
     */
    public EjbPredialCuatro() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public String getRegistroPagoCuotaInicial(
        String compania,
        String numeroorden,
        String usuario,
        String observacion,
        Date fechacorte,
        String codpredio,
        String codigobanco,
        String nroacuerdo,
        String nrorecibo,
        BigDecimal totalpagado,
        int nrocuota,
        String trpcod)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA      => '", compania, "', ",
                                    "UN_NUMEROORDEN   => '", numeroorden,
                                    "', ", "UN_USUARIO    =>'", usuario,
                                    "', ", "UN_OBSERVACION => '",
                                    observacion, "', ",
                                    "UN_FECHACORTE     => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'),  ",
                                    "UN_CODPREDIO         => '", codpredio,
                                    "', ", "UN_CODIGOBANCO       =>'",
                                    codigobanco, "', ",
                                    "UN_NROACUERDO        => '", nroacuerdo,
                                    "', ", "UN_NRORECIBO         => '",
                                    nrorecibo, "', ",
                                    "UN_TOTALPAGADO       => ",
                                    totalpagado.toString(), ", ",
                                    "UN_NROCUOTA          => ",
                                    Integer.toString(nrocuota), ", ",
                                    "UN_TRPCOD            => '", trpcod, "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.FC_REGISTROPAGOCUOTAINICIAL",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getRegistroPagoCuotaInicial(
        String compania,
        String nrorecibo,
        String codbanco,
        Date fechacorte,
        String usuario,
        String nroacuerdo,
        String codpredio,
        int nrocuota,
        BigDecimal totalpagado,
        String obspagos)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA   => '", compania, "', ",
                                    "UN_NRORECIBO   => '", nrorecibo, "', ",
                                    "UN_CODBANCO    => '", codbanco, "', ",
                                    "UN_FECHACORTE  => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'),  ", "UN_USUARIO     => '",
                                    usuario, "', ", "UN_NROACUERDO  => '",
                                    nroacuerdo, "', ", "UN_CODPREDIO   => '",
                                    codpredio, "', ", "UN_NROCUOTA    => ",
                                    Integer.toString(nrocuota), ", ",
                                    "UN_TOTALPAGADO => ",
                                    totalpagado.toString(), ", ",
                                    "UN_OBSPAGOS    => '", obspagos, "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.FC_REGISTROPAGOCUOTAACEPTAR",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getAnularRegistroPago(
        String compania,
        String numeroorden,
        String numfactura,
        String codpredio,
        String pagban,
        String paquete,
        Date fechapago,
        String acuerdo,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA   => '", compania, "', ",
                                    "UN_NUMEROORDEN => '", numeroorden, "', ",
                                    "UN_NUMFACTURA  => '", numfactura, "', ",
                                    "UN_CODPREDIO   => '", codpredio, "', ",
                                    "UN_PAGBAN      => '", pagban, "', ",
                                    "UN_PAQUETE     => '", paquete, "', ",
                                    "UN_FECHAPAGO   => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechapago),
                                    "', 'DD/MM/YYYY'), ", "UN_ACUERDO     => '",
                                    acuerdo, "', ", "UN_USUARIO     => '",
                                    usuario, "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.FC_ANULARREGISTROPAGOPREDIAL",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getReactivarPredio(
        String compania,
        int nivel,
        String codigo,
        String noresolucion,
        Date fecresolucion,
        String elabresolucion,
        String firmaresolucion,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA        => '", compania, "', ",
                                    "UN_NIVEL           => ",
                                    Integer.toString(nivel), ", ",
                                    "UN_CODIGO          => '", codigo, "', ",
                                    "UN_NORESOLUCION    => '", noresolucion,
                                    "', ", "UN_FECRESOLUCION   => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecresolucion),
                                    "', 'DD/MM/YYYY'), ",
                                    "UN_ELABRESOLUCION  => '", elabresolucion,
                                    "', ", "UN_FIRMARESOLUCION => '",
                                    firmaresolucion, "', ",
                                    "UN_USUARIO         => '", usuario, "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.FC_REACTIVARPREDIO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getAnularPredio(
        String compania,
        int nivel,
        String codigo,
        String noresolucion,
        Date fecresolucion,
        String elabresolucion,
        String firmaresolucion,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                    "UN_NIVEL           =>",
                                    Integer.toString(nivel), ", ",
                                    "UN_CODIGO          =>'", codigo, "', ",
                                    "UN_NORESOLUCION    =>'", noresolucion,
                                    "', ", "UN_FECRESOLUCION   =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecresolucion),
                                    "' , 'DD/MM/YYYY'), ",
                                    "UN_ELABRESOLUCION  =>'", elabresolucion,
                                    "', ", "UN_FIRMARESOLUCION =>'",
                                    firmaresolucion, "', ",
                                    "UN_USUARIO         =>'", usuario, "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.FC_ANULARPREDIO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void registrarPreinscripcion(
        int anioprescripcion,
        String codigousuario,
        String compania,
        String codigopred,
        String fechaprescripcion,
        String resolucion,
        String observacion)
                    throws SystemException {
        String[] parametros = { "UN_ANIOPRESCRIPCION  => ",
                                Integer.toString(anioprescripcion), ", ",
                                "UN_CODIGOUSUARIO     => '", codigousuario,
                                "', ", "UN_COMPANIA       =>  '", compania,
                                "', ", "UN_CODIGOPRED        => '", codigopred,
                                "', ", "UN_FECHAPRESCRIPCION => '",
                                fechaprescripcion, "', ",
                                "UN_RESOLUCION        => '", resolucion, "', ",
                                "UN_OBSERVACION     => '", observacion, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.PR_REGISTRARPRESCRIPCION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String getRecibosExcedentesUno(
        String compania,
        String numfacturaex,
        String codigopredioex,
        String numeroorden,
        String banco,
        String paquete,
        Date fecha,
        String usuario,
        String codigopredio,
        String numordenex,
        String barras,
        String facturapago,
        String excedentes,
        String docnum,
        String rppredio,
        int rpanio,
        String rptotal,
        String rpavaluo,
        boolean insrecibopago,
        boolean actualiza)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA       => '", compania, "', ",
                                    "UN_NUMFACTURAEX   =>'", numfacturaex,
                                    "', ", "UN_CODIGOPREDIOEX =>'",
                                    codigopredioex, "', ",
                                    "UN_NUMEROORDEN    => '", numeroorden,
                                    "', ",
                                    "UN_BANCO          =>'", banco, "', ",
                                    "UN_PAQUETE        =>'", paquete, "', ",
                                    "UN_FECHA          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "' , 'DD/MM/YYYY'), ",
                                    "UN_USUARIO        => '", usuario, "', ",
                                    "UN_CODIGOPREDIO   =>'", codigopredio,
                                    "', ", "UN_NUMORDENEX     =>'", numordenex,
                                    "', ", "UN_BARRAS         =>'", barras,
                                    "', ", "UN_FACTURAPAGO    =>'", facturapago,
                                    "', ",
                                    "UN_EXCEDENTES        =>'", excedentes,
                                    "', ", "UN_DOCNUM            =>'", docnum,
                                    "', ", "UN_RPPREDIO          =>'", rppredio,
                                    "', ", "UN_RPANIO            =>",
                                    Integer.toString(rpanio), ", ",
                                    "UN_RPTOTAL           =>'", rptotal, "', ",
                                    "UN_RPAVALUO          =>'", rpavaluo, "', ",
                                    "UN_INSRECIBOPAGO     =>",
                                    insrecibopago ? "-1" : "0", ", ",
                                    "UN_ACTUALIZA         =>",
                                    actualiza ? "-1" : "0", "" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.FC_RECIBOSEXCEDENTESUNO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean registrarExoneracionVigencias(
        String compania,
        String codigo,
        Date fecresolucion,
        String numresolucion,
        String observacion,
        int viginicial,
        int vigfinal,
        String elabresolucion,
        String firmaresolucion,
        String usuario,
        boolean indaplicar)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                    "UN_CODIGO           =>'", codigo, "', ",
                                    "UN_FECRESOLUCION    =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecresolucion),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NUMRESOLUCION    =>'", numresolucion,
                                    "', ",
                                    "UN_OBSERVACION      =>'", observacion,
                                    "', ",
                                    "UN_VIGINICIAL       =>",
                                    Integer.toString(viginicial), ", ",
                                    "UN_VIGFINAL         =>",
                                    Integer.toString(vigfinal), ", ",
                                    "UN_ELABRESOLUCION   =>'", elabresolucion,
                                    "', ",
                                    "UN_FIRMARESOLUCION   =>'",
                                    firmaresolucion, "', ",
                                    "UN_USUARIO          =>'", usuario, "', ",
                                    "UN_INDAPLICAR       =>",
                                    indaplicar ? "-1" : "0"
            };
            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.FC_REG_EXONERACION_VIGENCIAS",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void anulaRecibosExcedentes(
        String compania,
        String numeroorden,
        String numerofactura,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       => '", compania, "', ",
                                "UN_NUMEROORDEN    => '", numeroorden, "', ",
                                "UN_NUMEROFACTURA  => '", numerofactura, "', ",
                                "UN_USUARIO        => '", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.PR_ACTUALIZA_DGRE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String arreglarCupones(
        String compania,
        String fechainicial,
        String fechafinal,
        String bancoinicial,
        String bancofinal,
        String paqueteinicial,
        String paquetefinal,
        String nombrebancoini,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_FECHAINICIAL   =>'", fechainicial, "', ",
                                "UN_FECHAFINAL     =>'", fechafinal, "', ",
                                "UN_BANCOINICIAL   =>'", bancoinicial, "', ",
                                "UN_BANCOFINAL     =>'", bancofinal, "', ",
                                "UN_PAQUETEINICIAL =>'", paqueteinicial, "', ",
                                "UN_PAQUETEFINAL   =>'", paquetefinal, "', ",
                                "UN_NOMBREBANCOINI =>'", nombrebancoini, "', ",
                                "UN_USUARIO        =>'", usuario, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_EJECUTARDEPURACIONCUPONES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String consultarCodigoAuditoria(
        String compania,
        String movimiento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                "UN_MOVIMIENTO =>'", movimiento, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_CONSULTARCODIGOAUDITORIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String generarFacturaExcedente(
        String compania,
        String codigoPredio,
        String numeroOrden,
        String usuario,
        String nombre)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA      => '", compania, "', ",
                                "UN_CODIGO_PREDIO => '", codigoPredio, "', ",
                                "UN_NUMERO_ORDEN  => '", numeroOrden, "', ",
                                "UN_USUARIO       => '", usuario, "', ",
                                "UN_NOMBRE        => '", nombre, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_GENERARFACTURAEXCEDENTE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void imprimirVigenciaCaja(
        String compania,
        String codigopredial,
        String numeroorden,
        BigDecimal avaluoano,
        Date fechacorte,
        boolean aplicaley1066,
        boolean aplicaley1175,
        boolean aniounico,
        int aniofinal,
        int pagoano,
        String usuario,
        String nompropietario,
        String nitpropfact,
        String banco)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                    "UN_CODIGOPREDIAL  =>'", codigopredial,
                                    "', ", "UN_NUMEROORDEN    =>'", numeroorden,
                                    "', ", "UN_AVALUOANO      =>",
                                    avaluoano.toString(), ", ",
                                    "UN_FECHACORTE     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ", "UN_APLICALEY1066  =>",
                                    aplicaley1066 ? "-1" : "0", ", ",
                                    "UN_APLICALEY1175  =>",
                                    aplicaley1175 ? "-1" : "0", ", ",
                                    "UN_ANIOUNICO      =>",
                                    aniounico ? "-1" : "0", ", ",
                                    "UN_ANIOFINAL      =>",
                                    Integer.toString(aniofinal), ", ",
                                    "UN_PAGOANO        =>",
                                    Integer.toString(pagoano), ", ",
                                    "UN_USUARIO        =>'", usuario, "', ",
                                    "UN_NOMPROPIETARIO =>'", nompropietario,
                                    "', ", "UN_NITPROPFACT    =>'", nitpropfact,
                                    "', ", "UN_BANCO          =>'", banco,
                                    "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM4.PR_IMPRESORAVIGENCIACAJA",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void incautarPredio(
        String compania,
        String codigopred,
        String codigousuario,
        String observacion,
        int opcion,
        String camposAux,
        String noresolucion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_CODIGOPRED    =>'", codigopred, "', ",
                                "UN_CODIGOUSUARIO =>'", codigousuario, "', ",
                                "UN_OBSERVACION   =>'", observacion, "', ",
                                "UN_OPCION        =>", Integer.toString(opcion),
                                ", ", "UN_CAMPOS_AUX    =>'", camposAux, "', ",
                                "UN_NORESOLUCION  =>'", noresolucion, "' " };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.PR_INCAUTARPREDIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String getNombreConceptoPredial(
        String compania,
        int codigo,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                "UN_CODIGO   =>", Integer.toString(codigo),
                                ", ", "UN_ANIO    =>",
                                Integer.toString(anio) };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String armarConsultaMorososPredial(
        String compania,
        int anioInicial,
        int anioFinal,
        boolean checkacuerdos)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                "UN_ANIO_INICIAL    =>",
                                Integer.toString(anioInicial), ", ",
                                "UN_ANIO_FINAL      =>",
                                Integer.toString(anioFinal), ", ",
                                "UN_CHECKACUERDOS   =>",
                                checkacuerdos ? "-1" : "0" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_ARMACONSULTA_MOROSOS_PRD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String armarConsultaFacturadosPredial(
        String compania,
        int anioInicial,
        int anioFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_ANIO_INICIAL =>",
                                Integer.toString(anioInicial), ", ",
                                "UN_ANIO_FINAL   =>",
                                Integer.toString(anioFinal) };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_ARMACONSULTA_FACTURADOS_PRD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void copiarConceptos(
        String compania,
        String codigopredial,
        String numeroorden,
        int pagoano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_CODIGOPREDIAL =>'", codigopredial, "', ",
                                "UN_NUMEROORDEN   =>'", numeroorden, "', ",
                                "UN_PAGOANO       =>",
                                Integer.toString(pagoano), ", ",
                                "UN_USUARIO       =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.PR_COPIACONCEPTOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String obtenerAvaluoVigencia(
        String compania,
        String predio,
        int anio,
        String numeroOrden)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA     => '", compania, "', ",
                                "UN_PREDIO       => '", predio, "', ",
                                "UN_ANIO         => ", Integer.toString(anio),
                                ", ", "UN_NUMERO_ORDEN => '", numeroOrden,
                                "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_AVALUOVIGENCIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String obtenerTarifaVigencia(
        String compania,
        String predio,
        String numeroOrden,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio) };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM4.FC_TARIFAVIGENCIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

}
