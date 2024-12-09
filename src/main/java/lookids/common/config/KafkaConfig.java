package lookids.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import lookids.feedread.dto.in.BlockKafkaDto;
import lookids.feedread.dto.in.FeedDeleteKafkaDto;
import lookids.feedread.dto.in.PetImageKafkaDto;
import lookids.feedread.dto.in.PetKafkaDto;
import lookids.feedread.dto.in.UuidRequestKafkaDto;
import lookids.feedread.dto.out.FavoriteResponseDto;
import lookids.feedread.dto.in.FeedKafkaDto;
import lookids.feedread.dto.in.UserImageKafkaDto;
import lookids.feedread.dto.in.UserKafkaDto;
import lookids.feedread.dto.in.UserNickNameKafkaDto;
import lookids.feedread.dto.out.FollowResponseDto;

@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServer;

	@Bean
	public Map<String, Object> FavoriteUuidProducerConfigs() {
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return producerProps;
	}

	@Bean
	public ProducerFactory<String, UuidRequestKafkaDto> FavoriteUuidNotification() {
		return new DefaultKafkaProducerFactory<>(FavoriteUuidProducerConfigs());
	}

	@Bean
	public KafkaTemplate<String, UuidRequestKafkaDto> favoriteKafkaTemplate() {
		return new KafkaTemplate<>(FavoriteUuidNotification());
	}

	@Bean
	public Map<String, Object> FollowUuidProducerConfigs() {
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return producerProps;
	}

	@Bean
	public ProducerFactory<String, UuidRequestKafkaDto> FollowUuidNotification() {
		return new DefaultKafkaProducerFactory<>(FollowUuidProducerConfigs());
	}

	@Bean
	public KafkaTemplate<String, UuidRequestKafkaDto> followKafkaTemplate() {
		return new KafkaTemplate<>(FollowUuidNotification());
	}

	@Bean
	public Map<String, Object> BlockUuidProducerConfigs() {
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return producerProps;
	}

	@Bean
	public ProducerFactory<String, UuidRequestKafkaDto> BlockUuidNotification() {
		return new DefaultKafkaProducerFactory<>(BlockUuidProducerConfigs());
	}

	@Bean
	public KafkaTemplate<String, UuidRequestKafkaDto> blockKafkaTemplate() {
		return new KafkaTemplate<>(BlockUuidNotification());
	}

	@Bean
	public Map<String, Object> petProfileProducerConfigs() {
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return producerProps;
	}

	@Bean
	public ProducerFactory<String, PetKafkaDto> petProfileNotification() {
		return new DefaultKafkaProducerFactory<>(petProfileProducerConfigs());
	}

	@Bean
	public KafkaTemplate<String, PetKafkaDto> petKafkaTemplate() {
		return new KafkaTemplate<>(petProfileNotification());
	}


	@Bean
	public ConsumerFactory<String, FeedKafkaDto> feedConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");

		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(FeedKafkaDto.class, false)));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, FeedKafkaDto> feedEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, FeedKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(feedConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, UserKafkaDto> userProfileConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(UserKafkaDto.class, false)));
	}
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, UserKafkaDto> userProfileEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, UserKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(userProfileConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, UserNickNameKafkaDto> userNickNameConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(UserNickNameKafkaDto.class, false)));
	}
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, UserNickNameKafkaDto> userNickNameEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, UserNickNameKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(userNickNameConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, UserImageKafkaDto> userImageConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(UserImageKafkaDto.class, false)));
	}
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, UserImageKafkaDto> userImageEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, UserImageKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(userImageConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, FavoriteResponseDto> favoriteConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(FavoriteResponseDto.class, false)));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, FavoriteResponseDto> favoriteEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, FavoriteResponseDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(favoriteConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, FollowResponseDto> followConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(FollowResponseDto.class, false)));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, FollowResponseDto> followEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, FollowResponseDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(followConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, FeedDeleteKafkaDto> DeleteConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(FeedDeleteKafkaDto.class, false)));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, FeedDeleteKafkaDto> deleteEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, FeedDeleteKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(DeleteConsumerFactory());
		return factory;
	}
	@Bean
	public ConsumerFactory<String, BlockKafkaDto> BlockConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");

		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(BlockKafkaDto.class, false)));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, BlockKafkaDto> blockEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, BlockKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(BlockConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, PetImageKafkaDto> petConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(PetImageKafkaDto.class, false)));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PetImageKafkaDto> petEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, PetImageKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(petConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, PetImageKafkaDto> petImageConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(PetImageKafkaDto.class, false)));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PetImageKafkaDto> petProfileEventListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, PetImageKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(petImageConsumerFactory());
		return factory;
	}
}
