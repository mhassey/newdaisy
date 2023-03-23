package com.daisy.pojo.response

import com.google.gson.annotations.SerializedName


data class Store (

  @SerializedName("AccountID"    ) var AccountID    : String? = null,
  @SerializedName("StoreID"      ) var StoreID      : String? = null,
  @SerializedName("StoreName"    ) var StoreName    : String? = null,
  @SerializedName("PhoneNumber"  ) var PhoneNumber  : String? = null,
  @SerializedName("StoreEmail"   ) var StoreEmail   : String? = null,
  @SerializedName("Lookup"       ) var Lookup       : String? = null,
  @SerializedName("Address"      ) var Address      : String? = null,
  @SerializedName("StreetNumber" ) var StreetNumber : String? = null,
  @SerializedName("Street"       ) var Street       : String? = null,
  @SerializedName("Neighborhood" ) var Neighborhood : String? = null,
  @SerializedName("City"         ) var City         : String? = null,
  @SerializedName("County"       ) var County       : String? = null,
  @SerializedName("State"        ) var State        : String? = null,
  @SerializedName("Country"      ) var Country      : String? = null,
  @SerializedName("PostalCode"   ) var PostalCode   : String? = null,
  @SerializedName("TimeZone"     ) var TimeZone     : String? = null,
  @SerializedName("Latitude"     ) var Latitude     : String? = null,
  @SerializedName("Longitude"    ) var Longitude    : String? = null,
  @SerializedName("OrgLevel1"    ) var OrgLevel1    : String? = null,
  @SerializedName("OrgLevel2"    ) var OrgLevel2    : String? = null,
  @SerializedName("OrgLevel3"    ) var OrgLevel3    : String? = null,
  @SerializedName("OrgLevel4"    ) var OrgLevel4    : String? = null

)