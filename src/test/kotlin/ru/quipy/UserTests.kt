package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.UserController

@SpringBootTest
class UserTests {

	@Autowired
	private lateinit var userController: UserController

	@Test
	fun createUser() {
		val response = userController.createUser(
			"testLogin",
			"testName",
			"testPassword"
		)
		Assertions.assertEquals("testLogin", response.login)
		Assertions.assertEquals("testName", response.userName)
		Assertions.assertEquals("testPassword", response.password)
		Assertions.assertEquals(1, response.version)
	}

	@Test
	fun getUser() {
		val createdUserID = userController.createUser(
			"testLogin",
			"testName",
			"testPassword"
		).userID

		val response = userController.getUser(createdUserID)
		Assertions.assertNotNull(response)
		Assertions.assertEquals("testLogin", response!!.getLogin())
		Assertions.assertEquals("testName", response.getName())
		Assertions.assertEquals("testPassword", response.getPassword())
	}

	@Test
	fun createAndUpdateUser() {
		var response = userController.createUser(
			"testLogin",
			"testName",
			"testPassword"
		)
		Assertions.assertEquals("testLogin", response.login)
		Assertions.assertEquals("testName", response.userName)
		Assertions.assertEquals("testPassword", response.password)
		Assertions.assertEquals(1, response.version)

		userController.updateUser(
			response.userID,
			"testLogin1",
			"testName1",
			"testPassword1"
		)
		val userInfo = userController.getUser(response.userID)
		Assertions.assertEquals("testLogin1", userInfo!!.getLogin())
		Assertions.assertEquals("testName1", userInfo.getName())
		Assertions.assertEquals("testPassword1", userInfo.getPassword())
	}
}