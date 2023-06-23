package egovframework.innopam.tile;

import org.junit.jupiter.api.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReadMetaDataFromTif {
    public void readTiffMetadata(String filePath) {
        try {
            File file = new File(filePath);
            ImageInputStream iis = ImageIO.createImageInputStream(file);

            ImageReader reader = ImageIO.getImageReadersByFormatName("tif").next();
            reader.setInput(iis);

            IIOMetadata metadata = reader.getImageMetadata(0);
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

            traverseMetadata(root);

            reader.dispose();
            iis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void traverseMetadata(IIOMetadataNode node) {
        // Iterate over child nodes
        for (int i = 0; i < node.getLength(); i++) {
            IIOMetadataNode childNode = (IIOMetadataNode) node.item(i);

            // Extract desired metadata information from child node
            String nodeName = childNode.getNodeName();
            String nodeValue = childNode.getNodeValue();
            Map<String, String> nodeAttributes = extractAttributes(childNode);

            // Process or display the extracted metadata information as needed
            System.out.println("Node Name: " + nodeName);
            System.out.println("Node Value: " + nodeValue);
            System.out.println("Node Attributes: " + nodeAttributes);

            // Recursively traverse child nodes
            traverseMetadata(childNode);
        }
    }

    private static Map<String, String> extractAttributes(IIOMetadataNode node) {
        NamedNodeMap attributes = node.getAttributes();
        Map<String, String> attributeMap = new HashMap<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            attributeMap.put(attribute.getNodeName(), attribute.getNodeValue());
        }

        return attributeMap;
    }

    @Test
    public void test() {
        String filePath = "/Users/ymcho/dev/PM2023_JSAT_BE/share_folder/upload/Seoul_RGB.tif";
        readTiffMetadata(filePath);
    }

}
