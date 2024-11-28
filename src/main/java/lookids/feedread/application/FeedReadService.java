package lookids.feedread.application;

import org.springframework.data.domain.Page;

import lookids.feedread.dto.FeedListResponseDto;
import lookids.feedread.dto.FeedReadDetailResponseDto;
import lookids.feedread.dto.FeedReadResponseDto;

public interface FeedReadService {
	Page<FeedReadResponseDto> readFeedThumbnailList(String uuid, int page, int size);
	Page<FeedReadResponseDto> readFeedFavoriteList(String uuid,int page, int size);
	Page<FeedListResponseDto> readFeedList(String uuid,int page, int size);
	FeedReadDetailResponseDto readFeedDetail(String feedCode);


}
