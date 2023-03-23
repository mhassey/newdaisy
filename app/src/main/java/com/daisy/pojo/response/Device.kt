package com.daisy.pojo.response
import com.google.gson.annotations.SerializedName


data class Device (

  @SerializedName("AccountID"            ) var AccountID            : String?  = null,
  @SerializedName("StoreID"              ) var StoreID              : String?  = null,
  @SerializedName("TimeStamp"            ) var TimeStamp            : String?  = null,
  @SerializedName("Sent"                 ) var Sent                 : Int?     = null,
  @SerializedName("Received"             ) var Received             : Int?     = null,
  @SerializedName("Latitude"             ) var Latitude             : String?  = null,
  @SerializedName("Longitude"            ) var Longitude            : String?  = null,
  @SerializedName("Accuracy"             ) var Accuracy             : Int?     = null,
  @SerializedName("BricTECHAgentType"    ) var BricTECHAgentType    : String?  = null,
  @SerializedName("BrictechAgentVersion" ) var BrictechAgentVersion : String?  = null,
  @SerializedName("Status"               ) var Status               : String?  = null,
  @SerializedName("DeviceID"             ) var DeviceID             : String?  = null,
  @SerializedName("SerialNumber"         ) var SerialNumber         : String?  = null,
  @SerializedName("IMEI"                 ) var IMEI                 : String?  = null,
  @SerializedName("EnterpriseNumber"     ) var EnterpriseNumber     : String?  = null,
  @SerializedName("Manufacturer"         ) var Manufacturer         : String?  = null,
  @SerializedName("Model"                ) var Model                : String?  = null,
  @SerializedName("SSID"                 ) var SSID                 : String?  = null,
  @SerializedName("Locked"               ) var Locked               : Boolean? = null,
  @SerializedName("Power"                ) var Power                : Boolean? = null,
  @SerializedName("PowerTimestamp"       ) var PowerTimestamp       : Int?     = null,
  @SerializedName("PowerLevel"           ) var PowerLevel           : Int?     = null,
  @SerializedName("EnrollmentStatus"     ) var EnrollmentStatus     : String?  = null,
  @SerializedName("GeoFenceStatus"       ) var GeoFenceStatus       : String?  = null,
  @SerializedName("OrgLevel1"            ) var OrgLevel1            : String?  = null,
  @SerializedName("OrgLevel2"            ) var OrgLevel2            : String?  = null,
  @SerializedName("OrgLevel3"            ) var OrgLevel3            : String?  = null,
  @SerializedName("OrgLevel4"            ) var OrgLevel4            : String?  = null,
  @SerializedName("Category"             ) var Category             : String?  = null,
  @SerializedName("LowBattery"           ) var LowBattery           : Boolean? = null,
  @SerializedName("TamperSwitch"         ) var TamperSwitch         : Boolean? = null

)