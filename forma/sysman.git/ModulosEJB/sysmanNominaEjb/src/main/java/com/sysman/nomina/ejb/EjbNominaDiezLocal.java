package com.sysman.nomina.ejb;


import javax.ejb.Local;

import com.sysman.exception.SystemException;

@Local
public interface EjbNominaDiezLocal {

	String discoBancoAgrario(
			String compania, 
			int proceso, 
			int anio, 
			int mes, 
			int periodo, 
			String banco,
			String fechareporte, 
			boolean todoslosbancos, 
			String observacion, 
			String lote, 
			int informe,
			String tcuentabanorigen, 
			String cuentabanorigen) throws SystemException;

	void actPersonalHist(
			String compania, 
			int procesoin, 
			int ano, 
			int mes, 
			int periodoin, 
			String usuario)
			throws SystemException;

	void duplicarNovedadesVac(
			String compania, 
			String proceso, 
			String concepto, 
			String empleado, 
			String nomEmpleado,
			String escalafon, 
			String ano, 
			String mes, 
			String periodo, 
			String usuario) throws SystemException;
	
	
	public  void  actEnvioCorreoDian(
			String compania, 
			String empleado, 
			String ano) 
					throws SystemException;
	
	int copiarDistribucion(
            String compania, 
            int proceso, 
            int anoOrigen, 
            int anoDestino, 
            int mesOrigen,
            int mesDestino, 
            int periodoOrigen, 
            int periodoDestino) throws SystemException;

    String ajustarDecimalesDist(
            String compania, 
            String mes, 
            String periodo, 
            String anio, 
            String proceso, 
            String user)
            throws SystemException;

    void distribuirDatos(
            String compania, 
            String proceso, 
            String ano, 
            String mes, 
            String periodo, 
            String usuario)
            throws SystemException;

    void calcularDistAux(
            String compania, 
            String mes, 
            String periodo, 
            String anio, 
            String proceso, 
            String codempleado,
            String user) throws SystemException;

	void generarDistMensual(
			String compania, 
			String proceso, 
			String anio, 
			String mes, 
			String periodo, 
			String usuario)
			throws SystemException;

}



