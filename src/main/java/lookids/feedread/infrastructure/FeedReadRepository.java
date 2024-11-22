package lookids.feedread.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import lookids.feedread.domain.FeedRead;

public interface FeedReadRepository extends MongoRepository<FeedRead, String> {
	Page<FeedRead> findByUuidAndStateFalse(String uuid, Pageable pageable);
	List<FeedRead> findAllByUuid(String uuid);
	Optional<FeedRead> findByFeedCodeAndStateFalse(String feedCode);
}
