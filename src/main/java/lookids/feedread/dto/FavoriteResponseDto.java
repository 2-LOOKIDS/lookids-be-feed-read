package lookids.feedread.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class FavoriteResponseDto {

	private String uuid;
	private List<String> targetCodeList;

	@Builder
	public FavoriteResponseDto(String uuid, List<String> targetCodeList) {
		this.uuid = uuid;
		this.targetCodeList = targetCodeList;
	}
}
