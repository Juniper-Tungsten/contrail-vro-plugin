if (!cidr || ContrailUtils.isValidCidr(cidr)){
    return null;
}
return "Enter valid IPv4 or IPv6 Subnet/Mask";