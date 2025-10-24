package com.labdevs.comandar.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    // Define el "costo" del hashing. Un valor entre 10 y 12 es un buen punto de partida.
    // A mayor costo, más lento y seguro es el hash.
    private static final int WORK_FACTOR = 12;

    /**
     * Hashea una contraseña en texto plano usando BCrypt.
     * La sal se genera automáticamente y se incluye en el hash resultante.
     * @param plainPassword La contraseña a hashear.
     * @return El hash de la contraseña en formato de String.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(WORK_FACTOR, plainPassword.toCharArray());
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash BCrypt guardado.
     * @param plainPassword La contraseña que el usuario ha introducido.
     * @param hashedPassword El hash que está guardado en la base de datos.
     * @return true si la contraseña es correcta, false en caso contrario.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
