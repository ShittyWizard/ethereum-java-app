package ipfs.impl;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import ipfs.IPFSService;

@Service
public class DefaultIPFSService implements IPFSService, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultIPFSService.class);

    @Value("${ipfs.node.multiaddr}")
    private String multiaddr;

    @Value("${ipfs.test.upload.file.path")
    private String pathToUploadFile;
    @Value("${ipfs.test.download.file.path}")
    private String pathToDownloadFile;

    private IPFS ipfs;

    @Override
    public void afterPropertiesSet() {
        ipfs = new IPFS(multiaddr);
    }

    @Override
    public boolean uploadFile(String pathToFile) {
        try {
            NamedStreamable.InputStreamWrapper is = new NamedStreamable.InputStreamWrapper(new FileInputStream(pathToFile));
            MerkleNode response = ipfs.add(is).get(0);
            String hash = response.name.orElse("empty");
            LOG.debug("Successful uploading. Hash - {}", hash);
            return true;
        } catch (IOException e) {
            LOG.error("Uploading failed. {}", e.getMessage());
            return false;
        }
    }

    @Override
    public byte[] downloadFile(String hash) {
        Multihash multihash = Multihash.fromBase58(hash);
        byte[] content = new byte[0];
        try {
            content = ipfs.cat(multihash);
            LOG.debug("Successful downloading of file - {}", hash);
        } catch (IOException e) {
            LOG.error("Downloading of file failed. {}", e.getMessage());
        }
        return content;
    }
}
