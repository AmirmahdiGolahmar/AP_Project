package dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemSearchRequestDto {
    String search;
    Long price;
    List<String> keywords;
}
