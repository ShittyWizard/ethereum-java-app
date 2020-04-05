pragma solidity ^0.5.0;

import "../contracts/StringUtils.sol";

contract KycContract {
    mapping (address => bytes32[]) filesByOwner;
    event KycStatusChange(address indexed owner, bytes32 indexed hash);
    bytes32[] existingHashes;

    function addFileToMyStore(string memory hash) public {
        bytes32 hashOfFile = StringUtils.stringToBytes32(hash);
        bool isHere = false;
        emit KycStatusChange(msg.sender, hashOfFile);
        for (uint i = 0; i < existingHashes.length; i++) {
            if(existingHashes[i] == hashOfFile) {
                isHere = true;
                break;
            }
        }
        if (!isHere) {
            filesByOwner[msg.sender].push(hashOfFile);
            emit KycStatusChange(msg.sender, hashOfFile);
            existingHashes.push(hashOfFile);
        }
    }

    function getFilesByOwner() public view returns (bytes32[] memory) {
        return filesByOwner[msg.sender];
    }

    function getHash(bytes32 hash) public pure returns (string memory) {
        return StringUtils.bytes32ToString(hash);
    }
}
