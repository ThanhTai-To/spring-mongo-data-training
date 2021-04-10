package com.pycogroup.superblog.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "category")
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @Getter
    private ObjectId categoryId;

    @Setter
    @Getter
    private String categoryName;
}
