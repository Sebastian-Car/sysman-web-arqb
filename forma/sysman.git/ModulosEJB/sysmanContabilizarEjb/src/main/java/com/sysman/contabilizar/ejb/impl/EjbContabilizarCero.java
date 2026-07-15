package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarCeroLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarCeroRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilizarCero
 */
@Stateless
@LocalBean
public class EjbContabilizarCero
implements EjbContabilizarCeroRemote, EjbContabilizarCeroLocal {

	@EJB
	private EjbContabilizarCeroGeneral ejbContabilizarCeroGeneral;

	/**
	 * Default constructor.
	 */
	public EjbContabilizarCero() {
	}

	@Override
	public void cargarInterfazAlmacenCC(
			String compania,
			String codigoElemento,
			int ano)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_CODIGOELEMENTO    =>'", codigoElemento,
				"', ", "UN_ANO               =>",
				Integer.toString(ano), ""
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CONTABILIZAR.PR_CARGARINTERFAZALMACENCC",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public String contabilizarPorPlano(
			String compania,
			boolean resumido,
			boolean sinPptal,
			boolean terceroDetalle,
			boolean conciliar,
			String plano,
			String usuario)
					throws SystemException {
		return ejbContabilizarCeroGeneral.contabilizarPorPlano(compania,
				resumido, sinPptal, terceroDetalle, conciliar, plano,
				usuario);
	}

	@Override
	public void cargarInterfazAlmacenCCNIIF(
			String compania,
			String codigoelemento,
			int ano)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				"UN_CODIGOELEMENTO    =>'", codigoelemento,
				"', ",
				"UN_ANO               =>",
				Integer.toString(ano), ""
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CONTABILIZAR.PR_CARGARINTERFAZALMACENCCNIIF",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public void prepararContabilizacionSiguienteAnio(
			String compania,
			int anioInicial,
			int anoFinal,
			String usuario)
					throws SystemException {
		String[] parametros = { "UN_COMPANIA          => '", compania, "', ",
				"UN_ANIOINICIAL       => ",
				Integer.toString(anioInicial), " , ",
				"UN_ANIOFINAL         => ",
				Integer.toString(anoFinal), ", ",
				"UN_USUARIO           => '", usuario, "' "
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CONTABILIZAR.PR_PREPARARCONTABI_SIGANO",
				SysmanFunciones.concatenar(parametros));
	}

	@Override
	public String contabilizarPorPlanoEsp(
			String compania,
			boolean resumido,
			boolean sinPptal,
			boolean terceroDetalle,
			boolean conciliar,
			String plano,
			String usuario)
					throws SystemException {
		return ejbContabilizarCeroGeneral.contabilizarPorPlanoEsp(compania,
				resumido, sinPptal, terceroDetalle, conciliar, plano,
				usuario);
	}



	@Override
	public  String     cargarInterfazporXls(
			String compania, 
			String tipoCpte, 
			String numero, 
			String cadena, 
			int colfecha, 
			int colidcontable, 
			int colvalor, 
			int colvalorCredito,
			int coldetalle, 
			int coltexto, 
			int colbase, 
			int colcontrato, 
			int coldocumento, 
			int coltercero, 
			int colsucursal, 
			int colcentrocosto, 
			int fuenteRecursos,
			int referencia,
			int colauxiliar, 
			int coltipocontrato, 
			boolean retenciones, 
			boolean tercero, 
			boolean agrupado, 
			boolean aplicaaux, 
			int modulo, 
			int filaini, 
			String auxContra,
			String centroContra,
			String fuenteRContra,
			String referenciaContra,
			String descripcion, 
			String usuario)
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_TIPO_CPTE         =>'" , tipoCpte , "', "
					, "UN_NUMERO            =>'" , numero , "', "
					, "UN_CADENA            =>" , cadena , ", "
					, "UN_COLFECHA          =>" , Integer.toString(colfecha) , ", "
					, "UN_COLIDCONTABLE     =>" , Integer.toString(colidcontable) , ", "
					, "UN_COLVALOR          =>" , Integer.toString(colvalor) , ", "
					, "UN_COLVALOR_CREDITO  =>" , Integer.toString(colvalorCredito) , ", "
					, "UN_COLDETALLE        =>" , Integer.toString(coldetalle) , ", "
					, "UN_COLTEXTO          =>" , Integer.toString(coltexto) , ", "
					, "UN_COLBASE           =>" , Integer.toString(colbase) , ", "
					, "UN_COLCONTRATO       =>" , Integer.toString(colcontrato) , ", "
					, "UN_COLDOCUMENTO      =>" , Integer.toString(coldocumento) , ", "
					, "UN_COLTERCERO        =>" , Integer.toString(coltercero) , ", "
					, "UN_COLSUCURSAL       =>" , Integer.toString(colsucursal) , ", "
					, "UN_COLCENTROCOSTO    =>" , Integer.toString(colcentrocosto) , ", "
					, "UN_COLFUENTER        =>" , Integer.toString(fuenteRecursos) , ", "
					, "UN_COLREFERENCIA     =>" , Integer.toString(referencia) , ", "
					, "UN_COLAUXILIAR       =>" , Integer.toString(colauxiliar) , ", "
					, "UN_COLTIPOCONTRATO   =>" , Integer.toString(coltipocontrato) , ", "
					, "UN_RETENCIONES       =>" , (retenciones?"-1":"0")  , ", "
					, "UN_TERCERO           =>" , (tercero?"-1":"0")  , ", "
					, "UN_AGRUPADO          =>" , (agrupado?"-1":"0")  , ", "
					, "UN_APLICAAUX         =>" , (aplicaaux?"-1":"0")  , ", "
					, "UN_MODULO            =>" , Integer.toString(modulo) , ", "
					, "UN_FILAINI           =>" , Integer.toString(filaini)   ,", "
					, "UN_AUX_CONTRA        =>'" , auxContra    , "', "
					, "UN_CENTRO_CONTRA     =>'" , centroContra , "', "
					, "UN_FUENTER_CONTRA    =>'" , fuenteRContra, "', "
					, "UN_REFERENCIA_CONTRA =>'" , referenciaContra, "', "
					, "UN_DESCRIPCION       =>'" , descripcion , "', "
					, "UN_USUARIO           =>'" , usuario , "'"
			};
			return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTABILIZAR.FC_CARGARINTERFAZ_POR_XLS",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));

		}
		catch ( IOException | SQLException  e) {
			throw new SystemException(e);
		}
	}

	@Override
	public  String     cargarProcesoJudicialporXls(
			String compania, 
			String anio, 
			String tipoCpte, 
			String numero, 
			String cadena, 
			int colfecha, 
			int colidcontable, 
			int coldetalle, 
			int coltexto, 
			int colbase, 
			int colcontrato, 
			int coldocumento, 
			int coltercero, 
			int colsucursal, 
			int colcentrocosto, 
			int colfuenter, 
			int colreferencia, 
			int colauxiliar, 
			int coltipocontrato, 
			int coltipopagoSia, 
			int colcodigoSia, 
			int colvalorDebito, 
			int colvalorCredito, 
			int colnroProceso, 
			boolean tercero, 
			boolean agrupado, 
			boolean aplicaaux, 
			int modulo, 
			int filaini, 
			String descripcion, 
			String usuario) 
					throws SystemException {
		try {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				, "UN_ANIO              =>'" , anio , "', "
				, "UN_TIPO_CPTE         =>'" , tipoCpte , "', "
				, "UN_NUMERO            =>'" , numero , "', "
				, "UN_CADENA            =>" , cadena , ", "
				, "UN_COLFECHA          =>" , Integer.toString(colfecha) , ", "
				, "UN_COLIDCONTABLE     =>" , Integer.toString(colidcontable) , ", "
				, "UN_COLDETALLE        =>" , Integer.toString(coldetalle) , ", "
				, "UN_COLTEXTO          =>" , Integer.toString(coltexto) , ", "
				, "UN_COLBASE           =>" , Integer.toString(colbase) , ", "
				, "UN_COLCONTRATO       =>" , Integer.toString(colcontrato) , ", "
				, "UN_COLDOCUMENTO      =>" , Integer.toString(coldocumento) , ", "
				, "UN_COLTERCERO        =>" , Integer.toString(coltercero) , ", "
				, "UN_COLSUCURSAL       =>" , Integer.toString(colsucursal) , ", "
				, "UN_COLCENTROCOSTO    =>" , Integer.toString(colcentrocosto) , ", "
				, "UN_COLFUENTER        =>" , Integer.toString(colfuenter) , ", "
				, "UN_COLREFERENCIA     =>" , Integer.toString(colreferencia) , ", "
				, "UN_COLAUXILIAR       =>" , Integer.toString(colauxiliar) , ", "
				, "UN_COLTIPOCONTRATO   =>" , Integer.toString(coltipocontrato) , ", "
				, "UN_COLTIPOPAGO_SIA   =>" , Integer.toString(coltipopagoSia) , ", "
				, "UN_COLCODIGO_SIA     =>" , Integer.toString(colcodigoSia) , ", "
				, "UN_COLVALOR_DEBITO   =>" , Integer.toString(colvalorDebito) , ", "
				, "UN_COLVALOR_CREDITO  =>" , Integer.toString(colvalorCredito) , ", "
				, "UN_COLNRO_PROCESO    =>" , Integer.toString(colnroProceso) , ", "
				, "UN_TERCERO           =>" , (tercero?"-1":"0")  , ", "
				, "UN_AGRUPADO          =>" , (agrupado?"-1":"0")  , ", "
				, "UN_APLICAAUX         =>" , (aplicaaux?"-1":"0")  , ", "
				, "UN_MODULO            =>" , Integer.toString(modulo) , ", "
				, "UN_FILAINI           =>" , Integer.toString(filaini) , ", "
				, "UN_DESCRIPCION       =>'" , descripcion , "', "
				, "UN_USUARIO           =>'" , usuario , "'"
		};
		return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTABILIZAR.FC_PROCESO_JUDICIAL_XLS",
				SysmanFunciones.concatenar(parametros),
				Types.CLOB));
		}
		catch ( IOException | SQLException  e) {
			throw new SystemException(e);
		}
	}



}