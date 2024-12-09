package lookids.feedread.dto.in;

import java.time.LocalDateTime;

import org.apache.kafka.common.errors.FencedLeaderEpochException;
import org.bson.types.ObjectId;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.domain.FeedRead;

@Getter
@ToString
@NoArgsConstructor
public class FeedDeleteKafkaDto {

	private String feedCode;
	private String uuid;
	private LocalDateTime createdAt;

	@Builder
	public FeedDeleteKafkaDto(String feedCode, String uuid, LocalDateTime createdAt) {
		this.feedCode = feedCode;
		this.uuid = uuid;
		this.createdAt = createdAt;
	}

	public FeedRead toUpdatedEntity(FeedRead feedRead) {
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
			.state(true)
			.build();
	}
}
