pragma solidity ^0.4.0;

contract KycContract {
    string value;

    struct Document {

    }

    function KycContract(){

    }

    function getValue() public view returns(string) {
        return value;
    }
}
