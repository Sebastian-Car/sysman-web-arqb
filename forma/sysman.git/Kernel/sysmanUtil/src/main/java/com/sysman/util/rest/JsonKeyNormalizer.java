package com.sysman.util.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JsonKeyNormalizer {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Normaliza todas las claves del JSON a MAYÚSCULAS
     */
    public static String normalizeKeysToUpper(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode normalized = normalizeNode(root);
        return mapper.writeValueAsString(normalized);
    }

    private static JsonNode normalizeNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = mapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String upperKey = entry.getKey().toUpperCase();
                objectNode.set(upperKey, normalizeNode(entry.getValue()));
            }
            return objectNode;
        } else if (node.isArray()) {
            ArrayNode arrayNode = mapper.createArrayNode();
            for (JsonNode element : node) {
                arrayNode.add(normalizeNode(element));
            }
            return arrayNode;
        } else {
            return node; // valores primitivos (texto, número, booleano, null)
        }
    }
}