package lookids.feedread.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.domain.FeedRead;

@Getter
@ToString
@NoArgsConstructor
public class UserImageKafkaDto {

	private String uuid;
	private String image;

	@Builder
	public UserImageKafkaDto(String uuid, String image) {
		this.uuid = uuid;
		this.image = image;
	}

	public FeedRead toImageUpdate(FeedRead feedRead) {
		return FeedRead.builder()
			.id(feedRead.getId())
			.feedCode(feedRead.getFeedCode())
			.uuid(feedRead.getUuid())
			.nickname(feedRead.getNickname())
			.tag(feedRead.getTag())
			.image(image)
			.content(feedRead.getContent())
			.state(feedRead.isState())
			.petCode(feedRead.getPetCode())
			.tagList(feedRead.getTagList())
			.mediaUrlList(feedRead.getMediaUrlList())
			.createdAt(feedRead.getCreatedAt())
			.build();
	}
}
