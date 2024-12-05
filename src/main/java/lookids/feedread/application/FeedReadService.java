package lookids.feedread.application;

import org.springframework.data.domain.Page;

import lookids.feedread.dto.out.FeedListResponseDto;
import lookids.feedread.dto.out.FeedReadDetailResponseDto;
import lookids.feedread.dto.out.FeedReadResponseDto;

public interface FeedReadService {
	Page<FeedReadResponseDto> readFeedThumbnailList(String uuid, int page, int size);
	Page<FeedReadResponseDto> readFeedFavoriteList(String uuid,int page, int size);
	Page<FeedListResponseDto> readFeedAndTagList(String uuid, String tag, int page, int size);
	Page<FeedListResponseDto> readFeedRandomList(int page, int size);
	FeedReadDetailResponseDto readFeedDetail(String feedCode);
	Boolean readFeedCheck(String uuid, String feedCode);
}
