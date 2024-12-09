package lookids.feedread.dto.in;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UuidListKafkaDto {

	private List<String> uuid;

	@Builder
	public UuidListKafkaDto(List<String>uuid) {
		this.uuid = uuid;
	}
}
