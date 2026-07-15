package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoUnoLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoUnoRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PresupuestoUno
 * 
 * @version 2.0, 10/06/2017, <strong>pespitia</strong>:<br>
 * Implementacion de la funcion SysmanFunciones.concatenar
 */
@Stateless
@LocalBean
public class EjbPresupuestoUno
                implements EjbPresupuestoUnoRemote, EjbPresupuestoUnoLocal {
    /**
     * Default constructor.
     */
    public EjbPresupuestoUno() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean verificarIndicadoresDeMovimientoPresupuestales(
        String compania,
        int anio,
        String codigo)
                    throws SystemException {

        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_CODIGO            =>'", codigo, "'" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO1.FC_VERIFICAR_INDICADORES_PPTO",
                        SysmanFunciones.concatenar(par),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void insertarSaldosAuxiliaresPresupuestales(
        String compania,
        int anio,
        String codigo,
        String centro,
        String tercero,
        String sucursal,
        String auxiliar,
        String referencia,
        String fuenterecurso,
        String naturaleza,
        boolean indDepurados)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_CODIGO            =>'", codigo, "', ",
                         "UN_CENTRO            =>'", centro, "', ",
                         "UN_TERCERO           =>'", tercero, "', ",
                         "UN_SUCURSAL          =>'", sucursal, "', ",
                         "UN_AUXILIAR          =>'", auxiliar, "', ",
                         "UN_REFERENCIA        =>'", referencia, "', ",
                         "UN_FUENTERECURSO     =>'", fuenterecurso, "', ",
                         "UN_NATURALEZA        =>'", naturaleza, "', ",
                         "UN_IND_DEPURADOS     =>", (indDepurados ? "-1" : "0"),
                         "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO1.PR_CREAR_SALDOAUXPRESUPUESTAL",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void prepararActualizacionPresupuesto(
        String tipoCpte,
        String compania,
        Date fecha,
        String codigo,
        String centro,
        String tercero,
        String sucursal,
        String auxiliar,
        String referencia,
        String fuente,
        String naturaleza,
        BigDecimal debitoAnt,
        BigDecimal creditoAnt,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal diferencia,
        BigDecimal diferenciaant,
        String tipo,
        String tipoingreso)
                    throws SystemException {
        try {
            String[] par = { "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                             "UN_COMPANIA          =>'", compania, "', ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ", "UN_CODIGO            =>'",
                             codigo, "', ", "UN_CENTRO            =>'", centro,
                             "', ", "UN_TERCERO           =>'", tercero, "', ",
                             "UN_SUCURSAL          =>'", sucursal, "', ",
                             "UN_AUXILIAR          =>'", auxiliar, "', ",
                             "UN_REFERENCIA        =>'", referencia, "', ",
                             "UN_FUENTE            =>'", fuente, "', ",
                             "UN_NATURALEZA        =>'", naturaleza, "', ",
                             "UN_DEBITO_ANT        =>", debitoAnt.toString(),
                             ", ", "UN_CREDITO_ANT       =>",
                             creditoAnt.toString(), ", ",
                             "UN_DEBITO            =>", debito.toString(), ", ",
                             "UN_CREDITO           =>", credito.toString(),
                             ", ", "UN_DIFERENCIA        =>",
                             diferencia.toString(), ", ",
                             "UN_DIFERENCIAANT     =>",
                             diferenciaant.toString(), ", ",
                             "UN_TIPO              =>'", tipo, "', ",
                             "UN_TIPOINGRESO       =>'", tipoingreso, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO1.PR_ACTPPTO",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarAuxiliaresContables(
        String clase,
        String compania,
        int anio,
        String codigo,
        String tercero,
        String sucursal,
        String auxiliar,
        String centro,
        String referencia,
        String fuenterecurso,
        int mes,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal debitoAnt,
        BigDecimal creditoAnt,
        String naturaleza,
        BigDecimal diferencia,
        BigDecimal diferenciaant,
        String tipo,
        String tipoingreso,
        boolean indDepurados)
                    throws SystemException {
        String[] par = { "UN_CLASE             =>'", clase, "', ",
                         "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_CODIGO            =>'", codigo, "', ",
                         "UN_TERCERO           =>'", tercero, "', ",
                         "UN_SUCURSAL          =>'", sucursal, "', ",
                         "UN_AUXILIAR          =>'", auxiliar, "', ",
                         "UN_CENTRO            =>'", centro, "', ",
                         "UN_REFERENCIA        =>'", referencia, "', ",
                         "UN_FUENTERECURSO     =>'", fuenterecurso, "', ",
                         "UN_MES               =>", Integer.toString(mes), ", ",
                         "UN_DEBITO            =>", debito.toString(), ", ",
                         "UN_CREDITO           =>", credito.toString(), ", ",
                         "UN_DEBITO_ANT        =>", debitoAnt.toString(), ", ",
                         "UN_CREDITO_ANT       =>", creditoAnt.toString(), ", ",
                         "UN_NATURALEZA        =>'", naturaleza, "', ",
                         "UN_DIFERENCIA        =>", diferencia.toString(), ", ",
                         "UN_DIFERENCIAANT     =>", diferenciaant.toString(),
                         ", ", "UN_TIPO              =>'", tipo, "', ",
                         "UN_TIPOINGRESO       =>'", tipoingreso, "', ",
                         "UN_IND_DEPURADOS     =>", (indDepurados ? "-1" : "0"),
                         "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO1.PR_ACTPPTO0AUX",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void crearComprobanteDeModificacionPresupuestal(
        String clase,
        String compania,
        int modulo,
        int anio,
        int mes,
        String codigo,
        BigDecimal debitoant,
        BigDecimal creditoant,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal diferencia,
        BigDecimal diferenciaant,
        String tipo,
        String tipoingreso,
        int aniocpte,
        String tipocpte,
        BigInteger nrocpte,
        BigDecimal debitocpte,
        BigDecimal creditocpte,
        String ctacpte,
        BigDecimal csccpte)
                    throws SystemException {
        String[] par = { "UN_CLASE             =>'", clase, "', ",
                         "UN_COMPANIA          =>'", compania, "', ",
                         "UN_MODULO            =>", Integer.toString(modulo),
                         ", ", "UN_ANIO              =>",
                         Integer.toString(anio), ", ",
                         "UN_MES               =>", Integer.toString(mes), ", ",
                         "UN_CODIGO            =>'", codigo, "', ",
                         "UN_DEBITOANT         =>", debitoant.toString(), ", ",
                         "UN_CREDITOANT        =>", creditoant.toString(), ", ",
                         "UN_DEBITO            =>", debito.toString(), ", ",
                         "UN_CREDITO           =>", credito.toString(), ", ",
                         "UN_DIFERENCIA        =>", diferencia.toString(), ", ",
                         "UN_DIFERENCIAANT     =>", diferenciaant.toString(),
                         ", ", "UN_TIPO              =>'", tipo, "', ",
                         "UN_TIPOINGRESO       =>'", tipoingreso, "', ",
                         "UN_ANIOCPTE          =>", Integer.toString(aniocpte),
                         ", ", "UN_TIPOCPTE          =>'", tipocpte, "', ",
                         "UN_NROCPTE           =>", nrocpte.toString(), ", ",
                         "UN_DEBITOCPTE        =>", debitocpte.toString(), ", ",
                         "UN_CREDITOCPTE       =>", creditocpte.toString(),
                         ", ", "UN_CTACPTE           =>'", ctacpte, "', ",
                         "UN_CSCCPTE           =>", csccpte.toString(), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO1.PR_ACTPTO0_MOP",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public String seleccionarDocumentoAfectar(
        String compania,
        int anoh,
        String tipoh,
        String tipoc,
        BigDecimal numeroh,
        BigDecimal numeroc,
        String claseh,
        String clasec,
        Date fechaCpte,
        Date fechaAux,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_ANOH              =>", Integer.toString(anoh),
                             ", ", "UN_TIPOH             =>'", tipoh, "', ",
                             "UN_TIPOC             =>'", tipoc, "', ",
                             "UN_NUMEROH           =>", numeroh.toString(),
                             ", ", "UN_NUMEROC           =>",
                             numeroc.toString(), ", ",
                             "UN_CLASEH            =>'", claseh, "', ",
                             "UN_CLASEC            =>'", clasec, "', ",
                             "UN_FECHA_CPTE        =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechaCpte),
                             "','DD/MM/YYYY'), ",
                             "UN_FECHA_AUX         =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(
                                             fechaAux),
                             "','DD/MM/YYYY'), ", "UN_USUARIO           =>'",
                             usuario, "'" };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO1.FC_DOCUMENTOAFECTAR_PPTAL",
                            SysmanFunciones.concatenar(par),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarValorDocumento(
        String compania,
        int ano,
        String tipomovimiento,
        BigInteger movimiento)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_TIPOMOVIMIENTO    =>'", tipomovimiento, "', ",
                         "UN_MOVIMIENTO        =>", movimiento.toString(), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO1.PR_ACTUALIZAVALORDOCUMENTO",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void insertarSaldosPlanPresupuestal(
        String compania,
        int anio,
        String codigo)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_CODIGO            =>'", codigo, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO1.PR_CREAR_SALDOPLANPPTAL",
                        SysmanFunciones.concatenar(par));
    }
}