package nl.menninga.menno.cms.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

	private Long cmsObjectId;
	private String thumbnailUrl;
	private String name;
	private String color;
	private String price;
	private Long quantity;
	private String url;
}