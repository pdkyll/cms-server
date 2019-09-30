package nl.menninga.menno.cms.entity.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CmsObjectWithParentIdProjection {

	Long getId();
	String getName();
	String getNamePath();
	@Value("#{(target != null ? target.parent : null)}")
	CmsObjectWithParentIdProjection getParent();
}
