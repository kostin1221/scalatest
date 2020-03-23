package statistics.metrika.db

object MetrikaFieldsMap {

  private val fieldsMap = Map(
    ("ym:s:visitID", "visit_id"),
    ("ym:s:date", "date"),
    ("ym:s:pageViews", "page_views"),
    ("ym:s:visitDuration", "visit_duration"),
    ("ym:s:bounce", "bounce"),
    ("ym:s:regionCountry", "region_country"),
    ("ym:s:clientID", "client_id"),
    ("ym:s:goalsID", "completed_goals"),
    ("ym:s:UTMCampaign", "utm_campaign"),
    ("ym:s:UTMContent", "utm_content"),
    ("ym:s:UTMMedium", "utm_medium"),
    ("ym:s:UTMSource", "utm_source"),
    ("ym:s:UTMTerm", "utm_term"),
    ("ym:s:lastGCLID", "last_glid"),
    ("ym:s:deviceCategory", "device_category"),
    ("ym:s:mobilePhone", "mobile_phone"),
    ("ym:s:mobilePhoneModel", "mobile_phone_model"),
    ("ym:s:operatingSystem", "os"),
    ("ym:s:browser", "browser"),
    ("ym:s:browserMajorVersion", "browser_major_version"),
    ("ym:s:lastDirectClickOrder", "last_direct_click_order"),
    ("ym:s:lastDirectBannerGroup", "last_direct_banner_group"),
    ("ym:s:lastDirectClickBanner", "last_direct_click_banner"),
    ("ym:s:lastDirectPlatformType", "last_direct_platform_type"),
    ("ym:s:lastDirectPlatform", "last_direct_platform"),
    ("ym:s:lastDirectConditionType", "last_direct_condition_type"),
  )

  def getAllMetrikaFields: List[String] = fieldsMap.keys.toList

  def generateCreateTableSql = {
    val columns = fieldsMap
      .map(f => s"${f._2} varchar(255)")
      .reduce(_ + ",\n" + _)
    s"CREATE TABLE metrika_visits (id serial PRIMARY KEY, request_id INT, $columns);"
  }

  def mapMetrikaFieldsToColumns(csvHead: Array[String]) =
    csvHead.map(fieldsMap.getOrElse(_, "undefined"))
}
