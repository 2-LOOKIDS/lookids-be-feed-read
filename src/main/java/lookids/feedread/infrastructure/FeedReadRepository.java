package lookids.feedread.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import lookids.feedread.domain.FeedRead;

public interface FeedReadRepository extends MongoRepository<FeedRead, String> {
	List<FeedRead> findAllByUuid(String uuid);
	Optional<FeedRead> findByFeedCodeAndStateFalse(String feedCode);
	Boolean existsByUuidAndFeedCode(String uuid, String feedCode);
	List<FeedRead> findAllBypetCode(String petCode);

}
