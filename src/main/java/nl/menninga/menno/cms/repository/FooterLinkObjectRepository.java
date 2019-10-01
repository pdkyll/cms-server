package nl.menninga.menno.cms.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.cms.entity.FooterLinkObject;

@Repository
@Transactional
public interface FooterLinkObjectRepository extends JpaRepository<FooterLinkObject, Long> {
	
	@Modifying
	@Query(nativeQuery=true, value=
		"DELETE FROM footer_link_object flo " + 
		"WHERE  NOT EXISTS (" + 
		"   SELECT FROM cms_object co" + 
		"   WHERE  co.id = flo.cms_object_id" + 
		");"
	)
	public void deleteAllNotReferenced();
	
	public void deleteByCmsObjectId(Long cmsObjectId);
}
