package lookids.feedread.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserKafkaDto {

	// private String uuid;
	// private String nickname;
	// private String image;
	//
	// @Builder
	// public UserKafkaDto(String uuid, String nickname, String image) {
	// 	this.uuid = uuid;
	// 	this.nickname = nickname;
	// 	this.image = image;
	// }
	private String uuid;
	private String content;


	@Builder
	public  UserKafkaDto(String uuid, String content) {
		this.uuid = uuid;
		this.content = content;
	}
}