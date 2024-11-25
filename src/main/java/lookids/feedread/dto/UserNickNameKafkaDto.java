package lookids.feedread.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.domain.FeedRead;

@Getter
@ToString
@NoArgsConstructor
public class UserNickNameKafkaDto {

	private String uuid;
	private String nickname;
	private String tag;


	@Builder
	public UserNickNameKafkaDto(String uuid, String nickname, String tag) {
		this.uuid = uuid;
		this.nickname = nickname;
		this.tag = tag;
	}

	public FeedRead toNickNameUpdate(FeedRead feedRead) {
		return FeedRead.builder()
			.id(feedRead.getId())
			.feedCode(feedRead.getFeedCode())
			.uuid(feedRead.getUuid())
			.tag(feedRead.getTag())
			.nickname(nickname)
			.image(feedRead.getImage())
			.content(feedRead.getContent())
			.state(feedRead.isState())
			.petCode(feedRead.getPetCode())
			.tagList(feedRead.getTagList())
			.mediaUrlList(feedRead.getMediaUrlList())
			.createdAt(feedRead.getCreatedAt())
			.build();
	}
}