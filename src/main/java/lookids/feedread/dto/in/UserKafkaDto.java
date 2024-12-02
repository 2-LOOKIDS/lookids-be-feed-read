package lookids.feedread.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserKafkaDto {

	private String uuid;
	private String nickname;
	private String image;
	private String tag;

	@Builder
	public UserKafkaDto(String uuid, String nickname, String image, String tag) {
		this.uuid = uuid;
		this.nickname = nickname;
		this.image = image;
		this.tag = tag;
	}
}
