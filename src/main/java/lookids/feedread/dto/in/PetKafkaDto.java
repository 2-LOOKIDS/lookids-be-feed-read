package lookids.feedread.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lookids.feedread.domain.FeedRead;

@Getter
@NoArgsConstructor
@ToString
public class PetKafkaDto {

	private String petCode;

	@Builder
	public PetKafkaDto(String petCode) {
		this.petCode = petCode;
	}

	public static PetKafkaDto toDto(FeedRead feedRead) {
		return PetKafkaDto.builder()
				.petCode(feedRead.getPetCode() != null && !feedRead.getPetCode().isEmpty()
						? feedRead.getPetCode().get(0) : null)
				.build();
	}
}
