package com.pycogroup.superblog.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "articles")
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class Article {
	@Id
	@Getter
	private ObjectId id;

	@Getter
	@Setter
	private String articleId;

	@Getter
	@Setter
	private String title;


	@Getter
	@Setter
	private String content;

	@Getter
	@Setter
	private String authorEmail;

	@Getter
	@Setter
	private Date createdAt;

	@Getter
	@Setter
	private ArrayList<String> categories;

	@Getter
	@Setter
	private List<Comment> comments;

}
