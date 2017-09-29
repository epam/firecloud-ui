package org.broadinstitute.dsde.firecloud.test.metadata

import org.broadinstitute.dsde.firecloud.config.{AuthToken, Config, UserPool, Credentials}
import java.io.{File, PrintWriter}

import org.broadinstitute.dsde.firecloud.api.{AclEntry, WorkspaceAccessLevel}
import org.broadinstitute.dsde.firecloud.config.{AuthToken, AuthTokens, Config}
>>>>>>> in progress
import org.broadinstitute.dsde.firecloud.page.workspaces.WorkspaceDataPage
import org.broadinstitute.dsde.firecloud.test.{CleanUp, WebBrowserSpec, WebBrowserUtil}
import org.broadinstitute.dsde.firecloud.fixture.{UserFixtures, WorkspaceFixtures}
import org.scalatest.selenium.WebBrowser
import org.scalatest.{FlatSpec, ParallelTestExecution, ShouldMatchers}

class DataSpec extends FlatSpec with WebBrowserSpec
  with UserFixtures with WorkspaceFixtures with ParallelTestExecution
  with ShouldMatchers with WebBrowser with WebBrowserUtil with CleanUp {

  val billingProject = Config.Projects.default
  val defaultUser: Credentials = UserPool.chooseStudent
  implicit val authToken: AuthToken = AuthToken(defaultUser)
  behavior of "Data"

  it should "import a participants file" in withWebDriver { implicit driver =>
    withWorkspace(billingProject, "TestSpec_FireCloud_import_participants_file_") { workspaceName =>
      withSignIn(defaultUser) { _ =>
        val filename = "src/test/resources/participants.txt"
        val workspaceDataTab = new WorkspaceDataPage(billingProject, workspaceName).open
        workspaceDataTab.importFile(filename)
        workspaceDataTab.getNumberOfParticipants shouldBe 1
      }
    }
  }

  def makeTempMetadataFile(filePrefix: String, headers: List[String], rows: List[List[String]]): File = {
    val metadataFile = File.createTempFile(filePrefix, "txt")
    val writer = new PrintWriter(metadataFile)
    val rowStrings = rows map { _.mkString(s"\t") }
    val fileContent = s"""entity:${headers.mkString(s"\t")}\n${rowStrings.mkString(s"\n")}"""
    writer.write(fileContent)
    writer.close()
    metadataFile
  }

  def createAndImportMetadataFile(fileName: String, headers: List[String], dataTab: WorkspaceDataPage): Unit = {
    val data = for {
      h <- headers
    }yield {
      if (h == "participant_id") {
        "participant1"
      } else {
        h.takeRight(1)
      }
    }
    val file = makeTempMetadataFile(fileName, headers, List(data))
    dataTab.importFile(file.getAbsolutePath)
  }

  "Writer and reader should see new columns" - {
    "With no defaults or local preferences when writer imports metadata with new column" in withWebDriver { implicit driver =>
      implicit val authToken: AuthToken = AuthTokens.harry
      withWorkspace(Config.Projects.default, "DataSpec_column_display", aclEntries = List(AclEntry(Config.Users.ron.email, WorkspaceAccessLevel.Reader))) { workspaceName =>

        signIn(Config.Users.harry)
        val workspaceDataTab = new WorkspaceDataPage(Config.Projects.default, workspaceName).open
        val headers1 = List("participant_id", "test1")
        createAndImportMetadataFile("DataSpec_column_display", headers1, workspaceDataTab)
        workspaceDataTab.ui.readColumnHeaders shouldEqual headers1
        val headers2 = headers1 :+ "test2"
        createAndImportMetadataFile("DataSpec_column_display2", headers2, workspaceDataTab)
        workspaceDataTab.ui.readColumnHeaders shouldEqual headers2
        workspaceDataTab.signOut()
        signIn(Config.Users.ron)
        workspaceDataTab.open
        workspaceDataTab.ui.readColumnHeaders shouldEqual headers2
      }
    }

    "With local preferences, but no defaults when writer imports metadata with new column" in withWebDriver { implicit driver =>
      implicit val authToken: AuthToken = AuthTokens.harry
      withWorkspace(Config.Projects.default, "DataSpec_col_display_w_preferences", aclEntries = List(AclEntry(Config.Users.ron.email, WorkspaceAccessLevel.Reader))) { workspaceName =>

        signIn(Config.Users.harry)
        val workspaceDataTab = new WorkspaceDataPage(Config.Projects.default, workspaceName).open
        val headers1 = List("participant_id", "test1", "test2")
        createAndImportMetadataFile("DataSpec_column_display", headers1, workspaceDataTab)
        workspaceDataTab.hideColumn("test1")
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test2")
        workspaceDataTab.signOut()

        signIn(Config.Users.ron)
        workspaceDataTab.open
        workspaceDataTab.ui.readColumnHeaders shouldEqual headers1
        workspaceDataTab.hideColumn("test2")
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1")
        workspaceDataTab.signOut()

        signIn(Config.Users.harry)
        workspaceDataTab.open
        val headers2 = List("participant_id", "test1", "test2", "test3")
        createAndImportMetadataFile("DataSpec_column_display2", headers2, workspaceDataTab)
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test2", "test3")
        workspaceDataTab.signOut()

        signIn(Config.Users.ron)
        workspaceDataTab.open
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1", "test3")
      }
    }

    "With defaults on workspace, but no local preferences when writer imports metadata with new column" in withWebDriver { implicit driver =>
      implicit val authToken: AuthToken = AuthTokens.harry
      withWorkspace(Config.Projects.default, "DataSpec_col_display_w_defaults", aclEntries = List(AclEntry(Config.Users.ron.email, WorkspaceAccessLevel.Reader))) {
        workspaceName =>

          signIn(Config.Users.harry)
          val workspaceSummaryTab = new WorkspaceSummaryPage(Config.Projects.default, workspaceName).open
          workspaceSummaryTab.ui.beginEditing
          workspaceSummaryTab.ui.addWorkspaceAttribute("workspace-column-defaults", "{\"participant\": {\"shown\": [\"participant_id\", \"test1\"], \"hidden\": [\"test2\", \"test3\"]}}")
          workspaceSummaryTab.ui.save
          val workspaceDataTab = new WorkspaceDataPage(Config.Projects.default, workspaceName).open
          val headers1 = List("participant_id", "test1", "test2", "test3")
          createAndImportMetadataFile("DataSpec_column_display", headers1, workspaceDataTab)
          workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1")
          workspaceDataTab.signOut()

          signIn(Config.Users.ron)
          workspaceDataTab.open
          workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1")
          workspaceDataTab.signOut()

          signIn(Config.Users.harry)
          workspaceDataTab.open
          val headers2 = headers1 :+ "test4"
          createAndImportMetadataFile("DataSpec_column_display2", headers2, workspaceDataTab)
          workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1", "test4")
          workspaceDataTab.signOut()

          signIn(Config.Users.ron)
          workspaceDataTab.open
          workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1", "test4")
      }
    }

    "With defaults on workspace and local preferences for reader and writer when writer imports metadata with new column" in withWebDriver { implicit driver =>
      implicit val authToken: AuthToken = AuthTokens.harry
      withWorkspace(Config.Projects.default, "DataSpec_col_display_w_defaults_and_local", aclEntries = List(AclEntry(Config.Users.ron.email, WorkspaceAccessLevel.Reader))) { workspaceName =>

        signIn(Config.Users.harry)
        val workspaceSummaryTab = new WorkspaceSummaryPage(Config.Projects.default, workspaceName).open
        workspaceSummaryTab.ui.beginEditing
        workspaceSummaryTab.ui.addWorkspaceAttribute("workspace-column-defaults", "{\"participant\": {\"shown\": [\"participant_id\", \"test1\" \"test4\"], \"hidden\": [\"test2\", \"test3\"]}}")
        workspaceSummaryTab.ui.save
        val workspaceDataTab = new WorkspaceDataPage(Config.Projects.default, workspaceName).open
        val headers1 = List("participant_id", "test1", "test2", "test3", "test4")
        createAndImportMetadataFile("DataSpec_column_display", headers1, workspaceDataTab)
        workspaceDataTab.hideColumn("test1")
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test2", "test3", "test4")
        workspaceDataTab.signOut()

        signIn(Config.Users.ron)
        workspaceDataTab.open
        workspaceDataTab.hideColumn("test4")
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1", "test2", "test3")
        workspaceDataTab.signOut()

        signIn(Config.Users.harry)
        workspaceDataTab.open
        val headers2 = headers1 :+ "test5"
        createAndImportMetadataFile("DataSpec_column_display2", headers2, workspaceDataTab)
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test2", "test3", "test4", "test5")
        workspaceDataTab.signOut

        signIn(Config.Users.ron)
        workspaceDataTab.open
        workspaceDataTab.ui.readColumnHeaders shouldEqual List("participant_id", "test1", "test2", "test3", "test5")
      }
    }
  }


    }
  }
}
