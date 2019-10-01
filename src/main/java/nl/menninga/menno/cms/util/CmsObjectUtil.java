package nl.menninga.menno.cms.util;

import nl.menninga.menno.cms.entity.CmsObject;

public class CmsObjectUtil {
	
	public static CmsObject getRootCmsObject(CmsObject cmsObject) {
		if(cmsObject != null && cmsObject.getParent() != null) {
			return getRootCmsObject(cmsObject.getParent());
		}
		return cmsObject;
	}
}