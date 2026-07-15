package com.sysman.presupuesto.ejb.impl;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroGeneralLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroGeneralRemote;
import com.sysman.util.SysmanFunciones;

/**
 * Session Bean implementation class PresupuestoCuatro
 */
@Stateless
@LocalBean
public class EjbPresupuestoCuatroGeneral implements EjbPresupuestoCuatroGeneralRemote,
EjbPresupuestoCuatroGeneralLocal {

	/**
	 * Default constructor.
	 */
	public EjbPresupuestoCuatroGeneral() {
	}

	@Override
	public String cargarAuxiliares(
			String tabla,
			String cadena,
			String usuario)
					throws SystemException {
		try {
		
			String[] parametro = {
					"UN_TABLA      =>'", tabla, "', ",
					"UN_CADENA     =>", cadena, ", ",
					"UN_USUARIO    =>'", usuario, "'"
			};

			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_PRESUPUESTO_COM4.FC_CARGAR_AUXILIARES",
					SysmanFunciones.concatenar(parametro),
					Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}

}
