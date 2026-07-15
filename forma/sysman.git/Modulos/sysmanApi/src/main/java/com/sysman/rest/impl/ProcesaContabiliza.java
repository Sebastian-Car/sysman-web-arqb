package com.sysman.rest.impl;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;

import com.google.gson.Gson;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroGeneralRemote;
import com.sysman.contabilidad.ejb.impl.EjbContabilidadCeroGeneral;
import com.sysman.contabilizar.ejb.EjbContabilizarJsonGeneralRemote;
import com.sysman.contabilizar.ejb.impl.EjbContabilizarJsonGeneral;
import com.sysman.exception.SystemException;

import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.enums.EnumAuxiliaresVarios;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.Comprobante;
import com.sysman.rest.logica.DetalleComprobante;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.RespuestaApi;
import javax.inject.Named;

/**
 * Procesador de Contabilizar
 *
 * @version 1.0, 07/03/2019
 * @author jgomez
 */
@Named ("procesaContabiliza")
@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesaContabiliza
extends Procesador<Comprobante, RespuestaApi>
{

    /**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Abstraccion creada para emitir respuesta del procesador
     */
    RespuestaApi respuestaAPi = new RespuestaApi();

    /**
     * comprobante Parámetro que representan los valores obtenidos del objeto
     * Comprobante
     */
    private Comprobante comprobante;
     
    /**
     * LOG Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProcesaContabiliza.class);

    /**
     * Lista los errores que se controlan del json
     */
    List<String> errores = new ArrayList<>();

    /**
     * CONSTANTEANTERIOR Constante que obtendrá el que en el modelo 
     * access tenian los auxiliares para VARIOS
     */
    private static final String AUX_ANTERIOR = "9999999999";

    /**
     * AUXILIAR Constante que obtendrá el valor por defecto 9999999999999999
     */
    private static final String AUXILIAR = AUX_ANTERIOR;											
    /**
     * CONSTCENTRO Constante que obtendrá el valor por defecto 9999999999
     * 
     */
    private static final String CENTRO = AUX_ANTERIOR;
    /**
     * CONSTERCERO Constante que obtendrá el valor por defecto 999999999999999999
     * ver PCK_DATOS
     */
    /**
     * Constante que identifica el tercero VARIOS de access 
     */
    private static final String TERCERO = "99999999999";

    /**
     * constante el modulo de contabilidad
     */
    private static final int MODULO_CONTABILIDAD = 1;

    /**
     * constante el proceso de contabilidad
     */
    private static final int PROCESO_CONTABILIDAD = 1;

    /**
     * constante para identificar el estado Activo
     */
    private static final String ESTADO_ACTIVO = "A";

    /**
     * conseDetalle Constante que se utiliza como contador para asignarle el valor al
     * consecutivo del detalle comprobante
     */
    private int conseDetalle;

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private static EjbSysmanUtilRemote ejbSysmanUtil = new EjbSysmanUtil();

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_CONTABILIDAD
     */
    @EJB
    private static EjbContabilidadCeroGeneralRemote ejbContabilidadCero = new EjbContabilidadCeroGeneral();
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_CONTABILIDAD
     */
    @EJB
    private static EjbContabilizarJsonGeneralRemote ejbContabilizarJson = new EjbContabilizarJsonGeneral(); 
    
    /**
     * Para implementar hilos con la clase <code>Runnable</code>,
     */
    @Override
    public void run()
    {
        // Sin implementar
    }

    /**
     * Acciones que se ejecutan antes de procesar una petición.
     * @throws NegocioExcepcion 
     */
    @Override
    protected void preProcesar() throws NegocioExcepcion
    {
        /**
         * valorDebitodouble Constante para obtener el valor débito en tipo INT
         */
        String credito;
        /**
         * debito Constante para obtener el valor débito
         */
        String debito;    
        if (contexto != null)
        {
            /**
             * Se procesa el contexto de ejecución
             */
            this.comprobante = contexto;

            /*
             * Se realiza la respectiva validación de los campos ingresados del comprobante
             * Header
             */
            if ( SysmanFunciones.validarVariableVacio(comprobante.getCompania())) {
                errores.add(new StringBuilder().append((errores.isEmpty() ? "" : (char) 13))
                                .append("Ingrese correctamente la compania del comprobante").toString());
            }
            if (SysmanFunciones.validarVariableVacio(comprobante.getTipo())) {
                errores.add(new StringBuilder().append((errores.isEmpty() ? "" : (char) 13))
                                .append("Ingrese correctamente el tipo de comprobante").toString());
            }
            if (SysmanFunciones.validarVariableVacio(comprobante.getFecha())) {
                errores.add(new StringBuilder().append((errores.isEmpty() ? "" : (char) 13))
                                .append("Ingrese correctamente la fecha del comprobante").toString());
            }	

            /*
             * Se valida para asignar los valores de VARIOS a los auxiliares cuando estos
             * no se envian en la petición
             */
            if ((TERCERO).equals(comprobante.getTercero())) {
                comprobante.setTercero(EnumAuxiliaresVarios.TERCERO.getValue());
            }
            if (SysmanFunciones.validarVariableVacio(comprobante.getTercero()) || SysmanFunciones.validarVariableVacio(comprobante.getSucursal())) {
                comprobante.setTercero(EnumAuxiliaresVarios.TERCERO.getValue());
                comprobante.setSucursal(EnumAuxiliaresVarios.SUCURSAL.getValue());
            }
            if (EnumAuxiliaresVarios.TERCERO.getValue().equals(comprobante.getTercero()) && !(EnumAuxiliaresVarios.SUCURSAL.getValue()).equals(comprobante.getSucursal())) {
                comprobante.setSucursal(EnumAuxiliaresVarios.SUCURSAL.getValue());
            }
            if (!(EnumAuxiliaresVarios.TERCERO.getValue().equals(comprobante.getTercero())) && EnumAuxiliaresVarios.SUCURSAL.getValue().equals(comprobante.getSucursal())) {
                comprobante.setSucursal(EnumAuxiliaresVarios.SUCURSAL001.getValue());
            }			
            if (!(comprobante.getSucursal().matches("[0-9]*"))) {
                errores.add(new StringBuilder().append((errores.isEmpty() ? "" : (char) 13))
                                .append("La sucursal del comprobante ").append(comprobante.getSucursal())
                                .append(" no es válido. Por favor Ingrese correctamente").toString());
            }
            /*
             * Se valida que los auxiliares de modelos anteriores queden con las nuevas 
             */
            if (SysmanFunciones.validarVariableVacio(comprobante.getCentroCosto())) {
                comprobante.setCentroCosto(EnumAuxiliaresVarios.CENTROCOSTO.getValue());				
            }
            if (SysmanFunciones.validarVariableVacio(comprobante.getAuxiliar())) {
                comprobante.setAuxiliar(EnumAuxiliaresVarios.AUXILIAR.getValue());				
            }
            if (SysmanFunciones.validarVariableVacio(comprobante.getReferencia())) {
                comprobante.setReferencia(EnumAuxiliaresVarios.REFERENCIA.getValue());				
            }
            if (SysmanFunciones.validarVariableVacio(comprobante.getFuenteRecurso())) {
                comprobante.setFuenteRecurso(EnumAuxiliaresVarios.FUENTERECURSO.getValue());				
            }
            if (CENTRO.equals(comprobante.getCentroCosto())) {
                comprobante.setCentroCosto(EnumAuxiliaresVarios.CENTROCOSTO.getValue());
            }
            if (AUXILIAR.equals(comprobante.getAuxiliar())) {
                comprobante.setAuxiliar(EnumAuxiliaresVarios.AUXILIAR.getValue());
            }
            
            if (SysmanFunciones.validarVariableVacio(comprobante.getNroDocumento())) {
                comprobante.setNroDocumento("");
            } 

            /*
             * validar que la fecha venga en el formato correcto
             */
            String fechaC = comprobante.getFecha();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_CO"));
            formatter.setLenient(false);
            try {
                Date date = formatter.parse(fechaC.trim());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String fechaComp = sdf.format(date);
                comprobante.setFecha(fechaComp);
                /*
                 * Extrae el año de la fecha ingresada
                 */
                String formatoano2 = "yyyy";
                SimpleDateFormat dateFormat2 = new SimpleDateFormat(formatoano2);
                int anos = Integer.parseInt(dateFormat2.format(date));

                /*
                 * Extrae el mes de la fecha ingresada
                 */
                String formatomes2 = "MM";
                SimpleDateFormat dateFormato = new SimpleDateFormat(formatomes2);
                int mes = Integer.parseInt(dateFormato.format(date));

                /*
                 * Extrae el mes de la fecha ingresada
                 */
                String formatomes3 = "dd";
                SimpleDateFormat dateFormato3 = new SimpleDateFormat(formatomes3);
                int dia = Integer.parseInt(dateFormato3.format(date));

                String estadoDia = ejbSysmanUtil.verificarEstadoDiario(
                                comprobante.getCompania(),
                                anos,
                                mes,
                                dia,
                                MODULO_CONTABILIDAD,
                                PROCESO_CONTABILIDAD);
                if (!(ESTADO_ACTIVO.equals(estadoDia))) {
                    respuestaAPi.setCuerpo(new StringBuilder().append("La fecha: ").append(comprobante.getFecha())
                                    .append(" ingresada esta cerrado. Por favor revise e intente de nuevo")
                                    .toString());
                    respuestaAPi.setCodigo(10016);
                    throw new NegocioExcepcion(new StringBuilder().append("La fecha: ").append(comprobante.getFecha())
                                    .append(" ingresada esta cerrado. Por favor revise e intente de nuevo")
                                    .toString());
                }
            } catch (ParseException  e) {
                String mensaje =new StringBuilder().append("La fecha no es válida")
                                .append(fechaC)
                                .append(". El formato valido es dd/mm/yyyy").toString();
                respuestaAPi.setCuerpo(mensaje);
                respuestaAPi.setCodigo(10016);
                throw new NegocioExcepcion(mensaje);				
            } catch (SystemException e) {
                respuestaAPi.setCuerpo("Error al consultar el estado del año");
                respuestaAPi.setCodigo(10016);
                throw new NegocioExcepcion("Error al consultar el estado del año");
            }
            /*
             * Se realiza la respectiva validación de los campos ingresados del detalle
             * comprobante
             */
            for (DetalleComprobante lista : comprobante.getDetalle()) {
                conseDetalle++;
                if (SysmanFunciones.validarVariableVacio(lista.getCuenta())) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                                    .append("Ingrese correctamente la cuenta del registro Número ").append(conseDetalle).toString());
                }

                if (SysmanFunciones.validarVariableVacio(lista.getValorCredito())) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                                    .append("Ingrese correctamente el valor del cŕedito del registro Número").append(conseDetalle)
                                    .toString());
                }
                if (SysmanFunciones.validarVariableVacio(lista.getValorDebito())) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                                    .append("Ingrese correctamente el valor del débito del registro Número").append(conseDetalle)
                                    .toString());
                }

                if (SysmanFunciones.validarVariableVacio(lista.getNroDocumento())) {
                    lista.setNroDocumento("");
                } 

                if (!lista.getValorDebito().matches("[^,]*")) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                                    .append("El valor débito").append(lista.getValorDebito())
                                    .append(" del registro número: ").append(conseDetalle).append(" es inválido").toString());
                }
                if (lista.getValorDebito().matches("[0-9.]*")) {
                    debito = lista.getValorDebito().replace("0", "");
                    if (debito.equalsIgnoreCase("")) {
                        lista.setValorDebito("0");
                    } else {
                        lista.setValorDebito(lista.getValorDebito());
                    }
                }
                if (!lista.getValorCredito().matches("[^,]*")) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                                    .append("El valor crédito ").append(lista.getValorCredito())
                                    .append(" del registro número:").append(conseDetalle).append(" es inválido").toString());
                }
                if (lista.getValorCredito().matches("[0-9.]*")) {
                    credito = lista.getValorCredito().replace("0", "");
                    if (credito.equalsIgnoreCase("")) {
                        lista.setValorCredito("0");
                    } else {
                        lista.setValorCredito(lista.getValorCredito());
                    }
                }

                /*
                 * Se realiza una consulta para validar la cuenta, la sentencia esta
                 * identificada como SQL_10. Ver en la tabla CONSULTAS_GENERALES
                 * Se valida si la cuenta existe en al plan contable y tiene movimiento o auxiliar 
                 * a demas si tiene cuenta presupuestas para asignarla
                 */
                try {
                    @SuppressWarnings("unused")
                    String natu=ejbContabilidadCero.validarCuentaUtilizar(comprobante.getCompania(), comprobante.getAno(), lista.getCuenta(), true);
                } catch (SystemException e) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                                    .append("El registro número: ").append(conseDetalle)
                                    .append(" La siguiente cuenta: ").append(lista.getCuenta())
                                    .append(" no existe o no tiene movimiento o auxiliar en el plan de cuenta y esta adjudicada a los procesos.")
                                    .append("Por favor revise su configuración y vuelve a  realizar el proceso.")                                    
                                    .toString());
                }				
            }
            if (errores.isEmpty() || errores == null) {
                /**
                 * Se aprueba prevalidación
                 */
                esValido = true;
            } else {
                esValido=false;
                respuestaAPi.setCuerpo(errores);
                respuestaAPi.setCodigo(10006);                
               throw new NegocioExcepcion(errores.toString());
            }
        }
    }

    /**
     * Acciones que se ejecutan despues de procesar una petición.
     */
    @Override
    protected void posProcesar()
    {
        LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(),
                        resultado);
    }

    /**
     * Acciones que ejecuta el procesador o comando concreto.
     */
    @Override
    public void ejecutar() throws NegocioExcepcion
    {
        Gson gSon  = new Gson();
        String json="";
        json=gSon.toJson(comprobante);
        String jsonSql ="";
        try {
            int largo = json.length();
            int control=0;
            while (control+3000<largo) {
                jsonSql = jsonSql +  " TO_CLOB('" + json.substring(control, control+3000) + "') || ";
                control+=3000 ;
            }
            jsonSql = jsonSql +  " TO_CLOB('" + json.substring(control) + "') ";
            
            int numero =ejbContabilizarJson.contabilizarJson(jsonSql);
            respuestaAPi.setCuerpo(numero);
            resultado = respuestaAPi;
        } catch (SystemException e) {
            String rta = e.getMessage();
            if (rta.contains("@#INI#@")) {
                    rta = rta.substring(e.getMessage().indexOf("@#INI#@"));
                    rta = rta.substring(rta.indexOf("Log:"), rta.indexOf("@#FIN#@"));
            }
            respuestaAPi.setMensaje("Error en el Procedimiento");
            respuestaAPi.setCuerpo(rta);
            respuestaAPi.setCodigo(10006);
            resultado =respuestaAPi;
        }		
    }

    /**
     * Obtiene el ejecutable o worker.
     */
    @Override
    public Runnable getEjecutable()
    {
        return this;
    }

    /**
     * Retorna resultado esperado posejecución
     */
    @Override
    public RespuestaApi getResultado()
    {
        return resultado;
    }

    
}
