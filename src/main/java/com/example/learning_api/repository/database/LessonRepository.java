package com.example.learning_api.repository.database;


import com.example.learning_api.entity.sql.database.LessonEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface LessonRepository extends MongoRepository<LessonEntity, String>{
    @Query("([\n" +
            "  {\n" +
            "    $lookup: {\n" +
            "      from: \"resources\",\n" +
            "      localField: \"_id\",\n" +
            "      foreignField: \"lesson_id\",\n" +
            "      as: \"resources\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $lookup: {\n" +
            "      from: \"media\",\n" +
            "      localField: \"_id\",\n" +
            "      foreignField: \"lesson_id\",\n" +
            "      as: \"media\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $lookup: {\n" +
            "      from: \"substances\",\n" +
            "      localField: \"_id\",\n" +
            "      foreignField: \"lesson_id\",\n" +
            "      as: \"substances\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $unwind: {\n" +
            "      path: \"$resources\",\n" +
            "      preserveNullAndEmptyArrays: true\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $unwind: {\n" +
            "      path: \"$media\",\n" +
            "      preserveNullAndEmptyArrays: true\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $unwind: {\n" +
            "      path: \"$substances\",\n" +
            "      preserveNullAndEmptyArrays: true\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $sort: {\n" +
            "      \"resources.created_at\": 1,\n" +
            "      \"media.created_at\": 1,\n" +
            "      \"substances.created_at\": 1\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $group: {\n" +
            "      _id: \"$_id\",\n" +
            "      lesson: { $first: \"$$ROOT\" },\n" +
            "      resources: { $push: \"$resources\" },\n" +
            "      media: { $push: \"$media\" },\n" +
            "      substances: { $push: \"$substances\" }\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $replaceRoot: {\n" +
            "      newRoot: { $mergeObjects: [ { $arrayElemAt: [ \"$lesson\", 0 ] }, \"$$ROOT\" ] }\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    $project: {\n" +
            "      \"lesson._id\": 0,\n" +
            "      \"lesson.resources\": 0,\n" +
            "      \"lesson.media\": 0,\n" +
            "      \"lesson.substances\": 0\n" +
            "    }\n" +
            "  }\n" +
            "])")
    LessonEntity getLessonWithResourcesAndMediaAndSubstances(String id);
}
