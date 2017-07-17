package org.broadinstitute.dsde.firecloud.pages

import org.openqa.selenium.WebDriver

/**
  * Base class for pages that are reachable after signing in.
  */
abstract class AuthenticatedPage(implicit webDriver: WebDriver) extends FireCloudView {

  /**
    * Sign out of FireCloud.
    */
  def signOut(): Unit = {
    ui.clickAccountDropdown()
    ui.clickSignOut()
  }


  trait UI {
    private val accountDropdown = testId("account-dropdown")
    private val signOutLink = testId("sign-out")

    def checkAccountDropdown: Boolean = {
      find(accountDropdown).isDefined
    }

    def clickAccountDropdown(): Unit = {
      click on accountDropdown
    }

    def clickSignOut(): Unit = {
      click on (await enabled signOutLink)
    }
  }

  /*
   * This must be private so that subclasses can provide their own object
   * named "ui". The only disadvantage is that subclasses that want one MUST
   * provide their own "ui" object. However, it should be very rare that a
   * page class will want a "ui" object without also providing an extension of
   * the AuthenticatedPage.UI trait.
   */
  private object ui extends UI
}
