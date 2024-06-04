package com.example.learning_api.service.common;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.example.learning_api.constant.CloudinaryConstant.*;

@Component
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;


    public void updateImage(String publicId) throws Exception {
        cloudinary.api().update(publicId,
                ObjectUtils.asMap(
                        "tags", "important",
                        "moderation_status", "approved"));
    }

    public String getImageUrl(String publicId) {
        try {
            return cloudinary.url().version(cloudinary.api().resource(publicId, null).get("version")).generate(publicId);
        } catch (Exception e) {
            throw new CustomException(ErrorConstant.NOT_FOUND);
        }
    }

    public String getThumbnailUrl(String publicId) {
        try {
            String quality = "auto:low";

            Transformation transformation = new Transformation().quality(quality).width(300).height(200);

            return cloudinary.url().transformation(transformation)
                    .version(cloudinary.api().resource(publicId, null).get("version"))
                    .generate(publicId);
        } catch (Exception e) {
            throw new CustomException(ErrorConstant.NOT_FOUND);
        }
    }

    public CloudinaryUploadResponse uploadFileToFolder(String pathName, String fileName, byte[] fileData, String resourceType) throws IOException {
        try {
            var file = cloudinary.uploader()
                    .upload(fileData, Map.of(
                            PUBLIC_ID, fileName,
                            UPLOAD_PRESET, pathName,
                            OVERWRITE, true,
                            "resource_type", resourceType,
                            "public", "true"));
            String response = file.toString();
            if (response == null || response.isEmpty()) {
                throw new IOException("Cloudinary returned an empty or null response");
            }
            return CloudinaryUploadResponse.fromString(response);
        } catch (IOException e) {
            throw new IOException("Failed to upload file to Cloudinary", e);
        }
    }
    public static String getPublicId(String url) {
        // Kiểm tra đường dẫn hợp lệ
        if (!url.startsWith("http")) {
            return null;
        }

        // Tìm vị trí của dấu / cuối cùng
        int lastSlashIndex = url.lastIndexOf('/');

        // Nếu không có dấu / trong URL
        if (lastSlashIndex == -1) {
            return null;
        }

        // Lấy phần sau dấu / cuối cùng
        String fileNameWithExtension = url.substring(lastSlashIndex + 1);

        // Tìm vị trí của dấu chấm (.) cuối cùng để lấy phần mở rộng
        int lastDotIndex = fileNameWithExtension.lastIndexOf('.');

        // Nếu không có phần mở rộng
        if (lastDotIndex == -1) {
            return null;
        }

        // Lấy public ID bằng cách xóa phần mở rộng
        String publicId = fileNameWithExtension.substring(0, lastDotIndex);

        return publicId;
    }

    public void deleteImage(String url) throws IOException {
        // Lấy public ID từ URL
        String publicId = getPublicId(url);
        // Xóa hình ảnh từ Cloudinary bằng cách sử dụng public ID
        Map<String, String> options = ObjectUtils.asMap("invalidate", true);
        cloudinary.uploader().destroy(publicId, options);
    }

}
