
var ruleSequence = new ContrailSequenceType(-1, -1);
var ruleUuid = ContrailUtils.randomUUID();

var parsedPorts = ContrailUtils.parsePorts(ports);
var addr = [ContrailUtils.createAddress(addressType, addressCidr, null, null, addressSecurityGroup)];

var localPorts = ContrailUtils.parsePorts("0-65535");
var localAddr = [ContrailUtils.createAddress("Security Group", null, null, null, null)];

var trafficDirection = ">"

var srcAddr, dstAddr, srcPorts, dstPorts;

if (direction == "ingress") {
    srcAddr = addr;
    srcPorts = parsedPorts;
    dstAddr = localAddr;
    dstPorts = localPorts;
} else {
    dstAddr = addr;
    dstPorts = parsedPorts;
    srcAddr = localAddr;
    srcPorts = localPorts;
}

var rule = new ContrailPolicyRuleType(null, ruleUuid, trafficDirection, ContrailUtils.lowercase(protocol), srcAddr, srcPorts, null, dstAddr, dstPorts, null, ethertype);
var rules = parent.getEntries();
if (!rules) {
    rules = new ContrailPolicyEntriesType();
    parent.setEntries(rules);
}
rules.addPolicyRule(rule);

var id = parent.internalId;
var executor = ContrailConnectionManager.executor(id.toString());
executor.updateSecurityGroup(parent);