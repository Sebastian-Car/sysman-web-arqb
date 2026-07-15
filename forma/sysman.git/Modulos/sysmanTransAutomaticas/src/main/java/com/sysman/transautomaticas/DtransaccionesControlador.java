/*-
 * DtransaccionesControlador.java
 *
 * 1.0
 *
 * 04/10/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.transautomaticas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.AseguradorasControlador;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.transautomaticas.ejb.EjbTransAutomaticasCeroRemote;
import com.sysman.transautomaticas.enums.DTransaccionesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * @version 1.0, 04/10/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class DtransaccionesControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String valor;
    private String baseGravable;
    private String cuentaContable;
    private String orden;
    private String ano;
    private String tipo;
    private String numero;
    private String numeroModelo;
    private boolean bloqueoValor;
    private boolean bloqueoContinuo;
    private boolean validarModificar;
    private boolean inactivar;
    private int indice;

    Map<String, Object> parametroTransaccion;
    /**
     * Crea una nueva instancia de DtransaccionesControlador
     */
    @EJB
    private EjbTransAutomaticasCeroRemote ejbTransAutomaticas;

    @SuppressWarnings("unchecked")
    public DtransaccionesControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {

            numFormulario = GeneralCodigoFormaEnum.DTRANSACCIONES_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            inactivar = true;
            if (parametrosEntrada != null)
            {

                parametroTransaccion = (Map<String, Object>) parametrosEntrada
                                .get("rowTransaccion");
                ano = (String) parametrosEntrada
                                .get(GeneralParameterEnum.ANO.getName());
                tipo = parametrosEntrada
                                .get(GeneralParameterEnum.TIPO.getName()).toString();
                numero = (String) parametrosEntrada
                                .get(GeneralParameterEnum.NUMERO.getName());
                numeroModelo = (String) parametrosEntrada.get("NUMEROMODELO");
                validarModificar = (boolean) parametrosEntrada
                                .get("confirmado");
            }
            bloqueoValor = false;

            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(AseguradorasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.D_TRANSACCIONES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        validarConfirmarTransaccion();
        abrirFormulario();
    }

    public void validarConfirmarTransaccion()
    {

        try
        {

            Map<String, Object> parametrosComp = new HashMap<>();

            parametrosComp.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosComp.put(GeneralParameterEnum.ANO.getName(),
                            ano);
            parametrosComp.put(GeneralParameterEnum.TIPO.getName(), tipo);
            parametrosComp.put("NUMEROMODELO", numeroModelo);
            parametrosComp.put(GeneralParameterEnum.NUMERO.getName(), numero);

            Registro registroComprobante;
            registroComprobante = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DTransaccionesControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), parametrosComp));
            if (registroComprobante != null)
            {

                if (SysmanFunciones.validarCampoVacio(
                                registroComprobante.getCampos(),
                                GeneralParameterEnum.TIPO_CPTE.getName())
                    && SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                    GeneralParameterEnum.COMPROBANTE
                                                    .getName()))
                {
                    inactivar = false;
                }
                else
                {
                    inactivar = true; // true
                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametrosListado.put("NUMEROMODELO", numeroModelo);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);

        try
        {
            String salida = ejbTransAutomaticas.modificarTransaccionesRete(
                            compania,
                            Integer.parseInt(ano),
                            tipo,
                            numeroModelo,
                            new BigInteger(numero),
                            SessionUtil.getUser().getCodigo());
            if (!"".equals(salida.trim()))
            {
                JsfUtil.agregarMensajeAlerta(salida);
            }
            salida = ejbTransAutomaticas.calcularTransaccion(
                            compania,
                            Integer.parseInt(ano),
                            tipo,
                            numeroModelo,
                            new BigInteger(numero),
                            SessionUtil.getUser().getCodigo());
            if (!"".equals(salida.trim()))
            {
                JsfUtil.agregarMensajeAlerta(salida);
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * @param registro
     */
    public void activarEdicion(Registro registro)
    {

        indice = listaInicial.getRowIndex();

    }

    /**
     *
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues()
    {

        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        registro.getCampos().remove("MOSTRARPEDIRNOMBRE");
        registro.getCampos().remove("PEDIRMOSTRAR");
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarAntes()
    {

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarDespues()
    {

        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     *
     */
    public void ejecutarrcCerrar()
    {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("parametroTransaccion", parametroTransaccion);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        parametros.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametros.put(GeneralParameterEnum.NUMERO.getName(), numero);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.DATOS_TRANSACCIONS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    public void oprimirCalcular()
    {
        try
        {
            String salida = ejbTransAutomaticas.modificarTransaccionesRete(
                            compania,
                            Integer.parseInt(ano),
                            tipo,
                            numeroModelo,
                            new BigInteger(numero),
                            SessionUtil.getUser().getCodigo());
            if (!"".equals(salida.trim()))
            {
                JsfUtil.agregarMensajeAlerta(salida);
            }
            salida = ejbTransAutomaticas.calcularTransaccion(
                            compania,
                            Integer.parseInt(ano),
                            tipo,
                            numeroModelo,
                            new BigInteger(numero),
                            SessionUtil.getUser().getCodigo());
            if (!"".equals(salida.trim()))
            {
                JsfUtil.agregarMensajeAlerta(salida);
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getCompania()
    {
        return compania;
    }

    public String getValor()
    {
        return valor;
    }

    public void setValor(String valor)
    {
        this.valor = valor;
    }

    public String getBaseGravable()
    {
        return baseGravable;
    }

    public void setBaseGravable(String baseGravable)
    {
        this.baseGravable = baseGravable;
    }

    public String getCuentaContable()
    {
        return cuentaContable;
    }

    public void setCuentaContable(String cuentaContable)
    {
        this.cuentaContable = cuentaContable;
    }

    public String getOrden()
    {
        return orden;
    }

    public void setOrden(String orden)
    {
        this.orden = orden;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public String getNumero()
    {
        return numero;
    }

    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    public Map<String, Object> getParametroTransaccion()
    {
        return parametroTransaccion;
    }

    public void setParametroTransaccion(
        Map<String, Object> parametroTransaccion)
    {
        this.parametroTransaccion = parametroTransaccion;
    }

    public EjbTransAutomaticasCeroRemote getEjbTransAutomaticas()
    {
        return ejbTransAutomaticas;
    }

    public void setEjbTransAutomaticas(
        EjbTransAutomaticasCeroRemote ejbTransAutomaticas)
    {
        this.ejbTransAutomaticas = ejbTransAutomaticas;
    }

    public boolean isBloqueoValor()
    {
        return bloqueoValor;
    }

    public void setBloqueoValor(boolean bloqueoValor)
    {
        this.bloqueoValor = bloqueoValor;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public boolean isBloqueoContinuo()
    {
        return bloqueoContinuo;
    }

    public void setBloqueoContinuo(boolean bloqueoContinuo)
    {
        this.bloqueoContinuo = bloqueoContinuo;
    }

    public boolean isValidarModificar()
    {
        return validarModificar;
    }

    public void setValidarModificar(boolean validarModificar)
    {
        this.validarModificar = validarModificar;
    }

    public String getNumeroModelo()
    {
        return numeroModelo;
    }

    public void setNumeroModelo(String numeroModelo)
    {
        this.numeroModelo = numeroModelo;
    }

    public boolean isInactivar()
    {
        return inactivar;
    }

    public void setInactivar(boolean inactivar)
    {
        this.inactivar = inactivar;
    }

}
