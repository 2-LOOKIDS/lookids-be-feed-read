package lookids.feedread.application;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lookids.common.entity.BaseResponseStatus;
import lookids.common.exception.BaseException;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.dto.in.FeedDeleteKafkaDto;
import lookids.feedread.dto.in.FeedKafkaDto;
import lookids.feedread.dto.in.UserImageKafkaDto;
import lookids.feedread.dto.in.UserKafkaDto;
import lookids.feedread.dto.in.UserNickNameKafkaDto;
import lookids.feedread.dto.in.UuidRequestKafkaDto;
import lookids.feedread.dto.out.FavoriteResponseDto;
import lookids.feedread.dto.out.FeedListResponseDto;
import lookids.feedread.dto.out.FeedReadDetailResponseDto;
import lookids.feedread.dto.out.FeedReadResponseDto;
import lookids.feedread.dto.out.FollowResponseDto;
import lookids.feedread.infrastructure.FeedReadRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@ToString
public class FeedReadServiceImpl implements FeedReadService {

	private final ConcurrentHashMap<String, CompletableFuture<FeedKafkaDto>> feedEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<UserKafkaDto>> userEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<FavoriteResponseDto>> favoriteEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<FollowResponseDto>> followEventFutureMap = new ConcurrentHashMap<>();
	private final KafkaTemplate<String, UuidRequestKafkaDto> favoriteKafkaTemplate;
	private final KafkaTemplate<String, UuidRequestKafkaDto> followKafkaTemplate;
	private final FeedReadRepository feedReadRepository;
	private final MongoTemplate mongoTemplate;

	@KafkaListener(topics = "feed-create", groupId = "feed-read-group", containerFactory = "feedEventListenerContainerFactory")
	public void FeedConsume(FeedKafkaDto feedKafkaDto) {
		String uuid = feedKafkaDto.getUuid();
		CompletableFuture<FeedKafkaDto> feedEventFuture = feedEventFutureMap.computeIfAbsent(uuid,
			key -> new CompletableFuture<>());
		feedEventFuture.complete(feedKafkaDto);
		checkAndCreateFeedEventListener(uuid);
	}

	@KafkaListener(topics = "feed-create-join-userprofile", groupId = "feed-read-group", containerFactory = "userProfileEventListenerContainerFactory")
	public void UserConsume(UserKafkaDto userKafkaDto) {
		String uuid = userKafkaDto.getUuid();
		CompletableFuture<UserKafkaDto> userprofileEventFuture = userEventFutureMap.computeIfAbsent(uuid,
			key -> new CompletableFuture<>());
		userprofileEventFuture.complete(userKafkaDto);
		checkAndCreateFeedEventListener(uuid);
	}

	private void checkAndCreateFeedEventListener(String uuid) {
		CompletableFuture<UserKafkaDto> userProfileEventFuture = userEventFutureMap.get(uuid);
		CompletableFuture<FeedKafkaDto> feedEventFuture = feedEventFutureMap.get(uuid);
		if (userProfileEventFuture != null && feedEventFuture != null) {
			userProfileEventFuture.thenCombine(feedEventFuture, (userKafkaDto, feedKafkaDto) -> {
				FeedRead feedRead = FeedRead.toDto(feedKafkaDto, userKafkaDto);
				feedReadRepository.save(feedRead);
				feedEventFutureMap.remove(uuid);
				userEventFutureMap.remove(uuid);
				return null;
			});
		}
	}

	@Transactional
	@KafkaListener(topics = "userprofile-nickname-update", groupId = "feed-read-group", containerFactory = "userNickNameEventListenerContainerFactory")
	public void NickNameUpdateConsume(UserNickNameKafkaDto userNickNameKafkaDto) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(userNickNameKafkaDto.getUuid());
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> nickNameUpdate = findUuid.stream().map(userNickNameKafkaDto::toNickNameUpdate)
			.collect(Collectors.toList());
		feedReadRepository.saveAll(nickNameUpdate);
	}

	@Transactional
	@KafkaListener(topics = "userprofile-image-update", groupId = "feed-read-group", containerFactory = "userProfileEventListenerContainerFactory")
	public void ImageUpdateConsume(UserImageKafkaDto userImageKafkaDto) {
		List<FeedRead> findUuid = feedReadRepository.findAllByUuid(userImageKafkaDto.getUuid());
		if (findUuid.isEmpty()) {
			throw new BaseException(BaseResponseStatus.NO_EXIST_FEED);
		}
		List<FeedRead> ImageUpdate = findUuid.stream().map(userImageKafkaDto::toImageUpdate)
			.collect(Collectors.toList());
		feedReadRepository.saveAll(ImageUpdate);
	}

	@KafkaListener(topics = "feed-delete", groupId = "feed-read-group", containerFactory = "deleteEventListenerContainerFactory")
	public void FeedDeleteConsume(FeedDeleteKafkaDto feedDeleteKafkaDto) {
		FeedRead feedRead = feedReadRepository.findByFeedCodeAndStateFalse(feedDeleteKafkaDto.getFeedCode())
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FEED));
		FeedRead updatedFeedRead = feedDeleteKafkaDto.toUpdatedEntity(feedRead);
		feedReadRepository.save(updatedFeedRead);
	}

	@Override
	public Page<FeedReadResponseDto> readFeedFavoriteList(String uuid, int page, int size) {
		favoriteKafkaTemplate.send("favorite-request", UuidRequestKafkaDto.toDto(uuid));
		CompletableFuture<FavoriteResponseDto> futureFeedCodeList = new CompletableFuture<>();
		favoriteEventFutureMap.put(uuid, futureFeedCodeList);
		List<String> targetCodeList;
		try {
			targetCodeList = futureFeedCodeList.get().getTargetCodeList();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error while fetching favorite feed codes", e);
			targetCodeList = Collections.emptyList();
		}
		Criteria criteria = Criteria.where("feedCode").in(targetCodeList).and("state").is(false);
		Query query = new Query(criteria)
			.with(Sort.by(Sort.Order.desc("createdAt")))
			.skip((long) page * size)
			.limit(size);
		List<FeedReadResponseDto> feedDtoList = mongoTemplate.find(query, FeedRead.class).stream()
			.map(FeedReadResponseDto::toDto)
			.collect(Collectors.toList());
		long total = mongoTemplate.count(Query.query(Criteria.where("feedCode").in(targetCodeList).and("state").is(false)), "feedRead");
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		return new PageImpl<>(feedDtoList, pageable, total);
	}

	@KafkaListener(topics = "favorite-response", groupId = "feed-read-group", containerFactory = "favoriteEventListenerContainerFactory")
	public void readFeedFavorite(FavoriteResponseDto favoriteResponseDto) {
		String uuid = favoriteResponseDto.getUuid();
		CompletableFuture<FavoriteResponseDto> futureFeedCodeList = favoriteEventFutureMap.get(uuid);
		futureFeedCodeList.complete(favoriteResponseDto);
	}

	@Override
	public Page<FeedListResponseDto> readFeedAndTagList(String uuid, String tag, int page, int size) {
		followKafkaTemplate.send("follow-request", UuidRequestKafkaDto.toDto(uuid));
		CompletableFuture<FollowResponseDto> futureUuidList = new CompletableFuture<>();
		followEventFutureMap.put(uuid, futureUuidList);
		List<String> followUuid;
		try {
			followUuid = futureUuidList.get().getFollowUuid();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error while fetching follow list", e);
			followUuid = Collections.emptyList();
		}
		Criteria criteria = Criteria.where("uuid").in(followUuid).and("state").is(false);
		if (tag != null && !tag.isEmpty()) {
			criteria.and("tagList").in(tag);
		}
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(criteria),
			Aggregation.sort(Sort.by(Sort.Order.desc("createdAt"))),
			Aggregation.skip((long) page * size),
			Aggregation.limit(size));
		List<FeedListResponseDto> feedDtoList = mongoTemplate.aggregate(aggregation, "feedRead", FeedRead.class).getMappedResults()
			.stream()
			.map(FeedListResponseDto::toDto)
			.collect(Collectors.toList());
		long total = mongoTemplate.count(Query.query(criteria), "feedRead");
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		return new PageImpl<>(feedDtoList, pageable, total);
	}

	@KafkaListener(topics = "follow-response", groupId = "feed-read-group", containerFactory = "followEventListenerContainerFactory")
	public void readFeedFollow(FollowResponseDto followResponseDto) {
		String uuid = followResponseDto.getUuid();
		CompletableFuture<FollowResponseDto> futureUuidList = followEventFutureMap.get(uuid);
		futureUuidList.complete(followResponseDto);
	}

	@Override
	public Page<FeedListResponseDto> readFeedRandomList(int page, int size) {
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(Criteria.where("state").is(false)),
			Aggregation.sample(size),
			Aggregation.skip((long) page * size),
			Aggregation.limit(size));
		List<FeedRead> feedReadList = mongoTemplate.aggregate(aggregation, "feedRead", FeedRead.class).getMappedResults();
		long total = mongoTemplate.count(Query.query(Criteria.where("state").is(false)), "feedRead");
		Pageable pageable = PageRequest.of(page, size);
		List<FeedListResponseDto> feedRandomList = feedReadList
			.stream().map(FeedListResponseDto::toDto)
			.toList();
		return new PageImpl<>(feedRandomList, pageable, total);
	}

	@Override
	public Page<FeedReadResponseDto> readFeedThumbnailList(String uuid, int page, int size) {
		Criteria criteria = Criteria.where("state").is(false).and("uuid").is(uuid);
		Query query = new Query(criteria)
				.with(Sort.by(Sort.Order.desc("createdAt")))
				.skip((long) page * size)
				.limit(size);
		List<FeedReadResponseDto> feedDtoList = mongoTemplate.find(query, FeedRead.class).stream()
			.map(FeedReadResponseDto::toDto)
			.collect(Collectors.toList());
		long total = mongoTemplate.count(Query.query(Criteria.where("state").is(false).and("uuid").is(uuid)), "feedRead");
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		return new PageImpl<>(feedDtoList, pageable, total);
	}

	@Override
	public FeedReadDetailResponseDto readFeedDetail(String feedCode) {
		FeedRead feedRead = feedReadRepository.findByFeedCodeAndStateFalse(feedCode)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FEED));
		return FeedReadDetailResponseDto.toDto(feedRead);
	}

	@Override
	public Boolean readFeedCheck(String uuid, String feedCode) {
		return feedReadRepository.existsByUuidAndFeedCode(uuid, feedCode);
	}
}