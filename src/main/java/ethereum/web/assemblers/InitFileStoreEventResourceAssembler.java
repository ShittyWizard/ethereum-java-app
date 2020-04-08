package ethereum.web.assemblers;

import org.springframework.stereotype.Component;

import contracts.generated.FileStorageContract;
import ethereum.web.dto.InitFileStoreEventResource;

@Component
public class InitFileStoreEventResourceAssembler {
    public InitFileStoreEventResource toResource(FileStorageContract.InitFileStoreEventResponse event) {
        InitFileStoreEventResource resource = new InitFileStoreEventResource();
        resource.setOwner(event.owner);
        resource.setHashOfFile(new String(event.hashOfFile));

        return resource;
    }
}
