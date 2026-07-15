package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroGeneralRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PresupuestoCero * Modificado por
 * jgomez 10/06/2017. Implementacion metodo concatenar de la clase
 * SysmanFunciones para el envio de parametros a los diferentes
 * procedimientos y funciones.
 */

@Stateless
@LocalBean
public class EjbPresupuestoCero
                implements EjbPresupuestoCeroRemote, EjbPresupuestoCeroLocal {

    @EJB
    private EjbPresupuestoCeroGeneralRemote ejbPresupuestoCeroGeneral;

    /**
     * Default constructor.
     */
    public EjbPresupuestoCero() {
    }

    @Override
    public boolean proyectarPresupuestoSiguienteVigencia(
        String compania,
        int anofuente,
        int anodestino,
        BigDecimal inc,
        boolean sa,
        boolean regalias,
        boolean soloregalias)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
                               "UN_ANOFUENTE  =>", Integer.toString(anofuente),
                               ", ",
                               "UN_ANODESTINO =>", Integer.toString(anodestino),
                               ", ",
                               "UN_INC        =>", inc.toString(), ", ",
                               "UN_SA         =>", sa ? "-1" : "0", ", ",
                               "UN_REGALIAS   =>", regalias ? "-1" : "0",
                               ", ",
                               "UN_SOLOREGALIAS =>", soloregalias ? "-1" : "0"
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.FC_PROYECTARPRESUPUESTO",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);

        return rta != 0;
    }

    @Override
    public boolean verificarPeriodoPresupuestal(
        String compania,
        int ano,
        int mes)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA =>'", compania, "', ",
                               "UN_ANO      =>", Integer.toString(ano), ", ",
                               "UN_MES      =>", Integer.toString(mes)
        };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.FC_VERIFICAPERIODOPPTAL",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void mayorizarRubrosPresupuestales(
        String compania,
        int ano,
        int mesinicial,
        int mesfinal)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
                               "UN_ANO        =>", Integer.toString(ano), ", ",
                               "UN_MESINICIAL =>", Integer.toString(mesinicial),
                               ", ",
                               "UN_MESFINAL   =>", Integer.toString(mesfinal)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.PR_MAYORIZARCUENTASHPTO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public boolean revisarAfectacionesPresupuestales(
        String compania,
        int ano,
        String usuario
        )
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
                               "UN_ANO        =>", Integer.toString(ano) ,"," ,
                               "UN_USUARIO    =>'", usuario, "'"
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.FC_REVISARAFECTACIONESHR",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }


    @Override
    public String consultarPredecesorPresupuestal(
        String compania,
        int anio,
        String codigo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA =>'", compania, "', ",
                               "UN_ANIO     =>", Integer.toString(anio), ", ",
                               "UN_CODIGO   =>'", codigo, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.FC_PREDECESORPPTAL",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String consultarConsecutivosPresupuestalesFaltantes(
        String tipo,
        String compania,
        Date fechainicial,
        Date fechafinal)
                    throws SystemException {
        try {
            String[] parametro = { "UN_TIPO              =>'", tipo, "', ",
                                   "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_FECHAINICIAL      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechainicial),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAFINAL        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechafinal),
                                   "','DD/MM/YYYY')"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO.FC_CONCATENACOMPROBANTESPPTO",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarApropiacionesIniciales(
        String compania,
        int anio)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA =>'", compania, "' ",
                                   ", UN_ANIO     =>", Integer.toString(anio)
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO.FC_CONTABILIZARAPROPINICIAL",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public long insertarSaldosPresupuestalesIniciales(
        String compania,
        int anio)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA =>'", compania, "' ",
                               ", UN_ANIO     =>", Integer.toString(anio)
        };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.FC_CREARSALDOSPPTALES",
                        SysmanFunciones.concatenar(parametro),
                        Types.BIGINT);
    }

    @Override
    public long actualizarBancoDeProyectos(
        String compania,
        int ano,
        String tipo,
        BigInteger comprobante)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA    =>'", compania, "', ",
                               "UN_ANO         =>", Integer.toString(ano), ", ",
                               "UN_TIPO        =>'", tipo, "', ",
                               "UN_COMPROBANTE =>", comprobante.toString()
        };
        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.FC_ACTUALIZABANCOPROYECTOS",
                        SysmanFunciones.concatenar(parametro),
                        Types.BIGINT);
    }

    @Override
    public void eliminarComprobantePresupuestal(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        int mes,
        int dia,
        BigDecimal afectaciones,
        String usuario,
        boolean impreso)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA    =>'", compania, "', ",
                               "UN_ANO         =>", Integer.toString(ano), ", ",
                               "UN_TIPO        =>'", tipo, "', ",
                               "UN_NUMERO      =>", numero.toString(), ", ",
                               "UN_MES         =>", Integer.toString(mes), ", ",
                               "UN_DIA               =>", Integer.toString(dia),
                               ", ",
                               "UN_AFECTACIONES      =>",
                               afectaciones.toString(), ", ",
                               "UN_USUARIO           =>'", usuario, "',",
                               "UN_IMPRESO     =>", impreso ? "-1" : "0"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.PR_ELIMINAR_COMPROBANTEPPTAL",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void validarDisponible(
        String compania,
        int anio,
        String codigo,
        BigDecimal apropiacioninicial) throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_CODIGO            =>'", codigo, "', ",
                               "UN_APROPIACIONINICIAL =>",
                               apropiacioninicial.toString()
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.PR_VALIDARDISPONIBLE",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void insertarAuxiliarenPresupuesto(
        String compania, int ano, String usuario)
                    throws SystemException {

        ejbPresupuestoCeroGeneral.insertarAuxiliarenPresupuesto(compania, ano,
                        usuario);

    }

    @Override
    public void limpiarSaldoPPtal(
        String compania,
        int anio,
        String codigoinicial,
        String codigofinal)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "', ",
                               "UN_ANIO      =>", Integer.toString(anio), ", ",
                               "UN_CODIGOINICIAL=>'", codigoinicial, "', ",
                               "UN_CODIGOFINAL  =>'", codigofinal, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.PR_LIMPIA_SALDO_AUX_PPTAL",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void contabilizarComprobantePptal(
        String compania,
        int anio,
        String tipoCpte,
        BigInteger comprobante,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPOCPTE          =>'", tipoCpte, "', ",
                                "UN_COMPROBANTE       =>",
                                comprobante.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.PR_CONTABILIZARCOMPPPTAL",
                        SysmanFunciones.concatenar(parametros));
    }

}