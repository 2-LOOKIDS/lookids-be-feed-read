package lookids.feedread.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PetImageKafkaDto {

	private String petCode;
	private String image;

	@Builder
	public PetImageKafkaDto(String petCode, String image) {
		this.petCode = petCode;
		this.image = image;
	}
}
