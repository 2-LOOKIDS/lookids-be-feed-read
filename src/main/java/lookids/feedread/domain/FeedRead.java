package lookids.feedread.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.dto.in.FeedKafkaDto;
import lookids.feedread.dto.in.UserKafkaDto;

@Getter
@NoArgsConstructor
@Document
@ToString
public class FeedRead {
	@Id
	private ObjectId id;
	private String feedCode;
	private String uuid;
	private String nickname;
	private String tag;
	private String image;
	private String content;
	private boolean state;
	private List<String> petCode;
	private List<String> tagList;
	private List<String> mediaUrlList;
	private LocalDateTime createdAt;


	@Builder
	public FeedRead(ObjectId id, String feedCode, String uuid, String tag, List<String> petCode, String content,
		List<String> tagList, boolean state, List<String> mediaUrlList, LocalDateTime createdAt, String nickname, String image) {
		this.id = id;
		this.feedCode = feedCode;
		this.uuid = uuid;
		this.content = content;
		this.state = state;
		this.petCode = petCode;
		this.tagList = tagList;
		this.mediaUrlList = mediaUrlList;
		this.createdAt = createdAt;
		this.nickname = nickname;
		this.image = image;
		this.tag = tag;
	}

	public static FeedRead toEntity(FeedKafkaDto feedKafkaDto, UserKafkaDto userKafkaDto) {
		return FeedRead.builder()
			.feedCode(feedKafkaDto.getFeedCode())
			.uuid(feedKafkaDto.getUuid())
			.content(feedKafkaDto.getContent())
			.state(feedKafkaDto.isState())
			.petCode(feedKafkaDto.getPetCode())
			.tagList(feedKafkaDto.getTagList())
			.mediaUrlList(feedKafkaDto.getMediaUrlList())
			.createdAt(feedKafkaDto.getCreatedAt())
			.uuid(userKafkaDto.getUuid())
			// .nickname(userKafkaDto.getNickname())
			// .image(userKafkaDto.getImage())
			// .tag(userKafkaDto.getTag())
			.build();
	}
}
