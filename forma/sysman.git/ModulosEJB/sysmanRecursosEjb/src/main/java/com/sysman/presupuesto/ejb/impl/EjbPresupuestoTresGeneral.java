package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
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
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class PresupuestoTresGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbPresupuestoTresGeneral
                implements EjbPresupuestoTresGeneralRemote,
                EjbPresupuestoTresGeneralLocal {
    /**
     * Default constructor.
     */
    public EjbPresupuestoTresGeneral() {
    }

    @Override
    public int actualizarValorSolicitado(
            String compania,
            String itemAfectado,
            int vigenciaItemAfectado,
            BigDecimal valorDigitado,
            int codigoItem,
            int tipoSolicitud,
            String accion,
            BigDecimal valorAntiguo,
            String codigo,
            int ano,
            BigDecimal valorRubro,
            String fuente,
            String centroCosto,
            String referencia,
            int solicitudAfect,
            int numeroSDP)
                        throws SystemException {

            String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                    "UN_ITEM_AFECTADO       =>'", itemAfectado, "', ",
                                    "UN_VIGENCIA_ITEM_AFECTADO =>",
                                    Integer.toString(vigenciaItemAfectado), ", ",
                                    "UN_VALOR_DIGITADO      =>",
                                    valorDigitado.toString(), ", ",
                                    "UN_CODIGO_ITEM         =>",
                                    Integer.toString(codigoItem), ", ",
                                    "UN_TIPO_SOLICITUD      =>",
                                    Integer.toString(tipoSolicitud), ", ",
                                    "UN_ACCION              =>'", accion, "', ",
                                    "UN_VALOR_ANTIGUO       =>",
                                    valorAntiguo.toString(), ", ",
                                    "UN_CODIGO              =>'", codigo, "', ",
                                    "UN_ANO                 =>",
                                    Integer.toString(ano), ", ",
                                    "UN_VALOR_RUBRO         =>",
                                    valorRubro.toString(), ", ",
                                    "UN_FUENTE              =>'", fuente, "', ",
                                    "UN_CENTRO_COSTO        =>'", centroCosto, "', ",
                                    "UN_REFERENCIA          =>'", referencia, "', ",
                                    "UN_NRO_SOLICITUD_AFECT         =>",
                                    Integer.toString(solicitudAfect), ", ",
                                    "UN_NRO_SDP =>'", Integer.toString(numeroSDP), "' "
            };
            return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO3.FC_ACTUALIZAR_VALOR_SOLICITADO",
                            SysmanFunciones.concatenar(parametros),
                            Types.INTEGER);
        }

    @Override
    public void afectarSolicitud(
        String compania,
        long solicitudAfectada,
        long solicitudNueva,
        int tipoSolicitudNueva)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_SOLICITUD_AFECTADA =>",
                                Long.toString(solicitudAfectada), ", ",
                                "UN_SOLICITUD_NUEVA   =>",
                                Long.toString(solicitudNueva), ", ",
                                "UN_TIPO_SOLICITUD_NUEVA =>",
                                Integer.toString(tipoSolicitudNueva), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.PR_AFECTAR_SOLICITUD",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarTerDeoSolicitud(
        String compania,
        long numero,
        String tercero,
        String dependencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_DEPENDENCIA       =>'", dependencia, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.PR_ACTLZ_TER_DEP_SOLICITUD",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean esOrdenador(
        String compania,
        String cedula)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CEDULA            =>'", cedula, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_ES_ORDENADOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean actualizarSolicitudesNoAprobadas(
        String compania,
        long solicitud,
        String aprobacion)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_SOLICITUD         =>",
                                Long.toString(solicitud), ", ",
                                "UN_APROBACION        =>'", aprobacion, "'"

        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO3.FC_ACTU_SOLIC_NO_APRO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public  String    generarPlanoSecretaria(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String tipocomp,
        String cptInicial,
        String cptFinal,

        String responsableppto)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicial),
                                    "','DD/MM/YYYY'),",
                                    "UN_FECHAFINAL       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY'),",
                                    "UN_TIPOCOMP          =>'", tipocomp, "', ",
                                    "UN_CPTEINICIAL =>'", cptInicial, "', ",
                                    "UN_CPTEFINAL =>'", cptFinal, "', ",
                                    "UN_RESPONSABLEPPTO   =>", responsableppto,
                                    ""
            };

            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PRESUPUESTO_COM4.FC_GENERAR_PLANO_SECRETARIA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {// TODO
            // Auto-generated
            // catch
            // block
            throw new SystemException(e);
        }
    }

    @Override
    public  String    generarPlanoOrdenPago(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String tipocomp,
        String cptInicial,
        String cptFinal,
        String vigencia,
        String responsableppto)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                    "UN_FECHAINICIAL=>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicial),
                                    "','DD/MM/YYYY'),",
                                    "UN_FECHAFINAL=>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY'),", "UN_TIPOCOMP =>'",
                                    tipocomp, "', ", "UN_CPTEINICIAL =>'",
                                    cptInicial, "', ", "UN_CPTEFINAL =>'",
                                    cptFinal, "', ", "UN_VIGENCIA      =>'",
                                    vigencia, "', ", "UN_RESPONSABLEPPTO   =>",
                                    responsableppto, ""
            };

            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PRESUPUESTO_COM4.FC_GENERAR_PLANO_ORDEN_PAGO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {// TODO
            // Auto-generated
            // catch
            // block
            throw new SystemException(e);
        }
    }

    public  String    generarPlanoOrdenPagoReserva(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String tipocomp,
        String vigencia,
        String cptInicial,
        String cptFinal,
        String responsableppto)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA=>'", compania, "', ",
                                    "UN_FECHAINICIAL=>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicial),
                                    "','DD/MM/YYYY'),",
                                    "UN_FECHAFINAL=>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY'),", "UN_TIPOCOMP=>'",
                                    tipocomp, "', ", "UN_VIGENCIA      =>'",
                                    vigencia, "', ", "UN_CPTEINICIAL =>'",
                                    cptInicial, "', ", "UN_CPTEFINAL =>'",
                                    cptFinal, "', ", "UN_RESPONSABLEPPTO   =>",
                                    responsableppto, ""
            };

            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PRESUPUESTO_COM4.FC_GENERAR_PLANO_OP_RESERVA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {// TODO
            // Auto-generated
            // catch
            // block
            throw new SystemException(e);
        }
    }

    public String actualizaSituacionFondos(String compania, int ano) {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIO            =>'",
                                    Integer.toString(ano), "'"
            };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO_COM4.PR_ACTLZ_SITUACION_FONDOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizarFuenteDetalle(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_ANIO        =>", Integer.toString(anio),
                                ",",
                                "UN_USUARIO    =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO_COM4.PR_ACT_FUENTE_DETALLE",
                        SysmanFunciones.concatenar(parametros));
    }
                
	/**
	 * Se reemplaza el nombre del metodo original FC_ACTUALIZARCLASIFICADORESPPTAL por 
	 * FC_ACT_CLASIFICADORESPPTAL para evitar que soprepase los 30 caractereres
	 */

    @Override
    public BigDecimal actualizarClasificadoresPptal(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_ANIO        =>", Integer.toString(anio),
                                ",",
                                "UN_USUARIO    =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO_COM4.FC_ACT_CLASIFICADORESPPTAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal actualizaClasificadorCadenaPptal(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_ANIO        =>", Integer.toString(anio),
                                ",",
                                "UN_USUARIO    =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO_COM4.FC_ACT_CLAS_CADENA_PPTAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }
    /**
	 * Se reemplaza el nombre del metodo original FC_CARGAR_RECLASIFICACION_CIERRE por 
	 * FC_CARGA_RECLASIFICA_CIERRE para evitar que soprepase los 30 caractereres
	 */
    @Override
    public String cargarReclasificacionCierre(
        String compania,
        int anio,
        String clase,
        String cadena,
        String usuario)
                    throws SystemException {
        try {

        	String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
        			"UN_ANO        =>", Integer.toString(anio),", ",
        			"UN_CLASE      =>'", clase, "', ",
        			"UN_CADENA     =>", cadena, ", ",        			
        			"UN_USUARIO    =>'", usuario, "'" };
        	
        	return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                    ConectorPool.ESQUEMA_SYSMAN,
                    "PCK_PRESUPUESTO_COM4.FC_CARGA_RECLASIFICA_CIERRE",
                    SysmanFunciones.concatenar(parametros),
                    Types.CLOB));
        }
          catch (SQLException | IOException e) {
          throw new SystemException(e);
       }
    }
}