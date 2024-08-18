package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.FileOwnerType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "files")
public class FileEntity {
    @Id
    private String id;
    private String url; // URL của file hoặc hình ảnh
    private String type; // Loại file (ví dụ: image, document, etc.)
    private String ownerId; // ID của đối tượng sở hữu file (ví dụ: ID của ForumEntity hoặc UserEntity)
    private FileOwnerType ownerType; // Loại đối tượng sở hữu file (ví dụ: "forum", "user", etc.)
    private String createdAt;
    private String updatedAt;
}
