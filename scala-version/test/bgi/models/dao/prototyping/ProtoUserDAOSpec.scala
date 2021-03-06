package bgi.models.dao.prototyping

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import bgi.models._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global 

class ProtoUserDAOSpec extends FlatSpec with ScalaFutures{

	trait UserDAOTestDAL {
		val userDAO = new ProtoUserDAO()
		val hash = UserPassword("password", UserPasswordComplexity.Normal)
	}
	
	"The ProtoUserDAO" should "create a User" in new UserDAOTestDAL {
		val user = new User(name = "name", hash = hash, id = 0)
		whenReady(userDAO.create(user)) { dbUser =>
			assert(dbUser.get == user)
		}
	}

	it should "find a created User by ID" in new UserDAOTestDAL {
		val user = new User(name = "name", hash = hash)
		whenReady(userDAO.create(user)) { dbUser =>
			whenReady(userDAO.findById(dbUser.get.id)) { found =>
				assert(found.get.name == user.name)
				assert(found.get.hash == user.hash)
			}
		}
	}

	it should "assigned each created user a sequential id" in new UserDAOTestDAL {
		val users = List.range(0,10).map( i => new User("name",hash, id = i))
		val futureUsers : Future[List[Option[User]]] = Future.sequence(users.map(userDAO.create(_)))

		whenReady(futureUsers) { listOfOptionUsers =>
			val createdIds = listOfOptionUsers.map(_.get.id)
			for(i <- 0 until 10) {
				assert(createdIds.contains(i))
			}
		}
	}
	
	it should "remove users by id" in new UserDAOTestDAL {
		val users = List.range(0,10).map( i => new User("name",hash, id = i))
		val futureUsers : Future[List[Option[User]]] = Future.sequence(users.map(userDAO.create(_)))
		whenReady(futureUsers) { listOfOptionUsers =>
			val futureRemoves : Future[List[Boolean]] = Future.sequence(listOfOptionUsers.map(x => userDAO.remove(x.get.id)))
			whenReady(futureRemoves) { listOfBools => 
				listOfBools.map(x => assert(x == true))
			}
		}		
	}

	it should "update a user" in new UserDAOTestDAL {
		val user = new User(name = "name", hash = hash, id = 0)
		whenReady(userDAO.create(user)) { dbUser =>
			val futureSuccessBool = userDAO.update(user.copy(name = "test"))
			whenReady(futureSuccessBool) { successBool =>
				assert(successBool == true)
				whenReady(userDAO.findById(dbUser.get.id)) { found =>
					assert(found.get.name == "test")
				}
			}
		}
	}

	it should "increment a user's attempted logins" in new UserDAOTestDAL {
		val user = new User(name = "name", hash = hash, id = 0)
		whenReady(userDAO.create(user)) { dbUser =>
			val futureSuccessBool = userDAO.incrementLoginAttempt(user)
			whenReady(futureSuccessBool) { successBool =>
				assert(successBool == true)
				whenReady(userDAO.findById(dbUser.get.id)) { found =>
					assert(found.get.loginAttempts == 1)
				}
			}
		}	
	}

	it should "be able to reset a user's attempted logins" in new UserDAOTestDAL {
		val user = new User(name = "name", hash = hash, id = 0)
		whenReady(userDAO.create(user)) { dbUser =>
			val futureSuccessBool = userDAO.incrementLoginAttempt(user)
			whenReady(futureSuccessBool) { successBool =>
				assert(successBool == true)
				whenReady(userDAO.findById(dbUser.get.id)) { found =>
					assert(found.get.loginAttempts == 1)
					whenReady(userDAO.resetLoginAttempts(user)) { success =>
						assert(success == true)
						whenReady(userDAO.findById(dbUser.get.id)) { foundAgain =>
							assert(foundAgain.get.loginAttempts == 0)
						}
					}
				}
			}
		}	
	}

}