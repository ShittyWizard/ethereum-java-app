pragma solidity ^0.5.1;
pragma experimental ABIEncoderV2;

contract FileStorageContract {
    //    maybe store sha256 instead of bytes32?
//    mapping (address => bytes32[]) fileStore;
    mapping (address => string[]) fileStore;
    //    todo: add check that this file was not uploaded before
    //    bytes32[] existingFiles;

//    event InitFileStore(bytes32 hashOfFile, address owner);
    event InitFileStore(string hashOfFile, address owner);

    //    todo: add feature for providing rights for file to another account
    //    event ChangeFileOwner(bytes32 hashOfFile, address prevOwner, address actualOwner);

//    function initFileStore(bytes32 hashOfFile) public {
//        fileStore[msg.sender].push(hashOfFile);
//        emit InitFileStore(hashOfFile, msg.sender);
//    }

    function initFileStore(string memory  hashOfFile) public {
        fileStore[msg.sender].push(hashOfFile);
        emit InitFileStore(hashOfFile, msg.sender);
    }


//    function getFileStore() public view returns (bytes32[] memory) {
//        return fileStore[msg.sender];
//    }

    function getFileStore() public view returns (string[] memory) {
        return fileStore[msg.sender];
    }
}
