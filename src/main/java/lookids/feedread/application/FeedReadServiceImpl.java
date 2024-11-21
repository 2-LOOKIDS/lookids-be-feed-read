package lookids.feedread.application;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lookids.common.entity.BaseResponseStatus;
import lookids.common.exception.BaseException;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.dto.FeedKafkaDto;
import lookids.feedread.dto.FeedReadDetailResponseDto;
import lookids.feedread.dto.FeedReadResponseDto;
import lookids.feedread.dto.UserKafkaDto;
import lookids.feedread.infrastructure.FeedReadRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class FeedReadServiceImpl implements FeedReadService{

	private final ConcurrentHashMap<String, CompletableFuture<FeedKafkaDto>> feedEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<UserKafkaDto>> userEventFutureMap = new ConcurrentHashMap<>();
	private final FeedReadRepository feedReadRepository;
	@Autowired
	private MongoTemplate mongoTemplate;

	//feed service consumer
	@KafkaListener(topics = "feed-create", groupId = "feed-read-group", containerFactory = "feedEventListenerContainerFactory")
	public void FeedConsume(FeedKafkaDto feedKafkaDto) {
		String uuid = feedKafkaDto.getUuid();
		CompletableFuture<FeedKafkaDto> feedEventFuture = feedEventFutureMap.computeIfAbsent(uuid,
			key-> new CompletableFuture<>());
		feedEventFuture.complete(feedKafkaDto);
		log.info("consume: {}", feedKafkaDto);
		checkAndCreateFeedEventListener(uuid);
	}

	//user service consumer
	@KafkaListener(topics = "feed-create-join-userprofile", groupId = "feed-read-group", containerFactory = "userProfileEventListenerContainerFactory")
	public void UserConsume(UserKafkaDto userKafkaDto) {
		String uuid = userKafkaDto.getUuid();
		CompletableFuture<UserKafkaDto> userprofileEventFuture = userEventFutureMap.computeIfAbsent(uuid,
			key-> new CompletableFuture<>());
		userprofileEventFuture.complete(userKafkaDto);
		log.info("User consume: {}", userKafkaDto);
		checkAndCreateFeedEventListener(uuid);
	}

	//feed, user service save
	private void checkAndCreateFeedEventListener(String uuid) {
		CompletableFuture<UserKafkaDto> userProfileEventFuture = userEventFutureMap.get(uuid);
		CompletableFuture<FeedKafkaDto> feedEventFuture = feedEventFutureMap.get(uuid);
		if (userProfileEventFuture != null && feedEventFuture != null) {
			userProfileEventFuture.thenCombine(feedEventFuture, (userKafkaDto, feedKafkaDto) -> {
				FeedRead feedRead = FeedRead.builder()
					.feedCode(feedKafkaDto.getFeedCode())
					.petCode(feedKafkaDto.getPetCode())
					.uuid(feedKafkaDto.getUuid())
					.content(feedKafkaDto.getContent())
					.tags(feedKafkaDto.getTags())
					.mediaUrl(feedKafkaDto.getMediaUrl())
					.state(feedKafkaDto.isState())
					.createdAt(feedKafkaDto.getCreatedAt())
					.uuid(userKafkaDto.getUuid())
					.image(userKafkaDto.getImage())
					.nickname(userKafkaDto.getNickname())
					.build();
				feedReadRepository.save(feedRead);
				feedEventFutureMap.remove(uuid);
				userEventFutureMap.remove(uuid);
				return null;
			});
		}
	}

	// uuid 기준 조회
	@Override
	public Page<FeedReadResponseDto> readFeed(String uuid, int page, int size) {
		Query query = new Query(Criteria.where("uuid").is(uuid).and("state").is(false));
		query.with(Sort.by(Sort.Order.desc("createdAt")));
		Pageable pageable = PageRequest.of(page, size);
		query.skip(pageable.getPageNumber() * pageable.getPageSize());
		query.limit(pageable.getPageSize());
		List<FeedRead> feedReadList = mongoTemplate.find(query, FeedRead.class);
		long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), FeedRead.class);

		List<FeedReadResponseDto> feedDtoList = feedReadList.stream()
			.map(FeedReadResponseDto::toDto)
			.collect(Collectors.toList());
		return new PageImpl<>(feedDtoList, pageable, total);
	}

	// feedCode 기준 조회
	@Override
	public FeedReadDetailResponseDto readFeed(String uuid, String feedCode) {
		FeedRead feedRead = feedReadRepository.findByFeedCodeAndStateFalse(feedCode, uuid)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FEED));
		return FeedReadDetailResponseDto.toDto(feedRead);
	}
}
