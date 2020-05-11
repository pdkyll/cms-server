package nl.menninga.menno.cms.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.menninga.menno.cms.entity.CmsObject;
import nl.menninga.menno.cms.entity.projection.CmsObjectMinProjection;
import nl.menninga.menno.cms.entity.projection.CmsObjectWithParentIdProjection;
import nl.menninga.menno.cms.service.CmsObjectService;
import nl.menninga.menno.cms.util.SecurityUtil;

@RestController
@CrossOrigin
public class CmsObjectController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CmsObjectController.class);

	@Autowired
	private SecurityUtil securityUtil;
	
	@Autowired
	private CmsObjectService cmsObjectService;
	
	@GetMapping("api/cmsobject")
    public ResponseEntity<?> cmsObjectGet(@RequestParam(required=false) String path) {
		CmsObject cmsObject = null;
		try {
			boolean isAdmin = securityUtil.hasRole("CMS_ADMIN");
			cmsObject = cmsObjectService.getCmsObjectByPath(path != null && !path.isEmpty() ? "root/" + path : "root", isAdmin);
			cmsObject.setChildren(cmsObjectService.getAllChildren(cmsObject, isAdmin));
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		Map<String, Object> result = new HashMap<>();
		result.put("currentObject", cmsObject);
		result.put("parent", cmsObject != null ? cmsObject.getParent() : null);
		return ResponseEntity.ok(result);
    }
	
	@GetMapping("api/cmsobject/path/{id}")
    public ResponseEntity<?> cmsObjectPath(@PathVariable Long id) {
		boolean isAdmin = securityUtil.hasRole("CMS_ADMIN");
		
		String path = cmsObjectService.getPathForCmsObject(id, isAdmin);
		
		Map<String, Object> result = new HashMap<>();
		result.put("path", path);
		return ResponseEntity.ok(result);
    }
	
	@GetMapping("api/cmsobject/navigation/{id}")
    public ResponseEntity<?> cmsObjectBaseGetChildren(@PathVariable Long id) {
		boolean isAdmin = securityUtil.hasRole("CMS_ADMIN");
		CmsObject cmsObject = new CmsObject();
		cmsObject.setId(id);
		CmsObjectWithParentIdProjection cmsObjectWithParent = cmsObjectService.getNavigationCmsObject(id, isAdmin);
		List<CmsObjectMinProjection> cmsObjectBaseChildren = cmsObjectService.getAllBaseChildren(cmsObject, isAdmin);
		Map<String, Object> result = new HashMap<>();
		result.put("currentObject", cmsObjectWithParent);
		result.put("children", cmsObjectBaseChildren);
		return ResponseEntity.ok(result);
    }
	
	@GetMapping("api/cmsobject/checkexistence")
	@PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> cmsObjectCheckExistence(@RequestParam Long parentId, @RequestParam(required=false) Long id, @RequestParam String name) {
		boolean exists = cmsObjectService.checkPathExistence(parentId, id, name);
		return ResponseEntity.ok("{\"pathExists\": " + exists + "}");
    }

	@PostMapping("api/cmsobject")
    @PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> cmsObjectPost(Principal principal, @RequestBody CmsObject cmsObject) {
		try {
			cmsObject = cmsObjectService.createCmsObject(cmsObject);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be created!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		}
		return ResponseEntity.ok(cmsObject);
    }

	@PutMapping("api/cmsobject")
    @PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> cmsObjectPut(Principal principal, @RequestBody CmsObject cmsObject) {
		try {
			cmsObject = cmsObjectService.updateCmsObject(cmsObject);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to update could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		Map<String, Object> result = new HashMap<>();
		result.put("currentObject", cmsObject);
		result.put("parent", cmsObject != null ? cmsObject.getParent() : null);
		return ResponseEntity.ok(result);
    }
	
	@DeleteMapping("api/cmsobject/{id}")
	@PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> cmsObjectDelete(@PathVariable Long id) {
		try {
	        cmsObjectService.deleteCmsObject(id);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("CmsObject deleted.");
    }
	
	@DeleteMapping("api/cmsobject/path")
	@PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> cmsObjectDeleteByPath(@RequestParam String path) {
		cmsObjectService.deleteRecursiveCmsObjectByPath("root/" + path);
        return ResponseEntity.ok("CmsObject deleted.");
    }
	
	@GetMapping("api/cmsobject/search")
    public ResponseEntity<?> cmsObjectSearch(@RequestParam String searchString) {
		boolean isAdmin = securityUtil.hasRole("CMS_ADMIN");
		List<CmsObject> cmsObjects = cmsObjectService.searchCmsObjects(isAdmin, searchString);
		return ResponseEntity.ok(cmsObjects);
    }
}