package lookids.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import lookids.feedread.dto.FeedKafkaDto;
import lookids.feedread.dto.UserImageKafkaDto;
import lookids.feedread.dto.UserKafkaDto;
import lookids.feedread.dto.UserNickNameKafkaDto;

@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServer;

	@Bean
	public ConsumerFactory<String, FeedKafkaDto> feedConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-read-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
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
}
