package com.pycogroup.superblog.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "comments")
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @Getter
    private ObjectId id;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private Date commentedAt;

    @Getter
    @Setter
    private String userEmail;

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private String status;

}
