package lookids.feedread.application;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lookids.common.entity.BaseResponseStatus;
import lookids.common.exception.BaseException;
import lookids.feedread.domain.FeedRead;
import lookids.feedread.dto.in.BlockKafkaDto;
import lookids.feedread.dto.in.FeedKafkaDto;
import lookids.feedread.dto.in.PetImageKafkaDto;
import lookids.feedread.dto.in.PetKafkaDto;
import lookids.feedread.dto.in.UserKafkaDto;
import lookids.feedread.dto.in.UuidKafkaDto;
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
	private final ConcurrentHashMap<String, CompletableFuture<BlockKafkaDto>> blockEventFutureMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, CompletableFuture<PetImageKafkaDto>> petEventFutureMap = new ConcurrentHashMap<>();
	private final KafkaTemplate<String, UuidKafkaDto> favoriteKafkaTemplate;
	private final KafkaTemplate<String, UuidKafkaDto> followKafkaTemplate;
	private final KafkaTemplate<String, UuidKafkaDto> blockKafkaTemplate;
	private final KafkaTemplate<String, PetKafkaDto> petKafkaTemplate;
	private final FeedReadRepository feedReadRepository;
	private final MongoTemplate mongoTemplate;
	private final FeedKafkaListener feedKafkaListener;

	@KafkaListener(topics = "favorite-response", groupId = "feed-read-group", containerFactory = "favoriteEventListenerContainerFactory")
	public void readFeedFavorite(FavoriteResponseDto favoriteResponseDto) {
		String uuid = favoriteResponseDto.getUuid();
		CompletableFuture<FavoriteResponseDto> futureFeedCodeList = favoriteEventFutureMap.get(uuid);
		futureFeedCodeList.complete(favoriteResponseDto);
	}

	@KafkaListener(topics = "block-response", groupId = "feed-read-group", containerFactory = "blockEventListenerContainerFactory")
	public void readBlockUuid(BlockKafkaDto blockKafkaDto) {
		String uuid = blockKafkaDto.getUuid();
		CompletableFuture<BlockKafkaDto> futureBlockList = blockEventFutureMap.get(uuid);
		futureBlockList.complete(blockKafkaDto);
	}

	@KafkaListener(topics = "follow-response", groupId = "feed-read-group", containerFactory = "followEventListenerContainerFactory")
	public void readFeedFollow(FollowResponseDto followResponseDto) {
		String uuid = followResponseDto.getUuid();
		CompletableFuture<FollowResponseDto> futureUuidList = followEventFutureMap.get(uuid);
		futureUuidList.complete(followResponseDto);
	}

	@KafkaListener(topics = "petprofile-response", groupId = "feed-read-group", containerFactory = "petEventListenerContainerFactory")
	public void readPetImage(PetImageKafkaDto petImageKafkaDto) {
		String petCode = petImageKafkaDto.getPetCode();
		CompletableFuture<PetImageKafkaDto> futurePetImage = petEventFutureMap.get(petCode);
		if (futurePetImage != null) {
			futurePetImage.complete(petImageKafkaDto);
		}
	}

	@Override
	public Page<FeedReadResponseDto> readFeedFavoriteList(String uuid, int page, int size) {
		favoriteKafkaTemplate.send("favorite-request", UuidKafkaDto.toDto(uuid));
		CompletableFuture<FavoriteResponseDto> futureFeedCodeList = new CompletableFuture<>();
		favoriteEventFutureMap.put(uuid, futureFeedCodeList);
		List<String> targetCodeList;
		try {
			targetCodeList = futureFeedCodeList.get(5, TimeUnit.SECONDS).getTargetCodeList();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error while fetching favorite feed codes", e);
			targetCodeList = Collections.emptyList();
		}
		catch (TimeoutException e) {
			log.warn("Timeout while waiting for favorite feed codes", e);
			targetCodeList = Collections.emptyList();
		}
		Criteria criteria = Criteria.where("feedCode").in(targetCodeList).and("state").is(true);
		Query query = new Query(criteria)
			.with(Sort.by(Sort.Order.desc("createdAt")))
			.skip((long) page * size)
			.limit(size);
		List<FeedReadResponseDto> feedDtoList = mongoTemplate.find(query, FeedRead.class).stream()
			.map(FeedReadResponseDto::toDto)
			.collect(Collectors.toList());
		long total = mongoTemplate.count(Query.query(criteria), "feedRead");
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		return new PageImpl<>(feedDtoList, pageable, total);
	}

	@Override
	public Page<FeedListResponseDto> readFeedAndTagList(String uuid, String tag, int page, int size) {
		followKafkaTemplate.send("follow-request", UuidKafkaDto.toDto(uuid));
		CompletableFuture<FollowResponseDto> futureUuidList = new CompletableFuture<>();
		followEventFutureMap.put(uuid, futureUuidList);
		blockKafkaTemplate.send("block-request", UuidKafkaDto.toDto(uuid));
		CompletableFuture<BlockKafkaDto> futureBlockList = new CompletableFuture<>();
		blockEventFutureMap.put(uuid, futureBlockList);
		List<String> followUuid;
		try {
			followUuid = futureUuidList.get(5, TimeUnit.SECONDS).getFollowUuid();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error while fetching follow list", e);
			followUuid = Collections.emptyList();
		}
		catch (TimeoutException e) {
			log.warn("Timeout", e);
			followUuid = Collections.emptyList();
		}
		List<String> BlockUuidList;
		try {
			BlockUuidList = futureBlockList.get(5, TimeUnit.SECONDS).getBlockUuid();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error while fetching block list", e);
			BlockUuidList = Collections.emptyList();
		}
		catch (TimeoutException e) {
			log.warn("Timeout while waiting for favorite feed codes", e);
			BlockUuidList = Collections.emptyList();
		}
		Criteria followCriteria = Criteria.where("uuid").in(followUuid).and("state").is(true);
		if (tag != null && !tag.isEmpty()) {
			followCriteria = followCriteria.and("tagList").in(tag);
		}
		Criteria blockCriteria = new Criteria();
		if (!BlockUuidList.isEmpty()) {
			blockCriteria = Criteria.where("uuid").nin(BlockUuidList);
 		}
		Criteria combinedCriteria = new Criteria().andOperator(followCriteria, blockCriteria);
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(combinedCriteria),
			Aggregation.sort(Sort.by(Sort.Order.desc("createdAt"))),
			Aggregation.skip((long) page * size),
			Aggregation.limit(size)
		);
		List<FeedListResponseDto> feedDtoList = mongoTemplate.aggregate(aggregation, "feedRead", FeedRead.class).getMappedResults()
			.stream()
			.map(feedRead -> {
				String image = readImageByPetCode(feedRead);
				return FeedListResponseDto.toDto(feedRead, image);
			})
			.collect(Collectors.toList());
		long total = mongoTemplate.count(Query.query(combinedCriteria), "feedRead");
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		return new PageImpl<>(feedDtoList, pageable, total);
	}

	@Override
	public Page<FeedReadResponseDto> readFeedMemberRandomList(String uuid, int page, int size) {
		blockKafkaTemplate.send("block-request", UuidKafkaDto.toDto(uuid));
		CompletableFuture<BlockKafkaDto> futureBlockList = new CompletableFuture<>();
		blockEventFutureMap.put(uuid, futureBlockList);
		List<String> BlockUuidList;
		try {
			BlockUuidList = futureBlockList.get(5, TimeUnit.SECONDS).getBlockUuid();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error while fetching favorite feed codes", e);
			BlockUuidList = Collections.emptyList();
		}
		catch (TimeoutException e) {
			log.warn("Timeout while waiting for favorite feed codes", e);
			BlockUuidList = Collections.emptyList();
		}
		Criteria criteria = Criteria.where("state").is(true);
		if (!BlockUuidList.isEmpty()) {
			criteria = criteria.and("uuid").nin(BlockUuidList);
		}

		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(criteria),
			Aggregation.sample(size),
			Aggregation.skip((long) page * size),
			Aggregation.limit(size)
		);
		List<FeedRead> feedReadList = mongoTemplate.aggregate(aggregation, "feedRead", FeedRead.class).getMappedResults();
		long total = mongoTemplate.count(Query.query(criteria), "feedRead");
		Pageable pageable = PageRequest.of(page, size);
		List<FeedReadResponseDto> feedRandomList = feedReadList
			.stream()
			.map(FeedReadResponseDto::toDto)
			.toList();
		return new PageImpl<>(feedRandomList, pageable, total);
	}

	@Override
	public Page<FeedListResponseDto> readFeedRandomList(int page, int size) {
			Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("state").is(true)),
				Aggregation.skip((long) page * size),
				Aggregation.limit(size),
				Aggregation.sample(size));
		List<FeedRead> feedReadList = mongoTemplate.aggregate(aggregation, "feedRead", FeedRead.class).getMappedResults();
		List<FeedListResponseDto> feedRandomList = feedReadList
			.stream().map(feedRead -> {
				String image = readImageByPetCode(feedRead);
				return FeedListResponseDto.toDto(feedRead, image);
			})
			.collect(Collectors.toList());
		long total = mongoTemplate.count(Query.query(Criteria.where("state").is(true)), "feedRead");
		Pageable pageable = PageRequest.of(page, size);
		return new PageImpl<>(feedRandomList, pageable, total);
	}

	@Override
	public Page<FeedReadResponseDto> readFeedThumbnailList(String uuid, int page, int size) {
		Criteria criteria = Criteria.where("state").is(true).and("uuid").is(uuid);
		Query query = new Query(criteria)
			.with(Sort.by(Sort.Order.desc("createdAt")))
			.skip((long) page * size)
			.limit(size);
		List<FeedReadResponseDto> feedDtoList = mongoTemplate.find(query, FeedRead.class).stream()
			.map(FeedReadResponseDto::toDto)
			.collect(Collectors.toList());
		long total = mongoTemplate.count(Query.query(criteria), "feedRead");
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		return new PageImpl<>(feedDtoList, pageable, total);
	}


	@Override
	public FeedReadDetailResponseDto readFeedDetail(String feedCode) {
		FeedRead feedRead = feedReadRepository.findByFeedCodeAndStateTrue(feedCode)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FEED));
		String image = readImageByPetCode(feedRead);
		return FeedReadDetailResponseDto.toDto(feedRead, image);
	}

	@Override
	public Boolean readFeedCheck(String uuid, String feedCode) {
		return feedReadRepository.existsByUuidAndFeedCode(uuid, feedCode);
	}

	private String readImageByPetCode(FeedRead feedRead) {
		if (feedRead.getPetCode() == null || feedRead.getPetCode().isEmpty()) {
			return null;
		}
		String petCode = feedRead.getPetCode().get(0);

		CompletableFuture<PetImageKafkaDto> futurePetImage = new CompletableFuture<>();
		petEventFutureMap.put(petCode, futurePetImage);

		PetKafkaDto petKafkaDto = PetKafkaDto.toDto(feedRead);
		petKafkaTemplate.send("petprofile-request", petKafkaDto);

		try {
			PetImageKafkaDto petImageDto = futurePetImage.get();
			return petImageDto.getImage();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error fetching pet image for petCode {}", petCode, e);
			return null;
		}
	}
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
				FeedRead feedRead = FeedRead.toEntity(feedKafkaDto, userKafkaDto);
				feedReadRepository.save(feedRead);
				feedEventFutureMap.remove(uuid);
				userEventFutureMap.remove(uuid);
				return null;
			});
		}
	}
}