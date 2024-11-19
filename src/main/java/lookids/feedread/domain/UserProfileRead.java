package lookids.feedread.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileRead {

	private String uuid;
	private String nickname;
	private String image;

	@Builder
	public UserProfileRead(String uuid, String nickname, String image) {
		this.uuid = uuid;
		this.nickname = nickname;
		this.image = image;
	}
}
