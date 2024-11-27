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
public class FeedListResponseVo {
	// private String uuid;
	private String nickname;
	private String tag;
	private String image;
	private String feedCode;
	private List<String> mediaUrlList;
	private String content;
	private LocalDateTime createdAt;
}
