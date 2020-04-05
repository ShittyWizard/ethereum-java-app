# Ethereum java app

## Short description
This project is part of my masters degree application for providing 
cross-chain transactions between **R3 Corda** platform and **Ethereum**, for example, for banking sector.


## Scenario
I try to implement typical for banking sector **KYC** scenario (Know your customer). 
*Bank_1* collects personal info about their customers and then, 
if customer wants to go to another bank (*Bank_2*), 
*Bank_1* just send transaction to *Bank_2* with all KYC data. *Bank_1* lose rights for using this information, *Bank_2* get these rights. 


It works well for banks inside one DLT-platfrom, such as **R3 Corda**, **Hyperledger Fabric** and other.
But the banking sector does not have a single platform and 
all banks are divided into separate groups with their own technology or platform inside.

So, I will try to solve this problem for the two platforms that are actively used in the banking sector - **R3 Corda** and **Ethereum**.

_**todo:** add links to KYC, R3 Corda, Ethereum, Hyperledger Fabric and part with Corda application_