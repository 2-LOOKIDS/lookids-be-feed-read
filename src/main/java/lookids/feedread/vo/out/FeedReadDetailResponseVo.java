package lookids.feedread.vo.out;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedReadDetailResponseVo {

	private String uuid;
	private String tag;
	private String nickname;
	private String image;
	private List<String> petCode;
	private String content;
	private List<String> tagList;
	private List<String> mediaUrlList;
	private LocalDateTime createdAt;
}
