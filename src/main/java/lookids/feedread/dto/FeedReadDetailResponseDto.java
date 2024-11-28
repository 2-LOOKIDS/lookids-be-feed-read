package lookids.feedread.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.vo.out.FeedReadDetailResponseVo;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedReadDetailResponseDto {

	private String uuid;
	private String nickname;
	private String tag;
	private String image;
	private String petCode;
	private String content;
	private List<String> tagList;
	private List<String> mediaUrlList;
	private LocalDateTime createdAt;

	public static FeedReadDetailResponseDto toDto(FeedRead feedRead) {
		return FeedReadDetailResponseDto.builder()
			.uuid(feedRead.getUuid())
			.petCode(feedRead.getPetCode())
			.nickname(feedRead.getNickname())
			.tag(feedRead.getTag())
			.image(feedRead.getImage())
			.content(feedRead.getContent())
			.tagList(feedRead.getTagList())
			.mediaUrlList(feedRead.getMediaUrlList())
			.createdAt(feedRead.getCreatedAt()
				.atZone(ZoneId.systemDefault())
				.withZoneSameInstant(ZoneId.of("Asia/Seoul"))
				.toLocalDateTime())
			.build();
	}

	public FeedReadDetailResponseVo toDetailVo() {
		return FeedReadDetailResponseVo.builder()
			.uuid(uuid)
			.nickname(nickname)
			.tag(tag)
			.image(image)
			.petCode(petCode)
			.content(content)
			.tagList(tagList)
			.mediaUrlList(mediaUrlList)
			.createdAt(createdAt)
			.build();
	}

}
