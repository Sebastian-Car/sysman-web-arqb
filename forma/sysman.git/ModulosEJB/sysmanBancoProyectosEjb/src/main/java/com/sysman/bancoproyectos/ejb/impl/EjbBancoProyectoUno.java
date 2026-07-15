package com.sysman.bancoproyectos.ejb.impl;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoUnoLocal;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoUnoRemote;
import com.sysman.exception.SystemException;
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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class BancoProyectoUno
 */
@Stateless
@LocalBean
public class EjbBancoProyectoUno
                implements EjbBancoProyectoUnoRemote, EjbBancoProyectoUnoLocal {
    /**
     * Default constructor.
     */
    public EjbBancoProyectoUno() {
    }

    @Override
    public boolean existeNivelPlanIndicativoporDigito(
        String compania,
        int anio,
        int digito)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_ANIO             =>",
                                Integer.toString(anio), ", ",
                                "UN_DIGITO            =>",
                                Integer.toString(digito), ""
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY1.FC_VALIDARDIGITOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public BigDecimal copiarActividadesBancoProyecto(
        String compania,
        String proyecto,
        String tipocomponente,
        String componente,
        String nombrecomponente,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_PROYECTO         =>'", proyecto, "', ",
                                "UN_TIPOCOMPONENTE   =>'", tipocomponente,
                                "', ", "UN_COMPONENTE        =>'", componente,
                                "', ", "UN_NOMBRECOMPONENTE  =>'",
                                nombrecomponente, "', ",
                                "UN_VIGENCIA         =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY1.FC_BTNCOPIARACTIVIDADES",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal eliminarProgramacionActividad(
        String compania,
        String proyecto,
        String tipocomponente,
        String codigocomponente,
        String codigoactividad,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTO          =>'", proyecto, "', ",
                                "UN_TIPOCOMPONENTE    =>'", tipocomponente,
                                "', ", "UN_CODIGOCOMPONENTE  =>'",
                                codigocomponente, "', ",
                                "UN_CODIGOACTIVIDAD   =>'", codigoactividad,
                                "', ", "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY1.FC_PROGRACTIAFTERDELETE",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public double calcularPorcentaje(
        String compania,
        String tipoComponente,
        String codigoComponente,
        String proyecto,
        int vigencia,
        String valorAprobado,
        String valorProgramado,
        String tipoEstado,
        BigDecimal valorTotal,
        String valor,
        String valorAnt,
        int periodoProyecto,
        String tipotQueAprueba,
        String tipotQueApruebaProg,
        String clasetQueAprueba,
        String clasetQueApruebaProg,
        BigInteger codigoQueApruebaProg,
        BigInteger codigoQueAprueba,
        BigInteger codigoitemQueApruebaProg,
        BigInteger codigoItemQueAprueba,
        String dependenciaQueApruebaProg,
        String dependenciaQueAprueba,
        String codigoActividad,
        String nomActividad,
        String codigoProg,
        String cantidadProg,
        BigDecimal valorTotalProg,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCOMPONENTE    =>'", tipoComponente,
                                "', ", "UN_CODIGOCOMPONENTE  =>'",
                                codigoComponente, "', ",
                                "UN_PROYECTO          =>'", proyecto, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_VALORAPROBADO     =>'", valorAprobado,
                                "', ", "UN_VALORPROGRAMADO   =>'",
                                valorProgramado, "', ",
                                "UN_TIPOESTADO        =>'", tipoEstado, "', ",
                                "UN_VALORTOTAL        =>",
                                valorTotal.toString(), ", ",
                                "UN_VALOR             =>'", valor, "', ",
                                "UN_VALOR_ANT         =>'", valorAnt, "', ",
                                "UN_PERIODOPROYECTO   =>",
                                Integer.toString(periodoProyecto), ", ",
                                "UN_TIPOT_QUEAPRUEBA  =>'", tipotQueAprueba,
                                "', ", "UN_TIPOT_QUEAPRUEBA_PROG =>'",
                                tipotQueApruebaProg, "', ",
                                "UN_CLASET_QUEAPRUEBA =>'", clasetQueAprueba,
                                "', ", "UN_CLASET_QUEAPRUEBA_PROG =>'",
                                clasetQueApruebaProg, "', ",
                                "UN_CODIGO_QUEAPRUEBA_PROG =>",
                                codigoQueApruebaProg.toString(), ", ",
                                "UN_CODIGO_QUEAPRUEBA =>",
                                codigoQueAprueba.toString(), ", ",
                                "UN_CODIGOITEM_QUEAPRUEBA_PROG =>",
                                codigoitemQueApruebaProg.toString(), ", ",
                                "UN_CODIGOITEM_QUEAPRUEBA =>",
                                codigoItemQueAprueba.toString(), ", ",
                                "UN_DEPENDENCIA_QUEAPRUEBA_PROG =>'",
                                dependenciaQueApruebaProg, "', ",
                                "UN_DEPENDENCIA_QUEAPRUEBA =>'",
                                dependenciaQueAprueba, "', ",
                                "UN_CODIGOACTIVIDAD   =>'", codigoActividad,
                                "', ", "UN_NOMACTIVIDAD      =>'", nomActividad,
                                "', ", "UN_CODIGO_PROG       =>'", codigoProg,
                                "', ", "UN_CANTIDAD_PROG     =>'", cantidadProg,
                                "', ", "UN_VALORTOTAL_PROG   =>'",
                                valorTotalProg.toString(), "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (double) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY1.FC_CALCULARPORCENTAJE",
                        SysmanFunciones.concatenar(parametros),
                        Types.DOUBLE);
    }
    
    @Override
    public String cargarRubrosProyecto(
            String compania,
            String cadena,
            String usuario)
            throws SystemException {

        String[] parametros = {
            "UN_COMPANIA   =>'", compania, "', ",
            "UN_CADENA     =>", cadena, ", ",
            "UN_USUARIO    =>'", usuario, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(
                ConectorPool.ESQUEMA_SYSMAN,
                "PCK_BANCOS_PROY1.FC_CARGAR_RUBROS_PROYECTO",
                SysmanFunciones.concatenar(parametros),
                Types.VARCHAR
        );
    }
    
	@Override
	public String actualizarActivXProyecto(String compania, String cadena, String usuario, String vigencia)
			throws SystemException {
		try {

			String[] parametros = { "UN_COMPANIA   =>'", compania, "', ", "UN_CADENA     =>", cadena, ", ",
					"UN_USUARIO    =>'", usuario, "',", "UN_VIGENCIA   =>", vigencia, "" };

			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_BANCOS_PROY1.FC_ACTUALIZAR_ACTIVXPROYECTO", SysmanFunciones.concatenar(parametros),
					Types.CLOB));

		} catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	
	@Override
	public String cargarArmonizacionPd(
			String compania,
			String cadena,
			String usuario)
					throws SystemException {
		try {
			String[] parametros = {
					"UN_COMPANIA   =>'", compania, "', ",
					"UN_CADENA     =>", cadena, ", ",
					"UN_USUARIO    =>'", usuario, "'"
			};

			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_BANCOS_PROY1.FC_CARGA_ARMONIZ_PPTAL",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		} catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

}