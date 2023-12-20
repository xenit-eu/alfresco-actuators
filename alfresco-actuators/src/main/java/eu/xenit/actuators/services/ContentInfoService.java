package eu.xenit.actuators.services;

import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.ContentStoreInfo;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@Service
public class ContentInfoService extends HealthIndicator {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private ContentService contentService;

    @Override
    protected ContentStoreInfo getHealthDetails() throws FileNotFoundException, IOException {
        long start = System.currentTimeMillis();
        NodeRef rootNode = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        NodeRef node = getNode(rootNode, "app:company_home/app:dictionary/cm:Alfresco Actuators/cm:alfresco-actuators.txt");
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

    public NodeRef getNode(NodeRef rootNode, String path) throws FileNotFoundException {
        List<String> pathElements = new LinkedList<>(Arrays.asList(path.split("/")));
        NodeRef node = rootNode;
        for (String pathElement : pathElements) {
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node);
            node = childAssocs.stream()
                    .filter(childAssociationRef -> {
                        if (pathElement.contains(":")) {
                            String[] splitPathElement = pathElement.split(":");

                            return childAssociationRef.getQName().getLocalName().equals(splitPathElement[1]);
                        } else {
                            return childAssociationRef.getQName().equals(QName.createQName(pathElement));
                        }
                    })
                    .findFirst()
                    .orElseThrow(() -> new FileNotFoundException("file with name "
                            + pathElement + " was not found from path" + path))
                    .getChildRef();
        }
        return node;

    }


}
