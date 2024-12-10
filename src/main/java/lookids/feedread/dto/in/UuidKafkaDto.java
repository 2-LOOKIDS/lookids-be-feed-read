package lookids.feedread.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.domain.FeedRead;

@Getter
@NoArgsConstructor
@ToString
public class UuidKafkaDto {

	private String uuid;

	@Builder
	public UuidKafkaDto(String uuid) {
		this.uuid = uuid;
	}

	public static UuidKafkaDto toDto(String uuid) {
		return UuidKafkaDto.builder()
			.uuid(uuid)
			.build();
	}

	public FeedRead toDelete(FeedRead feedRead) {
		return FeedRead.builder()
			.id(feedRead.getId())
			.feedCode(feedRead.getFeedCode())
			.uuid(feedRead.getUuid())
			.nickname(feedRead.getNickname())
			.image(feedRead.getImage())
			.tag(feedRead.getTag())
			.content(feedRead.getContent())
			.tagList(feedRead.getTagList())
			.petCode(feedRead.getPetCode())
			.mediaUrlList(feedRead.getMediaUrlList())
			.createdAt(feedRead.getCreatedAt())
			.state(false)
			.build();
	}
}
