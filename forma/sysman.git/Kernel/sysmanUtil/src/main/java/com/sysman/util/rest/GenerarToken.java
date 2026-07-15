package com.sysman.util.rest;

/**
 * Se encarga de Generar el token en formato Base 64
 * 
 * @author Maria Fernanda Ochoa
 * @version 1.0
 *
 */
public class GenerarToken {
    /**
     * atributo que se traido de un enumerado creado apartir del NIT
     * de la entidad
     */
    public String entidad;
    /**
     * Atributo que se hace referencia al cogigo del servicio que
     * necesita generar token
     */
    public String codigoServicio;
    /**
     * Atributo que guardara el hasgh
     */
    private int hashCodeResult;

    /**
     * COnstructor de la clase
     * 
     * @param entidad
     * @param codigoServicio
     */
    public GenerarToken(String entidad, String codigoServicio) {
        this.entidad = entidad;
        this.codigoServicio = codigoServicio;
    }

    /**
     * Obtener la entidad
     * 
     * @return entidad
     */

    public String getEntidad() {
        return entidad;
    }

    /**
     * MOdifica la entidad
     * 
     * @param entidadNueva
     */
    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    /**
     * Obtiene el codigo del servicio
     * 
     * @return codigo del servicio
     */
    public String getCodigoServicio() {
        return codigoServicio;
    }

    /**
     * Modifica el codigo del servicio
     * 
     * @param codigoServicio
     */
    public void setCodigoServicio(String codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    /* ******** M E T O D O S P R O P I O S ******** */
    @Override
    public int hashCode() {
        final int prime = Integer.parseInt(getEntidad());
        hashCodeResult = 1;
        setCodigoServicio("11");
        hashCodeResult = prime * hashCodeResult
            + ((getCodigoServicio() == null) ? 0
                : getCodigoServicio().hashCode());
        hashCodeResult = prime * hashCodeResult
            + ((getEntidad() == null) ? 0 : getEntidad().hashCode());
        return hashCodeResult;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GenerarToken other = (GenerarToken) obj;
        if (getCodigoServicio() == null) {
            if (other.getCodigoServicio() != null)
                return false;
        } else if (!getCodigoServicio().equals(other.getCodigoServicio()))
            return false;
        if (getEntidad() == null) {
            if (other.getEntidad() != null)
                return false;
        } else if (!getEntidad().equals(other.getEntidad()))
            return false;
        return true;
    }

    /**
     * Metodo que se encarga de devolver el Token en una cadena ya
     * codificada en base64
     */
    public String Base64Hash() {
        hashCode();
        Cripto cri = new Cripto();
        String b64 = null;
        try {
            b64 = cri.encodeBase64(String.valueOf(hashCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b64;
    }

    /**
     * Metodo que se encarga de validar un token recibido
     * 
     * @return Aceptacion o no de la validacion del token
     */
    public boolean validarToken(String TokenAvalidar) {
        Base64Hash();
        try {
            if (Base64Hash().equals(TokenAvalidar))
                return true;
        } catch (Exception e) {
        }
        return false;
    }

}