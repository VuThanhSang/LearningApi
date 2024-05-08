package com.example.learning_api.dto.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CloudinaryUploadResponse {
    private String signature;
    private String format;
    private String resourceType;
    private String secureUrl;
    private String createdAt;
    private String assetId;
    private String versionId;
    private String type;
    private long version;
    private String accessMode;
    private String url;
    private String publicId;
    private String[] tags;
    private String folder;
    private String originalFilename;
    private String apiKey;
    private long bytes;
    private int width;
    private String etag;
    private boolean placeholder;
    private int height;
    private boolean overwritten;

    public CloudinaryUploadResponse(Map<String, String> map) {
        this.signature = map.getOrDefault("signature", null);
        this.format = map.getOrDefault("format", null);
        this.resourceType = map.getOrDefault("resource_type", null);
        this.secureUrl = map.getOrDefault("secure_url", null);
        this.createdAt = map.getOrDefault("created_at", null);
        this.assetId = map.getOrDefault("asset_id", null);
        this.versionId = map.getOrDefault("version_id", null);
        this.type = map.getOrDefault("type", null);
        this.version = getLongValue(map, "version");
        this.accessMode = map.getOrDefault("access_mode", null);
        this.url = map.getOrDefault("url", null);
        this.publicId = map.getOrDefault("public_id", null);
        this.tags = getStringArray(map, "tags");
        this.folder = map.getOrDefault("folder", null);
        this.originalFilename = map.getOrDefault("original_filename", null);
        this.apiKey = map.getOrDefault("api_key", null);
        this.bytes = getLongValue(map, "bytes");
        this.overwritten = getBooleanValue(map, "overwritten");
        this.width = getIntegerValue(map, "width");
        this.etag = map.getOrDefault("etag", null);
        this.placeholder = getBooleanValue(map, "placeholder");
        this.height = getIntegerValue(map, "height");
    }
    public String getDirectDownloadUrl() {
        if (secureUrl == null) {
            return null;
        }

        StringBuilder urlBuilder = new StringBuilder(secureUrl);
        if (!secureUrl.contains("resource_type=raw")) {
            if (secureUrl.contains("?")) {
                urlBuilder.append("&resource_type=raw");
            } else {
                urlBuilder.append("?resource_type=raw");
            }
        }

        return urlBuilder.toString();
    }
    private long getLongValue(Map<String, String> map, String key) {
        String value = map.getOrDefault(key, null);
        return value != null ? Long.parseLong(value) : 0;
    }

    private int getIntegerValue(Map<String, String> map, String key) {
        String value = map.getOrDefault(key, null);
        return value != null ? Integer.parseInt(value) : 0;
    }

    private String[] getStringArray(Map<String, String> map, String key) {
        String value = map.getOrDefault(key, null);
        return value != null ? (value.isEmpty() ? new String[0] : value.split(",")) : null;
    }

    private boolean getBooleanValue(Map<String, String> map, String key) {
        String value = map.getOrDefault(key, null);
        return value != null && Boolean.parseBoolean(value);
    }

    public static CloudinaryUploadResponse fromString(String string) {
        Map<String, String> map = new HashMap<>();
        if (string != null && !string.isEmpty()) {
            for (String keyValue : string.substring(1, string.length() - 1).split(", ")) {
                String[] parts = keyValue.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1].equals("null") ? "" : parts[1]; // Thay thế null bằng chuỗi rỗng
                    map.put(key, value);
                }
            }
        }
        return new CloudinaryUploadResponse(map);
    }
}