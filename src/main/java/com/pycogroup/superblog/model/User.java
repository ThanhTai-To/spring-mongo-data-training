package com.pycogroup.superblog.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "admin_users")
@Builder
@QueryEntity
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@Getter
	private ObjectId id;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private Address address;

	@Getter
	@Setter
	private Boolean enabled;
}
