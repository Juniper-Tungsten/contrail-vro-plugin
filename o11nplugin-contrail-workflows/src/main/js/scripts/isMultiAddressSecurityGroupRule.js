var theRule = ContrailUtils.stringToRuleFromSecurityGroup(input.trim(), sg);

if (theRule.srcAddresses.length == 1 && theRule.dstAddresses.length == 1){
    return null;
}

return "Rules with multiple Source or Destination Addresses are incompatible with this workflow";