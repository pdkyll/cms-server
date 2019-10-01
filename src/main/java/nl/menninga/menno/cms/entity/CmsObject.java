package nl.menninga.menno.cms.entity;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.search.bridge.builtin.BooleanBridge;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cms_object", schema = "public")
@SequenceGenerator(name = "idgen", sequenceName = "cms_object_id_seq", initialValue = 1, allocationSize = 1)
@TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Indexed
@AnalyzerDef(name = "dutchanalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), 
		filters = {
				@TokenFilterDef(factory = LowerCaseFilterFactory.class),
				@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
						@Parameter(name = "language", value = "Dutch") 
				}) 
		}
)
public class CmsObject extends AuditingBaseEntity {

	private static final long serialVersionUID = -4600381516273787970L;

	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String namePath;
	@Column(nullable = false)
	private boolean onlyMenu;
	@Column(nullable = false)
	private String view;
	@Column(nullable = false)
	@Field(analyze = Analyze.NO)
	@FieldBridge(impl = BooleanBridge.class)
	private boolean published;
	@Column(nullable = false)
	private Long childIndex;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	@JsonBackReference
	private CmsObject parent;

	@Transient
	private List<CmsObject> children;

	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb")
	private JsonNode json;

	@Column
	@Field
	@Analyzer(definition = "dutchanalyzer")
	@JsonIgnore
	private String searchString;

	@PreUpdate
	@PrePersist
	void updateSearchString() {
		final String fullSearchString = StringUtils.join(Arrays.asList(name, json.toString()), " ").replaceAll("\\<.*?>"," ");
		this.searchString = fullSearchString;
	}
}
