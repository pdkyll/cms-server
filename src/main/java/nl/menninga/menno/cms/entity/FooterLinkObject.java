package nl.menninga.menno.cms.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "footer_link_object", schema = "public")
@SequenceGenerator(name = "idgen", sequenceName = "footer_link_object_id_seq", initialValue = 1, allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FooterLinkObject extends AuditingBaseEntity {
	
	private static final long serialVersionUID = -598159552528156770L;

	private Long cmsObjectId;
	private String name;
}