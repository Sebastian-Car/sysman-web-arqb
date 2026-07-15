package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbPrepararAnoLocal;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.util.SysmanFunciones;

import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbPrepararAno
 */
@Stateless
@LocalBean
public class EjbPrepararAno
                implements EjbPrepararAnoRemote, EjbPrepararAnoLocal {
    /**
     * Default constructor.
     */
    public EjbPrepararAno() {
    }

    @Override
    public void copiarAuxiliar(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_AUXILIAR",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarCentroCosto(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_CENTRO_COSTO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarReferencia(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_REFERENCIA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarFuenteRecurso(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_FUENTE_RECURSO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void prepararAnoSiguiente(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_PREPARA_ANO_SIGUIENTE",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarPlanContable(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_PLAN_CONTABLE",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarCuentaBanco(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_CUENTA_BANCOS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarParametro(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_PARAMETRO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTipoComprobante(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_TIPO_COMPROBANTE",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarConsecutivoTc(
        String compania,
        int anio,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_CONSECUTIVOTC",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANIO              =>" + anio + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTercero(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_TERCERO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTipoComprobantePptal(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_TIPOCOMPROBANTEPP",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarConsecutivoTcp(
        String compania,
        int anio,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_CONSECUTIVOTCP",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANIO              =>" + anio + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void crearDatosContables(
        String compania,
        int ano,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_CREAR_DATOS_CONTABLES",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO               =>" + ano + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void crearRegistrosBasicos(
        String compania,
        int anio)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_CREAR_REGISTROS_BASICOS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANIO              =>" + anio + "");
    }

    @Override
    public void copiarTiposDocumentos(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_TIPOS_DOCUMENTOS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTipoOrdenDeCompra(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_TIPOORDENDECOMPRA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarAno(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_ANO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarEstadoCivil(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_ESTADO_CIVIL",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void coipiarUnidadAv(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_UNIDAD_AV",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarGastoDeInversion(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_GASTOS_DE_INVERSION",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarClaseGastoInversion(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_CLASE_GASTOINVERSION",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiaBpActividades(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_BP_ACTIVIDADES",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTiposComponentes(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_BP_TIPOSCOMPONENTES",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarUnidadProyectos(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_UNIDAD_PROYECTOS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarSerctorDnp(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_SERCTORDNP",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarClaseProblema(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_SP_CLASEPROBLEMA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarServicio(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_SP_SERVICIO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarCodigosCiiu(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_SP_CODIGOSCIIU",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarEstadosCobro(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIA_SP_ESTADOSCOBRO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiaSolicitudServicio(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIA_SOLICITUDPORSERVICIO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarServiciosPublicos(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_SERVICIOS_PUBLICOS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarBanco(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_BANCO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTiposEmpleados(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_TIPOS_DE_EMPLEADO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarCausaRetiro(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_CAUSARETIRO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarFestivos(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_FESTIVOS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarParentesco(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_PARENTESCO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarProcesosNomina(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_PROCESOS_DE_NOMINA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarReporteFormateado(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_IP_REPORTE_FORMA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTipoActivo(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_TIPO_ACTIVO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarTipoNovedad(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_BPTIPONOVEDAD",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarJuzgados(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_JUZGADOS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void copiarRetefuenteUvt(
        String compania,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_RETEFUENTEUVT",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }

    @Override
    public void prepararInicioVigencia(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino,
        boolean copiarnomina,
        boolean copiarcontabilidad,
        boolean copiarfactgeneral)
                    throws SystemException {

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_INICIO_VIGENCIA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>"
                            + Integer.toString(anoDestino) + ", "
                            + "UN_ANO_ORIGEN        =>"
                            + Integer.toString(anoOrigen) + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "', "
                            + "UN_COPIARNOMINA      =>"
                            + (copiarnomina ? "-1" : "0") + ", "
                            + "UN_COPIARCONTABILIDAD =>"
                            + (copiarcontabilidad ? "-1" : "0") + ", "
                            + "UN_COPIARFACTGENERAL =>"
                            + (copiarfactgeneral ? "-1" : "0")

        );
    }
    
    @Override
    public void copiarPlanFlujoEfectivo(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_PLAN_FLUJO_EFECTIVO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }
    
    @Override
    public  void   crearDatosNomina(
    		String compania, 
    		int anioDestino, 
    		String companiaDestino) 
    				throws SystemException {
    	String[] parametros ={                   
    			  "UN_COMPANIA          =>'" , compania , "', "
    			, "UN_ANIO_DESTINO      =>" , Integer.toString(anioDestino) , ", "
    			, "UN_COMPANIA_DESTINO  =>'" , companiaDestino , "'"
    	};
    	AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_PREPARAR_ANO.PR_CREAR_DATOS_NOMINA",
    			SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void copiarConceptoCentroCosto(
        String compania,
        int anoDestino,
        int anoOrigen,
        int concepto,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREPARAR_ANO.PR_COPIAR_CPTO_CENTROCOSTO",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
                            + "UN_CONCEPTO          =>" + concepto + ", "
                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                            + "'");
    }
    
	@Override
	public  String     validarRefyCC(
			String compania, 
			int ano_i, 
			int ano_f
	)
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_ANO_I          =>" , Integer.toString(ano_i) , ", "
					, "UN_ANO_F     =>" , Integer.toString(ano_f) , ""
			};
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_PREPARAR_ANO.FC_VALIDAR_REFCC",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));

		}
		catch ( IOException | SQLException  e) {
			throw new SystemException(e);
		}
	}
	
	  @Override
	    public void copiarUnidadEjecutora(
	        String compania,
	        int anoDestino,
	        int anoOrigen,
	        String companiaDestino)
	                    throws SystemException {
	        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
	                        "PCK_PREPARAR_ANO.PR_COPIAR_UNIDAD_EJECUTORA",
	                        "UN_COMPANIA          =>'" + compania + "', "
	                            + "UN_ANO_DESTINO       =>" + anoDestino + ", "
	                            + "UN_ANO_ORIGEN        =>" + anoOrigen + ", "
	                            + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
	                            + "'");
	    }
	  
	  @Override
	  public String obtenerEquivalencias(
	      String compania,
	      int anoDestino,
	      int anoOrigen,
	      String companiaDestino)
	                  throws SystemException {
	      String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
	                             "UN_ANO_DESTINO       =>", Integer.toString(anoDestino), ", ",
	                             "UN_ANO_ORIGEN        =>", Integer.toString(anoOrigen), ", ",
	                             "UN_COMPANIA_DESTINO  =>'", companiaDestino, "'"
	      };
	      return (String) AccionesImp.ejecutarFuncion(
	                      ConectorPool.ESQUEMA_SYSMAN,
	                      "PCK_PREPARAR_ANO.FC_COPIAR_PPTAL_CTA_CNT",
	                      SysmanFunciones.concatenar(parametro),
	                      Types.VARCHAR);
	  }

}