package com.nicholasboari.rinha.util;

import org.json.JSONObject;

public class Validator {

    public static boolean validate(JSONObject obj) {
        if (obj.isNull("descricao")) {
            return false;
        }

        if (obj.getDouble("valor") % 1 != 0 || obj.getDouble("valor") < 0) {
            return false;
        }

        if (!obj.getString("tipo").equals("c") && !obj.getString("tipo").equals("d")) {
            return false;
        }

        return obj.getString("descricao").length() <= 10 && !obj.getString("descricao").isEmpty();
    }
}