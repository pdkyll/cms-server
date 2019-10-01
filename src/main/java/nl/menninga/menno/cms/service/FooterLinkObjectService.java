package nl.menninga.menno.cms.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import nl.menninga.menno.cms.entity.FooterLinkObject;
import nl.menninga.menno.cms.repository.FooterLinkObjectRepository;

@Service
public class FooterLinkObjectService {

	@Autowired
	private FooterLinkObjectRepository footerLinkObjectRepository;
	
	public List<FooterLinkObject> getFooterLinkObjects(){
		return footerLinkObjectRepository.findAll();
	}
	
	public FooterLinkObject getFooterLinkObjectById(Long id) throws NotFoundException {
		FooterLinkObject footerLinkObject = footerLinkObjectRepository.getOne(id);
		if(Objects.isNull(footerLinkObject)) {
			throw new NotFoundException();
		}
		return footerLinkObject;
	}
	
	@Transactional
	public void deleteFooterLinkObject(Long id) throws NotFoundException {
		FooterLinkObject footerLinkObject = footerLinkObjectRepository.getOne(id);
		if(Objects.isNull(footerLinkObject)) {
			throw new NotFoundException();
		}
		footerLinkObjectRepository.delete(footerLinkObject);
	}
	
	@Transactional
	public void deleteFooterLinkObjectById(Long id) {
		footerLinkObjectRepository.deleteById(id);
	}
	
	@Transactional
	public void deleteUnreferencedFooterLinkObjects() {
		footerLinkObjectRepository.deleteAllNotReferenced();
	}
	
	public FooterLinkObject createFooterLinkObject(FooterLinkObject footerLinkObject) throws IllegalArgumentException {
		return footerLinkObjectRepository.saveAndFlush(footerLinkObject);
	}

	public FooterLinkObject updateFooterLinkObject(FooterLinkObject footerLinkObject) throws NotFoundException {
		FooterLinkObject origFooterLinkObject = footerLinkObjectRepository.getOne(footerLinkObject.getId());
		if(Objects.isNull(origFooterLinkObject)) {
			throw new NotFoundException();
		}
		return footerLinkObjectRepository.saveAndFlush(footerLinkObject);
	}

	@Transactional
	public void deleteFooterLinkObjectByCmsObjectId(Long cmsObjectId) {
		footerLinkObjectRepository.deleteByCmsObjectId(cmsObjectId);
	}
}
