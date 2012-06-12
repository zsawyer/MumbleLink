/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.minecraft.src.MumbleLink;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zsawyer
 */
public class KeyValueContainer<K extends Enum<K>, V extends Enum<V>> {

    private Map<K, String> items;

    public KeyValueContainer(Class<K> classOfK) {
        items = new EnumMap<K, String>(classOfK);
    }

    public boolean compare(K key, V expectedValue) {
        return compare(key, expectedValue.toString());
    }

    private boolean compare(K key, String expectedValue) {
        if (isDefined(key)) {
            String actualValue = get(key);
            return actualValue.equals(expectedValue);
        }

        return false;
    }

    public void define(K key, V value) {
        define(key, value.toString());
    }

    public void define(K key, String value) {
        items.put(key, value.trim());
    }

    public String get(K key) throws IndexOutOfBoundsException {
        if (isDefined(key)) {
            return items.get(key).toString();
        }
        throw new IndexOutOfBoundsException("'" + key.toString() + "' is not defined in config file");
    }

    public boolean isDefined(K key) {
        return items.containsKey(key);
    }

    public void defineAll(Map<K, String> newSettings) {
        items.putAll(newSettings);
    }

    public Map<K, String> asMap() {
        return new EnumMap<K, String>(items);
    }

    public Map<String, String> asStringsMap() {
        Map<String, String> outputMap = new HashMap<String, String>();
        for(Map.Entry<K, String> entry : items.entrySet()) {
            outputMap.put(entry.getKey().toString(), entry.getValue());
        }
        return outputMap;
    }

}
