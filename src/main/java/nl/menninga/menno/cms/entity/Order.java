package nl.menninga.menno.cms.entity;

import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order", schema = "public")
@SequenceGenerator(name = "idgen", sequenceName = "order_id_seq", initialValue = 1, allocationSize = 1)
@TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Order extends AuditingBaseEntity {

	private static final long serialVersionUID = -4600381516273787970L;

	@Column(nullable = false)
	private String orderReference;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String email;
	@Column(nullable = true)
	private String note;
	@Column(nullable = false)
	private boolean emailSent = false;
	
	@Type(type = "jsonb-node")
	@Column(nullable = false)
	private JsonNode orderItems;

	@PrePersist
	void updateReferenceString() {
		this.orderReference = (Integer.toString(generateRandomDigits(3)) + "-" + Long.toString(System.currentTimeMillis()));
	}
	
	private static int generateRandomDigits(int n) {
	    int m = (int) Math.pow(10, n - 1);
	    return m + new Random().nextInt(9 * m);
	}
}
