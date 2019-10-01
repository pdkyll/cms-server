package nl.menninga.menno.cms.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import nl.menninga.menno.cms.entity.FooterLinkObject;
import nl.menninga.menno.cms.service.FooterLinkObjectService;

@RestController
@CrossOrigin
public class FooterLinkObjectController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FooterLinkObjectController.class);
	
	@Autowired
	private FooterLinkObjectService footerLinkObjectService;
	
	@GetMapping("api/footerlinkobjects")
	public ResponseEntity<?> footerLinkObjectsGet() {
		List<FooterLinkObject> footerLinkObjects = footerLinkObjectService.getFooterLinkObjects();
		return ResponseEntity.ok(footerLinkObjects);
    }
	
	@GetMapping("api/footerlinkobject/{id}")
	public ResponseEntity<?> footerLinkObjectGet(@PathVariable Long id) {
		FooterLinkObject footerLinkObject = null;		
		try {
			footerLinkObject = footerLinkObjectService.getFooterLinkObjectById(id);
		} catch (NotFoundException e) {
			LOGGER.info("Entity could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(footerLinkObject);
    }

	@PostMapping("api/footerlinkobject")
    @PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> footerLinkObjectPost(@RequestBody FooterLinkObject footerLinkObject) {
		try {
			footerLinkObject = footerLinkObjectService.createFooterLinkObject(footerLinkObject);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be created!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		}
		return ResponseEntity.ok(footerLinkObject);
    }

	@PutMapping("api/footerlinkobject")
    @PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> orderPut(@RequestBody FooterLinkObject footerLinkObject) {
		try {
			footerLinkObject = footerLinkObjectService.updateFooterLinkObject(footerLinkObject);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to update could not be found!", e);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(footerLinkObject);
    }
	
	@DeleteMapping("api/footerlinkobject/{id}")
	@PreAuthorize("hasRole('CMS_ADMIN')")
    public ResponseEntity<?> footerLinkObjectDelete(@PathVariable Long id) {
		try {
	        footerLinkObjectService.deleteFooterLinkObject(id);
		} catch (IllegalArgumentException e) {
			LOGGER.info("Entity could not be deleted!", e);
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
		} catch (NotFoundException e) {
			LOGGER.info("Entity to delete could not be found!", e);
			return ResponseEntity.notFound().build();
		}
        return ResponseEntity.ok("FooterLinkObject deleted.");
    }
}