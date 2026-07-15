package com.sysman.general;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;

import com.sysman.kernel.api.commons.util.exceptions.SysmanException;


/**
 *
 * @author Somos programadores https://web.facebook.com/developers08062019
 */
public class MD5 {

    public static void main(String[] args) {
        String secretKey = "SomosProgramadores";
        MD5 mMain = new MD5();
        String cadenaAEncriptar = "Ingresa la cadena a encriptar";
        String cadenaEncriptada = mMain.ecnode(secretKey, cadenaAEncriptar);
        System.out.println("Cadena encriptada: " + cadenaEncriptada);
        String cadenaDesencriptada = mMain.deecnode(secretKey, "5BC02A5C4E0330EFF4EB76C4067F3878");
        System.out.println("Cadena desencriptada: " + cadenaDesencriptada);

    }

    public String ecnode(String secretKey, String cadena) {
        String encriptacion = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] llavePassword = md5.digest(secretKey.getBytes("utf-8"));
            byte[] BytesKey = Arrays.copyOf(llavePassword, 24);
            SecretKey key = new SecretKeySpec(BytesKey, "DESede");
            Cipher cifrado = Cipher.getInstance("DESede");
            cifrado.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainTextBytes = cadena.getBytes("utf-8");
            byte[] buf = cifrado.doFinal(plainTextBytes);
            byte[] base64Bytes = Base64.encodeBase64(buf);
            encriptacion = new String(base64Bytes);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return encriptacion;
    }

    public String deecnode(String secretKey, String cadenaEncriptada) {
        String desencriptacion = "";
        try {
            byte[] message = Base64.decodeBase64(cadenaEncriptada.getBytes("utf-8"));
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md5.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);
            //byte[] plainText = decipher.doFinal(message);
            desencriptacion = new String(decipher.doFinal(message), "UTF-8");

        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return desencriptacion;
    }
}
