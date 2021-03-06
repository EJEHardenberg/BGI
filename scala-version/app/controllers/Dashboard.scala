package bgi.controllers

import play.api._
import play.api.mvc._

import bgi.forms._
import bgi.models._
import bgi.models.pagedata._
import bgi.models.charts._
import bgi.services._
import bgi.globals.{Context, AnormContext, Authenticated}

import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Await}

/** Controller for handling generic non-specific pages */
abstract class DashboardController extends Controller with Context {
	def index = Action { implicit request =>
		if (request.session.get("userId").isDefined) {
			Redirect(bgi.controllers.routes.Dashboard.dashboard)
		} else {
			Ok(views.html.index())
		}
	}

	def dashboard = Authenticated.async { implicit request =>
		val futureLineItems = lineItemService.findInThisMonth(request.user)
		val futureCategories = categoryService.getAllForUser(request.user)
		val futureCharts = Future.successful(List[Pie]())

		val futureResult = for {
			lineItems <- futureLineItems
			categories <- futureCategories
			charts <- futureCharts
		} yield DashboardPageData(lineItems, categories, charts ++ List(Charts.fromCategories(categories)))

		futureResult.map { thing => 
			Ok(views.html.dashboard(thing))
		}
	}

	def test = Authenticated { implicit request =>
		val pie = new bgi.models.charts.Pie(80)
		pie.addPortion(bgi.models.charts.Portion(33.5, "Loans" ))
		pie.addPortion(bgi.models.charts.Portion(375, "Fun" ))
		pie.addPortion(bgi.models.charts.Portion(69.12, "Bills" ))
		pie.addPortion(bgi.models.charts.Portion(23.4, "Groceries" ))
		Ok(views.html.svg.pie(pie))
	}
}

object Dashboard extends DashboardController with AnormContext{ 

}
