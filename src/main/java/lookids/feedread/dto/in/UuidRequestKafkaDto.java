package lookids.feedread.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class UuidRequestKafkaDto {

	private String uuid;

	@Builder
	public UuidRequestKafkaDto(String uuid) {
		this.uuid = uuid;
	}
}
