package com.t24.apiproxy.model;

import java.util.HashMap;
import java.util.Map;

public class Metadata {
        private final Map<String, Object> properties = new HashMap<>();

        public Metadata() {}

        public Metadata add(String key, Object value) {
            properties.put(key, value);
            return this;
        }

        public Object get(String key) {
            return properties.get(key);
        }

        public Map<String, Object> asMap() {
            return properties;
        }
        public boolean isEmpty() {
            return properties.isEmpty();
        }
        public void clear() {
            properties.clear();
        }
        @Override
        public String toString() {
            return "Metadata{" +
                    "properties=" + properties +
                    '}';
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Metadata metadata = (Metadata) o;
            return properties.equals(metadata.properties);
        }
        @Override
        public int hashCode() {
            return properties.hashCode();
        }
        public static Metadata empty() {
            return new Metadata();
        }
        public static Metadata of(String key, Object value) {
            Metadata metadata = new Metadata();
            metadata.add(key, value);
            return metadata;
        }
        public static Metadata of(Map<String, Object> properties) {     
            Metadata metadata = new Metadata();
            if (properties != null) {
                metadata.properties.putAll(properties);
            }
            return metadata;
        }
        public static Metadata from(Metadata metadata) {
            if (metadata == null) {
                return empty();
            }
            Metadata newMetadata = new Metadata();
            newMetadata.properties.putAll(metadata.properties);
            return newMetadata;
        }
        
        public static Metadata merge(Metadata... metadataArray) {
            Metadata merged = new Metadata();
            for (Metadata metadata : metadataArray) {
                if (metadata != null) {
                    merged.properties.putAll(metadata.properties);
                }
            }
            return merged;
        }
    }
