package com.sysman.workflow.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.ejb.EjbWorkflowCeroLocal;
import com.sysman.workflow.ejb.EjbWorkflowCeroRemote;

import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class WorkflowCero
 */
@Stateless
@LocalBean
public class EjbWorkflowCero
implements EjbWorkflowCeroRemote, EjbWorkflowCeroLocal {
	/**
	 * Default constructor.
	 */
	public EjbWorkflowCero() {
	}

	@Override
	public void cerrarTramite(
			String compania,
			String proceso,
			String tipoTramite,
			BigInteger tramite,
			String nodoActual,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO           =>'", proceso, "', ",
				"UN_TIPO_TRAMITE      =>'", tipoTramite, "', ",
				"UN_TRAMITE           =>", tramite.toString(),
				", ", "UN_NODO_ACTUAL       =>'", nodoActual,
				"', ", "UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.PR_CERRAR_TRAMITE",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void prepararVariablesTramite(
			String compania,
			String proceso,
			String tipoTramite,
			BigInteger tramite,
			String nodo,
			BigInteger dTramite,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO           =>'", proceso, "', ",
				"UN_TIPO_TRAMITE      =>'", tipoTramite, "', ",
				"UN_TRAMITE           =>", tramite.toString(),
				", ", "UN_NODO              =>'", nodo, "', ",
				"UN_D_TRAMITE         =>", dTramite.toString(),
				", ", "UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.PR_PREPARAR_VAR_TRAMITE",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void tramitar(
			String compania,
			String proceso,
			String tipoTramite,
			BigInteger numero,
			String nodoOrigen,
			String nodoDestino,
			boolean tramiteIni,
			String usuarioDestino,
			String archivocentral,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO           =>'", proceso, "', ",
				"UN_TIPO_TRAMITE      =>'", tipoTramite, "', ",
				"UN_NUMERO            =>", numero.toString(), ", ", 
				"UN_NODO_ORIGEN       =>'", nodoOrigen, "', ", 
				"UN_NODO_DESTINO      =>'", nodoDestino,"', ", 
				"UN_TRAMITE_INI       =>", (tramiteIni ? "-1" : "0"), ", ",
				"UN_USUARIO_DESTINO   =>'", usuarioDestino,   "', ",
				"UN_ARCHIVOCENTRAL    =>'" , archivocentral , "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.PR_TRAMITAR",
				SysmanFunciones.concatenar(parametros));
	}



	@Override
	public void validarInfDetallada(
			String compania,
			String proceso,
			String tipotramite,
			BigInteger tramite,
			BigInteger dtramite,
			String nodo)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO           =>'", proceso, "', ",
				"UN_TIPOTRAMITE       =>'", tipotramite, "', ",
				"UN_TRAMITE           =>", tramite.toString(),
				", ", "UN_DTRAMITE          =>",
				dtramite.toString(), ", ",
				"UN_NODO              =>'", nodo, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.PR_VALIDAR_INF_DETALLADA",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void eliminarTramite(
			String compania,
			String proceso,
			String tipotramite,
			BigInteger tramite)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO           =>'", proceso, "', ",
				"UN_TIPOTRAMITE       =>'", tipotramite, "', ",
				"UN_TRAMITE           =>", tramite.toString()
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.PR_ELIMINAR_TRAMITE",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void cambiarEjecutor(
			String compania,
			String proceso,
			String tipoTramite,
			BigInteger tramite,
			BigInteger dTramite,
			String usuarioInterno,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO           =>'", proceso, "', ",
				"UN_TIPO_TRAMITE      =>'", tipoTramite, "', ",
				"UN_TRAMITE           =>", tramite.toString(),
				", ",
				"UN_D_TRAMITE         =>", dTramite.toString(),
				", ", "UN_USUARIO_INTERNO   =>'",
				usuarioInterno, "', ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.PR_CAMBIAR_EJECUTOR",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void proyectarTramite(
			String compania,
			String tipoTramite,
			String proceso,
			BigInteger tramite,
			String nodoOrigen,
			int diasReprog,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_TIPO_TRAMITE      =>'", tipoTramite, "', ",
				"UN_PROCESO           =>'", proceso, "', ",
				"UN_TRAMITE           =>", tramite.toString(),
				", ",
				"UN_NODO_ORIGEN       =>'", nodoOrigen, "', ",
				"UN_DIAS_REPROG       =>",
				Integer.toString(diasReprog), ", ",
				"UN_USUARIO           =>'", usuario, "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.PR_PROYECTAR_TRAMITE",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public BigInteger workflow_pqrs(
			String compania,
			String proceso,
			BigInteger consecutivo,
			String nodo,
			String usuario,
			boolean proyecciones,
			String tipoTramite,
			String identificacion,
			String nombre,
			String direccion,
			String descripcion,
			String email,
			String anexo1,
			String anexo2,
			String anexo3,
			String tipoTramiteUsuario
			)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_PROCESO      =>'", proceso, "', ",
				"UN_CONSECUTIVO       =>'", consecutivo.toString(), "', ",
				"UN_NODO      =>'", nodo, "', ", 
				"UN_USUARIO           =>'", usuario, "', ",
				"UN_PROYECCIONES      =>'", (proyecciones ? "-1" : "0"), "'",
				"UN_TIPO_TRAMITE           =>'", tipoTramite, "', ",
				"UN_IDENTIFICACION      =>'", identificacion,"', ",
				"UN_NOMBRE =>'", usuario, "', ",
				"UN_DIRECCION =>'", usuario, "', ",
				"UN_DESCRIPCION_SOLICITD =>'", compania, "', ",
				"UN_EMAIL =>'", usuario, "', ",
				"UN_ANEXO1 =>'", usuario, "', ",
				"UN_ANEXO2 =>'", usuario, "', ",
				"UN_ANEXO3 =>'", usuario, "', ",      
				"UN_TIPO_TRAMITE_USUARIO           =>'", tipoTramiteUsuario, "', ",
		};
		return (BigInteger) AccionesImp.ejecutarFuncion(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_WORKFLOW.FC_WORKFLOW_PQRS",
				SysmanFunciones.concatenar(parametros),
				Types.BIGINT);

	}

	@Override
	public int tramitarAProceso(String compania, String proceso, String tipoTramite, BigInteger numero,
			BigInteger consecutivo, String nodoOrigen, String nodoDestino, String usuario, boolean proyecciones,
			String nombre, String direccion, String email, String anexo1, String anexo2, String anexo3,
			String descripcion, String tipoTramiteUsuario) throws SystemException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public  void  cargarSerieDocumental(
			String compania,
			String cadenaseries, 
			String usuario) 
					throws SystemException {
		String[] parametros ={ "UN_COMPANIA => '" , compania , "', "
				, "UN_CADENASERIES => " , cadenaseries , ", "
				, "UN_USUARIO      => '" , usuario , "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_WORKFLOW.PR_CARGAR_SERIEDOCUMENTAL",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public  void  cargarTramites(
			String compania, 
			String cadenaTramite, 
			String usuario) 
					throws SystemException {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				, "UN_CADENA_TRAMITE    =>" , cadenaTramite , ", "
				, "UN_USUARIO           =>'" , usuario , "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_WORKFLOW.PR_CARGAR_TRAMITES",
				SysmanFunciones.concatenar(parametros));
	}


	@Override
	public  void  prepararVariablesProceso(
			String compania, 
			String proceso, 
			String tipoTramite, 
			String tramite, 
			String usuario) 
					throws SystemException {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				, "UN_PROCESO           =>'" , proceso , "', "
				, "UN_TIPO_TRAMITE      =>'" , tipoTramite , "', "
				, "UN_TRAMITE           =>'" ,tramite ,"', "
				, "UN_USUARIO           =>'" , usuario , "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_WORKFLOW.PR_PREPARAR_VAR_PROCESO",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public  void  actualizarProcedencia(
			String compania, 
			String proceso, 
			String nodo, 
			String tipoTramite, 
			String tramite, 
			String direccion, 
			String procedencia, 
			String usuario) 
					throws SystemException {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				, "UN_PROCESO           =>'" , proceso , "', "
				, "UN_NODO              =>'" , nodo , "', "
				, "UN_TIPO_TRAMITE      =>'" , tipoTramite , "', "
				, "UN_TRAMITE           =>" ,tramite ,", "
				, "UN_DIRECCION         =>'" , direccion , "', "
				, "UN_PROCEDENCIA       =>'" , procedencia , "', "
				, "UN_USUARIO           =>'" , usuario , "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_WORKFLOW.PR_ACT_INF_PROD",
				SysmanFunciones.concatenar(parametros));
	}
	
	 @Override
	    public  void     cargarHistorialVar(
	String compania, 
	String cadenaTramite, 
	String usuario) 
	                      throws SystemException {
	         String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
	                 , "UN_CADENA_TRAMITE    =>" , cadenaTramite , ", "
	                 , "UN_USUARIO           =>'" , usuario , "'"
	};
	         AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_WORKFLOW.PR_CARGAR_HISTORIAL_VAR",
	SysmanFunciones.concatenar(parametros));
	    }
	



}