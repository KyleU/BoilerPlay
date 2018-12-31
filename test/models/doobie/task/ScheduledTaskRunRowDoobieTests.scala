/* Generated File */
package models.doobie.task

import com.kyleu.projectile.services.database.doobie.DoobieQueryService.Imports._
import com.kyleu.projectile.services.database.doobie.DoobieTestHelper.yolo._
import models.task.ScheduledTaskRunRow
import org.scalatest._

class ScheduledTaskRunRowDoobieTests extends FlatSpec with Matchers {

  "Doobie queries for [ScheduledTaskRunRow]" should "typecheck" in {
    ScheduledTaskRunRowDoobie.countFragment.query[Long].check.unsafeRunSync
    ScheduledTaskRunRowDoobie.selectFragment.query[ScheduledTaskRunRow].check.unsafeRunSync
    (ScheduledTaskRunRowDoobie.selectFragment ++ whereAnd(ScheduledTaskRunRowDoobie.searchFragment("..."))).query[ScheduledTaskRunRow].check.unsafeRunSync
  }
}
