package lookids.feedread.infrastructure;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import lookids.feedread.domain.FeedRead;

public interface FeedReadRepository extends MongoRepository<FeedRead, String> {
	Optional<FeedRead> findByFeedCodeAndStateFalse(String feedCode);
}
