package nl.menninga.menno.cms.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.cms.entity.CmsObject;

@Repository
@Transactional
public interface CmsObjectRepository extends JpaRepository<CmsObject, Long> {

	@Query(nativeQuery=true, value=
			"WITH RECURSIVE cms_object_temp (id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string, level, path_array) AS (" + 
			"    SELECT id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string, 0, ARRAY[name_path]" + 
			"    FROM cms_object" + 
			"    WHERE parent_id is null" + 
			"    UNION ALL" + 
			"    SELECT c.id, c.creation_time, c.modification_time, c.name, c.name_path, c.view, c.published, c.child_index, c.parent_id, c.json, c.search_string, (t0.level + 1), ARRAY_APPEND(t0.path_array, c.name_path)" + 
			"    FROM cms_object c" + 
			"            INNER JOIN cms_object_temp t0 ON t0.id = c.parent_id" + 
			") " +
			"SELECT id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string " + 
			"FROM cms_object_temp " + 
			"WHERE ARRAY_TO_STRING(path_array, '/') = :path"
	)
	CmsObject findOneByPath(@Param("path") String path);

	@Query(nativeQuery=true, value=
			"WITH RECURSIVE cms_object_temp (id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string, level, path_array) AS (" + 
			"    SELECT id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string, 0, ARRAY[name_path]" + 
			"    FROM cms_object" + 
			"    WHERE parent_id is null AND published = :published" + 
			"    UNION ALL" + 
			"    SELECT c.id, c.creation_time, c.modification_time, c.name, c.name_path, c.view, c.published, c.child_index, c.parent_id, c.json, c.search_string, (t0.level + 1), ARRAY_APPEND(t0.path_array, c.name_path)" + 
			"    FROM cms_object c" + 
			"            INNER JOIN cms_object_temp t0 ON t0.id = c.parent_id" + 
			") " +
			"SELECT id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string " + 
			"FROM cms_object_temp " + 
			"WHERE ARRAY_TO_STRING(path_array, '/') = :path AND published = :published"
	)
	CmsObject findOneByPathAndPublished(@Param("path") String path, @Param("published") boolean published);

	CmsObject findOneByName(String name);
	
	<T> List<T> findByParentOrderByChildIndexAsc(CmsObject parent, Class<T> tClass);

	<T> List<T> findByParentAndPublishedTrueOrderByChildIndexAsc(CmsObject parent, Class<T> tClass);
	
	<T> T findOneById(Long id, Class<T> tClass);
	<T> T findOneByIdAndPublishedTrue(Long id, Class<T> tClass);
	
	boolean existsByParentAndName(CmsObject parentCmsObject, String name);

	boolean existsByParentAndNameAndIdIsNot(CmsObject parentCmsObject, String name, Long id);

	@Modifying
	@Query(nativeQuery=true, value=
			"WITH RECURSIVE cms_object_temp (id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string, level, path_array) AS (" + 
			"    SELECT id, creation_time, modification_time, name, name_path, view, published, child_index, parent_id, json, search_string, 0, ARRAY[name_path]" + 
			"    FROM cms_object" + 
			"    WHERE parent_id is null" + 
			"    UNION ALL" + 
			"    SELECT c.id, c.creation_time, c.modification_time, c.name, c.name_path, c.view, c.published, c.child_index, c.parent_id, c.json, c.search_string, (t0.level + 1), ARRAY_APPEND(t0.path_array, c.name_path)" + 
			"    FROM cms_object c" + 
			"            INNER JOIN cms_object_temp t0 ON t0.id = c.parent_id" + 
			") " +
			"DELETE " + 
			"FROM cms_object " + 
			"WHERE id IN (SELECT id FROM cms_object_temp WHERE ARRAY_TO_STRING(path_array, '/') LIKE :path%)"
	)
	void deleteByPath(String path);
}
