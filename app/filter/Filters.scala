package filter

import javax.inject.Inject

import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

/**
  * Created by Mikhail_Miroliubov on 6/21/2017.
  */
class Filters @Inject() (corsFilter: CORSFilter) extends DefaultHttpFilters(corsFilter)
