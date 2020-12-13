package ethereum.web.assemblers;

import org.springframework.stereotype.Component;

import contracts.generated.FileStorageContract;
import ethereum.web.dto.ChangeFileOwnerEventResource;

@Component
public class ChangeFileOwnerEventResourceAssembler {
    public ChangeFileOwnerEventResource toResource(FileStorageContract.ChangeFileOwnerEventResponse event) {
        ChangeFileOwnerEventResource resource = new ChangeFileOwnerEventResource();
        resource.setHashOfFile(event.hashOfFile);
        resource.setPrevOwner(event.prevOwner);
        resource.setActualOwner(event.sendToAddress);

        return resource;
    }
}
