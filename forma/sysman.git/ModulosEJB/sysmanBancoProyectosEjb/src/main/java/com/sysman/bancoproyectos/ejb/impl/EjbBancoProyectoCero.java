package com.sysman.bancoproyectos.ejb.impl;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroLocal;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectosCeroGeneralRemote;
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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class BancoProyectoCero
 */
@Stateless
@LocalBean
public class EjbBancoProyectoCero implements EjbBancoProyectoCeroRemote,
                EjbBancoProyectoCeroLocal {
    @EJB
    private EjbBancoProyectosCeroGeneralRemote ejbBancosProyectosCeroGeneral;

    /**
     * Default constructor.
     */
    public EjbBancoProyectoCero() {
    }

    @Override
    public BigDecimal insertarVigencias(
        String compania,
        int anioIni,
        int anioFin,
        String opcion,
        String usuario)
                    throws SystemException {

        return ejbBancosProyectosCeroGeneral.insertarVigencias(compania,
                        anioIni, anioFin, opcion, usuario);

    }

    @Override
    public String actualizarProgramado(
        String compania,
        int vigencia,
        String proyectoini,
        String proyectofin,
        int opcion,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                    "UN_VIGENCIA         =>",
                                    Integer.toString(vigencia), ", ",
                                    "UN_PROYECTOINI      =>'", proyectoini,
                                    "', ",
                                    "UN_PROYECTOFIN      =>'", proyectofin,
                                    "', ",
                                    "UN_OPCION           =>",
                                    Integer.toString(opcion), ", ",
                                    "UN_USUARIO          =>'", usuario, "'"
            };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_BANCOS_PROY.FC_ACTU_VALORPROGRAMADO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarDescripcionPlanIndi(
        String compania,
        int vigenciaini,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_VIGENCIAINI        =>",
                                Integer.toString(vigenciaini), ", ",
                                "UN_USUARIO            =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY.PR_ACTU_DESCRIPCIONPLANINDIC",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String anularSolicitudBancoProyecto(
        String compania,
        String tipot,
        String claset,
        long codigo,
        String dependencia,
        int modulo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOT             =>'", tipot, "', ",
                                "UN_CLASET            =>'", claset, "', ",
                                "UN_CODIGO            =>",
                                Long.toString(codigo), ", ",
                                "UN_DEPENDENCIA       =>'", dependencia, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY.FC_ANULAR_SOLICITUD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String anularSolicitudesBancoProyectoRes(
        Date fecha,
        String compania,
        String tipot,
        int modulo,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOT             =>'", tipot, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY.FC_RESUMENANULACION",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException | ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean crearActividades(
        String compania,
        String componente,
        String codigoProyecto,
        String tipoComponente,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_COMPONENTE        =>'", componente, "', ",
                                "UN_CODIGOPROYECTO    =>'", codigoProyecto,
                                "', ", "UN_TIPOCOMPONENTE    =>'",
                                tipoComponente, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY.FC_CREARACTIVIDADES",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void insertarComponentes(
        String compania,
        String codigo,
        BigDecimal valorTotal,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_VALORTOTAL        =>",
                                valorTotal.toString(), ", ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY.PR_INSERTARCOMPONENTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void eliminarComponentes(
        String compania,
        String codigo,
        BigDecimal valorTotal,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_VALORTOTAL        =>",
                                valorTotal.toString(), ", ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY.PR_ELIMINARCOMPONENTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void armonizarPd(
        String compania,
        int vigencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY.PR_ARMONIZARPD",
                        SysmanFunciones.concatenar(parametros));
    }

}