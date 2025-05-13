package eu.xenit.actuators.services;

import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.ContentStoreInfo;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;


@Service
public class ContentInfoService extends HealthIndicator {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private ContentService contentService;

    @Override
    protected ContentStoreInfo getHealthDetails() throws IOException {
        long start = System.currentTimeMillis();
        //it is fixed node ref for a file uploaded using acp
        //under path /app:company_home/app:dictionary/cm:Alfresco_x0020_Actuators/cm:alfresco-actuators.txt
        NodeRef node = new NodeRef("workspace://SpacesStore/b9da637d-c86b-4ecc-b676-ea1cd60e61b0");
        AccessContentForNode(node);
        return new ContentStoreInfo().accessTime(System.currentTimeMillis() - start);
    }

    private boolean AccessContentForNode(NodeRef node) throws IOException {
        final ContentData contentData = (ContentData) nodeService.getProperty(node, ContentModel.PROP_CONTENT);
        ContentReader contentReader = contentService.getRawReader(contentData.getContentUrl());
        try (InputStream ignored = contentReader.getContentInputStream()) {
            return true;
        }
    }
}
