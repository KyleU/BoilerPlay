/* Generated File */
package models.auth

import com.kyleu.projectile.models.result.BaseResult
import com.kyleu.projectile.models.result.filter.Filter
import com.kyleu.projectile.models.result.orderBy.OrderBy
import com.kyleu.projectile.models.result.paging.PagingOptions
import com.kyleu.projectile.util.DateUtils
import com.kyleu.projectile.util.JsonSerializers._
import java.time.LocalDateTime

final case class PasswordInfoRowResult(
    override val filters: Seq[Filter] = Nil,
    override val orderBys: Seq[OrderBy] = Nil,
    override val totalCount: Int = 0,
    override val paging: PagingOptions = PagingOptions(),
    override val results: Seq[PasswordInfoRow] = Nil,
    override val durationMs: Int = 0,
    override val occurred: LocalDateTime = DateUtils.now
) extends BaseResult[PasswordInfoRow]

object PasswordInfoRowResult {
  implicit val jsonEncoder: Encoder[PasswordInfoRowResult] = (r: PasswordInfoRowResult) => io.circe.Json.obj(
    ("filters", r.filters.asJson),
    ("orderBys", r.orderBys.asJson),
    ("totalCount", r.totalCount.asJson),
    ("paging", r.paging.asJson),
    ("results", r.results.asJson),
    ("durationMs", r.durationMs.asJson),
    ("occurred", r.occurred.asJson)
  )

  implicit val jsonDecoder: Decoder[PasswordInfoRowResult] = (c: io.circe.HCursor) => for {
    filters <- c.downField("filters").as[Seq[Filter]]
    orderBys <- c.downField("orderBys").as[Seq[OrderBy]]
    totalCount <- c.downField("totalCount").as[Int]
    paging <- c.downField("paging").as[PagingOptions]
    results <- c.downField("results").as[Seq[PasswordInfoRow]]
    durationMs <- c.downField("durationMs").as[Int]
    occurred <- c.downField("occurred").as[LocalDateTime]
  } yield PasswordInfoRowResult(filters, orderBys, totalCount, paging, results, durationMs, occurred)

  def fromRecords(
    q: Option[String], filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None,
    startMs: Long, totalCount: Int, results: Seq[PasswordInfoRow]
  ) = {
    val paging = PagingOptions.from(totalCount, limit, offset)
    val durationMs = (DateUtils.nowMillis - startMs).toInt
    PasswordInfoRowResult(paging = paging, filters = filters, orderBys = orderBys, totalCount = totalCount, results = results, durationMs = durationMs)
  }
}
