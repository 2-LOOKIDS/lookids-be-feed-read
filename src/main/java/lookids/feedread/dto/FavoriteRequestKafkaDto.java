package lookids.feedread.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class FavoriteRequestKafkaDto {

	private String uuid;

	@Builder
	public FavoriteRequestKafkaDto(String uuid) {
		this.uuid = uuid;
	}
}
