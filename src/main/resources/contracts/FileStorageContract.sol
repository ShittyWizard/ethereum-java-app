pragma solidity >=0.5.0 <0.8.0;
pragma experimental ABIEncoderV2;

contract FileStorageContract {
    mapping (address => string[]) fileStore;
    string[] existingFiles;

    event InitFileStore(string hashOfFile, address owner);
    event ChangeFileOwner(string hashOfFile, address prevOwner, address sendToAddress);

    function initFileStore(string memory  hashOfFile) public {
        if (!doesFileExist(hashOfFile)) {
            fileStore[msg.sender].push(hashOfFile);
            existingFiles.push(hashOfFile);
            emit InitFileStore(hashOfFile, msg.sender);
        }
    }

    function changeFileOwner(string memory hashOfFile, address sendToAddress) public {
        if (!doesFileExist(hashOfFile)) {
            initFileStore(hashOfFile);
        }
        if(doesFileBelongToSender(hashOfFile)) {
            uint256 index = getIndexOfExistingFile(hashOfFile);
            emit ChangeFileOwner(hashOfFile, msg.sender, sendToAddress);
            fileStore[sendToAddress].push(hashOfFile);
            delete fileStore[msg.sender][index];
        }
    }

    function getFileStore() public view returns (string[] memory) {
        return fileStore[msg.sender];
    }

    function doesFileExist(string memory hashOfFile) private view returns(bool) {
        for (uint i = 0; i < existingFiles.length; i++) {
            if (equalString(hashOfFile, existingFiles[i])) {
                return true;
            }
        }
        return false;
    }

    function doesFileBelongToSender(string memory hashOfFile) private view returns(bool) {
        for (uint256 i = 0; i <= fileStore[msg.sender].length; i++) {
            if (equalString(hashOfFile, fileStore[msg.sender][i])) {
                return true;
            }
        }
        return false;
    }

    function getIndexOfExistingFile(string memory hashOfFile) private view returns(uint256) {
        for (uint256 i = 0; i <= fileStore[msg.sender].length; i++) {
            if (equalString(hashOfFile, fileStore[msg.sender][i])) {
                return i;
            }
        }
        return 0;
    }

    function compare(string memory _a, string memory _b) private view returns (int) {
        bytes memory a = bytes(_a);
        bytes memory b = bytes(_b);
        uint minLength = a.length;
        if (b.length < minLength) minLength = b.length;
        //@todo unroll the loop into increments of 32 and do full 32 byte comparisons
        for (uint i = 0; i < minLength; i ++)
            if (a[i] < b[i])
                return -1;
            else if (a[i] > b[i])
                return 1;
        if (a.length < b.length)
            return -1;
        else if (a.length > b.length)
            return 1;
        else
            return 0;
    }
    function equalString(string memory _a, string memory _b) private view returns (bool) {
        return compare(_a, _b) == 0;
    }
}
