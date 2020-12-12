package ipfs.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import ipfs.services.IPFSService;

@Service
public class DefaultIPFSService implements IPFSService, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultIPFSService.class);

//    @Value("${ipfs.node.multiaddr}")
    private String multiaddr = "/ip4/178.154.248.132/tcp/5001";

    private IPFS ipfs;

    @Override
    public void afterPropertiesSet() {
        ipfs = new IPFS(multiaddr);
    }

    @Override
    public String uploadMultipartFile(MultipartFile file) {
        try {
            NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(file.getInputStream());
            MerkleNode response = ipfs.add(is).get(0);
            String hash = response.name.orElse("");
            LOG.info("Successful uploading. Hash - {}", hash);
            return hash;
        } catch (IOException e) {
            LOG.error("Uploading failed. {}", e.getMessage());
            return "";
        }
    }

    @Override
    public String uploadFileByInputStream(InputStream inputStream) {
        try {
            NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(inputStream);
            MerkleNode response = ipfs.add(is).get(0);
            String hash = response.name.orElse("");
            LOG.info("Successful uploading. Hash - {}", hash);
            return hash;
        } catch (IOException e) {
            LOG.error("Uploading failed. {}", e.getMessage());
            return "";
        }
    }

    @Override
    public InputStreamResource downloadFile(String hash) {
        Multihash multihash = Multihash.fromBase58(hash);
        byte[] content = new byte[0];
        try {
            content = ipfs.cat(multihash);
            LOG.info("Successful downloading of file - {}", hash);
        } catch (IOException e) {
            LOG.info("Downloading of file failed. {}", e.getMessage());
        }
        InputStream inputStream = new ByteArrayInputStream(content);
        return new InputStreamResource(inputStream);
    }
}
