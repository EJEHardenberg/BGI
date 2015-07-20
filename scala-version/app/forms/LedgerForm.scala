package bgi.forms

import play.api.data._
import play.api.data.Forms._

import com.typesafe.config.ConfigFactory

/** Container for the ledger form 
 * 
 * @param amount An amount being submitted, regular currency double to be converted to cents
 * @param name A name to keep track of the item being added
 * @param categoryId The id of the category the submitted line item belongs to
 */
case class LedgerForm(amount: Double, name: String, categoryId: Int)

/** Companion object of the Ledger (LineItem) form 
 *
 * {{{
 * LedgerForm.form.bindFromRequest.fold(
 *   formWithErrors => { BadRequest(...)} , 
 *   formNoErrors => { Redirect(...) }
 * )	
 * }}}
 */
object LedgerForm {
	val form = Form(
		mapping(
			"amount" -> nonEmptyText(minLength = 3, maxLength = 128),
			"name" -> nonEmptyText(minLength = 4, maxLength = 256)
			"categoryId" -> intNumber
		)(LedgerForm.apply)(LedgerForm.unapply)
	)
}
