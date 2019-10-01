package nl.menninga.menno.cms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import nl.menninga.menno.cms.entity.CmsObject;
import nl.menninga.menno.cms.entity.projection.CmsObjectMinProjection;
import nl.menninga.menno.cms.entity.projection.CmsObjectWithParentIdProjection;
import nl.menninga.menno.cms.repository.CmsObjectRepository;
import nl.menninga.menno.cms.repository.CmsObjectSearchRepository;
import nl.menninga.menno.cms.util.StringUrlUtils;

@Service
public class CmsObjectService {

	@Autowired
	private CmsObjectRepository cmsObjectRepository;
	
	@Autowired
	private CmsObjectSearchRepository cmsObjectSearchRepository;
	
	@Autowired
	private FooterLinkObjectService footerLinkObjectService;
	
	public CmsObject getCmsObjectByPath(String path, boolean isAdmin) throws NotFoundException {
		CmsObject cmsObject;
		if(isAdmin) {
			cmsObject = cmsObjectRepository.findOneByPath(path);
		}else {
			cmsObject = cmsObjectRepository.findOneByPathAndPublished(path, true);
		}
		if(Objects.isNull(cmsObject)) {
			throw new NotFoundException();
		}
		return cmsObject;
	}
	
	public boolean checkPathExistence(Long parentId, Long id, String name) {
		CmsObject parentCmsObject = new CmsObject();
		parentCmsObject.setId(parentId);
		if(id == null) {
			return cmsObjectRepository.existsByParentAndName(parentCmsObject, StringUrlUtils.stringToFriendlyUrlPart(name));
		}else {
			return cmsObjectRepository.existsByParentAndNameAndIdIsNot(parentCmsObject, StringUrlUtils.stringToFriendlyUrlPart(name), id);
		}
	}
	
	public List<CmsObject> getAllChildren(CmsObject parent, boolean isAdmin){
		if(isAdmin) {
			return cmsObjectRepository.findByParentOrderByChildIndexAsc(parent, CmsObject.class);
		}else {
			return cmsObjectRepository.findByParentAndPublishedTrueOrderByChildIndexAsc(parent, CmsObject.class);
		}
	}
	
	public List<CmsObjectMinProjection> getAllBaseChildren(CmsObject parent, boolean isAdmin){
		if(isAdmin) {
			return cmsObjectRepository.findByParentOrderByChildIndexAsc(parent, CmsObjectMinProjection.class);
		}else {
			return cmsObjectRepository.findByParentAndPublishedTrueOrderByChildIndexAsc(parent, CmsObjectMinProjection.class);
		}
	}
	
	public CmsObjectWithParentIdProjection getNavigationCmsObject(Long id, boolean isAdmin) {
		if(isAdmin) {
			return cmsObjectRepository.findOneById(id, CmsObjectWithParentIdProjection.class);
		}else {
			return cmsObjectRepository.findOneByIdAndPublishedTrue(id, CmsObjectWithParentIdProjection.class);
		}
	}
	
	public String getPathForCmsObject(Long id, boolean isAdmin) {
		CmsObjectWithParentIdProjection cmsObject = null;
		if(isAdmin) {
			cmsObject = cmsObjectRepository.findOneById(id, CmsObjectWithParentIdProjection.class);
		}else {
			cmsObject = cmsObjectRepository.findOneByIdAndPublishedTrue(id, CmsObjectWithParentIdProjection.class);
		}
		List<String> pathList = recursivePathResolver(cmsObject, new ArrayList<String>());
		Collections.reverse(pathList);
		return String.join("/", pathList);
	}
	
	private List<String> recursivePathResolver(CmsObjectWithParentIdProjection cmsObject, List<String> pathList) {
		if(cmsObject.getParent() != null) {
			pathList.add(cmsObject.getNamePath());
			pathList = recursivePathResolver(cmsObject.getParent(), pathList);
		}
		return pathList;
	}
	
	public CmsObject getCmsObjectByName(String name) throws NotFoundException {
		CmsObject cmsObject = cmsObjectRepository.findOneByName(name);
		if(Objects.isNull(cmsObject)) {
			throw new NotFoundException();
		}
		return cmsObject;
	}
	
	public CmsObject getCmsObjectById(Long id) throws NotFoundException {
		CmsObject cmsObject = cmsObjectRepository.getOne(id);
		if(Objects.isNull(cmsObject)) {
			throw new NotFoundException();
		}
		return cmsObject;
	}
	
	@Transactional
	public void deleteCmsObject(Long id) throws NotFoundException {
		CmsObject cmsObject = cmsObjectRepository.getOne(id);
		if(Objects.isNull(cmsObject)) {
			throw new NotFoundException();
		}
		cmsObjectRepository.delete(cmsObject);
		footerLinkObjectService.deleteFooterLinkObjectByCmsObjectId(id);
	}
	
	public CmsObject createCmsObject(CmsObject cmsObject) throws IllegalArgumentException {
		cmsObject.setNamePath(StringUrlUtils.stringToFriendlyUrlPart(cmsObject.getName()));
		return cmsObjectRepository.saveAndFlush(cmsObject);
	}

	public CmsObject updateCmsObject(CmsObject cmsObject) throws NotFoundException {
		CmsObject origCmsObject = cmsObjectRepository.getOne(cmsObject.getId());
		if(Objects.isNull(origCmsObject)) {
			throw new NotFoundException();
		}
		cmsObject.setNamePath(StringUrlUtils.stringToFriendlyUrlPart(cmsObject.getName()));
		return cmsObjectRepository.saveAndFlush(cmsObject);
	}
	
	@Transactional
	public void deleteCmsObjectByPath(String path) {
		cmsObjectRepository.deleteByPath(path);
		footerLinkObjectService.deleteUnreferencedFooterLinkObjects();
	}
	
	public List<CmsObject> searchCmsObjects(boolean isAdmin, String searchString){
		return cmsObjectSearchRepository.search(isAdmin, searchString);
	}
}
