/* Generated File */
package models.audit

import com.kyleu.projectile.models.result.BaseResult
import com.kyleu.projectile.models.result.filter.Filter
import com.kyleu.projectile.models.result.orderBy.OrderBy
import com.kyleu.projectile.models.result.paging.PagingOptions
import com.kyleu.projectile.util.DateUtils
import com.kyleu.projectile.util.JsonSerializers._
import java.time.LocalDateTime

final case class AuditRecordRowResult(
    override val filters: Seq[Filter] = Nil,
    override val orderBys: Seq[OrderBy] = Nil,
    override val totalCount: Int = 0,
    override val paging: PagingOptions = PagingOptions(),
    override val results: Seq[AuditRecordRow] = Nil,
    override val durationMs: Int = 0,
    override val occurred: LocalDateTime = DateUtils.now
) extends BaseResult[AuditRecordRow]

object AuditRecordRowResult {
  implicit val jsonEncoder: Encoder[AuditRecordRowResult] = (r: AuditRecordRowResult) => io.circe.Json.obj(
    ("filters", r.filters.asJson),
    ("orderBys", r.orderBys.asJson),
    ("totalCount", r.totalCount.asJson),
    ("paging", r.paging.asJson),
    ("results", r.results.asJson),
    ("durationMs", r.durationMs.asJson),
    ("occurred", r.occurred.asJson)
  )

  implicit val jsonDecoder: Decoder[AuditRecordRowResult] = (c: io.circe.HCursor) => for {
    filters <- c.downField("filters").as[Seq[Filter]]
    orderBys <- c.downField("orderBys").as[Seq[OrderBy]]
    totalCount <- c.downField("totalCount").as[Int]
    paging <- c.downField("paging").as[PagingOptions]
    results <- c.downField("results").as[Seq[AuditRecordRow]]
    durationMs <- c.downField("durationMs").as[Int]
    occurred <- c.downField("occurred").as[LocalDateTime]
  } yield AuditRecordRowResult(filters, orderBys, totalCount, paging, results, durationMs, occurred)

  def fromRecords(
    q: Option[String], filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None,
    startMs: Long, totalCount: Int, results: Seq[AuditRecordRow]
  ) = {
    val paging = PagingOptions.from(totalCount, limit, offset)
    val durationMs = (DateUtils.nowMillis - startMs).toInt
    AuditRecordRowResult(paging = paging, filters = filters, orderBys = orderBys, totalCount = totalCount, results = results, durationMs = durationMs)
  }
}
