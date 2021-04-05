package com.pycogroup.superblog.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "comments")
@Builder
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
    private String userId;

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private String status;

}
